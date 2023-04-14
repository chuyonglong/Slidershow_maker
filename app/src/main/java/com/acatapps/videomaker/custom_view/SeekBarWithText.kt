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

class SeekBarWithText : View {
    private val rect = Rect()
    private var mTextSize = 12f
    private var mLineSize = 2f
    private var mBallSize = 12f

    private var mDisableColor = Color.parseColor("#BEBEBE")
    private var mHighlightColor = Color.parseColor("#FF604D")

    private val mDisablePaint = Paint()
    private val mHighlightPaint = Paint()
    private val mTextProgressPaint = Paint()

    private var mProgressDistance = 0f

    private var mProgress = 100f

    private var mProgressChangeListener: ProgressChangeListener? = null

    constructor(context: Context) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attrs: AttributeSet?) {

        mTextSize = (Utils.density(context) * mTextSize)
        mLineSize = (Utils.density(context) * mLineSize)
        mBallSize = (Utils.density(context) * mBallSize)

        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeekBarWithText)
        mHighlightColor = typedArray.getColor(
            R.styleable.SeekBarWithText_highlightColor,
            Color.parseColor("#FF604D")
        )
        typedArray.recycle()

        mDisablePaint.apply {
            color = mDisableColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mHighlightPaint.apply {
            color = mHighlightColor
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mTextProgressPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mTextSize
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
                (mBallSize * 2 + 6).roundToInt()
            }
            else -> {
                height
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        mProgressDistance = width - mBallSize * 2
        drawDisableLine(canvas)
        drawHighlightLine(canvas)
        drawBall(canvas)
        drawTextProgress(canvas)
    }

    private fun drawDisableLine(canvas: Canvas?) {
        canvas?.drawRect(
            0f + mBallSize,
            height / 2f - mLineSize / 2,
            mProgressDistance + mBallSize,
            height / 2f + mLineSize / 2,
            mDisablePaint
        )
    }

    private fun drawHighlightLine(canvas: Canvas?) {
        val right = mProgressDistance * mProgress / 100
        canvas?.drawRect(
            0f + mBallSize,
            height / 2f - mLineSize / 2,
            right + mBallSize,
            height / 2f + mLineSize / 2,
            mHighlightPaint
        )
    }

    private fun drawBall(canvas: Canvas?) {
        val centerX = mProgressDistance * mProgress / 100
        canvas?.drawCircle(centerX + mBallSize, height / 2f, mBallSize, mHighlightPaint)
    }

    private fun drawTextProgress(canvas: Canvas?) {
        val textProgress = mProgress.roundToInt().toString()
        val right = (mProgressDistance * mProgress / 100) - getTextWidth(
            textProgress,
            mTextProgressPaint
        ) / 2f
        canvas?.drawText(
            textProgress,
            right + mBallSize,
            height / 2 + getTextHeight(textProgress, mTextProgressPaint) / 2f,
            mTextProgressPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            changeProgress(event.rawX)
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            changeProgress(event.rawX)
        }

        return true
    }

    private fun changeProgress(rawX: Float) {
        val distance = rawX - 0 - mBallSize - x
        var progress = ((distance / mProgressDistance) * 100)
        if (progress <= 0) progress = 0f
        else if (progress >= 100) progress = 100f

        if (progress.roundToInt() != mProgress.roundToInt()) {
            mProgressChangeListener?.onChange(progress.roundToInt())
        }

        mProgress = progress
        invalidate()
    }

    private fun getTextWidth(text: String, paint: Paint): Float {
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width().toFloat()
    }

    private fun getTextHeight(text: String, paint: Paint): Float {
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    fun setProgressChangeListener(onChange: (duration: Int) -> Unit) {
        mProgressChangeListener = object : ProgressChangeListener {
            override fun onChange(progress: Int) {
                onChange.invoke(progress)
            }
        }
    }

    fun setProgress(progress: Float) {
        mProgress = progress
        invalidate()
    }

    fun setHighlightColor(color: Int) {
        mHighlightPaint.color = color
        invalidate()
    }

    interface ProgressChangeListener {
        fun onChange(progress: Int)
    }


}