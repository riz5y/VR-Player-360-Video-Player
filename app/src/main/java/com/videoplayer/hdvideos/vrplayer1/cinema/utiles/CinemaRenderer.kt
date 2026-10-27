package com.videoplayer.hdvideos.vrplayer1.cinema.utiles


import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.opengl.EGLConfig
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import android.view.Surface
import com.videoplayer.hdvideos.vrplayer1.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10


class CinemaRenderer(
    private val context: Context,
    private val onSurfaceReady: (Surface) -> Unit
) : GLSurfaceView.Renderer {

    @Volatile var yaw: Float = 0f
    @Volatile var pitch: Float = 0f

    private var videoRotation = 0
    private var videoAspect = 16f / 9f
    private val baseHeight = 6.0f
    private val widthScale  = 1.25f   // extra width multiplier — increase to widen screen
    var screenZ = -10f

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix       = FloatArray(16)
    private val mvpMatrix        = FloatArray(16)
    private val rotMatrix        = FloatArray(16)
    private val stMatrix         = FloatArray(16)

    private var videoWidth  = 0
    private var videoHeight = 0

    // ── FOV zoom ──────────────────────────────────────────────────────────────
    private var currentFov = 90f
    private val minFov     = 20f
    private val maxFov     = 110f
    private var cachedWidth  = 1
    private var cachedHeight = 1

    // ── CPU-side geometry (kept only for initial VBO upload) ──────────────────
    private var sphereVertexBuffer: FloatBuffer? = null
    private var sphereTexBuffer:    FloatBuffer? = null
    private var sphereIndexBuffer:  ShortBuffer? = null
    private var sphereIndexCount = 0

    private var quadVertexBuffer: FloatBuffer? = null
    private var quadTexBuffer:    FloatBuffer? = null

    // ── FIX 1 – VBOs: geometry lives on the GPU after first upload ────────────
    // [0]=sphere vertices  [1]=sphere tex  [2]=quad vertices  [3]=quad tex
    private val vboIds    = IntArray(4)
    private val iboIds    = IntArray(1)   // sphere index buffer object
    private var vbosReady = false

    // ── Textures ──────────────────────────────────────────────────────────────
    private var sphereTextureId = 0
    private var videoTextureId  = 0

    private lateinit var surfaceTexture: SurfaceTexture

    // ── FIX 2 – Surface race: only expose Surface after GL init is done ───────
    @Volatile private var surfaceTextureReady = false

    // ── Shader programs ───────────────────────────────────────────────────────
    private var program2D  = 0
    private var programOES = 0

    // ── FIX 3 – Cached GL attribute / uniform locations (no per-frame lookup) ─
    // program2D (sphere)
    private var loc2D_aPos  = -1
    private var loc2D_aTex  = -1
    private var loc2D_uMVP  = -1
    private var loc2D_uTex  = -1
    // programOES (video quad)
    private var locOES_aPos      = -1
    private var locOES_aTex      = -1
    private var locOES_uMVP      = -1
    private var locOES_uTex      = -1
    private var locOES_uTexMatrix = -1
    private var locOES_uBright   = -1

    // Brightness uniform (bonus – costs 0 extra draw calls)
    @Volatile var brightness = 1.0f

    // ─────────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    override fun onSurfaceCreated(p0: GL10, p1: javax.microedition.khronos.egl.EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Build CPU buffers first (needed for VBO upload)
        buildSphere(radius = 50f, stacks = 64, slices = 64)
        buildQuad(baseHeight * videoAspect * widthScale, baseHeight, screenZ)

        // Compile shaders & cache ALL locations once
        program2D  = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_2D)
        programOES = createProgram(VERTEX_SHADER, FRAGMENT_SHADER_OES)
        cacheLocations()

        // FIX 1 – upload geometry to GPU via VBOs
        uploadVBOs()

        // Textures
        sphereTextureId = load2DTexture(R.drawable.imagethreesixty)
        videoTextureId  = createOesTexture()

        surfaceTexture = SurfaceTexture(videoTextureId)
        surfaceTexture.setDefaultBufferSize(1920, 1080)

        // FIX 2 – set flag BEFORE calling onSurfaceReady so the Activity
        // can safely attach the player; no race between GL thread and main thread
        surfaceTextureReady = true
        val surface = Surface(surfaceTexture)
        onSurfaceReady(surface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cachedWidth  = width
        cachedHeight = height
        GLES20.glViewport(0, 0, width, height)
        rebuildProjection()
    }


    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (surfaceTextureReady) {
            surfaceTexture.updateTexImage()
            surfaceTexture.getTransformMatrix(stMatrix)
        }

        Matrix.setIdentityM(rotMatrix, 0)
        Matrix.rotateM(rotMatrix, 0, yaw,   0f, 1f, 0f)
        Matrix.rotateM(rotMatrix, 0, pitch, 1f, 0f, 0f)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 0f, 0f, 0f, -1f, 0f, 1f, 0f)

        val tempView = FloatArray(16)
        Matrix.multiplyMM(tempView,   0, viewMatrix,       0, rotMatrix, 0)
        Matrix.multiplyMM(mvpMatrix,  0, projectionMatrix, 0, tempView,  0)

        GLES20.glDepthMask(false)
        drawSphere(mvpMatrix)
        GLES20.glDepthMask(true)

        drawQuad(mvpMatrix)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX 4 – Release: delete textures, VBOs, SurfaceTexture on teardown
    // Call this from the GL thread via glSurface.queueEvent { renderer.release() }
    // ─────────────────────────────────────────────────────────────────────────
    fun release() {
        surfaceTextureReady = false
        if (::surfaceTexture.isInitialized) surfaceTexture.release()

        val textures = intArrayOf(sphereTextureId, videoTextureId)
        GLES20.glDeleteTextures(textures.size, textures, 0)
        sphereTextureId = 0
        videoTextureId  = 0

        if (vbosReady) {
            GLES20.glDeleteBuffers(vboIds.size, vboIds, 0)
            GLES20.glDeleteBuffers(iboIds.size, iboIds, 0)
            vbosReady = false
        }

        GLES20.glDeleteProgram(program2D)
        GLES20.glDeleteProgram(programOES)
        program2D  = 0
        programOES = 0
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Zoom (FOV-based — screen stays fixed, lens narrows)
    // ─────────────────────────────────────────────────────────────────────────
    fun zoomBy(delta: Float) {
        currentFov = (currentFov - delta).coerceIn(minFov, maxFov)
        rebuildProjection()
    }

    fun resetZoom() {
        currentFov = 90f
        rebuildProjection()
    }

    private fun rebuildProjection() {
        val aspect = cachedWidth.toFloat() / cachedHeight.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, currentFov, aspect, 0.1f, 200f)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX 3 – Cache all GL locations once after program compilation
    // ─────────────────────────────────────────────────────────────────────────
    private fun cacheLocations() {
        loc2D_aPos = GLES20.glGetAttribLocation(program2D,  "aPosition")
        loc2D_aTex = GLES20.glGetAttribLocation(program2D,  "aTexCoord")
        loc2D_uMVP = GLES20.glGetUniformLocation(program2D, "uMVPMatrix")
        loc2D_uTex = GLES20.glGetUniformLocation(program2D, "uTexture")

        locOES_aPos       = GLES20.glGetAttribLocation(programOES,  "aPosition")
        locOES_aTex       = GLES20.glGetAttribLocation(programOES,  "aTexCoord")
        locOES_uMVP       = GLES20.glGetUniformLocation(programOES, "uMVPMatrix")
        locOES_uTex       = GLES20.glGetUniformLocation(programOES, "uTexture")
        locOES_uTexMatrix = GLES20.glGetUniformLocation(programOES, "uTexMatrix")
        locOES_uBright    = GLES20.glGetUniformLocation(programOES, "uBrightness")
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX 1 – VBO upload
    // ─────────────────────────────────────────────────────────────────────────
    private fun uploadVBOs() {
        GLES20.glGenBuffers(vboIds.size, vboIds, 0)
        GLES20.glGenBuffers(iboIds.size, iboIds, 0)

        // Sphere vertices
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            sphereVertexBuffer!!.capacity() * 4, sphereVertexBuffer,
            GLES20.GL_STATIC_DRAW)

        // Sphere tex coords
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[1])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            sphereTexBuffer!!.capacity() * 4, sphereTexBuffer,
            GLES20.GL_STATIC_DRAW)

        // Sphere indices (IBO)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboIds[0])
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
            sphereIndexBuffer!!.capacity() * 2, sphereIndexBuffer,
            GLES20.GL_STATIC_DRAW)

        // Quad – uploaded as DYNAMIC so we can update on aspect change
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[2])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            quadVertexBuffer!!.capacity() * 4, quadVertexBuffer,
            GLES20.GL_DYNAMIC_DRAW)

        // Quad tex coords (static)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[3])
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
            quadTexBuffer!!.capacity() * 4, quadTexBuffer,
            GLES20.GL_STATIC_DRAW)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        // Free CPU buffers – data now lives on GPU
        sphereVertexBuffer = null
        sphereTexBuffer    = null
        sphereIndexBuffer  = null
        quadTexBuffer      = null

        vbosReady = true
    }

    // Re-upload quad vertices only (called when aspect ratio changes)
    private fun updateQuadVBO() {
        if (!vbosReady) return
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[2])
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0,
            quadVertexBuffer!!.capacity() * 4, quadVertexBuffer)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        quadVertexBuffer = null
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sphere geometry
    // ─────────────────────────────────────────────────────────────────────────
    private fun buildSphere(radius: Float, stacks: Int, slices: Int) {
        val vertices  = mutableListOf<Float>()
        val texCoords = mutableListOf<Float>()
        val indices   = mutableListOf<Short>()

        for (stack in 0..stacks) {
            val phi    = Math.PI * stack / stacks
            val sinPhi = Math.sin(phi).toFloat()
            val cosPhi = Math.cos(phi).toFloat()
            val v      = stack.toFloat() / stacks

            for (slice in 0..slices) {
                val theta    = 2.0 * Math.PI * slice / slices
                val sinTheta = Math.sin(theta).toFloat()
                val cosTheta = Math.cos(theta).toFloat()
                val u        = slice.toFloat() / slices

                vertices.add(-radius * sinPhi * cosTheta)
                vertices.add(radius  * cosPhi)
                vertices.add(radius  * sinPhi * sinTheta)

                texCoords.add(1f - u)
                texCoords.add(v)
            }
        }

        for (stack in 0 until stacks) {
            for (slice in 0 until slices) {
                val first  = (stack * (slices + 1) + slice).toShort()
                val second = (first + slices + 1).toShort()
                indices.add(first);              indices.add(second)
                indices.add((first + 1).toShort())
                indices.add(second);             indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }

        sphereVertexBuffer = toFloatBuffer(vertices.toFloatArray())
        sphereTexBuffer    = toFloatBuffer(texCoords.toFloatArray())
        sphereIndexBuffer  = toShortBuffer(indices.toShortArray())
        sphereIndexCount   = indices.size
    }

    private fun drawSphere(mvp: FloatArray) {
        GLES20.glUseProgram(program2D)

        val sphereModel = FloatArray(16)
        val sphereMvp   = FloatArray(16)
        Matrix.setIdentityM(sphereModel, 0)
        Matrix.rotateM(sphereModel, 0, 90f, 0f, 1f, 0f)
        Matrix.multiplyMM(sphereMvp, 0, mvp, 0, sphereModel, 0)

        GLES20.glUniformMatrix4fv(loc2D_uMVP, 1, false, sphereMvp, 0)

        // FIX 1 – bind VBO instead of passing CPU pointer
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[0])
        GLES20.glEnableVertexAttribArray(loc2D_aPos)
        GLES20.glVertexAttribPointer(loc2D_aPos, 3, GLES20.GL_FLOAT, false, 0, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[1])
        GLES20.glEnableVertexAttribArray(loc2D_aTex)
        GLES20.glVertexAttribPointer(loc2D_aTex, 2, GLES20.GL_FLOAT, false, 0, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sphereTextureId)
        GLES20.glUniform1i(loc2D_uTex, 0)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, iboIds[0])
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereIndexCount,
            GLES20.GL_UNSIGNED_SHORT, 0)

        GLES20.glDisableVertexAttribArray(loc2D_aPos)
        GLES20.glDisableVertexAttribArray(loc2D_aTex)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Quad (cinema screen)
    // ─────────────────────────────────────────────────────────────────────────
    private fun buildQuad(width: Float, height: Float, z: Float) {
        val hw      = width  / 2f
        val hh      = height / 2f
        // yOffset: positive = raise screen up above eye level
        // Matches the cinema image where screen sits above the seating sightline
        val yOffset = 0.8f

        quadVertexBuffer = toFloatBuffer(floatArrayOf(
            -hw,  hh + yOffset, z,
            -hw, -hh + yOffset, z,
            hw, -hh + yOffset, z,
            hw,  hh + yOffset, z
        ))

        quadTexBuffer = toFloatBuffer(floatArrayOf(
            0f, 0f,
            0f, 1f,
            1f, 1f,
            1f, 0f
        ))
    }

    private fun drawQuad(mvp: FloatArray) {
        val modelMatrix = FloatArray(16)
        val finalMvp    = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)

        when (videoRotation) {
            90  -> Matrix.rotateM(modelMatrix, 0,  90f, 0f, 0f, 1f)
            180 -> Matrix.rotateM(modelMatrix, 0, 180f, 0f, 0f, 1f)
            270 -> Matrix.rotateM(modelMatrix, 0, 270f, 0f, 0f, 1f)
        }

        Matrix.multiplyMM(finalMvp, 0, mvp, 0, modelMatrix, 0)

        GLES20.glUseProgram(programOES)

        GLES20.glUniformMatrix4fv(locOES_uMVP,       1, false, finalMvp, 0)
        GLES20.glUniformMatrix4fv(locOES_uTexMatrix, 1, false, stMatrix, 0)
        GLES20.glUniform1f(locOES_uBright, brightness)

        // FIX 1 – bind quad VBOs
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[2])
        GLES20.glEnableVertexAttribArray(locOES_aPos)
        GLES20.glVertexAttribPointer(locOES_aPos, 3, GLES20.GL_FLOAT, false, 0, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboIds[3])
        GLES20.glEnableVertexAttribArray(locOES_aTex)
        GLES20.glVertexAttribPointer(locOES_aTex, 2, GLES20.GL_FLOAT, false, 0, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTextureId)
        GLES20.glUniform1i(locOES_uTex, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(locOES_aPos)
        GLES20.glDisableVertexAttribArray(locOES_aTex)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX 5 – Aspect ratio: swap once for rotation, never swap again
    // ─────────────────────────────────────────────────────────────────────────
    fun updateVideoAspect(width: Int, height: Int, rotation: Int) {
        if (width <= 0 || height <= 0) return
        videoRotation = rotation
        if (width == videoWidth && height == videoHeight) return

        videoWidth  = width
        videoHeight = height

        // Apply rotation swap ONCE — no second swap for portrait videos
        val (w, h) = if (rotation == 90 || rotation == 270) height to width
        else                                    width  to height

        videoAspect = w.toFloat() / h.toFloat()

        surfaceTexture.setDefaultBufferSize(width, height)

        buildQuad(baseHeight * videoAspect * widthScale, baseHeight, screenZ)
        updateQuadVBO()
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Texture loaders
    // ─────────────────────────────────────────────────────────────────────────
//    private fun load2DTexture(resId: Int): Int {
//        val ids = IntArray(1)
//        GLES20.glGenTextures(1, ids, 0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ids[0])
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
//        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
//        val bitmap = BitmapFactory.decodeResource(context.resources, resId)
//        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
//        bitmap.recycle()
//        return ids[0]
//    }
    private fun load2DTexture(resId: Int): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ids[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeResource(context.resources, resId, options)

        options.inSampleSize = calculateInSampleSize(options, 2048, 1024)
        options.inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
        return ids[0]
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth  = width  / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun createOesTexture(): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, ids[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        return ids[0]
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shader helpers
    // ─────────────────────────────────────────────────────────────────────────
    private fun createProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER,   vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        val prog = GLES20.glCreateProgram()
        GLES20.glAttachShader(prog, vert)
        GLES20.glAttachShader(prog, frag)
        GLES20.glLinkProgram(prog)
        GLES20.glDeleteShader(vert)
        GLES20.glDeleteShader(frag)
        return prog
    }

    private fun compileShader(type: Int, src: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, src)
        GLES20.glCompileShader(shader)
        return shader
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Buffer helpers
    // ─────────────────────────────────────────────────────────────────────────
    private fun toFloatBuffer(data: FloatArray): FloatBuffer =
        ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(data); position(0) }

    private fun toShortBuffer(data: ShortArray): ShortBuffer =
        ByteBuffer.allocateDirect(data.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply { put(data); position(0) }

    // ─────────────────────────────────────────────────────────────────────────
    // Shaders
    // ─────────────────────────────────────────────────────────────────────────
    companion object {

        private val VERTEX_SHADER = """
            uniform   mat4 uMVPMatrix;
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying   vec2 vTexCoord;
            void main() {
                gl_Position = uMVPMatrix * aPosition;
                vTexCoord   = aTexCoord;
            }
        """.trimIndent()

        private val FRAGMENT_SHADER_2D = """
            precision mediump float;
            uniform sampler2D uTexture;
            varying vec2      vTexCoord;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """.trimIndent()

        private val FRAGMENT_SHADER_OES = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES uTexture;
            varying vec2               vTexCoord;
            uniform mat4               uTexMatrix;
            uniform float              uBrightness;
            void main() {
                vec2 tex  = (uTexMatrix * vec4(vTexCoord, 0.0, 1.0)).xy;
                tex.y     = 1.0 - tex.y;
                vec4 col  = texture2D(uTexture, tex);
                gl_FragColor = vec4(col.rgb * uBrightness, col.a);
            }
        """.trimIndent()
    }
}