package com.acatapps.videomaker.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Utils
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class VideoControllerView : View {

    private val mTextPaint = Paint()
    private val mDisableLinePaint = Paint()
    private val mHighlightLinePaint = Paint()
    private val mWhiteBallPaint = Paint()
    private val mFullscreenIconPaint = Paint()

    private var mTextSize = 14f
    private var mLineHeight = 2f
    private var mBallRadius = 10f
    private var mPadding = 12f

    private var mDensity = 1f

    private var mMaxDuration = 0f
    private var mCurrentProgress = 0f

    private var mCurrentTimeTextWidth = 1f
    private var mMaxTimeTextOffsetStart = 1f
    private var mDistance = 1f

    private val mBallControllerRegion = Region()

    private var mDx = 0f

    private var mStartPositionOffset = 0L

    var onChangeListener: OnChangeListener? = null

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attrs: AttributeSet?) {
        setBackgroundColor(Color.parseColor("#26000000"))
        mDensity = Utils.density(context)

        mTextSize *= mDensity
        mLineHeight *= mDensity
        mBallRadius *= mDensity
        mPadding *= mDensity

        mTextPaint.apply {
            isAntiAlias = true
            textSize = mTextSize
            color = Color.WHITE
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }

        mDisableLinePaint.apply {
            isAntiAlias = true
            textSize = mTextSize
            color = Color.parseColor("#cccccc")
        }

        mHighlightLinePaint.apply {
            isAntiAlias = true
            textSize = mTextSize
            color = Color.parseColor("#F62834")
        }

        mWhiteBallPaint.apply {
            isAntiAlias = true
            textSize = mTextSize
            color = Color.WHITE
        }

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

    override fun onDraw(canvas: Canvas?) {
        drawCurrentTime(canvas)
        drawMaxTime(canvas)
        drawDisableLine(canvas)
        drawHighlightLine(canvas)
        drawBall(canvas)
    }


    private fun drawCurrentTime(canvas: Canvas?) {
        val currentTime = mMaxDuration * mCurrentProgress / 100

        val text = try {
            convertSecToTimeString(currentTime.roundToLong() / 1000)
        } catch (e: Exception) {
            convertSecToTimeString(0)
        }
        val right = mPadding

        canvas?.drawText(
            text,
            right,
            height / 2f + getTextHeight(text, mTextPaint) / 2f,
            mTextPaint
        )


    }

    private fun drawMaxTime(canvas: Canvas?) {
        val text = try {
            convertSecToTimeString((mMaxDuration / 1000).roundToInt())
        } catch (e: Exception) {
            convertSecToTimeString(0)
        }
        val right = width - 2 * mPadding - getTextWidth(text, mTextPaint)
        mMaxTimeTextOffsetStart = right
        mCurrentTimeTextWidth = getTextWidth(text, mTextPaint)
        canvas?.drawText(
            text,
            right,
            height / 2f + getTextHeight(text, mTextPaint) / 2f,
            mTextPaint
        )
    }

    private fun drawDisableLine(canvas: Canvas?) {
        val offset = mPadding * 2 + mCurrentTimeTextWidth
        mDistance = mMaxTimeTextOffsetStart - mPadding - offset
        mDx = mDistance * mCurrentProgress / 100 + offset
        val rectF = RectF(
            offset,
            height / 2f - mLineHeight / 2f,
            offset + mDistance,
            height / 2f + mLineHeight
        )
        canvas?.drawRect(rectF, mDisableLinePaint)


    }

    private fun drawHighlightLine(canvas: Canvas?) {

        val s = (mStartPositionOffset.toFloat() / mMaxDuration)
        if (mCurrentProgress / 100 < s) {
            return
        }

        val offset = mPadding * 2 + mCurrentTimeTextWidth + mDistance * s
        canvas?.drawRect(
            offset,
            height / 2f - mLineHeight / 2f,
            offset + mDistance * ((mCurrentProgress) / 100 - s),
            height / 2f + mLineHeight,
            mHighlightLinePaint
        )
    }


    private fun drawBall(canvas: Canvas?) {
        val path = Path().apply {
            addCircle(mDx, height / 2f, mBallRadius, Path.Direction.CW)
            val boundRecF = RectF()
            computeBounds(boundRecF, true)
            mBallControllerRegion.setPath(
                this,
                Region(
                    boundRecF.left.toInt(),
                    boundRecF.top.toInt(),
                    boundRecF.right.toInt(),
                    boundRecF.bottom.toInt()
                )
            )
        }
        canvas?.drawPath(path, mWhiteBallPaint)
    }

    fun setMaxDuration(newMaxDurationMiniSec: Int) {
        mMaxDuration = newMaxDurationMiniSec.toFloat()
        mCurrentProgress = 0f
        invalidate()
    }

    fun setCurrentDuration(durationMiniSec: Int) {
        if (mIsTouching) return
        var progress = (100 * durationMiniSec) / mMaxDuration
        if (progress <= 0f) progress = 0f
        else if (progress >= 100f) progress = 100f
        mCurrentProgress = progress
        invalidate()
    }

    fun setCurrentDuration(durationMiniSec: Long) {
        if (mIsTouching) return
        var progress = (100 * durationMiniSec) / mMaxDuration
        if (progress <= 0f) progress = 0f
        else if (progress >= 100f) progress = 100f
        mCurrentProgress = progress
        invalidate()
    }

    fun setProgress(progress: Float) {
        mCurrentProgress = when {
            progress <= 0 -> 0f
            progress >= 100f -> 100f
            else -> progress
        }
        invalidate()
    }

    private var mIsTouching = false
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN) {
            if (event.rawX < (2 * mPadding + mCurrentTimeTextWidth) || event.rawX > (mMaxTimeTextOffsetStart - mPadding)) {
            } else {
                mIsTouching = true
                onMoveController(event.rawX)
            }
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            onMoveController(event.rawX)
        } else if (event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL) {
            onTouchUp(event.rawX)
            mIsTouching = false
        }
        return true
    }

    private fun onMoveController(rawX: Float) {
        var progress = 100 * (rawX - (2 * mPadding + mCurrentTimeTextWidth)) / mDistance

        if (progress <= 0f) progress = 0f
        else if (progress >= 100f) progress = 100f
        mCurrentProgress = progress
        invalidate()
        onChangeListener?.onMove(mCurrentProgress)

    }

    private fun onTouchUp(rawX: Float) {
        var progress = 100 * (rawX - (2 * mPadding + mCurrentTimeTextWidth)) / mDistance
        if (progress <= 0f) progress = 0f
        else if (progress >= 100f) progress = 100f
        mCurrentProgress = progress
        invalidate()
        onChangeListener?.onUp((mMaxDuration * progress / 100).roundToInt())
    }

    private fun convertSecToTimeString(sec: Int): String {
        return if (sec >= 3600) {
            val h = zeroPrefix((sec / 3600).toString())
            val m = zeroPrefix(((sec % 3600) / 60).toString())
            val s = zeroPrefix(((sec % 3600) % 60).toString())
            "$h:$m:$s"
        } else {
            val m = zeroPrefix(((sec % 3600) / 60).toString())
            val s = zeroPrefix(((sec % 3600) % 60).toString())
            "$m:$s"
        }
    }

    private fun convertSecToTimeString(sec: Long): String {
        return if (sec >= 3600) {
            val h = zeroPrefix((sec / 3600).toString())
            val m = zeroPrefix(((sec % 3600) / 60).toString())
            val s = zeroPrefix(((sec % 3600) % 60).toString())
            "$h:$m:$s"
        } else {
            val m = zeroPrefix(((sec % 3600) / 60).toString())
            val s = zeroPrefix(((sec % 3600) % 60).toString())
            "$m:$s"
        }
    }

    private fun zeroPrefix(string: String): String {
        if (string.length < 2) return "0$string"
        return string
    }

    interface OnChangeListener {
        fun onUp(timeMilSec: Int)
        fun onMove(progress: Float)
    }

    fun changeStartPositionOffset(timeMs: Long) {
        mStartPositionOffset = timeMs
        invalidate()
    }

}