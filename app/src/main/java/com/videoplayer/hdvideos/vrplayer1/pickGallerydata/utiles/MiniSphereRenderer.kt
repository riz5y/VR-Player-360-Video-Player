package com.videoplayer.hdvideos.vrplayer1.pickGallerydata.utiles

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin

class MiniSphereRenderer : GLSurfaceView.Renderer {

    @Volatile var yaw: Float        = 0f
    @Volatile var pitch: Float      = 0f
    @Volatile var fovDegrees: Float = 90f

    private var program       = 0
    private var sphereVBO     = 0
    private var sphereIBO     = 0
    private var sphereIndexCount = 0
    private var imageTexture  = 0
    private var pendingBitmap: Bitmap? = null
    private var hasBitmap     = false

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix  = FloatArray(16)

    private var viewportW = 1
    private var viewportH = 1

    private val VERT = """
        uniform mat4 uMVP;
        attribute vec3 aPos;
        attribute vec2 aTex;
        varying vec2 vTex;
        void main() {
            gl_Position = uMVP * vec4(aPos, 1.0);
            vTex = aTex;
        }
    """.trimIndent()

    private val FRAG = """
        precision mediump float;
        uniform sampler2D uTex;
        varying vec2 vTex;
        void main() {
            gl_FragColor = texture2D(uTex, vTex);
        }
    """.trimIndent()

    fun setImageBitmap(bitmap: Bitmap) {
        pendingBitmap = bitmap
    }

    fun resetView() {
        yaw   = 0f
        pitch = 0f
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)

        val texIds = IntArray(1)
        GLES20.glGenTextures(1, texIds, 0)
        imageTexture = texIds[0]
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        program = buildProgram(VERT, FRAG)
        buildSphere(48, 48)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        viewportW = width
        viewportH = height
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        pendingBitmap?.let { bmp ->
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0)
            bmp.recycle()
            pendingBitmap = null
            hasBitmap     = true
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        if (!hasBitmap) return

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(pitch.toDouble()).toFloat(), 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, Math.toDegrees(-yaw.toDouble()).toFloat(),  0f, 1f, 0f)
        Matrix.perspectiveM(projMatrix, 0, fovDegrees, viewportW.toFloat() / viewportH, 0.1f, 100f)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, viewMatrix, 0)

        GLES20.glUseProgram(program)

        val uMVP = GLES20.glGetUniformLocation(program, "uMVP")
        GLES20.glUniformMatrix4fv(uMVP, 1, false, mvpMatrix, 0)

        val uTex = GLES20.glGetUniformLocation(program, "uTex")
        GLES20.glUniform1i(uTex, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, imageTexture)

        val stride = 5 * 4
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, sphereVBO)

        val aPos = GLES20.glGetAttribLocation(program, "aPos")
        GLES20.glEnableVertexAttribArray(aPos)
        GLES20.glVertexAttribPointer(aPos, 3, GLES20.GL_FLOAT, false, stride, 0)

        val aTex = GLES20.glGetAttribLocation(program, "aTex")
        GLES20.glEnableVertexAttribArray(aTex)
        GLES20.glVertexAttribPointer(aTex, 2, GLES20.GL_FLOAT, false, stride, 3 * 4)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, sphereIBO)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereIndexCount, GLES20.GL_UNSIGNED_SHORT, 0)

        GLES20.glDisableVertexAttribArray(aPos)
        GLES20.glDisableVertexAttribArray(aTex)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    private fun buildSphere(stacks: Int, slices: Int) {
        val vertexData = mutableListOf<Float>()
        val indexData  = mutableListOf<Short>()
        val PI = Math.PI.toFloat()

        for (i in 0..stacks) {
            val phi  = PI * i / stacks
            val sinP = sin(phi)
            val cosP = cos(phi)
            for (j in 0..slices) {
                val theta = 2f * PI * j / slices
                val x = sinP * cos(theta)
                val y = cosP
                val z = sinP * sin(theta)
                val u = 1f - j.toFloat() / slices
                val v = i.toFloat() / stacks
                vertexData += listOf(x, y, z, u, v)
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
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, ib.capacity() * 2, ib, GLES20.GL_STATIC_DRAW)
    }

    private fun buildProgram(vertSrc: String, fragSrc: String): Int {
        val vert = compileShader(GLES20.GL_VERTEX_SHADER,   vertSrc)
        val frag = compileShader(GLES20.GL_FRAGMENT_SHADER, fragSrc)
        return GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vert)
            GLES20.glAttachShader(it, frag)
            GLES20.glLinkProgram(it)
            GLES20.glDeleteShader(vert)
            GLES20.glDeleteShader(frag)
        }
    }

    private fun compileShader(type: Int, src: String): Int =
        GLES20.glCreateShader(type).also {
            GLES20.glShaderSource(it, src)
            GLES20.glCompileShader(it)
        }
}