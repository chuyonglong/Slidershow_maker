package com.acatapps.videomaker.custom_view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.models.BitmapWithIndexData
import com.acatapps.videomaker.models.RecordedData
import com.acatapps.videomaker.models.RecordedDataModel
import com.acatapps.videomaker.utils.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.max
import kotlin.math.roundToInt

class VideoTimelineView : View {

    private val mImagePreviewPathList = ArrayList<String>()
    private var mBitmapList = ArrayList<BitmapWithIndexData>()
    private var mDx = 0f
    private var mCenterPoint = 0f
    private var mCenterLineWidth = 2f
    private var mCenterLineHeight = 88f
    private var mDensity = 1f
    private val mNormalPaint = Paint()
    private var mIsRecording = false
    private var mMaxValueTimeMilSec = 0
    private val mTextPaint = Paint()
    private var mTimelineSize = 1
    private var mTimelineHeight = 56f
    private val mRecordedDataList = ArrayList<RecordedData>()
    private val mMarkedPaint = Paint().apply {
        color = Color.parseColor("#66ff0000")
    }
    var onMoveCallback: ((Int) -> Unit)? = null
    var onStopRecording: ((Int) -> Unit)? = null
    var onStropSuccess: ((RecordedData) -> Unit)? = null
    var onStartFail: (() -> Unit)? = null
    var onUpCallback:((Int)->Unit)? = null
    private var mTextSize = 12f

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attributes: AttributeSet?) {

        mDensity = Utils.density(context)
        mCenterLineHeight *= mDensity
        mCenterLineWidth *= mDensity
        mTimelineHeight *= mDensity

        mTextSize *= mDensity

        mNormalPaint.apply {
            color = Color.parseColor("#ff604d")
            isAntiAlias = true
        }

        mTextPaint.apply {
            isAntiAlias = true
            color = Color.BLACK
            textSize = mTextSize
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mCenterPoint = w / 2f
        mDx = mCenterPoint
    }

    override fun onDraw(canvas: Canvas?) {
        if (mTimelineSize > 0)
            for (index in 0 until mBitmapList.size) {
                val item = mBitmapList[index]
                canvas?.drawBitmap(
                    item.bitmap,
                    mDx + mDensity * 56 * index,
                    (mCenterLineHeight - item.bitmap.height) / 2f,
                    null
                )
            }
        if (mTimelineSize > 0)
            for (item in mRecordedDataList) {
                if (mTimelineSize > 0)
                    drawMark(canvas, item.startMs, item.endMs)
            }
        if (mIsRecording) {
            drawRecordingMark(canvas)
        }
        drawCenterLine(canvas)
        drawText(canvas)
        drawCurrentTimeText(canvas)
    }

    private fun drawCenterLine(canvas: Canvas?) {
        val circleR = 6 * mDensity
        canvas?.drawRect(
            mCenterPoint - mCenterLineWidth / 2f,
            circleR,
            mCenterPoint + mCenterLineWidth / 2f,
            mCenterLineHeight - circleR,
            mNormalPaint
        )
        canvas?.drawCircle(mCenterPoint, circleR, circleR, mNormalPaint)
        canvas?.drawCircle(mCenterPoint, mCenterLineHeight - circleR, circleR, mNormalPaint)
    }

    private fun drawMark(canvas: Canvas?, startMs: Int, endMs: Int) {
        val startOffset = (startMs * mTimelineSize) / mMaxValueTimeMilSec
        val endOffset = (endMs * mTimelineSize) / mMaxValueTimeMilSec
        canvas?.drawRect(
            mDx + startOffset,
            (mCenterLineHeight - mTimelineHeight) / 2f,
            mDx + startOffset + (endOffset - startOffset),
            (mCenterLineHeight + mTimelineHeight) / 2f,
            mMarkedPaint
        )
    }

    private fun drawRecordingMark(canvas: Canvas?) {
        val startOffset = (mStartRecordingOffset * mTimelineSize) / mMaxValueTimeMilSec
        val endOffset = (mEndRecordingOffset * mTimelineSize) / mMaxValueTimeMilSec
        canvas?.drawRect(
            mDx + startOffset,
            (mCenterLineHeight - mTimelineHeight) / 2f,
            mDx + startOffset + (endOffset - startOffset),
            (mCenterLineHeight + mTimelineHeight) / 2f,
            mMarkedPaint
        )
    }

    private fun drawText(canvas: Canvas?) {
        val totalText = context.getString(R.string.total)
        val text =
            "$totalText:${Utils.convertSecToTimeString((mMaxValueTimeMilSec / 1000f).roundToInt())}"
        val textW = Utils.getTextWidth(text, mTextPaint)
        val textH = Utils.getTextHeight(text, mTextPaint)
        canvas?.drawText(text, width - textW - 12 * mDensity, mCenterLineHeight + textH, mTextPaint)
    }

    private fun drawCurrentTimeText(canvas: Canvas?) {
        val text = Utils.convertSecToTimeString(getCurrentTime().toInt() / 1000)
        val textW = Utils.getTextWidth(text, mTextPaint)
        val textH = Utils.getTextHeight(text, mTextPaint)
        canvas?.drawText(text, width / 2 - textW / 2, mCenterLineHeight + textH, mTextPaint)
    }


    fun loadImage(imagePathList: ArrayList<String>) {
        Observable.fromCallable<String> {
            if (imagePathList.size >= 10) {
                val timePerImage = mMaxValueTimeMilSec / 10
                for (index in 0 until 10) {
                    mTimelineSize += (56 * mDensity).toInt()
                    val bitmap =
                        drawResizedBitmap(imagePathList[(index * timePerImage) * imagePathList.size / mMaxValueTimeMilSec])
                    mBitmapList.add(BitmapWithIndexData(index, bitmap))
                    Logger.e("path = ${imagePathList[index]}")
                }
            } else {
                for (index in 0 until imagePathList.size) {
                    mTimelineSize += (56 * mDensity).toInt()
                    val bitmap = drawResizedBitmap(imagePathList[index])
                    mBitmapList.add(BitmapWithIndexData(index, bitmap))
                    Logger.e("path = ${imagePathList[index]}")
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

    fun loadImageVideo(videoPathList: ArrayList<String>) {
        Observable.fromCallable<String> {

            val timePerImage = mMaxValueTimeMilSec / 10
            for (index in 0 until 10) {
                val targetTimeMs = index * timePerImage
                mTimelineSize += (56 * mDensity).toInt()
                for (videoPath in videoPathList) {
                    val videoDuration = Utils.getVideoDuration(videoPath)
                    var currentTotal = 0
                    if (currentTotal + videoDuration > targetTimeMs) {
                        val media = MediaMetadataRetriever()
                        media.setDataSource(videoPath)
                        val extractedImage = media.getFrameAtTime(
                            (targetTimeMs - currentTotal).toLong() * 1000,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                        )
                        extractedImage?.also { val bitmap = drawResizedBitmap(extractedImage)
                            mBitmapList.add(BitmapWithIndexData(index, bitmap)) }

                        break
                    } else {
                        currentTotal += videoDuration
                    }
                }
                Logger.e("path = ${videoPathList[index]}")
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

    private var mTouchPointX = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mIsRecording) return false
        if (event?.action == MotionEvent.ACTION_DOWN) {
            updateTouchPoint(event?.rawX)
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            onMoveView(event?.rawX)
        } else if(event?.action == MotionEvent.ACTION_CANCEL || event?.action == MotionEvent.ACTION_UP) {
            onUpCallback?.invoke(getCurrentTime().toInt())
        }

        return true
    }

    private fun updateTouchPoint(rawX: Float) {
        mTouchPointX = rawX
    }

    private var mCurrentTimeMs = 0
    private fun onMoveView(rawX: Float) {

        var dx = mDx + rawX - mTouchPointX
        if ((dx + mTimelineSize) <= mCenterPoint) {
            dx = mCenterPoint - mTimelineSize
        } else if (dx >= mCenterPoint) {
            dx = mCenterPoint
        }
        mDx = dx
        onMoveCallback?.invoke(getCurrentTime().roundToInt())
        mCurrentTimeMs = getCurrentTime().roundToInt()
        updateTouchPoint(rawX)
        invalidate()
    }


    private fun drawResizedBitmap(imagePath: String): Bitmap {
        val size = (Utils.density(context) * 56).toInt()

        val rawBitmap = Utils.getBitmapFromFilePath(imagePath)
        var outBitmapSize: Int
        outBitmapSize = if (rawBitmap.width < size && rawBitmap.height < size) {
            max(rawBitmap.width, rawBitmap.height)
        } else {
            size
        }
        outBitmapSize = size
        Logger.e("out size = $outBitmapSize")
        val rawBitmapResized = Utils.resizeMatchBitmap(rawBitmap, outBitmapSize.toFloat())

        val resizedBitmapWithBg =
            Bitmap.createBitmap(outBitmapSize, outBitmapSize, Bitmap.Config.ARGB_8888)

        Canvas(resizedBitmapWithBg).apply {
            drawARGB(255, 0, 0, 0)
            drawBitmap(
                rawBitmapResized,
                (outBitmapSize - rawBitmapResized.width) / 2f,
                (outBitmapSize - rawBitmapResized.height) / 2f,
                null
            )
        }

        return resizedBitmapWithBg
    }
    private fun drawResizedBitmap(bitmap: Bitmap): Bitmap {
        val size = (Utils.density(context) * 56).toInt()

        val rawBitmap = bitmap
        var outBitmapSize: Int
        outBitmapSize = if (rawBitmap.width < size && rawBitmap.height < size) {
            max(rawBitmap.width, rawBitmap.height)
        } else {
            size
        }
        outBitmapSize = size
        Logger.e("out size = $outBitmapSize")
        val rawBitmapResized = Utils.resizeMatchBitmap(rawBitmap, outBitmapSize.toFloat())

        val resizedBitmapWithBg =
            Bitmap.createBitmap(outBitmapSize, outBitmapSize, Bitmap.Config.ARGB_8888)

        Canvas(resizedBitmapWithBg).apply {
            drawARGB(255, 0, 0, 0)
            drawBitmap(
                rawBitmapResized,
                (outBitmapSize - rawBitmapResized.width) / 2f,
                (outBitmapSize - rawBitmapResized.height) / 2f,
                null
            )
        }

        return resizedBitmapWithBg
    }
    fun setMaxValue(maxValue: Int) {
        mMaxValueTimeMilSec = maxValue
    }


    private var mStartRecordingOffset = 0f
    private var mEndRecordingOffset = 0f
    private var mStartRecordingTime = 0L
    fun startRecording() {
        if (checkEnd(getCurrentTime().roundToInt())) {
            onStartFail?.invoke()
            return
        }
        mIsRecording = true
        mStartRecordingOffset = getCurrentTime()
        mStartRecordingTime = System.currentTimeMillis()
    }

    fun stopRecording(outPath: String) {
        mIsRecording = false
        mEndRecordingOffset = getCurrentTime()
        val recordedData = RecordedData(
            outPath,
            mStartRecordingOffset.roundToInt(),
            mEndRecordingOffset.roundToInt()
        )
        mRecordedDataList.add(recordedData)
        mStartRecordingOffset = 0f
        mEndRecordingOffset = 0f
        onStropSuccess?.invoke(recordedData)
        invalidate()
    }

    fun drawAndMove() {
        if (!mIsRecording) return
        val deltaTime = 40
        mStartRecordingTime = System.currentTimeMillis()
        val distance = (deltaTime.toFloat() * mTimelineSize / mMaxValueTimeMilSec)
        mDx -= distance


        mEndRecordingOffset = getCurrentTime()
        if (checkEnd(mEndRecordingOffset.roundToInt())) {
            onStopRecording?.invoke(getCurrentTime().roundToInt())
        }
        onMoveCallback?.invoke(getCurrentTime().roundToInt())
        invalidate()
    }


    private fun checkEnd(endOffset: Int): Boolean {
        if (getCurrentTime() >= mMaxValueTimeMilSec - 10) return true
        for (item in mRecordedDataList) {
            if (endOffset >= item.startMs - 10 && endOffset < item.endMs) return true
        }
        return false
    }

    private fun getBlackBitmap(): Bitmap {
        val size = (Utils.density(context) * 56).toInt()
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (i in 0 until bitmap.width)
            for (j in 0 until bitmap.height)
                bitmap.setPixel(i, j, Color.BLACK)
        return bitmap
    }

    fun deleteRecord(path: String) {
        for (item in mRecordedDataList) {
            if (item.recordFilePath == path) {
                mRecordedDataList.remove(item)
                invalidate()
                return
            }
        }
    }

    private fun getCurrentTime(): Float =
        ((mCenterPoint - mDx) * mMaxValueTimeMilSec / mTimelineSize)

    fun moveTo(timeMs: Int) {
        mDx = mCenterPoint - (timeMs * mTimelineSize / mMaxValueTimeMilSec)
        invalidate()
    }

    fun setDataList(dataList: ArrayList<RecordedDataModel>) {
        mRecordedDataList.clear()
        for (item in dataList) {
            mRecordedDataList.add(RecordedData(item.path, item.startOffset, item.endOffset))
        }
        invalidate()
    }

}