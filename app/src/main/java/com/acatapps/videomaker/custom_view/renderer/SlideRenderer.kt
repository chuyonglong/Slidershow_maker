package com.acatapps.videomaker.custom_view.renderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.acatapps.videomaker.slide_show.DrawerSlideShow
import com.acatapps.videomaker.models.FrameData
import com.acatapps.videomaker.slide_show_theme.SlideThemeDrawer
import com.acatapps.videomaker.models.ThemeData
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class SlideRenderer(val themeData: ThemeData) : GLSurfaceView.Renderer {

    private var mDrawerSlideShow:DrawerSlideShow? = null
    private var mSlideThemeDrawer:SlideThemeDrawer? = null

    init {
        mDrawerSlideShow = DrawerSlideShow()
        mSlideThemeDrawer = SlideThemeDrawer(themeData)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        mSlideThemeDrawer?.prepare()
        mDrawerSlideShow?.prepare()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {

        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        mDrawerSlideShow?.drawFrame()
        mSlideThemeDrawer?.drawFrame()
    }

    fun changeTheme(themeData: ThemeData) {
        mSlideThemeDrawer?.changeTheme(themeData)
        mDrawerSlideShow?.setUpdateTexture(true)
    }

    fun changeFrameData(frameData: FrameData) {
        mDrawerSlideShow?.changeFrameData(frameData)
    }

    fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition, fragmentShaderHandle: Int) {
        mDrawerSlideShow?.changeTransition(gsTransition, fragmentShaderHandle)
    }
}