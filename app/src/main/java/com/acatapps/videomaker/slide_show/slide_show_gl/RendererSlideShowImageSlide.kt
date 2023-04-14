package com.acatapps.videomaker.slide_show.slide_show_gl

import android.opengl.GLES20
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.SlideShow
import com.acatapps.videomaker.slide_show.DrawerSlideShow
import com.acatapps.videomaker.utils.Utils

class RendererSlideShowImageSlide : RendererSlideShow() {

    private var mDrawerSlideShow: DrawerSlideShow? = null
    private lateinit var mSlideShow: SlideShow

    init {
        mDrawerSlideShow = DrawerSlideShow()
    }

    override fun initData(filePathList: ArrayList<String>) {
        mSlideShow = SlideShow(filePathList)
    }

    override fun performPrepare() {
        mDrawerSlideShow?.prepare()
    }

    override fun performDraw() {
        mDrawerSlideShow?.drawFrame()
    }

    override fun onChangeTheme() {
        mDrawerSlideShow?.setUpdateTexture(true)
    }

    override fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition) {
        val handle = Utils.compileShader(
            GLES20.GL_FRAGMENT_SHADER,
            getFragmentShader(gsTransition.transitionCodeId)
        )
        mDrawerSlideShow?.changeTransition(gsTransition, handle)
    }

    override fun drawSlideByTime(timeMilSec: Int) {
        val frameData = mSlideShow.getFrameByVideoTime(timeMilSec)
        mDrawerSlideShow?.changeFrameData(frameData)
    }

    override fun getDuration(): Int {
        return mSlideShow.getTotalDuration()
    }

    override fun getDelayTimeSec(): Int {
        return mSlideShow.delayTimeSec
    }

    override fun setDelayTimeSec(timeSec: Int): Boolean {
        if (mSlideShow.delayTimeSec == timeSec) return false
        mSlideShow.updateTime(timeSec)
        return true
    }

    override fun repeat() {
        mSlideShow.repeat()
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