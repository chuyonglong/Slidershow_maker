package com.acatapps.videomaker.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.acatapps.videomaker.application.MainApp
import com.acatapps.videomaker.utils.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class CropVideoTimeView : View {
    private val mImagePreviewPathList = ArrayList<String>()

    private var mDensity = 0f

    private var mControlHeight = 56f

    private var mControlWidth = 14f
    private var mCornerRadius = 5f

    private var mLineHeight = 2f

    private var mTopOffset = 24f
    private var mTextSize = 12f

    private val mHighlightPaint = Paint()
    private val mHighlightColor = Color.parseColor("#FF604D")

    private val mGrayPaint = Paint()
    private val mGrayColor = Color.parseColor("#66000000")

    private val mTextPaint = Paint()

    private val mLeftRegionController = Region()
    private val mRightRegionController = Region()

    private var mLeftDx = 0f
    private var mRightDx = 100f

    private var mStartProgress = 0f
    private var mEndProgress = 100f

    private var mIsSwipeLeft = false
    private var mIsSwipeRight = false

    private var mImageBg: Bitmap? = null

    private val mBitmapHashMap = HashMap<String, Bitmap>()

    private var mMaxValue = 0

    var onChangeListener: OnChangeListener? = null

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attributes: AttributeSet?) {
        mDensity = Utils.density(context)

        mControlHeight *= mDensity
        mControlWidth *= mDensity
        mLineHeight *= mDensity
        mCornerRadius *= mDensity
        mTopOffset *= mDensity
        mTextSize *= mDensity
        mHighlightPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mHighlightColor
        }

        mGrayPaint.apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mGrayColor
        }

        mTextPaint.apply {
            color = Color.parseColor("#455A64")
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mTextSize
        }

    }


    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec)

        mRightDx = w - mControlWidth
    }


    fun loadImage(imagePathList: ArrayList<String>) {
        Logger.e("crop w = $width")
        Observable.fromCallable<String> {
            val bitmap = Bitmap.createBitmap(
                (width - 2 * mControlWidth).toInt(),
                (Utils.density(context) * 56).toInt(),
                Bitmap.Config.ARGB_8888
            )
            for (i in 0 until bitmap.width)
                for (j in 0 until bitmap.height) {
                    bitmap.setPixel(i, j, Color.WHITE)
                }
            mImageBg = bitmap
            val canvas = Canvas(mImageBg!!)
            var dx = 0f
            canvas.apply {
                while (dx < width) {
                    val bitmap = getBitmap(imagePathList[(dx * imagePathList.size / width).toInt()])
                    drawBitmap(bitmap, dx, 0f, null)
                    dx += mDensity * 56
                }
            }
            return@fromCallable ""
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {
                }

                override fun onComplete() {
                    invalidate()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun loadImage(videoPath: String, width: Int) {
        setMax(Utils.getVideoDuration(videoPath))
        Observable.fromCallable<String> {
            val bitmap = Bitmap.createBitmap(
                (width - 2 * mControlWidth).toInt(),
                (Utils.density(context) * 56).toInt(),
                Bitmap.Config.ARGB_8888
            )
            for (i in 0 until bitmap.width)
                for (j in 0 until bitmap.height) {
                    bitmap.setPixel(i, j, Color.WHITE)
                }
            mImageBg = bitmap
            val canvas = Canvas(mImageBg!!)

            var dx = 0f
            val media = MediaMetadataRetriever()
            media.setDataSource(videoPath)

            canvas.apply {
                while (dx < width) {
                    Logger.e("time = ${(mMaxValue * dx / width)}")
                    val extractedImage = media.getFrameAtTime(
                        (mMaxValue * 1000 * dx / width).roundToLong(),
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                    )
                    extractedImage?.also {
                        drawBitmap(
                            drawResizedBitmap(extractedImage),
                            dx,
                            0f,
                            null
                        )
                    }
                    dx += mDensity * 56
                }
            }
            return@fromCallable ""
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {
                }

                override fun onComplete() {
                    invalidate()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun loadVideoImagePreview(videoPathList: ArrayList<String>, width: Int) {

        Observable.fromCallable<String> {
            val bitmap = Bitmap.createBitmap(
                (width - 2 * mControlWidth).toInt(),
                (Utils.density(context) * 56).toInt(),
                Bitmap.Config.ARGB_8888
            )
            for (i in 0 until bitmap.width)
                for (j in 0 until bitmap.height) {
                    bitmap.setPixel(i, j, Color.WHITE)
                }
            mImageBg = bitmap
            val canvas = Canvas(mImageBg!!)

            var dx = 0f
            val numberImage = (width / (Utils.density(MainApp.getContext()) * 56)).toInt()
            Logger.e("number image = $numberImage")
            for (index in 0 until numberImage) {
                val targetTimeMs = mMaxValue * index / numberImage
                var currentTotal = 0
                for (videoPath in videoPathList) {
                    val videoDuration = Utils.getVideoDuration(videoPath)
                    if (currentTotal + videoDuration > targetTimeMs) {

                        canvas.apply {
                            val media = MediaMetadataRetriever()
                            media.setDataSource(videoPath)
                            val extractedImage = media.getFrameAtTime(
                                (targetTimeMs - currentTotal).toLong() * 1000,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                            )
                            extractedImage?.also {
                                drawBitmap(
                                    drawResizedBitmap(extractedImage),
                                    dx,
                                    0f,
                                    null
                                )
                            }
                            dx += (Utils.density(MainApp.getContext()) * 56)
                        }
                        break
                    } else {
                        currentTotal += videoDuration
                    }
                }
            }
            canvas.apply {
                val videoPath = videoPathList[videoPathList.size - 1]
                val media = MediaMetadataRetriever()
                media.setDataSource(videoPath)
                val extractedImage = media.getFrameAtTime(
                    (Utils.getVideoDuration(videoPath)).toLong() * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                extractedImage?.also { drawBitmap(drawResizedBitmap(extractedImage), dx, 0f, null) }
                dx += (Utils.density(MainApp.getContext()) * 56)
            }
            return@fromCallable ""
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<String> {
                override fun onNext(t: String) {
                }

                override fun onComplete() {
                    invalidate()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {}
            })
    }

    private fun getBitmap(path: String): Bitmap {
        return if (mBitmapHashMap[path] == null) {
            val bitmap = drawResizedBitmap(path)
            mBitmapHashMap[path] = bitmap
            bitmap
        } else {
            mBitmapHashMap[path]!!
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawImageBg(canvas)
        drawLeftGrayArea(canvas)
        drawRightGrayArea(canvas)
        drawTopAndBottomLine(canvas)
        drawLeftControl(canvas)
        drawRightControl(canvas)
        drawTimeText(canvas)
    }

    private fun drawImageBg(canvas: Canvas?) {
        mImageBg?.let {
            val bitmap = it
            canvas?.drawBitmap(bitmap, mControlWidth, mTopOffset, null)
        }
    }

    private fun drawLeftGrayArea(canvas: Canvas?) {
        val top = mTopOffset
        val leftDx = (mStartProgress * (width - mControlWidth)) / 100
        mRightDx = (mEndProgress / 100) * (width - mControlWidth)
        canvas?.drawRect(
            mControlWidth,
            top,
            leftDx + mControlWidth / 2f,
            mControlHeight + top,
            mGrayPaint
        )
    }

    private fun drawRightGrayArea(canvas: Canvas?) {
        val top = mTopOffset
        val rightDx = (mEndProgress * (width - mControlWidth)) / 100
        canvas?.drawRect(rightDx, 0f + top, width - mControlWidth, mControlHeight + top, mGrayPaint)
    }

    private fun drawLeftControl(canvas: Canvas?) {
        canvas?.drawPath(getControlLeftPath().apply {
            val top = mTopOffset
            val leftDx = mStartProgress * (width - mControlWidth) / 100
            offset(leftDx, top)
            val boundRecF = RectF()
            computeBounds(boundRecF, true)
            mLeftRegionController.setPath(
                this,
                Region(
                    boundRecF.left.toInt(),
                    boundRecF.top.toInt(),
                    boundRecF.right.toInt(),
                    boundRecF.bottom.toInt()
                )
            )
        }, mHighlightPaint)
    }

    private fun drawRightControl(canvas: Canvas?) {
        canvas?.drawPath(getControlRightPath().apply {
            val top = mTopOffset
            val rightDx = mEndProgress * (width - mControlWidth) / 100
            offset(rightDx, top)
            val boundRecF = RectF()
            computeBounds(boundRecF, true)
            mRightRegionController.setPath(
                this,
                Region(
                    boundRecF.left.toInt(),
                    boundRecF.top.toInt(),
                    boundRecF.right.toInt(),
                    boundRecF.bottom.toInt()
                )
            )
        }, mHighlightPaint)
    }

    private fun drawTopAndBottomLine(canvas: Canvas?) {
        val leftOffset = mStartProgress / 100 * (width - mControlWidth) + mControlWidth / 2f
        val rightOffset = mEndProgress / 100 * (width - mControlWidth) + mControlWidth / 2f
        val top = mTopOffset
        canvas?.drawRect(leftOffset, top, rightOffset, mLineHeight + top, mHighlightPaint)
        canvas?.drawRect(
            leftOffset,
            height - mLineHeight,
            rightOffset,
            height.toFloat(),
            mHighlightPaint
        )
    }

    private fun drawTimeText(canvas: Canvas?) {
        val startValue = mMaxValue * mStartProgress / 100
        val endValue = mMaxValue * mEndProgress / 100
        val startText = Utils.convertSecToTimeString((startValue / 1000).roundToInt())
        val endText = Utils.convertSecToTimeString((endValue / 1000).roundToInt())

        canvas?.drawText(
            startText,
            0f,
            getTextHeight(startText, mTextPaint) + 4 * mDensity,
            mTextPaint
        )
        canvas?.drawText(
            endText,
            width - getTextWidth(endText, mTextPaint) - 10f,
            getTextHeight(startText, mTextPaint) + 4 * mDensity,
            mTextPaint
        )
    }

    private fun getControlLeftPath(): Path {
        val rectF = RectF()
        return Path().apply {

            moveTo(mControlWidth, 0f)
            lineTo(mControlWidth, mControlHeight)
            lineTo(mCornerRadius, mControlHeight)
            lineTo(mCornerRadius, mControlHeight - mCornerRadius)
            lineTo(0f, mControlHeight - mCornerRadius)
            lineTo(0f, mCornerRadius)
            lineTo(mCornerRadius, mCornerRadius)
            lineTo(mCornerRadius, 0f)
            lineTo(mControlWidth, 0f)

            moveTo(mCornerRadius, mCornerRadius)
            rectF.set(0f, 0f, 2 * mCornerRadius, 2 * mCornerRadius)
            arcTo(rectF, -90f, -90f)

            moveTo(mControlWidth - mCornerRadius, mControlHeight - mCornerRadius)
            rectF.set(0f, mControlHeight - 2 * mCornerRadius, 2 * mCornerRadius, mControlHeight)
            arcTo(rectF, 90f, 90f)

            close()
        }
    }

    private fun getControlRightPath(): Path {
        val path = getControlLeftPath()
        val matrix = Matrix().apply {
            postRotate(180f, mControlWidth / 2f, mControlHeight / 2f)
        }
        path.apply {
            transform(matrix)

        }

        return path
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (mLeftRegionController.contains(event!!.x.toInt(), event!!.y.toInt())) {
                mIsSwipeLeft = true
                mIsSwipeRight = false
            } else if (mRightRegionController.contains(event!!.x.toInt(), event!!.y.toInt())) {
                mIsSwipeLeft = false
                mIsSwipeRight = true
            } else {
                mIsSwipeLeft = false
                mIsSwipeRight = false
            }
        } else if (event?.action == MotionEvent.ACTION_UP) {
            if (mIsSwipeLeft) onChangeListener?.onUpLeft(mStartProgress * mMaxValue / 100)
            else if (mIsSwipeRight) onChangeListener?.onUpRight(mEndProgress * mMaxValue / 100)
            mIsSwipeLeft = false
            mIsSwipeRight = false
        }

        if (mIsSwipeLeft) {
            if (event?.action == MotionEvent.ACTION_MOVE)
                onSwipeLeft(event.rawX)

        } else if (mIsSwipeRight) {
            if (event?.action == MotionEvent.ACTION_MOVE)
                onSwipeRight(event.rawX)
        }

        return true
    }

    private fun onSwipeLeft(rawX: Float) {
        var dx = rawX - x
        if (dx <= 0f) dx = 0f
        else if (dx >= mRightDx - mControlWidth) dx = mRightDx - mControlWidth
        if (mLeftDx != dx) {
            mLeftDx = dx
            mStartProgress = mLeftDx * 100 / (width - mControlWidth)
            onChangeListener?.onSwipeLeft(mStartProgress * mMaxValue / 100f)
            invalidate()
        }
    }

    private fun onSwipeRight(rawX: Float) {
        var dx = rawX - x
        if (dx >= width - mControlWidth) dx = width - mControlWidth
        else if (dx <= mLeftDx + mControlWidth) dx = mLeftDx + mControlWidth
        if (mRightDx != dx) {
            mRightDx = dx
            mEndProgress = mRightDx * 100 / (width - mControlWidth)
            onChangeListener?.onSwipeRight(mEndProgress * mMaxValue / 100f)
            invalidate()
        }
    }

    private fun drawResizedBitmap(imagePath: String): Bitmap {
        val size = (Utils.density(context) * 56).toInt()
        val rawBitmap = Utils.getBitmapFromFilePath(imagePath)
        val outBitmapSize: Int
        outBitmapSize = if (rawBitmap.width < size && rawBitmap.height < size) {
            max(rawBitmap.width, rawBitmap.height)
        } else {
            size
        }

        var rawBitmapResized: Bitmap? =
            Utils.resizeMatchBitmap(rawBitmap, outBitmapSize.toFloat())

        val resizedBitmapWithBg =
            Bitmap.createBitmap(outBitmapSize, outBitmapSize, Bitmap.Config.ARGB_8888)

        Canvas(resizedBitmapWithBg).apply {
            drawARGB(255, 0, 0, 0)
            drawBitmap(
                rawBitmapResized!!,
                (outBitmapSize - rawBitmapResized!!.width) / 2f,
                (outBitmapSize - rawBitmapResized!!.height) / 2f,
                null
            )
        }
        rawBitmapResized = null
        return resizedBitmapWithBg
    }

    private fun drawResizedBitmap(bitmap: Bitmap): Bitmap {
        val size = (Utils.density(context) * 56).toInt()
        val rawBitmap = bitmap
        val outBitmapSize: Int
        outBitmapSize = if (rawBitmap.width < size && rawBitmap.height < size) {
            max(rawBitmap.width, rawBitmap.height)
        } else {
            size
        }

        var rawBitmapResized: Bitmap? =
            Utils.resizeMatchBitmap(rawBitmap, outBitmapSize.toFloat())

        val resizedBitmapWithBg =
            Bitmap.createBitmap(outBitmapSize, outBitmapSize, Bitmap.Config.ARGB_8888)

        Canvas(resizedBitmapWithBg).apply {
            drawARGB(255, 0, 0, 0)
            drawBitmap(
                rawBitmapResized!!,
                (outBitmapSize - rawBitmapResized!!.width) / 2f,
                (outBitmapSize - rawBitmapResized!!.height) / 2f,
                null
            )
        }
        rawBitmapResized = null
        return resizedBitmapWithBg
    }

    fun setMax(timeMSec: Int) {
        mMaxValue = timeMSec
        invalidate()
    }

    fun setStartAndEnd(startTimeMilSec: Int, endTimeSec: Int) {
        mStartProgress = startTimeMilSec * 100f / mMaxValue
        mEndProgress = endTimeSec * 100f / mMaxValue


        if (mEndProgress <= 0f) mEndProgress = 0f
        else if (mEndProgress >= 100f) mEndProgress = 100f

        if (mStartProgress <= 0f) mStartProgress = 0f
        else if (mStartProgress >= 100f) mStartProgress = 90f

        invalidate()
    }

    private fun getTextWidth(text: String, paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width().toFloat()
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    interface OnChangeListener {
        fun onSwipeLeft(startTimeMilSec: Float)
        fun onUpLeft(startTimeMilSec: Float)
        fun onSwipeRight(endTimeMilSec: Float)
        fun onUpRight(endTimeMilSec: Float)
    }

}