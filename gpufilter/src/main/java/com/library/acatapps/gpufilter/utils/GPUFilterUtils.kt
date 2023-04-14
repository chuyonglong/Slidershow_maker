package com.library.gpu.filter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import com.library.acatapps.gpufilter.R
import com.library.acatapps.gpufilter.effect.*
import com.library.acatapps.gpufilter.filter.*
import com.library.acatapps.gpufilter.filter.GPUImageBilateralBlurFilter
import com.library.acatapps.gpufilter.filter.GPUImageLuminanceFilter
import com.library.acatapps.gpufilter.filter.GPUImageLuminanceThresholdFilter
import com.library.acatapps.gpufilter.filter.GPUImageSepiaToneFilter
import com.library.acatapps.gpufilter.filter.GPUImageSobelEdgeDetectionFilter
import com.library.acatapps.gpufilter.filter.GPUImageSolarizeFilter
import com.library.acatapps.gpufilter.filter.GPUImageThresholdEdgeDetectionFilter
import com.library.acatapps.gpufilter.filter.GPUImageVibranceFilter
import com.library.acatapps.gpufilter.filter.GPUImageZoomBlurFilter
import com.library.acatapps.gpufilter.utils.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.*

object GPUFilterUtils {

    fun applyFilter(context: Context, bitmap: Bitmap, gpuImageFilter: GPUImageFilter): Bitmap {
        val gpuImage = GPUImage(context)
        gpuImage.setFilter(gpuImageFilter)
//        val filterAdjuster =
        return gpuImage.getBitmapWithFilterApplied(bitmap)
    }

    class FilterModelList {
        val filters = arrayListOf<FilterModel>()
        fun addFilter(title: String, filterType: FilterType, context: Context) {
            filters.add(FilterModel(title, filterType, context))
        }

        fun addFilter(title: String, filter: GPUImageFilter, context: Context) {
            filters.add(FilterModel(title, filter, context))
        }
    }

    fun getEffect(context: Context): ArrayList<FilterModel> {
        val filters = FilterModelList()
            .apply {
//                addFilter("original",
//                    FilterType.NONE , context
//                )
//                addFilter("Rain",
//                    FilterType.RAIN , context
//                )
//                addFilter("Snow",
//                    FilterType.SNOW, context
//                )
                addFilter("Mirror (left to right)",
                    FilterType.MIRROR_LEFT_TO_RIGHT, context
                )
                addFilter("Mirror (right to left)",
                    FilterType.MIRROR_RIGHT_TO_LEFT, context
                )
                addFilter("Wisp",
                    FilterType.WISP, context
                )
                addFilter("Wavy",
                    FilterType.WAVY, context
                )
//                addFilter("Anathy",
//                    FilterType.ANAGLYTH, context
//                )
//                addFilter("Fire",
//                    FilterType.FIRE, context
//                )
//                addFilter("TvShop",
//                    FilterType.TVSHOP, context
//                )
//                addFilter("Asciart",
//                    FilterType.ASCIART, context
//                )
//                addFilter("Beveled",
//                    FilterType.BEVELED, context
//                )
//                addFilter("BlackBody",
//                    FilterType.BLACKBODY, context
//                )
                addFilter("Bleach",
                    FilterType.BLEACH, context
                )

//                addFilter("Droste",
//                    FilterType.DROSTE, context
//                )
//                addFilter("Night Vision",
//                    FilterType.NIGHTVISION, context
//                )
//                addFilter("Old Movie",
//                    FilterType.OLDMOVIE, context
//                )
//                addFilter("Orange",
//                    FilterType.ORANGE, context
//                )
//                addFilter("Polygon",
//                    FilterType.POLYGON, context
//                )
                addFilter("Radial Blur",
                    FilterType.RADIALBLUR, context
                )
                addFilter("Seventy",
                    FilterType.SEVENTY, context
                )
                addFilter("Spirals",
                    FilterType.SPIRALS, context
                )
//                addFilter("Split Color",
//                    FilterType.SPLITCOLOR, context
//                )

                addFilter("Mirror (top to bottom)",
                    FilterType.MIRROR_TOP_TO_BOTTOM, context
                )
                addFilter("Mirror (bottom to top)",
                    FilterType.MIRROR_BOTTOM_TO_TOP, context
                )
                addFilter("Blur",
                    FilterType.BLUR, context
                )
//                addFilter("Boker",
//                    FilterType.BOKEH, context
//                )
                addFilter("Bwstrobe",
                    FilterType.BWSTROBE, context
                )
//                addFilter("Crosshatching",
//                    FilterType.CROSSHATCHING, context
//                )
//                addFilter("CrossStitching",
//                    FilterType.CROSSSTITCHING, context
//                )
                addFilter("Dispersion",
                    FilterType.DISPERSION, context
                )
                addFilter("Mirror (more)",
                    FilterType.MIRROR_MORE, context
                )
                addFilter("FrostedGlass",
                    FilterType.FROSTEDGLASS, context
                )
                addFilter("Glitch",
                    FilterType.GLITCH, context
                )
                addFilter("Low Quality",
                    FilterType.LOWQUALITY, context
                )
//                addFilter("Moltengold",
//                    FilterType.MOLTENGOLD, context
//                )
//                addFilter("Tiles",
//                    FilterType.TILES, context
//                )
//                addFilter("Sepia filter",
//                    FilterType.SEPIAFILTER, context
//                )
                addFilter("Blend (Saturation)",
                    FilterType.BLEND_SATURATION, context
                )
//            addFilter("Grouped filters", FilterType.FILTER_GROUP)
//            addFilter("Invert", FilterType.INVERT)
//            addFilter("Blend (Color Burn)", FilterType.BLEND_COLOR_BURN)
                addFilter("Blend (Overlay)",
                    FilterType.BLEND_OVERLAY, context
                )
                addFilter("Blend (Screen)",
                    FilterType.BLEND_SCREEN, context
                )
                addFilter("Blend (Color)",
                    FilterType.BLEND_COLOR, context
                )
                addFilter("Blend (Soft Light)",
                    FilterType.BLEND_SOFT_LIGHT, context
                )
                addFilter("Gaussian Blur",
                    FilterType.GAUSSIAN_BLUR, context
                )
                addFilter("Crosshatch",
                    FilterType.CROSSHATCH, context
                )
//            addFilter("CGA Color Space", FilterType.CGA_COLORSPACE)
                addFilter("Dilation",
                    FilterType.DILATION, context
                )
                addFilter("Sketch",
                    FilterType.SKETCH, context
                )
                addFilter("Toon",
                    FilterType.TOON, context
                )
                addFilter("Halftone",
                    FilterType.HALFTONE, context
                )
//            addFilter("Glass Sphere", FilterType.GLASS_SPHERE)
//            addFilter("Laplacian", FilterType.LAPLACIAN)
//            addFilter("Sphere Refraction", FilterType.SPHERE_REFRACTION)
                addFilter("Swirl",
                    FilterType.SWIRL, context
                )
                addFilter("False Color",
                    FilterType.FALSE_COLOR, context
                )
                addFilter("Vibrance",
                    FilterType.VIBRANCE, context
                )
            }
        return filters.filters
    }

    fun getFilterModel(context: Context): ArrayList<FilterModel> {
        val filterModelList = FilterModelList().apply {
            addFilter("None", FilterType.NONE, context)
            addFilter("Contrast", FilterType.CONTRAST, context)
            addFilter("Invert", FilterType.INVERT, context)
            addFilter("Pixelation", FilterType.PIXELATION, context)
            addFilter("Hue", FilterType.HUE, context)
            addFilter("Gamma", FilterType.GAMMA, context)
           // addFilter("Brightness", FilterType.BRIGHTNESS, context)
            addFilter("Sepia", FilterType.SEPIA, context)
            addFilter("Grayscale", FilterType.GRAYSCALE, context)
            addFilter("Sharpness", FilterType.SHARPEN, context)
            addFilter("Sobel Edge Detection", FilterType.SOBEL_EDGE_DETECTION, context)
            addFilter("Threshold Edge Detection", FilterType.THRESHOLD_EDGE_DETECTION, context)
            // addFilter("3x3 Convolution", FilterType.THREE_X_THREE_CONVOLUTION, context)
            addFilter("Emboss", FilterType.EMBOSS, context)
            addFilter("Posterize", FilterType.POSTERIZE, context)
            addFilter("Grouped filters", FilterType.FILTER_GROUP, context)
            addFilter("Saturation", FilterType.SATURATION, context)
            addFilter("Exposure", FilterType.EXPOSURE, context)
            addFilter("Highlight Shadow", FilterType.HIGHLIGHT_SHADOW, context)
            addFilter("Monochrome", FilterType.MONOCHROME, context)
            addFilter("Opacity", FilterType.OPACITY, context)
            addFilter("RGB", FilterType.RGB, context)
            addFilter("White Balance", FilterType.WHITE_BALANCE, context)
            addFilter("Vignette", FilterType.VIGNETTE, context)
            addFilter("ToneCurve", FilterType.TONE_CURVE, context)

            addFilter("Luminance", FilterType.LUMINANCE, context)
            addFilter("Luminance Threshold", FilterType.LUMINANCE_THRESHSOLD, context)

            addFilter("Blend (Difference)", FilterType.BLEND_DIFFERENCE, context)
            //addFilter("Blend (Source Over)", FilterType.BLEND_SOURCE_OVER, context)
            //addFilter("Blend (Color Burn)", FilterType.BLEND_COLOR_BURN, context)
            addFilter("Blend (Color Dodge)", FilterType.BLEND_COLOR_DODGE, context)
            //addFilter("Blend (Darken)", FilterType.BLEND_DARKEN, context)
            addFilter("Blend (Dissolve)", FilterType.BLEND_DISSOLVE, context)
            addFilter("Blend (Exclusion)", FilterType.BLEND_EXCLUSION, context)
            //addFilter("Blend (Hard Light)", FilterType.BLEND_HARD_LIGHT, context)
            addFilter("Blend (Lighten)", FilterType.BLEND_LIGHTEN, context)
            addFilter("Blend (Add)", FilterType.BLEND_ADD, context)
            //addFilter("Blend (Divide)", FilterType.BLEND_DIVIDE, context)
            //addFilter("Blend (Multiply)", FilterType.BLEND_MULTIPLY, context)
            addFilter("Blend (Overlay)", FilterType.BLEND_OVERLAY, context)
            addFilter("Blend (Screen)", FilterType.BLEND_SCREEN, context)
            addFilter("Blend (Alpha)", FilterType.BLEND_ALPHA, context)
            addFilter("Blend (Color)", FilterType.BLEND_COLOR, context)
            addFilter("Blend (Hue)", FilterType.BLEND_HUE, context)
            addFilter("Blend (Saturation)", FilterType.BLEND_SATURATION, context)
           // addFilter("Blend (Luminosity)", FilterType.BLEND_LUMINOSITY, context)
            //addFilter("Blend (Linear Burn)", FilterType.BLEND_LINEAR_BURN, context)
            addFilter("Blend (Soft Light)", FilterType.BLEND_SOFT_LIGHT, context)
            addFilter("Blend (Subtract)", FilterType.BLEND_SUBTRACT, context)
            addFilter("Blend (Chroma Key)", FilterType.BLEND_CHROMA_KEY, context)
            //addFilter("Blend (Normal)", FilterType.BLEND_NORMAL, context)

            addFilter("Lookup (Amatorka)", FilterType.LOOKUP_AMATORKA, context)
            addFilter("Gaussian Blur", FilterType.GAUSSIAN_BLUR, context)
            addFilter("Crosshatch", FilterType.CROSSHATCH, context)

            addFilter("Box Blur", FilterType.BOX_BLUR, context)
            addFilter("CGA Color Space", FilterType.CGA_COLORSPACE, context)
            addFilter("Dilation", FilterType.DILATION, context)
            addFilter("Kuwahara", FilterType.KUWAHARA, context)
            addFilter("RGB Dilation", FilterType.RGB_DILATION, context)
            addFilter("Sketch", FilterType.SKETCH, context)
            addFilter("Toon", FilterType.TOON, context)
            addFilter("Smooth Toon", FilterType.SMOOTH_TOON, context)
            addFilter("Halftone", FilterType.HALFTONE, context)

            addFilter("Bulge Distortion", FilterType.BULGE_DISTORTION, context)
            addFilter("Glass Sphere", FilterType.GLASS_SPHERE, context)
            addFilter("Haze", FilterType.HAZE, context)
            addFilter("Laplacian", FilterType.LAPLACIAN, context)
            addFilter("Non Maximum Suppression", FilterType.NON_MAXIMUM_SUPPRESSION, context)
            addFilter("Sphere Refraction", FilterType.SPHERE_REFRACTION, context)
            addFilter("Swirl", FilterType.SWIRL, context)
            addFilter("Weak Pixel Inclusion", FilterType.WEAK_PIXEL_INCLUSION, context)
            addFilter("False Color", FilterType.FALSE_COLOR, context)

            addFilter("Color Balance", FilterType.COLOR_BALANCE, context)

            addFilter("Levels Min (Mid Adjust)", FilterType.LEVELS_FILTER_MIN, context)

            addFilter("Bilateral Blur", FilterType.BILATERAL_BLUR, context)

            addFilter("Zoom Blur", FilterType.ZOOM_BLUR, context)

            //addFilter("Transform (2-D)", FilterType.TRANSFORM2D, context)

            addFilter("Solarize", FilterType.SOLARIZE, context)

            addFilter("Vibrance", FilterType.VIBRANCE, context)
        }
        return filterModelList.filters
    }

    fun initFilter(filterType: FilterType, context: Context): GPUImageFilter {
        return when (filterType) {
            FilterType.CONTRAST -> GPUImageContrastFilter(2.0f)
            FilterType.GAMMA -> GPUImageGammaFilter(2.0f)
            FilterType.INVERT -> GPUImageColorInvertFilter()
            FilterType.PIXELATION -> GPUImagePixelationFilter()
            FilterType.HUE -> GPUImageHueFilter(90.0f)
            FilterType.BRIGHTNESS -> GPUImageBrightnessFilter(1.5f)
            FilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
            FilterType.SEPIA -> GPUImageSepiaToneFilter()
            FilterType.SHARPEN -> GPUImageSharpenFilter()
            FilterType.SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetectionFilter()
            FilterType.THRESHOLD_EDGE_DETECTION -> GPUImageThresholdEdgeDetectionFilter()
            FilterType.THREE_X_THREE_CONVOLUTION -> GPUImage3x3ConvolutionFilter()
            FilterType.EMBOSS -> GPUImageEmbossFilter()
            FilterType.POSTERIZE -> GPUImagePosterizeFilter()
            FilterType.FILTER_GROUP -> GPUImageFilterGroup(
                listOf(
                    GPUImageContrastFilter(),
                    GPUImageDirectionalSobelEdgeDetectionFilter(),
                    GPUImageGrayscaleFilter()
                )
            )

            FilterType.MIRROR_BOTTOM_TO_TOP -> GPUMirrorFilter.bottomToTop()
            FilterType.MIRROR_TOP_TO_BOTTOM -> GPUMirrorFilter.topToBottom()
            FilterType.MIRROR_RIGHT_TO_LEFT -> GPUMirrorFilter.rightToLeft()
            FilterType.MIRROR_LEFT_TO_RIGHT -> GPUMirrorFilter.leftToRight()
            FilterType.MIRROR_MORE -> GPUMirrorFilter.moreMirror()
            FilterType.WISP -> GPUWispFilter()
            FilterType.WAVY -> GPUWavyFilter()
            FilterType.RAIN -> GPURainFilter()
            FilterType.SNOW -> GPUSnowFilter()
            FilterType.ANAGLYTH -> GPUAnaglyphFilter()
            FilterType.TVSHOP -> GPUTvShopFilter()
            FilterType.ASCIART -> GPUAsciArtFilter()
            FilterType.BEVELED -> GPUBeveledFilter()
            FilterType.BLACKBODY -> GPUBackBodyFilter()
            FilterType.BLEACH -> GPUBleachFilter()
            FilterType.BLUR -> GPUBlueFilter()
            FilterType.BOKEH -> GPUBokehFilter()
            FilterType.BWSTROBE -> GPUBwStrobeFilter()
            FilterType.CROSSHATCHING -> GPUCrosshatchingFilter()
            FilterType.CROSSSTITCHING -> GPUCrossStitchingFilter()
            FilterType.DISPERSION -> GPUDispersionFilter()
            FilterType.DROSTE -> GPUDrosteFilter()
            FilterType.FIRE -> GPUFireFilter()
            FilterType.FROSTEDGLASS -> GPUFrostedGlassFilter()
            FilterType.GLITCH -> GPUGlitchEffect()
            FilterType.LOWQUALITY -> GPULowQualityFilter()
            FilterType.MOLTENGOLD -> GPUMoltenGoldFilter()
            FilterType.NIGHTVISION -> GPUNightVisionFilter()
            FilterType.OLDMOVIE -> GPUOldMovieFilter()
            FilterType.ORANGE -> GPUOrangeTealFilter()
            FilterType.POLYGON -> GPUPolygonsFilter()
            FilterType.RADIALBLUR -> GPURadialBlurFilter()
            FilterType.SEVENTY -> GPUSeventyFilter()
            FilterType.SPIRALS -> GPUSpiralsFilter()
            FilterType.SPLITCOLOR -> GPUSplitColorFilter()
            FilterType.TILES -> GPUTilesFilter()
            FilterType.SEPIAFILTER -> GPUSepiaFilter()

            FilterType.SATURATION -> GPUImageSaturationFilter(1.0f)
            FilterType.EXPOSURE -> GPUImageExposureFilter(0.0f)
            FilterType.HIGHLIGHT_SHADOW -> GPUImageHighlightShadowFilter(
                0.0f,
                1.0f
            )
            FilterType.MONOCHROME -> GPUImageMonochromeFilter(
                1.0f, floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
            )
            FilterType.OPACITY -> GPUImageOpacityFilter(1.0f)
            FilterType.RGB -> GPUImageRGBFilter(1.0f, 1.0f, 1.0f)
            FilterType.WHITE_BALANCE -> GPUImageWhiteBalanceFilter(
                5000.0f,
                0.0f
            )
            FilterType.VIGNETTE -> GPUImageVignetteFilter(
                PointF(0.5f, 0.5f),
                floatArrayOf(0.0f, 0.0f, 0.0f),
                0.3f,
                0.75f
            )
            FilterType.TONE_CURVE -> GPUImageToneCurveFilter().apply {
                setFromCurveFileInputStream(context.resources.openRawResource(R.raw.tone_cuver_sample))
            }
            FilterType.LUMINANCE -> GPUImageLuminanceFilter()
            FilterType.LUMINANCE_THRESHSOLD -> GPUImageLuminanceThresholdFilter(0.5f)
            FilterType.BLEND_DIFFERENCE -> createBlendFilter(
                context,
                GPUImageDifferenceBlendFilter::class.java
            )
            FilterType.BLEND_SOURCE_OVER -> createBlendFilter(
                context,
                GPUImageSourceOverBlendFilter::class.java
            )
            FilterType.BLEND_COLOR_BURN -> createBlendFilter(
                context,
                GPUImageColorBurnBlendFilter::class.java
            )
            FilterType.BLEND_COLOR_DODGE -> createBlendFilter(
                context,
                GPUImageColorDodgeBlendFilter::class.java
            )
            FilterType.BLEND_DARKEN -> createBlendFilter(
                context,
                GPUImageDarkenBlendFilter::class.java
            )
            FilterType.BLEND_DISSOLVE -> createBlendFilter(
                context,
                GPUImageDissolveBlendFilter::class.java
            )
            FilterType.BLEND_EXCLUSION -> createBlendFilter(
                context,
                GPUImageExclusionBlendFilter::class.java
            )

            FilterType.BLEND_HARD_LIGHT -> createBlendFilter(
                context,
                GPUImageHardLightBlendFilter::class.java
            )
            FilterType.BLEND_LIGHTEN -> createBlendFilter(
                context,
                GPUImageLightenBlendFilter::class.java
            )
            FilterType.BLEND_ADD -> createBlendFilter(
                context,
                GPUImageAddBlendFilter::class.java
            )
            FilterType.BLEND_DIVIDE -> createBlendFilter(
                context,
                GPUImageDivideBlendFilter::class.java
            )
            FilterType.BLEND_MULTIPLY -> createBlendFilter(
                context,
                GPUImageMultiplyBlendFilter::class.java
            )
            FilterType.BLEND_OVERLAY -> createBlendFilter(
                context,
                GPUImageOverlayBlendFilter::class.java
            )
            FilterType.BLEND_SCREEN -> createBlendFilter(
                context,
                GPUImageScreenBlendFilter::class.java
            )
            FilterType.BLEND_ALPHA -> createBlendFilter(
                context,
                GPUImageAlphaBlendFilter::class.java
            )
            FilterType.BLEND_COLOR -> createBlendFilter(
                context,
                GPUImageColorBlendFilter::class.java
            )
            FilterType.BLEND_HUE -> createBlendFilter(
                context,
                GPUImageHueBlendFilter::class.java
            )
            FilterType.BLEND_SATURATION -> createBlendFilter(
                context,
                GPUImageSaturationBlendFilter::class.java
            )
            FilterType.BLEND_LUMINOSITY -> createBlendFilter(
                context,
                GPUImageLuminosityBlendFilter::class.java
            )
            FilterType.BLEND_LINEAR_BURN -> createBlendFilter(
                context,
                GPUImageLinearBurnBlendFilter::class.java
            )
            FilterType.BLEND_SOFT_LIGHT -> createBlendFilter(
                context,
                GPUImageSoftLightBlendFilter::class.java
            )
            FilterType.BLEND_SUBTRACT -> createBlendFilter(
                context,
                GPUImageSubtractBlendFilter::class.java
            )
            FilterType.BLEND_CHROMA_KEY -> createBlendFilter(
                context,
                GPUImageChromaKeyBlendFilter::class.java
            )
            FilterType.BLEND_NORMAL -> createBlendFilter(
                context,
                GPUImageNormalBlendFilter::class.java
            )

            FilterType.LOOKUP_AMATORKA -> GPUImageLookupFilter().apply {
                bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lookup_amatorka)
            }
            FilterType.GAUSSIAN_BLUR -> GPUImageGaussianBlurFilter()
            FilterType.CROSSHATCH -> GPUImageCrosshatchFilter()
            FilterType.BOX_BLUR -> GPUImageBoxBlurFilter()
            FilterType.CGA_COLORSPACE -> GPUImageCGAColorspaceFilter()
            FilterType.DILATION -> GPUImageDilationFilter()
            FilterType.KUWAHARA -> GPUImageKuwaharaFilter()
            FilterType.RGB_DILATION -> GPUImageRGBDilationFilter()
            FilterType.SKETCH -> GPUImageSketchFilter()
            FilterType.TOON -> GPUImageToonFilter()
            FilterType.SMOOTH_TOON -> GPUImageSmoothToonFilter()
            FilterType.BULGE_DISTORTION -> GPUImageBulgeDistortionFilter()
            FilterType.GLASS_SPHERE -> GPUImageGlassSphereFilter()
            FilterType.HAZE -> GPUImageHazeFilter()
            FilterType.LAPLACIAN -> GPUImageLaplacianFilter()
            FilterType.NON_MAXIMUM_SUPPRESSION -> GPUImageNonMaximumSuppressionFilter()
            FilterType.SPHERE_REFRACTION -> GPUImageSphereRefractionFilter()
            FilterType.SWIRL -> GPUImageSwirlFilter()
            FilterType.WEAK_PIXEL_INCLUSION -> GPUImageWeakPixelInclusionFilter()
            FilterType.FALSE_COLOR -> GPUImageFalseColorFilter()
            FilterType.COLOR_BALANCE -> GPUImageColorBalanceFilter()
            FilterType.LEVELS_FILTER_MIN -> GPUImageLevelsFilter()
            FilterType.HALFTONE -> GPUImageHalftoneFilter()
            FilterType.BILATERAL_BLUR -> GPUImageBilateralBlurFilter()
            FilterType.ZOOM_BLUR -> GPUImageZoomBlurFilter()
            FilterType.TRANSFORM2D -> GPUImageTransformFilter()
            FilterType.SOLARIZE -> GPUImageSolarizeFilter()
            FilterType.VIBRANCE -> GPUImageVibranceFilter()
            FilterType.NONE -> GPUImageFilter()
        }
    }

    private fun createBlendFilter(
        context: Context,
        filterClass: Class<out GPUImageTwoInputFilter>
    ): GPUImageFilter {
        return try {
            filterClass.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            GPUImageFilter()
        }
    }

    public enum class FilterType {
        NONE, CONTRAST, GRAYSCALE, SHARPEN, SEPIA, SOBEL_EDGE_DETECTION, THRESHOLD_EDGE_DETECTION, THREE_X_THREE_CONVOLUTION, FILTER_GROUP, EMBOSS, POSTERIZE, GAMMA, BRIGHTNESS, INVERT, HUE, PIXELATION,
        SATURATION, EXPOSURE, HIGHLIGHT_SHADOW, MONOCHROME, OPACITY, RGB, WHITE_BALANCE, VIGNETTE, TONE_CURVE, LUMINANCE, LUMINANCE_THRESHSOLD, BLEND_COLOR_BURN, BLEND_COLOR_DODGE, BLEND_DARKEN,
        BLEND_DIFFERENCE, BLEND_DISSOLVE, BLEND_EXCLUSION, BLEND_SOURCE_OVER, BLEND_HARD_LIGHT, BLEND_LIGHTEN, BLEND_ADD, BLEND_DIVIDE, BLEND_MULTIPLY, BLEND_OVERLAY, BLEND_SCREEN, BLEND_ALPHA,
        BLEND_COLOR, BLEND_HUE, BLEND_SATURATION, BLEND_LUMINOSITY, BLEND_LINEAR_BURN, BLEND_SOFT_LIGHT, BLEND_SUBTRACT, BLEND_CHROMA_KEY, BLEND_NORMAL, LOOKUP_AMATORKA,
        GAUSSIAN_BLUR, CROSSHATCH, BOX_BLUR, CGA_COLORSPACE, DILATION, KUWAHARA, RGB_DILATION, SKETCH, TOON, SMOOTH_TOON, BULGE_DISTORTION, GLASS_SPHERE, HAZE, LAPLACIAN, NON_MAXIMUM_SUPPRESSION,
        SPHERE_REFRACTION, SWIRL, WEAK_PIXEL_INCLUSION, FALSE_COLOR, COLOR_BALANCE, LEVELS_FILTER_MIN, BILATERAL_BLUR, ZOOM_BLUR, HALFTONE, TRANSFORM2D, SOLARIZE, VIBRANCE,
        MIRROR_LEFT_TO_RIGHT, MIRROR_RIGHT_TO_LEFT, MIRROR_TOP_TO_BOTTOM, MIRROR_BOTTOM_TO_TOP, MIRROR_MORE, WISP, WAVY, RAIN, SNOW, ANAGLYTH, TVSHOP, ASCIART, BEVELED, BLACKBODY, BLEACH,
        BLUR, BOKEH, BWSTROBE, CROSSHATCHING, CROSSSTITCHING, DISPERSION, DROSTE, FIRE, FROSTEDGLASS, GLITCH, LOWQUALITY, MOLTENGOLD, NIGHTVISION, OLDMOVIE, ORANGE, POLYGON, RADIALBLUR,
        SEVENTY, SPIRALS, SPLITCOLOR, TILES, SEPIAFILTER
    }


}