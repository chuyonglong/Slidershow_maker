package com.daasuu.gpuv.egl.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.daasuu.gpuv.egl.filter.*
import com.daasuu.gpuv.egl.more_filter.filters.*

object Utils {
    fun createViewFilter(videoFilterType: VideoFilterType): GlFilter {
        return when (videoFilterType) {

            VideoFilterType.DEFAULT ->  GlFilter()

            VideoFilterType.BILATERAL_BLUR ->  GlBilateralFilter()
            VideoFilterType.BOX_BLUR ->  GlBoxBlurFilter()
            VideoFilterType.BULGE_DISTORTION ->  GlBulgeDistortionFilter()
            VideoFilterType.CGA_COLORSPACE ->  GlCGAColorspaceFilter()
            VideoFilterType.CROSSHATCH ->  GlCrosshatchFilter()
            VideoFilterType.EXPOSURE ->  GlExposureFilter()
            VideoFilterType.GAUSSIAN_FILTER ->   GlGaussianBlurFilter()
            VideoFilterType.GRAY_SCALE ->   GlGrayScaleFilter()
            VideoFilterType.HALFTONE ->   GlHalftoneFilter()
            VideoFilterType.HAZE ->   GlHazeFilter()
            VideoFilterType.INVERT ->   GlInvertFilter()

            VideoFilterType.LUMINANCE ->   GlLuminanceFilter()
            VideoFilterType.LUMINANCE_THRESHOLD ->   GlLuminanceThresholdFilter()
            VideoFilterType.PIXELATION ->   GlPixelationFilter()
            VideoFilterType.POSTERIZE ->   GlPosterizeFilter()
            VideoFilterType.RGB ->   GlRGBFilter().apply { setRed(0f) }
            VideoFilterType.SEPIA ->   GlSepiaFilter()
            VideoFilterType.SOLARIZE ->   GlSolarizeFilter()
            VideoFilterType.SPHERE_REFRACTION ->   GlSphereRefractionFilter().apply { setRadius(0.25f)
                setCenterX(0.5f)
                setCenterY(0.5f)
                setAspectRatio(1f)}
            VideoFilterType.SWIRL ->   GlSwirlFilter()
            VideoFilterType.TONE ->   GlToneFilter()
            VideoFilterType.VIBRANCE ->   GlVibranceFilter().apply { setVibrance(3f) }
            VideoFilterType.VIGNETTE ->   GlVignetteFilter()
            VideoFilterType.ZOOM_BLUR ->   GlZoomBlurFilter()
            VideoFilterType.ANAGLYPH ->   GlAnaglyphFilter()

            VideoFilterType.TV_SHOW ->   GlTvShopFilter()
            VideoFilterType.ASCIART ->   GlAsciArtFilter()
            VideoFilterType.BEVELED -> GlBeveledFilter()
            VideoFilterType.BINARY_GLITCH -> GlBinaryGlitchEffectFilter()
            VideoFilterType.BLACK_BODY -> GlBlackBodyFilter()
            VideoFilterType.BLEACH -> GlBleachFilter()
            VideoFilterType.BLUE -> GlBlueFilter()
            VideoFilterType.BOKEH -> GlBokehFilter()
            VideoFilterType.BW_STROBE -> GlBwStrobeFilter()
            VideoFilterType.COMMODORE -> GlCommodoreFilter()
            VideoFilterType.CROSS_HATCHING -> GlCrosshatchingFilter()
            VideoFilterType.CROSS_STITCHING -> GlCrossStitchingFilter()
            VideoFilterType.CRT -> GlCrtFilter()
            VideoFilterType.DAWN_BRINGER -> GlDawnbringerFilter()
            VideoFilterType.DISPERSION -> GlDispersionFilter()
            VideoFilterType.DROSTE -> GlDrosteFilter()
            VideoFilterType.DRUNK -> GlDrunkFilter()
            VideoFilterType.FALSE_COLOR -> GlFalseColorFilter()
            VideoFilterType.FIRE -> GlFireFilter()
            VideoFilterType.FISH_EYE -> GlFisheyeFilter()
            VideoFilterType.FIXED_TONE -> GlFixedToneFilter()
            VideoFilterType.FRESNEL -> GlFresnelFilter()
            VideoFilterType.FROSTED -> GlFrostedGlassFilter()
            VideoFilterType.GAME_BOY -> GlGameboyFilter()
            VideoFilterType.GLITCH -> GlGlitchEffect()
            VideoFilterType.HIGH_SPEED -> GlHighSpeedFilter()
            VideoFilterType.LAPLACE -> GlLaplaceFilter()
            VideoFilterType.LOW_QUALITY -> GlLowQualityFilter()
            VideoFilterType.MATRIX -> GlMatrixFilter()
            VideoFilterType.MIRROR -> GlMirrorFilter.leftToRight()
            VideoFilterType.MIRROR_01 -> GlMirrorFilter.rightToLeft()
            VideoFilterType.MIRROR_02 -> GlMirrorFilter.topToBottom()
            VideoFilterType.MIRROR_03 -> GlMirrorFilter.bottomToTop()
            VideoFilterType.MIRROR_04 -> GlMirrorFilter.moreMirror()
            VideoFilterType.MOLTEN -> GlMoltenGoldFilter()
            VideoFilterType.NIGHT_VISION -> GlNightVisionFilter()
            VideoFilterType.OLD_MOVIE -> GlOldMovieFilter()
            VideoFilterType.ORANGE_TEAL -> GlOrangeTealFilter()
            VideoFilterType.POLYGON -> GlPolygonsFilter()
            VideoFilterType.POSTERIZATION -> GlPosterizationFilter()
            VideoFilterType.RADIAL_BLUR -> GlRadialBlurFilter()
            VideoFilterType.RAIN -> GlRainFilter()
            VideoFilterType.SEVENTY -> GlSeventyFilter()
            VideoFilterType.SMOOTH_TONE -> GlSmoothToneFilter()
            VideoFilterType.SNOW -> GlSnowFilter()
            VideoFilterType.SOLARIZATION -> GlSolarizationFilter()
            VideoFilterType.SPIRALS -> GlSpiralsFilter()
            VideoFilterType.SPLIT -> GlSplitColorFilter()
            VideoFilterType.SPY_GLASS -> GlSpyGlassFilter()
            VideoFilterType.THERMAL -> GlThermalFilter()
            VideoFilterType.TILES -> GlTilesFilter()
            VideoFilterType.ULTRA_VIOLET -> GlUltravioletFilter()
            VideoFilterType.WARP -> GlWarpFilter()
            VideoFilterType.WAVY -> GlWavyFilter()
            VideoFilterType.WISP -> GlWispFilter()
        }
    }

    fun getVideoFilterType(): Array<VideoFilterType> {
        return VideoFilterType.values()
    }

    fun getBitmapFromAsset(path: String, context: Context): Bitmap {
        val inputStream = context.assets.open(path)
        return BitmapFactory.decodeStream(inputStream)
    }

    fun getLookupFilter(context: Context, lookupType: LookupType) :GlLookUpTableFilter {
        val bitmap = getBitmapFromAsset("luts/$lookupType.jpg", context)
        return GlLookUpTableFilter(bitmap)
    }


}