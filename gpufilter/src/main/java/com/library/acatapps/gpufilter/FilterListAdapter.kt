package com.library.acatapps.gpufilter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.library.gpu.filter.FilterModel
import com.library.gpu.filter.GPUFilterUtils
import com.library.acatapps.gpufilter.effect.*
import com.library.acatapps.gpufilter.utils.BitmapUtils
import com.library.acatapps.gpufilter.utils.GPUImageFilterTools
import com.library.acatapps.gpufilter.utils.LookupType
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.co.cyberagent.android.gpuimage.filter.*
import kotlinx.android.synthetic.main.item_filter_module.view.*

class FilterListAdapter(private val context: Context) :
    RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {

    private val itemList: ArrayList<FilterModel>
    private val compositeDisposable = CompositeDisposable()
    private var mCurrentFilterIndex = 0
    var onChange: OnChange? = null
    var previewImage: Bitmap? = null

    init {
        itemList = getFilter(context)
    }

    fun getFilter(context: Context): ArrayList<FilterModel> {
        val filterModelList = GPUFilterUtils.FilterModelList()
        for (type in LookupType.values()) {
            filterModelList.addFilter(type.name, GPULookUpTableFilter().apply {
                bitmap = getBitmapFromAsset("lookup/$type.jpg", context)
            }, context)
        }
        val filter = GPUFilterUtils.getEffect(context)
        for (ft in filter) {
            filterModelList.addFilter(ft.title ,  ft.filterType , context )
        }
        return filterModelList.filters
    }

    fun getBitmapFromAsset(path: String, context: Context): Bitmap {
        val inputStream = context.assets.open(path)
        return BitmapFactory.decodeStream(inputStream)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val item = itemList[position]
        view.setOnClickListener {
            itemList[mCurrentFilterIndex].selected = false
            notifyItemChanged(mCurrentFilterIndex)
            mCurrentFilterIndex = position
            itemList[mCurrentFilterIndex].selected = true
            notifyItemChanged(mCurrentFilterIndex)
            if (onChange != null)
                onChange!!.onChangeFilter(item)
        }


        if (item.selected) {
            view.percentFilterLabel.visibility = View.VISIBLE
            view.percentFilterLabel.text = item.currentPercent.toString()
        } else {
            view.percentFilterLabel.visibility = View.GONE
        }
        if (!item.filterAdjuster.canAdjust()) {
            view.percentFilterLabel.text = ""
        }

        view.titleFilterLabel.text = item.title
        val d = Observable.fromCallable<Bitmap> {
            if (previewImage == null)
                previewImage =
                    BitmapFactory.decodeResource(context.resources, R.drawable.demo_filter)
            val bitmap = GPUFilterUtils.applyFilter(context, previewImage!!, item.originnalFilter)
            return@fromCallable BitmapUtils.resizeBitmap(bitmap, 240f)
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    view.demoImageFilter.setImageBitmap(it)
                },
                onError = {

                },
                onComplete = {

                }
            )
        compositeDisposable.add(d)
    }

    fun changePreviewImage(bitmap: Bitmap?) {
        val d = Observable.fromCallable<Bitmap> {
            previewImage =
                bitmap ?: BitmapFactory.decodeResource(context.resources, R.drawable.demo_filter)
            previewImage = BitmapUtils.resizeBitmap(previewImage!!, 200f)
            return@fromCallable previewImage
        }.observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = {
                    for (i in 0..itemCount) {
                        notifyItemChanged(i)
                    }
                },
                onError = {

                },
                onComplete = {

                }
            )
        compositeDisposable.add(d)
    }

    fun adjust(percent: Int) {
        val item = itemList[mCurrentFilterIndex]
        item.currentPercent = percent
        item.filterAdjuster.adjust(percent)
        if (onChange != null)
            onChange!!.onAdjust(item)
        notifyItemChanged(mCurrentFilterIndex)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_filter_module
    }

    override fun getItemCount(): Int = itemList.size

    fun dispose() {
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnChange {
        fun onChangeFilter(filterData: FilterModel)
        fun onAdjust(filterModel: FilterModel)
    }

    class FilterAdjuster(filter: GPUImageFilter) {
        private val adjuster: Adjuster<out GPUImageFilter>?

        init {
            adjuster = when (filter) {
                is GPUImageSharpenFilter -> SharpnessAdjuster(filter)
                is GPUImageSepiaToneFilter -> SepiaAdjuster(filter)
                is GPUImageContrastFilter -> ContrastAdjuster(filter)
                is GPUImageGammaFilter -> GammaAdjuster(filter)
                is GPUImageBrightnessFilter -> BrightnessAdjuster(filter)
                is GPUImageSobelEdgeDetectionFilter -> SobelAdjuster(filter)
                is GPUImageThresholdEdgeDetectionFilter -> ThresholdAdjuster(filter)
                is GPUImage3x3ConvolutionFilter -> ThreeXThreeConvolutionAjuster(filter)
                is GPUImageEmbossFilter -> EmbossAdjuster(filter)
                is GPUImage3x3TextureSamplingFilter -> GPU3x3TextureAdjuster(filter)
                is GPUImageHueFilter -> HueAdjuster(filter)
                is GPUImagePosterizeFilter -> PosterizeAdjuster(filter)
                is GPUImagePixelationFilter -> PixelationAdjuster(filter)
                is GPUImageSaturationFilter -> SaturationAdjuster(filter)
                is GPUImageExposureFilter -> ExposureAdjuster(filter)
                is GPUImageHighlightShadowFilter -> HighlightShadowAdjuster(filter)
                is GPUImageMonochromeFilter -> MonochromeAdjuster(filter)
                is GPUImageOpacityFilter -> OpacityAdjuster(filter)
                is GPUImageRGBFilter -> RGBAdjuster(filter)
                is GPUImageWhiteBalanceFilter -> WhiteBalanceAdjuster(filter)
                is GPUImageVignetteFilter -> VignetteAdjuster(filter)
                is GPUImageLuminanceThresholdFilter -> LuminanceThresholdAdjuster(filter)
                is GPUImageDissolveBlendFilter -> DissolveBlendAdjuster(filter)
                is GPUImageGaussianBlurFilter -> GaussianBlurAdjuster(filter)
                is GPUImageCrosshatchFilter -> CrosshatchBlurAdjuster(filter)
                is GPUImageBulgeDistortionFilter -> BulgeDistortionAdjuster(filter)
                is GPUImageGlassSphereFilter -> GlassSphereAdjuster(filter)
                is GPUImageHazeFilter -> HazeAdjuster(filter)
                is GPUImageSphereRefractionFilter -> SphereRefractionAdjuster(filter)
                is GPUImageSwirlFilter -> SwirlAdjuster(filter)
                is GPUImageColorBalanceFilter -> ColorBalanceAdjuster(filter)
                is GPUImageLevelsFilter -> LevelsMinMidAdjuster(filter)
                is GPUImageBilateralBlurFilter -> BilateralAdjuster(filter)
                is GPUImageTransformFilter -> RotateAdjuster(filter)
                is GPUImageSolarizeFilter -> SolarizeAdjuster(filter)
                is GPUImageVibranceFilter -> VibranceAdjuster(filter)
                is GPULookUpTableFilter -> LookupAdjust(filter)
                is GPUWispFilter -> WispAdjuster(filter)
                is GPUWavyFilter -> WavyAdjuster(filter)
                is GPURainFilter -> RainAdjuster(filter)
                is GPUSnowFilter -> SnowAdjuster(filter)
                is GPUBleachFilter -> BleachAdjuster(filter)
                is GPUBwStrobeFilter -> BwStrobeAdjuster(filter)
                is GPUDispersionFilter -> DispersionAdjuster(filter)
                is GPUDrosteFilter -> DrosteAdjuster(filter)
                is GPUFireFilter -> FireAdjuster(filter)
                is GPUSpiralsFilter -> SpiralsAdjuster(filter)
                is GPUSplitColorFilter -> SplitColorAdjuster(filter)
                else -> null
            }
        }

        fun canAdjust(): Boolean {
            return adjuster != null
        }

        fun adjust(percentage: Int) {
            adjuster?.adjust(percentage)
        }

        private abstract inner class Adjuster<T : GPUImageFilter>(protected val filter: T) {

            abstract fun adjust(percentage: Int)

            protected fun range(percentage: Int, start: Float, end: Float): Float {
                return (end - start) * percentage / 100.0f + start
            }

            protected fun range(percentage: Int, start: Int, end: Int): Int {
                return (end - start) * percentage / 100 + start
            }

        }

        private inner class SharpnessAdjuster(filter: GPUImageSharpenFilter) :
            Adjuster<GPUImageSharpenFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSharpness(range(percentage, -4.0f, 4.0f))
            }
        }

        private inner class PixelationAdjuster(filter: GPUImagePixelationFilter) :
            Adjuster<GPUImagePixelationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setPixel(range(percentage, 1.0f, 100.0f))
            }
        }

        private inner class HueAdjuster(filter: GPUImageHueFilter) :
            Adjuster<GPUImageHueFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setHue(range(percentage, 0.0f, 360.0f))
            }
        }

        private inner class ContrastAdjuster(filter: GPUImageContrastFilter) :
            Adjuster<GPUImageContrastFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setContrast(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class GammaAdjuster(filter: GPUImageGammaFilter) :
            Adjuster<GPUImageGammaFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setGamma(range(percentage, 0.0f, 3.0f))
            }
        }

        private inner class BrightnessAdjuster(filter: GPUImageBrightnessFilter) :
            Adjuster<GPUImageBrightnessFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBrightness(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class SepiaAdjuster(filter: GPUImageSepiaToneFilter) :
            Adjuster<GPUImageSepiaToneFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class SobelAdjuster(filter: GPUImageSobelEdgeDetectionFilter) :
            Adjuster<GPUImageSobelEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class ThresholdAdjuster(filter: GPUImageThresholdEdgeDetectionFilter) :
            Adjuster<GPUImageThresholdEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
                filter.setThreshold(0.9f)
            }
        }

        private inner class ThreeXThreeConvolutionAjuster(filter: GPUImage3x3ConvolutionFilter) :
            Adjuster<GPUImage3x3ConvolutionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setConvolutionKernel(
                    floatArrayOf(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
                )
            }
        }

        private inner class EmbossAdjuster(filter: GPUImageEmbossFilter) :
            Adjuster<GPUImageEmbossFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.intensity = range(percentage, 0.0f, 4.0f)
            }
        }

        private inner class PosterizeAdjuster(filter: GPUImagePosterizeFilter) :
            Adjuster<GPUImagePosterizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                // In theorie to 256, but only first 50 are interesting
                filter.setColorLevels(range(percentage, 1, 50))
            }
        }

        private inner class GPU3x3TextureAdjuster(filter: GPUImage3x3TextureSamplingFilter) :
            Adjuster<GPUImage3x3TextureSamplingFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class SaturationAdjuster(filter: GPUImageSaturationFilter) :
            Adjuster<GPUImageSaturationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSaturation(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ExposureAdjuster(filter: GPUImageExposureFilter) :
            Adjuster<GPUImageExposureFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setExposure(range(percentage, -10.0f, 10.0f))
            }
        }

        private inner class HighlightShadowAdjuster(filter: GPUImageHighlightShadowFilter) :
            Adjuster<GPUImageHighlightShadowFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setShadows(range(percentage, 0.0f, 1.0f))
                filter.setHighlights(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class MonochromeAdjuster(filter: GPUImageMonochromeFilter) :
            Adjuster<GPUImageMonochromeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class OpacityAdjuster(filter: GPUImageOpacityFilter) :
            Adjuster<GPUImageOpacityFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setOpacity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class RGBAdjuster(filter: GPUImageRGBFilter) :
            Adjuster<GPUImageRGBFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRed(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class WhiteBalanceAdjuster(filter: GPUImageWhiteBalanceFilter) :
            Adjuster<GPUImageWhiteBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setTemperature(range(percentage, 2000.0f, 8000.0f))
            }
        }

        private inner class VignetteAdjuster(filter: GPUImageVignetteFilter) :
            Adjuster<GPUImageVignetteFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVignetteStart(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class LuminanceThresholdAdjuster(filter: GPUImageLuminanceThresholdFilter) :
            Adjuster<GPUImageLuminanceThresholdFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class DissolveBlendAdjuster(filter: GPUImageDissolveBlendFilter) :
            Adjuster<GPUImageDissolveBlendFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMix(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class GaussianBlurAdjuster(filter: GPUImageGaussianBlurFilter) :
            Adjuster<GPUImageGaussianBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBlurSize(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class CrosshatchBlurAdjuster(filter: GPUImageCrosshatchFilter) :
            Adjuster<GPUImageCrosshatchFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setCrossHatchSpacing(range(percentage, 0.0f, 0.06f))
                filter.setLineWidth(range(percentage, 0.0f, 0.006f))
            }
        }

        private inner class BulgeDistortionAdjuster(filter: GPUImageBulgeDistortionFilter) :
            Adjuster<GPUImageBulgeDistortionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
                filter.setScale(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class GlassSphereAdjuster(filter: GPUImageGlassSphereFilter) :
            Adjuster<GPUImageGlassSphereFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class HazeAdjuster(filter: GPUImageHazeFilter) :
            Adjuster<GPUImageHazeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistance(range(percentage, -0.3f, 0.3f))
                filter.setSlope(range(percentage, -0.3f, 0.3f))
            }
        }

        private inner class SphereRefractionAdjuster(filter: GPUImageSphereRefractionFilter) :
            Adjuster<GPUImageSphereRefractionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class SwirlAdjuster(filter: GPUImageSwirlFilter) :
            Adjuster<GPUImageSwirlFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setAngle(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ColorBalanceAdjuster(filter: GPUImageColorBalanceFilter) :
            Adjuster<GPUImageColorBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMidtones(
                    floatArrayOf(
                        range(percentage, 0.0f, 1.0f),
                        range(percentage / 2, 0.0f, 1.0f),
                        range(percentage / 3, 0.0f, 1.0f)
                    )
                )
            }
        }

        private inner class LevelsMinMidAdjuster(filter: GPUImageLevelsFilter) :
            Adjuster<GPUImageLevelsFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f)
            }
        }

        private inner class BilateralAdjuster(filter: GPUImageBilateralBlurFilter) :
            Adjuster<GPUImageBilateralBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f))
            }
        }

        private inner class RotateAdjuster(filter: GPUImageTransformFilter) :
            Adjuster<GPUImageTransformFilter>(filter) {
            override fun adjust(percentage: Int) {
                val transform = FloatArray(16)
                Matrix.setRotateM(transform, 0, (360 * percentage / 100).toFloat(), 0f, 0f, 1.0f)
                filter.transform3D = transform
            }
        }

        private inner class SolarizeAdjuster(filter: GPUImageSolarizeFilter) :
            Adjuster<GPUImageSolarizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class VibranceAdjuster(filter: GPUImageVibranceFilter) :
            Adjuster<GPUImageVibranceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVibrance(range(percentage, -1.2f, 1.2f))
            }
        }

        private inner class LookupAdjust(filter: GPULookUpTableFilter) :
            Adjuster<GPULookUpTableFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, -1f, 1f))
            }

        }

        private inner class WispAdjuster(filter: GPUWispFilter) : Adjuster<GPUWispFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }

        }

        private inner class WavyAdjuster(filter: GPUWavyFilter) : Adjuster<GPUWavyFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }

        }

        private inner class RainAdjuster(filter: GPURainFilter) : Adjuster<GPURainFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class SnowAdjuster(filter: GPUSnowFilter) : Adjuster<GPUSnowFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class BleachAdjuster(filter: GPUBleachFilter) :
            Adjuster<GPUBleachFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 100f))
            }
        }

        private inner class BwStrobeAdjuster(filter: GPUBwStrobeFilter) :
            Adjuster<GPUBwStrobeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class DispersionAdjuster(filter: GPUDispersionFilter) :
            Adjuster<GPUDispersionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class DrosteAdjuster(filter: GPUDrosteFilter) :
            Adjuster<GPUDrosteFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class FireAdjuster(filter: GPUFireFilter) :
            Adjuster<GPUFireFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class SpiralsAdjuster(filter: GPUSpiralsFilter) :
            Adjuster<GPUSpiralsFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }

        private inner class SplitColorAdjuster(filter: GPUSplitColorFilter) :
            Adjuster<GPUSplitColorFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 1f, 60f))
            }
        }
    }

}