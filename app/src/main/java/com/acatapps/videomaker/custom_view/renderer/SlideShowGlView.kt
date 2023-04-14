package com.acatapps.videomaker.custom_view.renderer

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.FrameData
import com.acatapps.videomaker.models.ThemeData
import com.acatapps.videomaker.utils.Utils


class SlideShowGlView : GLSurfaceView {

    private var mSlideRenderer: SlideRenderer? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        init()
    }

    private fun init() {
        setEGLContextClientVersion(2)
        preserveEGLContextOnPause = true
    }

    fun setSlideRenderer(render: SlideRenderer) {
        mSlideRenderer = render
        setRenderer(render)
    }

    fun drawSlide(frameData: FrameData) {
        mSlideRenderer?.changeFrameData(frameData)
    }

    fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition) {
        queueEvent(Runnable {
            val handle = Utils.compileShader(
                GLES20.GL_FRAGMENT_SHADER,
                getFragmentShader(gsTransition.transitionCodeId)
            )
            mSlideRenderer?.changeTransition(gsTransition, handle)
        })
    }

    fun changeTheme(themeData: ThemeData) {
        queueEvent(Runnable {
            mSlideRenderer?.changeTheme(themeData)

        })
    }

    private fun getFragmentShader(transitionCodeId: Int): String {
        val transitionCode =
            Utils.readTextFileFromRawResource(MainApp.getContext(), transitionCodeId)
        return "precision mediump float;\n" +
                "varying vec2 _uv;\n" +
                "uniform sampler2D from, to;\n" +
                "uniform float progress, ratio, _fromR, _toR, _zoomProgress;\n" +
                "\n" +
                "vec4 getFromColor(vec2 uv){\n" +
                "    return texture2D(from, vec2(1.0, -1.0)*uv*_zoomProgress);\n" +
                "}\n" +
                "vec4 getToColor(vec2 uv){\n" +
                "    return texture2D(to, vec2(1.0, -1.0)*uv*_zoomProgress);\n" +
                "}" +
                transitionCode +
                "void main(){gl_FragColor=transition(_uv);}"
    }


}