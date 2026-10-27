package com.videoplayer.hdvideos.vrplayer1.Detail.utils

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class SphereRenderer(
    private val viewMode: ViewMode,
    private val onSurfaceReady: (Surface) -> Unit
) : GLSurfaceView.Renderer {

    @Volatile
    var yaw: Float = 0f
    @Volatile
    var pitch: Float = 0f

    // ── GL resources ──────────────────────────────────────────────────────────
    private var sphereProgram = 0
    private var distortionProgram = 0
    private var sphereVBO = 0
    private var sphereIBO = 0
    private var sphereIndexCount = 0
    private var quadVBO = 0
    private var videoTexture = 0
    private var fboTexture = 0
    private var fbo = 0
    private var surfaceTexture: SurfaceTexture? = null

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val tmpMatrix = FloatArray(16)

    private var viewportW = 1
    private var viewportH = 1

    // ── Sphere vertex shader ──────────────────────────────────────────────────
    private val SPHERE_VERT = """
        uniform mat4 uMVP;
        attribute vec3 aPos;
        attribute vec2 aTex;
        varying vec2 vTex;
        void main() {
            gl_Position = uMVP * vec4(aPos, 1.0);
            vTex = aTex;
        }
    """.trimIndent()

    // Uses GL_TEXTURE_EXTERNAL_OES for the camera/video SurfaceTexture
    private val SPHERE_FRAG = """
        #extension GL_OES_EGL_image_external : require
        precision mediump float;
        uniform samplerExternalOES uTex;
        varying vec2 vTex;
        void main() {
            gl_FragColor = texture2D(uTex, vTex);
        }
    """.trimIndent()

    // ── Barrel-distortion shaders (Cardboard mode) ────────────────────────────
    private val DISTORT_VERT = """
        attribute vec2 aPos;
        attribute vec2 aTex;
        varying vec2 vTex;
        void main() {
            gl_Position = vec4(aPos, 0.0, 1.0);
            vTex = aTex;
        }
    """.trimIndent()

    /**
     * Simple radial barrel distortion.
     * k1/k2 approximate Google Cardboard v1 lens profile.
     */
    private val DISTORT_FRAG = """
        precision mediump float;
        uniform sampler2D uTex;
        varying vec2 vTex;

        // Cardboard v1 distortion coefficients
        const float k1 = 0.441;
        const float k2 = 0.156;

        vec2 distort(vec2 uv, vec2 center) {
            vec2 p  = uv - center;
            float r2 = dot(p, p);
            float factor = 1.0 + k1 * r2 + k2 * r2 * r2;
            return center + p * factor;
        }

        void main() {
            // Left eye (x in [0,0.5]) and right eye (x in [0.5,1])
            bool isRight = vTex.x > 0.5;
            vec2 center  = isRight ? vec2(0.75, 0.5) : vec2(0.25, 0.5);
            vec2 distorted = distort(vTex, center);

            // Discard pixels that land outside their half
            float lo = isRight ? 0.5 : 0.0;
            float hi = isRight ? 1.0 : 0.5;
            if (distorted.x < lo || distorted.x > hi ||
                distorted.y < 0.0 || distorted.y > 1.0) {
                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
            } else {
                gl_FragColor = texture2D(uTex, distorted);
            }
        }
    """.trimIndent()

    // ── Full-screen quad used for the distortion pass ─────────────────────────
    private val QUAD_VERTS = floatArrayOf(
        // x,    y,   u,   v
        -1f, -1f, 0f, 0f,
        1f, -1f, 1f, 0f,
        -1f, 1f, 0f, 1f,
        1f, 1f, 1f, 1f
    )

    // ─────────────────────────────────────────────────────────────────────────
    // GLSurfaceView.Renderer
    // ─────────────────────────────────────────────────────────────────────────

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        // ── Video texture (OES external) ──────────────────────────────────────
        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        videoTexture = texIds[0]
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTexture)
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )

        surfaceTexture = SurfaceTexture(videoTexture).also { st ->
            onSurfaceReady(Surface(st))
        }

        // ── Compile programs ──────────────────────────────────────────────────
        sphereProgram = buildProgram(SPHERE_VERT, SPHERE_FRAG)
        if (viewMode == ViewMode.CARDBOARD) {
            distortionProgram = buildProgram(DISTORT_VERT, DISTORT_FRAG)
        }

        // ── Geometry ──────────────────────────────────────────────────────────
        buildSphere(stacks = 64, slices = 64)
        buildQuadVBO()
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        viewportW = width
        viewportH = height

        // Projection: wide FOV for immersive 360
        Matrix.perspectiveM(projMatrix, 0, 90f, width.toFloat() / height, 0.1f, 100f)

        if (viewMode == ViewMode.CARDBOARD) {
            // Re-create FBO at new size
            rebuildFbo(width, height)
        }
    }

    override fun onDrawFrame(gl: GL10) {
        try {
            surfaceTexture?.updateTexImage()
        } catch (e: Exception) {
            // Frame not yet available from decoder or surface releasing
        }

        when (viewMode) {
            ViewMode.DEVICE, ViewMode.PANORAMIC -> {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                drawEye(0, 0, viewportW, viewportH)
            }

            ViewMode.SBS -> {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                val half = viewportW / 2
                drawEye(0, 0, half, viewportH)
                drawEye(half, 0, half, viewportH)
            }

            ViewMode.CARDBOARD -> {
                // ── Pass 1: render SBS into FBO ────────────────────────────
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo)
                GLES20.glViewport(0, 0, viewportW, viewportH)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
                val half = viewportW / 2
                drawEye(0, 0, half, viewportH)
                drawEye(half, 0, half, viewportH)

                // ── Pass 2: barrel distortion ──────────────────────────────
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                GLES20.glViewport(0, 0, viewportW, viewportH)
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                drawDistorted()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun drawEye(x: Int, y: Int, w: Int, h: Int) {
        // Set viewport for this eye
        GLES20.glViewport(x, y, w, h)

        // Enable scissor test to prevent drawing outside viewport
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
        GLES20.glScissor(x, y, w, h)

        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LESS)

        // View matrix from yaw / pitch
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(pitch.toDouble()).toFloat(), 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(-yaw.toDouble()).toFloat(), 0f, 1f, 0f)

        // For SBS / Cardboard recalculate projection per eye viewport
        val fov = if (viewMode == ViewMode.PANORAMIC) 120f else 90f
        Matrix.perspectiveM(projMatrix, 0, fov, w.toFloat() / h.toFloat(), 0.1f, 100f)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(sphereProgram)

        val uMVP = GLES20.glGetUniformLocation(sphereProgram, "uMVP")
        GLES20.glUniformMatrix4fv(uMVP, 1, false, mvpMatrix, 0)

        val uTex = GLES20.glGetUniformLocation(sphereProgram, "uTex")
        GLES20.glUniform1i(uTex, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTexture)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereVBO)
        val stride = 5 * 4  // 5 floats × 4 bytes

        val aPos = GLES20.glGetAttribLocation(sphereProgram, "aPos")
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, stride, 0)

        val aTex = GLES20.glGetAttribLocation(sphereProgram, "aTex")
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, stride, 3 * 4)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIBO)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, sphereIndexCount,
            GLES20.GL_UNSIGNED_SHORT, 0
        )

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glDisableVertexAttribArray(aTex)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)

        // Disable scissor test after drawing
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST)
    }

    private fun drawDistorted() {
        // Disable depth test for full-screen quad
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)

        GLES20.glUseProgram(distortionProgram)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexture)
        val uTex = GLES20.glGetUniformLocation(distortionProgram, "uTex")
        GLES20.glUniform1i(uTex, 0)

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVBO)
        val stride = 4 * 4
        val aPos = GLES20.glGetAttribLocation(distortionProgram, "aPos")
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, stride, 0)
        val aTex = GLES20.glGetAttribLocation(distortionProgram, "aTex")
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, stride, 2 * 4)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glDisableVertexAttribArray(aTex)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
    }

    /** Generate sphere vertices (position + UV) and upload to GPU. */
    private fun buildSphere(stacks: Int, slices: Int) {
        val vertexData = mutableListOf<Float>()
        val indexData = mutableListOf<Short>()
        val radius = 1f
        val PI = Math.PI.toFloat()

        for (i in 0..stacks) {
            val phi = PI * i / stacks
            val sinP = sin(phi)
            val cosP = cos(phi)
            for (j in 0..slices) {
                val theta = 2f * PI * j / slices
                val x = sinP * cos(theta)
                val y = cosP
                val z = sinP * sin(theta)
                val u = 1f - j.toFloat() / slices
                val v = i.toFloat() / stacks
                vertexData += listOf(x * radius, y * radius, z * radius, u, v)
            }
        }

        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val a = (i * (slices + 1) + j).toShort()
                val b = ((i + 1) * (slices + 1) + j).toShort()
                val c = ((i + 1) * (slices + 1) + j + 1).toShort()
                val d = (i * (slices + 1) + j + 1).toShort()
                indexData += listOf(a, b, d, b, c, d)
            }
        }

        sphereIndexCount = indexData.size

        // VBO
        val vboIds = IntArray(2)
        GLES20.glGenBuffers(2, vboIds, 0)
        sphereVBO = vboIds[0]
        sphereIBO = vboIds[1]

        val vb = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vb.put(vertexData.toFloatArray()).position(0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereVBO)
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vb.capacity() * 4, vb, GLES20.GL_STATIC_DRAW)

        val ib = ByteBuffer.allocateDirect(indexData.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        ib.put(indexData.toShortArray()).position(0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIBO)
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER, ib.capacity() * 2, ib,
            GLES20.GL_STATIC_DRAW
        )
    }

    private fun buildQuadVBO() {
        val ids = IntArray(1)
        GLES20.glGenBuffers(1, ids, 0)
        quadVBO = ids[0]
        val buf = ByteBuffer.allocateDirect(QUAD_VERTS.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        buf.put(QUAD_VERTS).position(0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVBO)
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER, buf.capacity() * 4, buf,
            GLES20.GL_STATIC_DRAW
        )
    }

    private fun rebuildFbo(w: Int, h: Int) {
        // Delete old FBO/texture if they exist
        if (fbo != 0) GLES20.glDeleteFramebuffers(1, intArrayOf(fbo), 0)
        if (fboTexture != 0) GLES20.glDeleteTextures(1, intArrayOf(fboTexture), 0)

        // Create color texture
        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        fboTexture = texIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexture)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
            w, h, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        )

        // Create depth renderbuffer
        val depthIds = IntArray(1)
        GLES20.glGenRenderbuffers(1, depthIds, 0)
        val depthRB = depthIds[0]
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRB)
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, w, h)

        // Create and setup FBO
        val fboIds = IntArray(1)
        GLES20.glGenFramebuffers(1, fboIds, 0)
        fbo = fboIds[0]
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo)
        GLES20.glFramebufferTexture2D(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTexture, 0
        )
        GLES20.glFramebufferRenderbuffer(
            GLES20.GL_FRAMEBUFFER,
            GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRB
        )

        // Check FBO status
        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e("VRPlayer", "FBO incomplete: $status")
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shader compilation
    // ─────────────────────────────────────────────────────────────────────────

    private fun buildProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER, vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        return GLES20.glCreateProgram().also { prog ->
            GLES20.glAttachShader(prog, vert)
            GLES20.glAttachShader(prog, frag)
            GLES20.glLinkProgram(prog)
            GLES20.glDeleteShader(vert)
            GLES20.glDeleteShader(frag)
        }
    }

    private fun compileShader(type: Int, src: String): Int {
        return GLES20.glCreateShader(type).also { s ->
            GLES20.glShaderSource(s, src)
            GLES20.glCompileShader(s)
        }
    }
}

//
//import android.graphics.Bitmap
//import android.graphics.SurfaceTexture
//import android.opengl.GLES11Ext
//import android.opengl.GLES20
//import android.opengl.GLSurfaceView
//import android.opengl.GLUtils
//import android.opengl.Matrix
//import android.util.Log
//import android.view.Surface
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import javax.microedition.khronos.egl.EGLConfig
//import javax.microedition.khronos.opengles.GL10
//import kotlin.math.cos
//import kotlin.math.sin
//
//class SphereRenderer(
//    private val viewMode: ViewMode,
//    private val onSurfaceReady: (Surface) -> Unit
//) : GLSurfaceView.Renderer {
//
//    // ── Camera state (written from main thread, read on GL thread) ────────────
//    @Volatile var yaw: Float   = 0f
//    @Volatile var pitch: Float = 0f
//
//    // ── GL resources ──────────────────────────────────────────────────────────
//    private var sphereProgram     = 0   // OES  — used for video
//    private var imageSphereProgram = 0  // 2D   — used for still images
//    private var distortionProgram = 0
//    private var sphereVBO         = 0
//    private var sphereIBO         = 0
//    private var sphereIndexCount  = 0
//    private var quadVBO           = 0
//
//    // Video path
//    private var videoTexture      = 0
//    private var fboTexture        = 0
//    private var fbo               = 0
//    private var surfaceTexture: SurfaceTexture? = null
//
//    // Image path
//    private var imageTexture      = 0          // GL_TEXTURE_2D holding the bitmap
//    private var pendingBitmap: Bitmap? = null  // set from GL thread via setImageBitmap()
//    private var isImageMode       = false      // flipped to true once bitmap is uploaded
//
//    private val projMatrix  = FloatArray(16)
//    private val viewMatrix  = FloatArray(16)
//    private val mvpMatrix   = FloatArray(16)
//
//    private var viewportW = 1
//    private var viewportH = 1
//
//    // ── Sphere vertex shader (shared by both programs) ────────────────────────
//    private val SPHERE_VERT = """
//        uniform mat4 uMVP;
//        attribute vec3 aPos;
//        attribute vec2 aTex;
//        varying vec2 vTex;
//        void main() {
//            gl_Position = uMVP * vec4(aPos, 1.0);
//            vTex = aTex;
//        }
//    """.trimIndent()
//
//    // Fragment shader — OES external texture (video / SurfaceTexture)
//    private val SPHERE_FRAG_OES = """
//        #extension GL_OES_EGL_image_external : require
//        precision mediump float;
//        uniform samplerExternalOES uTex;
//        varying vec2 vTex;
//        void main() {
//            gl_FragColor = texture2D(uTex, vTex);
//        }
//    """.trimIndent()
//
//    // Fragment shader — standard GL_TEXTURE_2D (still image / bitmap)
//    private val SPHERE_FRAG_2D = """
//        precision mediump float;
//        uniform sampler2D uTex;
//        varying vec2 vTex;
//        void main() {
//            gl_FragColor = texture2D(uTex, vTex);
//        }
//    """.trimIndent()
//
//    // ── Barrel-distortion shaders (Cardboard mode) ────────────────────────────
//    private val DISTORT_VERT = """
//        attribute vec2 aPos;
//        attribute vec2 aTex;
//        varying vec2 vTex;
//        void main() {
//            gl_Position = vec4(aPos, 0.0, 1.0);
//            vTex = aTex;
//        }
//    """.trimIndent()
//
//    private val DISTORT_FRAG = """
//        precision mediump float;
//        uniform sampler2D uTex;
//        varying vec2 vTex;
//
//        const float k1 = 0.441;
//        const float k2 = 0.156;
//
//        vec2 distort(vec2 uv, vec2 center) {
//            vec2 p   = uv - center;
//            float r2 = dot(p, p);
//            float factor = 1.0 + k1 * r2 + k2 * r2 * r2;
//            return center + p * factor;
//        }
//
//        void main() {
//            bool isRight   = vTex.x > 0.5;
//            vec2 center    = isRight ? vec2(0.75, 0.5) : vec2(0.25, 0.5);
//            vec2 distorted = distort(vTex, center);
//
//            float lo = isRight ? 0.5 : 0.0;
//            float hi = isRight ? 1.0 : 0.5;
//            if (distorted.x < lo || distorted.x > hi ||
//                distorted.y < 0.0 || distorted.y > 1.0) {
//                gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
//            } else {
//                gl_FragColor = texture2D(uTex, distorted);
//            }
//        }
//    """.trimIndent()
//
//    private val QUAD_VERTS = floatArrayOf(
//        -1f, -1f,  0f, 0f,
//        1f, -1f,  1f, 0f,
//        -1f,  1f,  0f, 1f,
//        1f,  1f,  1f, 1f
//    )
//
//    // ─────────────────────────────────────────────────────────────────────────
//    //  Public API — called from GalleryPlayerActivity on GL thread via queueEvent
//    // ─────────────────────────────────────────────────────────────────────────
//
//    /**
//     * Call via glSurface.queueEvent { renderer.setImageBitmap(bmp) }
//     * so it runs on the GL thread where textures can be uploaded.
//     */
//    fun setImageBitmap(bitmap: Bitmap) {
//        pendingBitmap = bitmap
//        // Actual upload happens in onDrawFrame to guarantee GL context is active
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    //  GLSurfaceView.Renderer
//    // ─────────────────────────────────────────────────────────────────────────
//
//    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
//        GLES20.glClearColor(0f, 0f, 0f, 1f)
//
//        // ── Video texture (OES external) ──────────────────────────────────────
//        val texIds = IntArray(1)
//        GLES20.glGenTextures(1, texIds, 0)
//        videoTexture = texIds[0]
//        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTexture)
//        GLES20.glTexParameteri(
//            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//            GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
//            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//
//        surfaceTexture = SurfaceTexture(videoTexture).also { st ->
//            onSurfaceReady(Surface(st))
//        }
//
//        // ── Image texture (GL_TEXTURE_2D) — allocated now, filled later ───────
//        val imgTexIds = IntArray(1)
//        GLES20.glGenTextures(1, imgTexIds, 0)
//        imageTexture = imgTexIds[0]
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
//
//        // ── Compile programs ──────────────────────────────────────────────────
//        sphereProgram      = buildProgram(SPHERE_VERT, SPHERE_FRAG_OES)  // video
//        imageSphereProgram = buildProgram(SPHERE_VERT, SPHERE_FRAG_2D)   // image
//
//        if (viewMode == ViewMode.CARDBOARD) {
//            distortionProgram = buildProgram(DISTORT_VERT, DISTORT_FRAG)
//        }
//
//        // ── Geometry ──────────────────────────────────────────────────────────
//        buildSphere(stacks = 64, slices = 64)
//        buildQuadVBO()
//    }
//
//    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
//        viewportW = width
//        viewportH = height
//        Matrix.perspectiveM(projMatrix, 0, 90f, width.toFloat() / height, 0.1f, 100f)
//        if (viewMode == ViewMode.CARDBOARD) {
//            rebuildFbo(width, height)
//        }
//    }
//
//    override fun onDrawFrame(gl: GL10) {
//        // ── Upload pending bitmap exactly once on the GL thread ───────────────
//        pendingBitmap?.let { bmp ->
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
//            bmp.recycle()
//            pendingBitmap = null
//            isImageMode   = true
//            Log.d("SphereRenderer", "Image bitmap uploaded to GL texture")
//        }
//
//        // ── Update SurfaceTexture only in video mode ──────────────────────────
//        if (!isImageMode) {
//            surfaceTexture?.updateTexImage()
//        }
//
//        when (viewMode) {
//            ViewMode.DEVICE    -> {
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//                drawEye(0, 0, viewportW, viewportH)
//            }
//            ViewMode.SBS       -> {
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//                val half = viewportW / 2
//                drawEye(0,    0, half, viewportH)
//                drawEye(half, 0, half, viewportH)
//            }
//            ViewMode.CARDBOARD -> {
//                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo)
//                GLES20.glViewport(0, 0, viewportW, viewportH)
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
//                val half = viewportW / 2
//                drawEye(0,    0, half, viewportH)
//                drawEye(half, 0, half, viewportH)
//
//                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
//                GLES20.glViewport(0, 0, viewportW, viewportH)
//                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
//                drawDistorted()
//            }
//        }
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    //  Draw helpers
//    // ─────────────────────────────────────────────────────────────────────────
//
//    private fun drawEye(x: Int, y: Int, w: Int, h: Int) {
//        GLES20.glViewport(x, y, w, h)
//        GLES20.glEnable(GLES20.GL_SCISSOR_TEST)
//        GLES20.glScissor(x, y, w, h)
//        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
//        GLES20.glDepthFunc(GLES20.GL_LESS)
//
//        Matrix.setIdentityM(viewMatrix, 0)
//        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(pitch.toDouble()).toFloat(),  1f, 0f, 0f)
//        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(-yaw.toDouble()).toFloat(),   0f, 1f, 0f)
//        Matrix.perspectiveM(projMatrix, 0, 90f, w.toFloat() / h.toFloat(), 0.1f, 100f)
//        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)
//
//        // ── Pick correct program + texture type ───────────────────────────────
//        if (isImageMode) {
//            // Still image — use GL_TEXTURE_2D program
//            GLES20.glUseProgram(imageSphereProgram)
//
//            val uMVP = GLES20.glGetUniformLocation(imageSphereProgram, "uMVP")
//            GLES20.glUniformMatrix4fv(uMVP, 1, false, mvpMatrix, 0)
//
//            val uTex = GLES20.glGetUniformLocation(imageSphereProgram, "uTex")
//            GLES20.glUniform1i(uTex, 0)
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
//
//            drawSphereGeometry(imageSphereProgram)
//        } else {
//            // Video — use OES external texture program
//            GLES20.glUseProgram(sphereProgram)
//
//            val uMVP = GLES20.glGetUniformLocation(sphereProgram, "uMVP")
//            GLES20.glUniformMatrix4fv(uMVP, 1, false, mvpMatrix, 0)
//
//            val uTex = GLES20.glGetUniformLocation(sphereProgram, "uTex")
//            GLES20.glUniform1i(uTex, 0)
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, videoTexture)
//
//            drawSphereGeometry(sphereProgram)
//        }
//
//        GLES20.glDisable(GLES20.GL_SCISSOR_TEST)
//    }
//
//    /**
//     * Binds the sphere VBO/IBO and issues the draw call.
//     * Program must already be bound and MVP/texture uniforms set.
//     */
//    private fun drawSphereGeometry(program: Int) {
//        val stride = 5 * 4
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereVBO)
//
//        val aPos = GLES20.glGetAttribLocation(program, "aPos")
//        GLES20.glEnableVertexAttribArray(aPos)
//        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, stride, 0)
//
//        val aTex = GLES20.glGetAttribLocation(program, "aTex")
//        GLES20.glEnableVertexAttribArray(aTex)
//        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, stride, 3 * 4)
//
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIBO)
//        GLES20.glDrawElements(
//            GLES20.GL_TRIANGLES, sphereIndexCount,
//            GLES20.GL_UNSIGNED_SHORT, 0
//        )
//
//        GLES20.glDisableVertexAttribArray(aPos)
//        GLES20.glDisableVertexAttribArray(aTex)
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
//    }
//
//    private fun drawDistorted() {
//        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
//        GLES20.glUseProgram(distortionProgram)
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexture)
//        val uTex = GLES20.glGetUniformLocation(distortionProgram, "uTex")
//        GLES20.glUniform1i(uTex, 0)
//
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVBO)
//        val stride = 4 * 4
//        val aPos = GLES20.glGetAttribLocation(distortionProgram, "aPos")
//        GLES20.glEnableVertexAttribArray(aPos)
//        GLES20.glVertexAttribPointer(aPos, 2, GLES20.GL_FLOAT, false, stride, 0)
//        val aTex = GLES20.glGetAttribLocation(distortionProgram, "aTex")
//        GLES20.glEnableVertexAttribArray(aTex)
//        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, stride, 2 * 4)
//
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
//
//        GLES20.glDisableVertexAttribArray(aPos)
//        GLES20.glDisableVertexAttribArray(aTex)
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    //  Geometry builders
//    // ─────────────────────────────────────────────────────────────────────────
//
//    private fun buildSphere(stacks: Int, slices: Int) {
//        val vertexData = mutableListOf<Float>()
//        val indexData  = mutableListOf<Short>()
//        val radius     = 1f
//        val PI         = Math.PI.toFloat()
//
//        for (i in 0..stacks) {
//            val phi  = PI * i / stacks
//            val sinP = sin(phi)
//            val cosP = cos(phi)
//            for (j in 0..slices) {
//                val theta = 2f * PI * j / slices
//                val x = sinP * cos(theta)
//                val y = cosP
//                val z = sinP * sin(theta)
//                val u = 1f - j.toFloat() / slices
//                val v = i.toFloat() / stacks
//                vertexData += listOf(x * radius, y * radius, z * radius, u, v)
//            }
//        }
//
//        for (i in 0 until stacks) {
//            for (j in 0 until slices) {
//                val a = (i * (slices + 1) + j).toShort()
//                val b = ((i + 1) * (slices + 1) + j).toShort()
//                val c = ((i + 1) * (slices + 1) + j + 1).toShort()
//                val d = (i * (slices + 1) + j + 1).toShort()
//                indexData += listOf(a, b, d, b, c, d)
//            }
//        }
//
//        sphereIndexCount = indexData.size
//
//        val vboIds = IntArray(2)
//        GLES20.glGenBuffers(2, vboIds, 0)
//        sphereVBO = vboIds[0]
//        sphereIBO = vboIds[1]
//
//        val vb = ByteBuffer.allocateDirect(vertexData.size * 4)
//            .order(ByteOrder.nativeOrder()).asFloatBuffer()
//        vb.put(vertexData.toFloatArray()).position(0)
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereVBO)
//        GLES20.glBufferData(
//            GLES20.GL_ARRAY_BUFFER, vb.capacity() * 4, vb, GLES20.GL_STATIC_DRAW)
//
//        val ib = ByteBuffer.allocateDirect(indexData.size * 2)
//            .order(ByteOrder.nativeOrder()).asShortBuffer()
//        ib.put(indexData.toShortArray()).position(0)
//        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIBO)
//        GLES20.glBufferData(
//            GLES20.GL_ELEMENT_ARRAY_BUFFER, ib.capacity() * 2, ib, GLES20.GL_STATIC_DRAW)
//    }
//
//    private fun buildQuadVBO() {
//        val ids = IntArray(1)
//        GLES20.glGenBuffers(1, ids, 0)
//        quadVBO = ids[0]
//        val buf = ByteBuffer.allocateDirect(QUAD_VERTS.size * 4)
//            .order(ByteOrder.nativeOrder()).asFloatBuffer()
//        buf.put(QUAD_VERTS).position(0)
//        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, quadVBO)
//        GLES20.glBufferData(
//            GLES20.GL_ARRAY_BUFFER, buf.capacity() * 4, buf, GLES20.GL_STATIC_DRAW)
//    }
//
//    private fun rebuildFbo(w: Int, h: Int) {
//        if (fbo != 0)        GLES20.glDeleteFramebuffers(1, intArrayOf(fbo), 0)
//        if (fboTexture != 0) GLES20.glDeleteTextures(1, intArrayOf(fboTexture), 0)
//
//        val texIds = IntArray(1)
//        GLES20.glGenTextures(1, texIds, 0)
//        fboTexture = texIds[0]
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fboTexture)
//        GLES20.glTexImage2D(
//            GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
//            w, h, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
//        GLES20.glTexParameteri(
//            GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
//
//        val depthIds = IntArray(1)
//        GLES20.glGenRenderbuffers(1, depthIds, 0)
//        val depthRB = depthIds[0]
//        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRB)
//        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, w, h)
//
//        val fboIds = IntArray(1)
//        GLES20.glGenFramebuffers(1, fboIds, 0)
//        fbo = fboIds[0]
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo)
//        GLES20.glFramebufferTexture2D(
//            GLES20.GL_FRAMEBUFFER,
//            GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, fboTexture, 0)
//        GLES20.glFramebufferRenderbuffer(
//            GLES20.GL_FRAMEBUFFER,
//            GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthRB)
//
//        val status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER)
//        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
//            Log.e("SphereRenderer", "FBO incomplete: $status")
//        }
//        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
//    }
//
//    // ─────────────────────────────────────────────────────────────────────────
//    //  Shader compilation
//    // ─────────────────────────────────────────────────────────────────────────
//
//    private fun buildProgram(vertSrc: String, fragSrc: String): Int {
//        val vert = compileShader(GLES20.GL_VERTEX_SHADER,   vertSrc)
//        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
//        return GLES20.glCreateProgram().also { prog ->
//            GLES20.glAttachShader(prog, vert)
//            GLES20.glAttachShader(prog, frag)
//            GLES20.glLinkProgram(prog)
//            GLES20.glDeleteShader(vert)
//            GLES20.glDeleteShader(frag)
//        }
//    }
//
//    private fun compileShader(type: Int, src: String): Int {
//        return GLES20.glCreateShader(type).also { s ->
//            GLES20.glShaderSource(s, src)
//            GLES20.glCompileShader(s)
//        }
//    }
//}