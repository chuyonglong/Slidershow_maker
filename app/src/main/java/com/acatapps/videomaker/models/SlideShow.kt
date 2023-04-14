package com.acatapps.videomaker.models

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.Serializable
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sin

class SlideShow(private val mImagePathList: ArrayList<String>):Serializable {

    private val mSlides = arrayListOf<Slide>()

    private var mFPS = 30
    private var mTimerPerSlide = 5000

    private var mDelayTime = 3000
    val delayTimeSec get() = mDelayTime/1000

    private var mTransitionTime = 2000
    private var mNumberFramePerSlide = mFPS * mTimerPerSlide

    private var mTotalFrame = 0
    val totalFrame get() = mTotalFrame

    private var mTotalTimeMiniSec = 0

    private var mNumberSlide = 0

    private var mCurrentSlide: SlideRenderData
    private var mNextSlide: SlideRenderData
    private var mBackupSlide: SlideRenderData

    private var mCurrentSlideIndex = 0

    private val mBlackBitmap:Bitmap

    private val mBitmapHashMap = HashMap<String, Bitmap>()

    private val mBitmapHashMapV2 = HashMap<String, String>()
    val bitmapHashMap get() = mBitmapHashMapV2

    var isReady = false

    init {

        mBlackBitmap = Utils.getBlackBitmap()
        initSlideData(mImagePathList)

        mCurrentSlide = SlideRenderData(mSlides[0], getBitmapFromHashMapV2(mSlides[0].imagePath))
        mNextSlide = SlideRenderData(mSlides[1], getBitmapFromHashMapV2(mSlides[1].imagePath))
        mCurrentSlideIndex = 2
        if (mSlides.size > 2) {
            mBackupSlide = SlideRenderData(mSlides[2], getBitmapFromHashMapV2(mSlides[2].imagePath))
        } else {
            mBackupSlide = getBlackSlide()
        }
        isReady = true
    }

    fun updateTime(delayTime:Int) :Boolean{
        if(delayTime == mDelayTime) return false
        mDelayTime = delayTime*1000
        mTimerPerSlide= mDelayTime+mTransitionTime
        mNumberFramePerSlide = mFPS * mTimerPerSlide/1000
        initSlideData(mImagePathList)
        isReady = false
      return true
    }

    private fun initSlideData(imagesPath: ArrayList<String>) {
        mSlides.clear()
        for (item in imagesPath) {
            mSlides.add(Slide(View.generateViewId(), item))
        }
        mNumberSlide = imagesPath.size
        mTotalFrame = mNumberFramePerSlide*mNumberSlide
        mTotalTimeMiniSec = mNumberSlide*mTimerPerSlide
    }


    fun getFrameByVideoTime(timeMiniSec:Int): FrameData? {

        if(timeMiniSec >= mTotalTimeMiniSec) {
            return null
        }

        var time = timeMiniSec
        if(timeMiniSec > mTotalTimeMiniSec) time = mTotalTimeMiniSec

        val targetSlideIndex = time/(mTimerPerSlide)

        val surplus = time%mTimerPerSlide
        val progress:Float
        if(surplus < mDelayTime) {
            progress = 0f
        } else {
            progress = (surplus - mDelayTime)/mTransitionTime.toFloat()
        }

        val zoom = 1f

        val targetSlide = mSlides[targetSlideIndex]
        if(mCurrentSlide.slide.slideId != targetSlide.slideId) {
            mCurrentSlide = mNextSlide
            mNextSlide = mBackupSlide
            updateBackupSlide()
        }
        return FrameData(mCurrentSlide.imageBitmap, mNextSlide.imageBitmap, progress, mCurrentSlide.slide.slideId, zoom)
    }

    fun getFrameByVideoTimeForRender(timeMiniSec:Int): FrameData? {

        if(timeMiniSec >= mTotalTimeMiniSec) {
            return null
        }

        var time = timeMiniSec
        if(timeMiniSec > mTotalTimeMiniSec) time = mTotalTimeMiniSec

        val surplus = time%mTimerPerSlide
        val progress:Float
        if(surplus < mDelayTime) {
            progress = 0f
        } else {
            progress = (surplus - mDelayTime)/mTransitionTime.toFloat()
        }

        val zoom = 1f

        var targetSlideIndex = (timeMiniSec-1)/(mTimerPerSlide)
        if(targetSlideIndex <= 0) targetSlideIndex = 0
        else if(targetSlideIndex >= mSlides.size-1) targetSlideIndex = mSlides.size-1
        mCurrentSlide = SlideRenderData(mSlides[targetSlideIndex], getBitmapFromHashMapV2(mSlides[targetSlideIndex].imagePath))
        if(targetSlideIndex < (mSlides.size - 1)) {
            mNextSlide = SlideRenderData(mSlides[targetSlideIndex+1], getBitmapFromHashMapV2(mSlides[targetSlideIndex+1].imagePath))
        } else {
            mNextSlide = getBlackSlide()
        }
        return FrameData(mCurrentSlide.imageBitmap, mNextSlide.imageBitmap, progress, mCurrentSlide.slide.slideId, zoom)
    }

    fun getFrameDataByNumberFrame(numberFrame: Int): FrameData? {
        if (numberFrame > mTotalFrame) return null
        val targetSlideIndex = numberFrame / mNumberFramePerSlide
        val surplus = numberFrame % (mNumberFramePerSlide)
        val progress: Float
        if (surplus < mDelayTime * mFPS) {
            progress = 0f
        } else {
            progress = ((surplus - (mDelayTime * mFPS)).toFloat() / (mTransitionTime * mFPS))
        }
        val zoomProgress: Float

        val j = numberFrame % (10 * mFPS * 4)
        zoomProgress = 0.9f + 0.1f * (abs(sin(PI * 2 * j.toFloat() / (5 * 32 * 4).toFloat()))).toFloat()


        val targetSlide = mSlides[targetSlideIndex]
        if (targetSlide.imagePath != mCurrentSlide.slide.imagePath) {
            mCurrentSlide = mNextSlide
            mNextSlide = mBackupSlide
            mCurrentSlideIndex++
            updateBackupSlide()
            return FrameData(
                mCurrentSlide.imageBitmap,
                mNextSlide.imageBitmap,
                progress,
                mCurrentSlide.slide.slideId,
                zoomProgress
            )
        } else {
            return FrameData(
                mCurrentSlide.imageBitmap,
                mNextSlide.imageBitmap,
                progress,
                mCurrentSlide.slide.slideId,
                zoomProgress
            )
        }
    }

    fun repeat() {
        isReady = false
        mCurrentSlide = SlideRenderData(mSlides[0], getBitmapFromHashMapV2(mSlides[0].imagePath))
        mNextSlide = SlideRenderData(mSlides[1], getBitmapFromHashMapV2(mSlides[1].imagePath))
        mCurrentSlideIndex = 2
        if (mSlides.size > 2) {
            mBackupSlide = SlideRenderData(mSlides[2], getBitmapFromHashMapV2(mSlides[2].imagePath))
        } else {
            mBackupSlide = getBlackSlide()
        }

        isReady = true
    }

    private fun updateBackupSlide() {
        Thread{
            if(mNextSlide.slide.imagePath == "none") {
                mBackupSlide = SlideRenderData(mSlides[0], getBitmapFromHashMapV2(mSlides[0].imagePath))
            } else {
                for(index in 0..mSlides.size) {
                    val item = mSlides[index]
                    if(item.slideId == mNextSlide.slide.slideId) {
                        if(index == mSlides.size-1) {
                            mBackupSlide = getBlackSlide()
                        } else {

                            mBackupSlide = SlideRenderData(mSlides[index+1], getBitmapFromHashMapV2(mSlides[index+1].imagePath))
                        }
                        break
                    }
                }
            }

        }.start()
    }

    fun seekTo(timeMilSec:Int, onComplete:()->Unit) {

        Observable.fromCallable<String> {
            var targetSlideIndex = (timeMilSec-1)/(mTimerPerSlide)
            if(targetSlideIndex <= 0) targetSlideIndex = 0
             else if(targetSlideIndex >= mSlides.size-1) targetSlideIndex = mSlides.size-1
            Logger.e("target slide = $targetSlideIndex")
            mCurrentSlide = SlideRenderData(mSlides[targetSlideIndex], getBitmapFromHashMapV2(mSlides[targetSlideIndex].imagePath))
            if(targetSlideIndex < (mSlides.size - 1)) {
                mNextSlide = SlideRenderData(mSlides[targetSlideIndex+1], getBitmapFromHashMapV2(mSlides[targetSlideIndex+1].imagePath))
            } else {
                mNextSlide = getBlackSlide()
            }
            return@fromCallable ""
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {

                }

                override fun onComplete() {
                    updateBackupSlide()
                    onComplete.invoke()
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {}
            })
    }

    private fun drawResizedBitmap(imagePath: String): Bitmap {
        val screenW = 1080
        val rawBitmap = Utils.getBitmapFromFilePath(imagePath)
        val outBitmapSize: Int
        if (rawBitmap.width < screenW && rawBitmap.height < screenW) {
            outBitmapSize = max(rawBitmap.width, rawBitmap.height)
        } else {
            outBitmapSize = screenW
        }

        val blurBgBitmap = Utils.blurBitmapV2(
            Utils.resizeMatchBitmap(
                rawBitmap,
                outBitmapSize.toFloat() + 100
            ), 20
        )

        val rawBitmapResized = Utils.resizeWrapBitmap(rawBitmap, outBitmapSize.toFloat())

        val resizedBitmapWithBg =
            Bitmap.createBitmap(outBitmapSize, outBitmapSize, Bitmap.Config.ARGB_8888)

        Canvas(resizedBitmapWithBg).apply {
            drawARGB(255, 0, 0, 0)
            blurBgBitmap?.let {
                drawBitmap(
                    it,
                    (outBitmapSize - it.width) / 2f,
                    (outBitmapSize - it.height) / 2f,
                    null
                )
            }
            drawBitmap(
                rawBitmapResized,
                (outBitmapSize - rawBitmapResized.width) / 2f,
                (outBitmapSize - rawBitmapResized.height) / 2f,
                null
            )
        }

        return resizedBitmapWithBg
    }

    private fun getBitmapFromHashMap(imagePath: String):Bitmap {
        if(mBitmapHashMap[imagePath] == null) {
            val bitmap = drawResizedBitmap(imagePath)
            mBitmapHashMap[imagePath] = bitmap
            return bitmap
        } else {
            return mBitmapHashMap[imagePath]!!
        }
    }

    private fun getBitmapFromHashMapV2(imagePath: String):Bitmap {
        if(mBitmapHashMapV2[imagePath] == null) {
           val bitmap = drawResizedBitmap(imagePath)
            val outFilePath = Utils.saveBitmapToTempData(bitmap)
            mBitmapHashMapV2[imagePath] = outFilePath
            return bitmap
        } else {
            return Utils.getBitmapFromFilePath(mBitmapHashMapV2[imagePath]!!)
        }
    }

    fun getTotalDuration():Int = mTotalTimeMiniSec

    private fun getBlackSlide() : SlideRenderData {
        return SlideRenderData(Slide(View.generateViewId(), "none"), mBlackBitmap)
    }

    fun getImagePathList():ArrayList<String> {
        return mImagePathList
    }

}