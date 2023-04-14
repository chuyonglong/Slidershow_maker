package com.acatapps.videomaker.custom_view.custom_imageshow

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.acatapps.videomaker.models.ThemeData

class ImageSlideGLView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs) {

    private lateinit var mImageSlideRenderer: ImageSlideRenderer

    init {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
    }

    fun doSetRenderer(imageSlideRenderer: ImageSlideRenderer) {
        mImageSlideRenderer = imageSlideRenderer

        setRenderer(imageSlideRenderer)
    }


    fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition) {

        queueEvent(Runnable {
            mImageSlideRenderer.changeTransition(gsTransition)
        })
    }




    fun changeTheme(themeData: ThemeData) {
        queueEvent(Runnable {
            mImageSlideRenderer.changeTheme(themeData)
        })
    }

}