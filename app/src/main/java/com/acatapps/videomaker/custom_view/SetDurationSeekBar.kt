package com.acatapps.videomaker.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Utils
import kotlin.math.roundToInt

class SetDurationSeekBar : View {

    private var mTextSize = 12f
    private var mSelectedTextSize = 14f
    private var mLineSize = 2f
    private var mMiniBallSize = 10f
    private var mBigBallSize = 20f
    private var mDisableColor = Color.parseColor("#BEBEBE")
    private var mHighlightColor = Color.parseColor("#26cac3")

    private val mDisablePaint = Paint()
    private val mHighlightPaint = Paint()
    private val mSelectedTextPaint = Paint()

    private var mCurrentPosition = 3

    private var mDurationChangeListener: DurationChangeListener? = null

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }


    private fun initAttrs(attributes: AttributeSet?) {
        mTextSize = (Utils.density(context) * mTextSize)
        mSelectedTextSize = (Utils.density(context) * mSelectedTextSize)
        mLineSize = (Utils.density(context) * mLineSize)
        mMiniBallSize = (Utils.density(context) * mMiniBallSize / 2)
        mBigBallSize = (Utils.density(context) * mBigBallSize / 2)

        mDisablePaint.apply {
            color = mDisableColor
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mTextSize
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }

        mHighlightPaint.apply {
            color = mHighlightColor
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mTextSize
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }

        mSelectedTextPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mSelectedTextSize
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            getMeasuredDimensionHeight(hMode, heightMeasureSpec)
        )
    }

    private fun getMeasuredDimensionHeight(mode: Int, height: Int): Int {
        return when (mode) {
            MeasureSpec.AT_MOST -> {
                (mTextSize + mMiniBallSize + mBigBallSize + 12 + 4).roundToInt()
            }
            else -> {
                height
            }
        }
    }

    private val rect = Rect()

    override fun onDraw(canvas: Canvas?) {
        drawDisableLine(canvas)
        drawHighlightLine(canvas)
        for (i in 1 until mCurrentPosition) {
            drawHighlightMiniBall(canvas, i)
            drawText(canvas, i, mHighlightPaint)
        }
        drawBigBall(canvas, mCurrentPosition)
        for (i in mCurrentPosition + 1..10) {
            drawDisableMiniBall(canvas, i)
            drawText(canvas, i, mDisablePaint)
        }
        drawSelectedText(canvas, mCurrentPosition, mSelectedTextPaint)
    }


    private fun drawDisableLine(canvas: Canvas?) {
        canvas?.drawRect(
            0f,
            height / 2f - mLineSize / 2,
            width.toFloat(),
            height / 2f + mLineSize / 2,
            mDisablePaint
        )
    }

    private fun drawHighlightLine(canvas: Canvas?) {
        var right = (width / 20f) + (width / 10f) * (mCurrentPosition - 1)
        if (mCurrentPosition == 10) right = width.toFloat()
        canvas?.drawRect(
            0f,
            height / 2f - mLineSize / 2,
            right,
            height / 2f + mLineSize / 2,
            mHighlightPaint
        )
    }

    private fun drawDisableMiniBall(canvas: Canvas?, number: Int) {
        val right = (width / 20f) + (width / 10f) * (number - 1)
        canvas?.drawCircle(right, height / 2f, mMiniBallSize, mDisablePaint)
    }

    private fun drawHighlightMiniBall(canvas: Canvas?, number: Int) {
        val right = (width / 20f) + (width / 10f) * (number - 1)
        canvas?.drawCircle(right, height / 2f, mMiniBallSize, mHighlightPaint)
    }

    private fun drawBigBall(canvas: Canvas?, number: Int) {
        val right = (width / 20f) + (width / 10f) * (number - 1)
        canvas?.drawCircle(right, height / 2f, mBigBallSize, mHighlightPaint)
    }

    private fun drawText(canvas: Canvas?, number: Int, paint: Paint) {
        val right = (width / 20f) + (width / 10f) * (number - 1) - 2 - (getTextWidth(
            number.toString(),
            paint
        ) / 2f)
        canvas?.drawText(
            number.toString(),
            right,
            getTextHeight(number.toString(), paint) + 4,
            paint
        )
    }

    private fun drawSelectedText(canvas: Canvas?, number: Int, paint: Paint) {
        val right = (width / 20f) + (width / 10f) * (number - 1) - 2 - (getTextWidth(
            number.toString(),
            paint
        ) / 2f)
        canvas?.drawText(
            number.toString(),
            right,
            height / 2f + getTextHeight(number.toString(), paint) / 2,
            paint
        )
    }

    private fun getTextWidth(text: String, paint: Paint): Float {
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width().toFloat()
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            changePosition(event.rawX)
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            changePosition(event.rawX)
        } else if (event?.action == MotionEvent.ACTION_UP) {
            mDurationChangeListener?.onTouchUp(mCurrentPosition)
        }

        return true
    }

    private fun changePosition(rawX: Float) {

        val x = rawX - 0 - width / 20f
        var position = (x / (width / 10f)).roundToInt() + 1
        if (position < 1) position = 1
        else if (position > 10) position = 10

        if (position != mCurrentPosition) {
            mDurationChangeListener?.onChange(position)
        }

        mCurrentPosition = position

        invalidate()
    }

    fun setCurrentDuration(duration: Int) {
        if (mCurrentPosition != duration) {
            mCurrentPosition = duration
            invalidate()
        }
    }

    fun setDurationChangeListener(
        onChange: (duration: Int) -> Unit,
        onTouchUp: (duration: Int) -> Unit
    ) {
        mDurationChangeListener = object : DurationChangeListener {
            override fun onChange(duration: Int) {
                onChange.invoke(duration)
            }

            override fun onTouchUp(duration: Int) {
                onTouchUp.invoke(duration)
            }
        }
    }

    interface DurationChangeListener {
        fun onChange(duration: Int)
        fun onTouchUp(duration: Int)
    }

}