package com.daasuu.gpuv.composer

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class StickerDrawer {
    private var mVertices = floatArrayOf(
        -1f, 1f, 0f,
        -1f, -1f, 0.0f,
        1f, 1f, 0.0f,
        -1f, -1f, 0.0f,
        1f, -1f, 0.0f,
        1f, 1f, 0.0f
    )

    private var mTextureCoordinateData = floatArrayOf(
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    )

    private val mVertexBuffer: FloatBuffer
    private val mTextureCoordinateBuffer: FloatBuffer

    private var mProgramHandle = 0

    private var mTex_1_Handle = 0
    private var mTextureUniformHandle = 0
    private var mPositionHandle = 0
    private var mTextureCoordinateHandle = 0

    init {

        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        mVertexBuffer.put(mVertices).position(0)

        mTextureCoordinateBuffer = ByteBuffer.allocateDirect(mTextureCoordinateData.size * 4).order(
            ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureCoordinateBuffer.put(mTextureCoordinateData).position(0)


    }

    fun prepare(bitmap: Bitmap?) {

        val vertexShader = getVertexShader()
        val fragmentShader = getFragmentShader()

        val vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        mProgramHandle = createAndLinkProgram(
            vertexShaderHandle, fragmentShaderHandle,
            arrayOf("a_Position", "a_TexCoordinate")
        )
        mTex_1_Handle = loadTexture(bitmap)
    }

    fun drawFrame() {

        GLES20.glUseProgram(mProgramHandle)

        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate")
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture")


        mVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)


        mTextureCoordinateBuffer.position(0)
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordinateBuffer)
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTex_1_Handle)
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.size/3)
        GLES20.glFinish()
    }
    fun drawFrame(viewPortW:Int, viewPortH:Int, viewPortX:Int, viewPortY:Int) {

        GLES20.glUseProgram(mProgramHandle)
        GLES20.glViewport(viewPortX, viewPortY, viewPortW, viewPortH)
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate")
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture")


        mVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)


        mTextureCoordinateBuffer.position(0)
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, mTextureCoordinateBuffer)
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTex_1_Handle)
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.size/3)
        GLES20.glFinish()
    }
    protected fun getVertexShader(): String? {

        return "attribute vec4 a_Position;\n" +
                "attribute vec2 a_TexCoordinate;\n" +
                "varying vec2 v_TexCoordinate;\n" +
                "void main()                                                 \t\n" +
                "{\n" +
                "\tv_TexCoordinate = a_TexCoordinate;\n" +
                "\tgl_Position = a_Position;\n" +
                "}    "
    }

    protected fun getFragmentShader(): String? {
        return "precision mediump float;\n" +
                "uniform sampler2D u_Texture;\n" +
                "varying vec2 v_TexCoordinate;\n" +
                "\n" +
                "void main()                    \t\t\n" +
                "{\n" +
                "    gl_FragColor = (texture2D(u_Texture, v_TexCoordinate));\n" +
                "}   "
    }

    fun compileShader(shaderType: Int, shaderSource: String?): Int {
        var shaderHandle = GLES20.glCreateShader(shaderType)
        if (shaderHandle != 0) {
            GLES20.glShaderSource(shaderHandle, shaderSource)

            GLES20.glCompileShader(shaderHandle)

            val compileStatus = IntArray(1)
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(shaderHandle)
                shaderHandle = 0
            }
        }
        if (shaderHandle == 0) {
            throw RuntimeException("Error creating shader.")
        }
        return shaderHandle
    }

    fun createAndLinkProgram(
        vertexShaderHandle: Int,
        fragmentShaderHandle: Int,
        attributes: Array<String?>?
    ): Int {
        var programHandle = GLES20.glCreateProgram()
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle)

            GLES20.glAttachShader(programHandle, fragmentShaderHandle)

            if (attributes != null) {
                val size = attributes.size
                for (i in 0 until size) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i])
                }
            }
            GLES20.glLinkProgram(programHandle)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle)
                programHandle = 0
            }
        }
        if (programHandle == 0) {
            throw java.lang.RuntimeException("Error creating program.")
        }
        return programHandle
    }

    fun loadTexture(bitmap: Bitmap?, isRecycle: Boolean = false): Int {
        if (bitmap == null)
            return -1
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)
        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_NEAREST
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_NEAREST
        )
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        if (isRecycle)
            bitmap.recycle()
        return textureHandle[0]
    }
}