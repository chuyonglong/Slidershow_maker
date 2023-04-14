package com.acatapps.videomaker.slide_video_package

import android.graphics.Point
import android.media.MediaMetadataRetriever
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Size
import com.acatapps.videomaker.custom_view.renderer.VideoDrawer
import com.acatapps.videomaker.slide_show_theme.SlideThemeDrawer
import com.acatapps.videomaker.models.ThemeData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VideoSlideRenderer(val mViewVideoSlideGL: ViewVideoSlideGL)  : GLSurfaceView.Renderer {



    private var mThemeDrawer: SlideThemeDrawer? = null
    private var mVideoDrawer: VideoDrawer? = null

    private var mThemeData = ThemeData()
    val themeData get() = mThemeData

    private val mVideoDataList = ArrayList<VideoDataForSlide>()

    private var mTotalDuration = 0L

    init {
        mThemeDrawer = SlideThemeDrawer(mThemeData)
        mVideoDrawer = VideoDrawer()
    }

    fun initData(videoList: ArrayList<String>) {
        mVideoDataList.clear()
        for(path in videoList) {
            val videoDataForSlide = getDataForSlide(path)
            mVideoDataList.add(videoDataForSlide)
            mTotalDuration+=(videoDataForSlide.endOffset-videoDataForSlide.startOffset)
        }
    }

    fun playByProgress(percent:Float) {
        val tartTime = percent*mTotalDuration

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mVideoDrawer?.prepare()
        mThemeDrawer?.prepare()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        mVideoDrawer?.drawFrame()
        mThemeDrawer?.drawFrame()
    }

    fun changeTheme(themeData: ThemeData) {
        mThemeData = themeData
        mThemeDrawer?.changeTheme(themeData)
    }

    fun changeVideo(videoPath:String, size: Size, point:Point) {
        mVideoDrawer?.changeVideo(videoPath, size, point)
    }

    private fun getDataForSlide(videoPath: String):VideoDataForSlide {
        val media = MediaMetadataRetriever()
        media.setDataSource(videoPath)
        val rotation = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt()
        val videoW:Int
        val videoH:Int
        if (rotation == 90) {
            videoW = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?:"-1").toInt()
            videoH = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "-1").toInt()
        } else {
            videoW = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "-1").toInt()
            videoH = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "-1").toInt()
        }
        val size =Size(videoW, videoH)
        val length = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) ?: "-1").toLong()
        return VideoDataForSlide(videoPath, 0, length, size)
    }

}