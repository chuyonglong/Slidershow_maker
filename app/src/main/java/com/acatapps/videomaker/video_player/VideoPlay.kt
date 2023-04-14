package com.acatapps.videomaker.video_player

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.Surface
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class VideoPlay(var videoPath: String, var autoPlay: Boolean = true) :
    SurfaceTexture.OnFrameAvailableListener {


    private val FLOAT_SIZE_BYTES = 4
    private val TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES
    private val TEXTURE_VERTICES_DATA_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES
    private val TRIANGLE_VERTICES_DATA_POS_OFFSET = 0
    private val TRIANGLE_VERTICES_DATA_UV_OFFSET = 0

    private val mTriangleVerticesData = floatArrayOf(
        -1.0f, -1.0f, 0f, 1.0f,
        -1.0f, 0f, -1.0f, 1.0f, 0f, 1.0f, 1.0f, 0f
    )

    private val mTextureVerticesData = floatArrayOf(
        0f, 0.0f, 1.0f, 0f,
        0.0f, 1f, 1.0f, 1.0f
    )
    private var mTriangleVertices: FloatBuffer
    private var mTextureVertices: FloatBuffer
    private val GL_TEXTURE_EXTERNAL_OES = 0x8D65

    private val mMVPMatrix = FloatArray(16)
    private val mSTMatrix = FloatArray(16)

    private var mProgram = 0
    private var mTextureID = 0

    private var maPositionHandle = 0
    private var maTextureHandle = 0

    private var muMVPMatrixHandle = 0
    private var muSTMatrixHandle = 0

    private lateinit var mSurface: SurfaceTexture

    private var updateSurface = false

    private var mPlayer: MediaPlayer? = null
    private val mVertexShader = ("uniform mat4 uMVPMatrix;\n"
            + "uniform mat4 uSTMatrix;\n" + "attribute vec4 aPosition;\n"
            + "attribute vec4 aTextureCoord;\n"
            + "varying vec2 vTextureCoord;\n" + "void main() {\n"
            + "  gl_Position = uMVPMatrix * aPosition;\n"
            + "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" + "}\n")

    private val mFragmentShader = ("#extension GL_OES_EGL_image_external : require\n"
            + "precision mediump float;\n"
            + "varying vec2 vTextureCoord;\n"
            + "uniform samplerExternalOES sTexture;\n"
            + "void main() {\n"
            + "vec4 p = texture2D(sTexture, vTextureCoord); "
            + "gl_FragColor = p;\n"
            + "}\n")

    init {

        mTriangleVertices = ByteBuffer
            .allocateDirect(
                mTriangleVerticesData.size * FLOAT_SIZE_BYTES
            )
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTriangleVertices.put(mTriangleVerticesData).position(0)

        mTextureVertices = ByteBuffer
            .allocateDirect(
                mTextureVerticesData.size * FLOAT_SIZE_BYTES
            )
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        mTextureVertices.put(mTextureVerticesData).position(0)

        Matrix.setIdentityM(mSTMatrix, 0)

    }

    fun prepare() {
        val vertexThemeShaderHandle =
            Utils.compileShader(GLES20.GL_VERTEX_SHADER, mVertexShader)
        val fragmentThemeShaderHandle =
            Utils.compileShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShader)
        mProgram = Utils.createAndLinkProgram(
            vertexThemeShaderHandle, fragmentThemeShaderHandle,
            arrayOf("uSTMatrix", "uMVPMatrix", "aTextureCoord")
        )

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        mTextureID = textures[0]

        mSurface = SurfaceTexture(mTextureID)
        mSurface.setOnFrameAvailableListener(this)

        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID)


        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        synchronized(this) { updateSurface = false }

        doPlayVideo()
    }

    private var mVolume = 1f
    fun prepare(volume: Float) {
        mVolume = volume
        val vertexThemeShaderHandle =
            Utils.compileShader(GLES20.GL_VERTEX_SHADER, mVertexShader)
        val fragmentThemeShaderHandle =
            Utils.compileShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShader)
        mProgram = Utils.createAndLinkProgram(
            vertexThemeShaderHandle, fragmentThemeShaderHandle,
            arrayOf("uSTMatrix", "uMVPMatrix", "aTextureCoord")
        )

        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord")
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix")

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        mTextureID = textures[0]

        mSurface = SurfaceTexture(mTextureID)
        mSurface.setOnFrameAvailableListener(this)

        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID)


        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )

        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )

        synchronized(this) { updateSurface = false }

        doPlayVideo()
    }

    fun drawFrame() {
        synchronized(this) {
            if (updateSurface) {
                mSurface.updateTexImage()
                mSurface.getTransformMatrix(mSTMatrix)
                updateSurface = false
            }
        }

        GLES20.glUseProgram(mProgram)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID)

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET)
        GLES20.glVertexAttribPointer(
            maPositionHandle,
            3,
            GLES20.GL_FLOAT,
            false,
            TRIANGLE_VERTICES_DATA_STRIDE_BYTES,
            mTriangleVertices
        )
        GLES20.glEnableVertexAttribArray(maPositionHandle)

        mTextureVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET)
        GLES20.glVertexAttribPointer(
            maTextureHandle,
            2,
            GLES20.GL_FLOAT,
            false,
            TEXTURE_VERTICES_DATA_STRIDE_BYTES,
            mTextureVertices
        )
        GLES20.glEnableVertexAttribArray(maTextureHandle)

        Matrix.setIdentityM(mMVPMatrix, 0)

        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glFinish()


    }

    fun seekTo(timeMilSec: Int) {
        mPlayer?.seekTo(timeMilSec)
    }

    private fun doPlayVideo() {
        val surface = Surface(mSurface)
        mPlayer = MediaPlayer()
        mPlayer?.setDataSource(videoPath)
        mPlayer?.setSurface(surface)
        mPlayer?.isLooping = true
        surface.release()
        try {
            mPlayer?.setOnPreparedListener {
                mPlayer?.seekTo(0)
                mPlayer?.isLooping = mVolume > 0f
                mPlayer?.setVolume(mVolume, mVolume)
                if (autoPlay)
                    playVideo()
                else
                    pauseVideo()

            }
            mPlayer?.prepare()
        } catch (e: Exception) {

        }
    }

    fun playVideo() {

        mPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }

    fun pauseVideo() {
        mPlayer?.let {
            if (it.isPlaying) {
                mPlayer?.pause()
            }
        }
    }

    fun getCurrentPosition(): Int? = mPlayer?.currentPosition

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        synchronized(this) {
            updateSurface = true
        }
    }

    fun onDestroy() {
        mPlayer?.release()
    }

    fun onPause() {
        Logger.e("player = $mPlayer")
        pauseVideo()
    }

}