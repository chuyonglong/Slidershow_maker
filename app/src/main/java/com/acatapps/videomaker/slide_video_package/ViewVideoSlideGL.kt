package com.acatapps.videomaker.slide_video_package

import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Size
import com.acatapps.videomaker.models.ThemeData

class ViewVideoSlideGL (context: Context, attributes: AttributeSet?) : GLSurfaceView(context, attributes) {

    private lateinit var mVideoSlideRenderer: VideoSlideRenderer


    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
    }

    fun performSetRenderer(videoSlideRenderer: VideoSlideRenderer) {
        mVideoSlideRenderer = videoSlideRenderer
        setRenderer(mVideoSlideRenderer)
    }


    fun changeTheme(themeData: ThemeData) {
        queueEvent(Runnable {
            mVideoSlideRenderer.changeTheme(themeData)
        })
    }

    fun changeVideo(videoDataForSlide: VideoDataForSlide) {
        queueEvent(Runnable {
            val size = videoDataForSlide.size
            val viewPortX:Int
            val viewPortY:Int
            val viewPortW:Int
            val viewPortH:Int
            if(size.width > size.height) {
                viewPortW = width
                viewPortH = width*size.height/size.width
                viewPortY = (height-viewPortH)/2
                viewPortX = 0
            } else {
                viewPortH = height
                viewPortW = height*size.width/size.height
                viewPortY = 0
                viewPortX = (width-viewPortW)/2
            }
            mVideoSlideRenderer.changeVideo(videoDataForSlide.path, Size(viewPortW, viewPortH), Point(viewPortX, viewPortY))
        })

    }




    fun seekTo(timeMilSec:Int) {

    }

}