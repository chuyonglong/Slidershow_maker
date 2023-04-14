package com.acatapps.videomaker.custom_view.custom_imageshow

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.models.ThemeData

import com.acatapps.videomaker.utils.Utils
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ImageSlideRenderer(gsTransition: com.acatapps.videomaker.transition.GSTransition):GLSurfaceView.Renderer {

    private var mImageSlideDrawer: ImageSlideDrawer? = null
    private var mImageSlideThemeDrawer: ImageSlideThemeDrawer? = null
    private var mGSTransition =gsTransition
    private var mThemeData = ThemeData()
    init {
        mImageSlideDrawer = ImageSlideDrawer()
        mImageSlideThemeDrawer = ImageSlideThemeDrawer(mThemeData)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        mImageSlideDrawer?.prepare(mGSTransition)
        if(mThemeData.themeVideoFilePath != "none") {
            mImageSlideThemeDrawer?.prepare()
        }

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {

    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClearColor(0f,0f,0f,1f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        mImageSlideDrawer?.drawFrame()
        if(mThemeData.themeVideoFilePath != "none")
        mImageSlideThemeDrawer?.drawFrame()
    }

    fun changeFrameData(frameData: ImageSlideFrame) {
        mImageSlideDrawer?.changeFrameData(frameData)
    }


    fun resetData() {
        mImageSlideDrawer?.reset()
    }



    fun changeTransition(gsTransition: com.acatapps.videomaker.transition.GSTransition) {
        mGSTransition = gsTransition

        val handle =  Utils.compileShader(GLES20.GL_FRAGMENT_SHADER, getFragmentShader(gsTransition.transitionCodeId))
        mImageSlideDrawer?.changeTransition(gsTransition, handle)
    }

    fun changeTheme(themeData: ThemeData) {
        if(mThemeData.themeVideoFilePath != themeData.themeVideoFilePath) {
            mThemeData = themeData
            mImageSlideThemeDrawer?.changeTheme(themeData)
            mImageSlideDrawer?.setUpdateTexture(true)
        }
    }

    fun onPause() {
        mImageSlideThemeDrawer?.pauseTheme()
    }

    fun onPlay() {
        mImageSlideThemeDrawer?.playTheme()
    }

    fun onDestroy() {
        mImageSlideThemeDrawer?.destroyTheme()
    }

    fun setUpdateTexture(needUpadate:Boolean) {
        mImageSlideDrawer?.setUpdateTexture(needUpadate)
    }

    fun seekTheme(videoTimeMs:Int) {
        mImageSlideThemeDrawer?.doSeekTo(videoTimeMs)
    }

    private fun getFragmentShader(transitionCodeId: Int): String {
        val transitionCode = Utils.readTextFileFromRawResource(MainApp.getContext(), transitionCodeId)
        return "precision highp float;" +
                "varying highp vec2 _uv;\n" +
                "uniform sampler2D from, to;\n" +
                "uniform sampler2D fromLookupTexture, toLookupTexture;\n" +
                "uniform float progress, ratio, _fromR, _toR;\n" +
                "uniform highp float _zoomProgress, _zoomProgress1;\n" +
                "\n" +
                "vec4 lookup(vec4 textureColor, sampler2D lookupBitmap, vec2 uv) {\n" +
                "    //highp vec4 textureColor = texture2D(inputTexture, uv);\n" +
                "\n" +
                "    highp float blueColor = textureColor.b * 63.0;\n" +
                "\n" +
                "    highp vec2 quad1;\n" +
                "    quad1.y = floor(floor(blueColor) / 8.0);\n" +
                "    quad1.x = floor(blueColor) - (quad1.y * 8.0);\n" +
                "\n" +
                "    highp vec2 quad2;\n" +
                "    quad2.y = floor(ceil(blueColor) / 8.0);\n" +
                "    quad2.x = ceil(blueColor) - (quad2.y * 8.0);\n" +
                "\n" +
                "    highp vec2 texPos1;\n" +
                "    texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "    texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "\n" +
                "    highp vec2 texPos2;\n" +
                "    texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);\n" +
                "    texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);\n" +
                "\n" +
                "    lowp vec4 newColor1 = texture2D(lookupBitmap, texPos1);\n" +
                "    lowp vec4 newColor2 = texture2D(lookupBitmap, texPos2);\n" +
                "\n" +
                "    lowp vec4 newColor = mix(newColor1, newColor2, fract(blueColor));\n" +
                "\n" +
                "    return mix(textureColor, vec4(newColor.rgb, textureColor.w), 1.);\n" +
                "}\n" +
                "\n" +
                "vec4 getFromColor(vec2 uv){\n" +
                "    return lookup(texture2D(from, vec2(1.0, -1.0)*uv*_zoomProgress), fromLookupTexture, _uv);\n" +
                "}\n" +
                "vec4 getToColor(vec2 uv){\n" +
                "    return lookup(texture2D(to, vec2(1.0, -1.0)*uv*_zoomProgress1), toLookupTexture, _uv) ;\n" +
                "}\n" +
                "\n" +
                transitionCode+
                "void main()\n" +
                "{\n" +
                "    gl_FragColor=transition(_uv);\n" +
                "}"
    }

}