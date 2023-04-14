package com.acatapps.videomaker.slide_show.slide_show_gl

import android.graphics.Bitmap
import android.opengl.GLES20
import com.acatapps.videomaker.R
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.utils.Utils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class DrawerSticker() {


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

        mVertexBuffer = ByteBuffer.allocateDirect(mVertices.size * 4).order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mVertexBuffer.put(mVertices).position(0)

        mTextureCoordinateBuffer = ByteBuffer.allocateDirect(mTextureCoordinateData.size * 4).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()
        mTextureCoordinateBuffer.put(mTextureCoordinateData).position(0)


    }

    fun prepare(bitmap: Bitmap) {

        val vertexShader = getVertexShader()
        val fragmentShader = getFragmentShader()

        val vertexShaderHandle = Utils.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader)
        val fragmentShaderHandle = Utils.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader)
        mProgramHandle = Utils.createAndLinkProgram(
            vertexShaderHandle, fragmentShaderHandle,
            arrayOf("a_Position", "a_TexCoordinate")
        )
        mTex_1_Handle = Utils.loadTexture(bitmap)
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
        GLES20.glVertexAttribPointer(
            mTextureCoordinateHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureCoordinateBuffer
        )
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTex_1_Handle)
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.size / 3)
        GLES20.glFinish()
    }

    fun drawFrame(viewPortW: Int, viewPortH: Int, viewPortX: Int, viewPortY: Int) {

        GLES20.glUseProgram(mProgramHandle)
        GLES20.glViewport(viewPortX, viewPortY, viewPortW, viewPortH)
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position")
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate")
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture")


        mVertexBuffer.position(0)
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glEnableVertexAttribArray(mPositionHandle)


        mTextureCoordinateBuffer.position(0)
        GLES20.glVertexAttribPointer(
            mTextureCoordinateHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureCoordinateBuffer
        )
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle)


        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTex_1_Handle)
        GLES20.glUniform1i(mTextureUniformHandle, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertices.size / 3)
        GLES20.glFinish()
    }

    protected fun getVertexShader(): String {
        return Utils.readTextFileFromRawResource(
            MainApp.getContext(),
            R.raw.multi_text_vertex_shader
        )
    }

    protected fun getFragmentShader(): String {
        return Utils.readTextFileFromRawResource(
            MainApp.getContext(),
            R.raw.multi_text_fragment_shader
        )
    }
}