package com.acatapps.videomaker.slide_show.slide_show_gl

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.acatapps.videomaker.slide_show_theme.SlideThemeDrawer
import com.acatapps.videomaker.models.ThemeData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class RendererSlideShow :GLSurfaceView.Renderer{

    private var mSlideThemeDrawer: SlideThemeDrawer? = null

    init {
        mSlideThemeDrawer = SlideThemeDrawer(ThemeData())
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mSlideThemeDrawer?.prepare()
        performPrepare()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        performDraw()
        drawTheme()
    }

    private fun drawTheme() {
        mSlideThemeDrawer?.drawFrame()
    }

    fun changeTheme(themeData: ThemeData) {
        mSlideThemeDrawer?.changeTheme(themeData)
        onChangeTheme()
    }

    abstract fun performPrepare()
    abstract fun performDraw()
    abstract fun onChangeTheme()

    abstract fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition)

    abstract fun initData(filePathList:ArrayList<String>)
    abstract fun drawSlideByTime(timeMilSec:Int)

    abstract fun getDuration():Int
    abstract fun getDelayTimeSec():Int
    abstract fun setDelayTimeSec(delayTimeSec:Int):Boolean
    abstract fun repeat()
}