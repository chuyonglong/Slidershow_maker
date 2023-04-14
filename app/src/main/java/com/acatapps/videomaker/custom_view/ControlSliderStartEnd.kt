package com.acatapps.videomaker.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlin.math.roundToInt

class ControlSliderStartEnd : View {

    private var mDensity = 0f

    private var mProgressLineHeight = 2f

    private val mDisablePaint = Paint()
    private val mHighlightPaint = Paint()
    private val mTextPaint = Paint()

    private var mControllerHeight = 20f
    private var mControllerWidth = 14f
    private var mLineInControllerWidth = 7.5f
    private var mLineInControllerHeight = 1.25f

    private var mControllerStartDx = 100f
    private var mControllerEndDx = 100f

    private var mStartProgress = 0f
    private var mEndProgress = 100f

    private var mProgressLineOffset = 0f
    private var mProgressLineDistance = 0f

    private var isChangeLeft = false
    private var isChangeRight = false

    private val mControlStartRegion = Region()
    private val mControlEndRegion = Region()

    private var mMaxValue = 0

    private var mTextSize = 12f

    private var mOnChangeListener: OnChangeListener? = null

    private var mTenSecWidth = 0f

    private val mMinimumTimeMilSec = 10000
    private val deltaRegion = 150
    private var mStartOffsetX = 0f
    private var mEndOffsetX = 0f
    private var mTextDurationHeight = 0f

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attrs: AttributeSet?) {

        mDensity = Utils.density(context)
        mProgressLineHeight *= mDensity
        mControllerHeight *= mDensity
        mControllerWidth *= mDensity
        mLineInControllerWidth *= mDensity
        mLineInControllerHeight *= mDensity
        mTextSize *= mDensity

        mProgressLineOffset = mControllerWidth / 2f

        mDisablePaint.apply {
            color = Color.parseColor("#bebebe")
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mHighlightPaint.apply {
            color = Color.parseColor("#FF604D")
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        mTextPaint.apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mTextSize
            typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        }
        mTextDurationHeight = Utils.getTextHeight("00:00", mTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            getMeasuredDimensionHeight(hMode, heightMeasureSpec)
        )

        mControllerStartDx = 0f
        mControllerEndDx = MeasureSpec.getSize(widthMeasureSpec).toFloat() - mControllerWidth
        mProgressLineDistance = MeasureSpec.getSize(widthMeasureSpec).toFloat() - mControllerWidth
    }

    private fun getMeasuredDimensionHeight(mode: Int, height: Int): Int {
        return when (mode) {
            MeasureSpec.AT_MOST -> {
                (mControllerHeight * 2 + 6).roundToInt()
            }
            else -> {
                height
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawDisableLine(canvas)
        drawHighlightLine(canvas)
        drawControllerStart(canvas)
        drawControllerEnd(canvas)
        drawTextDuration(canvas)
    }

    private fun drawDisableLine(canvas: Canvas?) {
        mProgressLineDistance = width - mControllerWidth
        val right = mProgressLineOffset
        canvas?.drawRect(
            0 + right,
            height / 2f,
            0 + right + mProgressLineDistance,
            height / 2f + mProgressLineHeight,
            mDisablePaint
        )
    }

    private fun drawHighlightLine(canvas: Canvas?) {
        val start = mStartProgress * mProgressLineDistance / 100 + mProgressLineOffset
        val end = mEndProgress * mProgressLineDistance / 100 + mProgressLineOffset
        canvas?.drawRect(
            0 + start,
            height / 2f,
            end,
            height / 2f + mProgressLineHeight,
            mHighlightPaint
        )
    }


    private fun drawControllerStart(canvas: Canvas?) {
        val path = getControllerPath()
        path.apply {
            val offsetX = (mStartProgress * mProgressLineDistance / 100)
            mStartOffsetX = offsetX + x
            offset(offsetX, (height / 2f).roundToInt().toFloat())
            val boundRecF = RectF()
            computeBounds(boundRecF, true)
            mControlStartRegion.setPath(
                this,
                Region(
                    boundRecF.left.toInt(),
                    boundRecF.top.toInt(),
                    boundRecF.right.toInt(),
                    boundRecF.bottom.toInt()
                )
            )
        }
        canvas?.drawPath(path, mHighlightPaint)
    }


    private fun drawControllerEnd(canvas: Canvas?) {
        val path = getControllerPath()
        path.apply {
            val offsetX = (mEndProgress * mProgressLineDistance / 100)
            mEndOffsetX = offsetX + x
            offset(offsetX, (height / 2f).roundToInt().toFloat())
            val boundRecF = RectF()
            computeBounds(boundRecF, true)
            mControlEndRegion.setPath(
                this,
                Region(
                    boundRecF.left.toInt(),
                    boundRecF.top.toInt(),
                    boundRecF.right.toInt(),
                    boundRecF.bottom.toInt()
                )
            )
        }

        canvas?.drawPath(path, mHighlightPaint)
    }

    private fun getControllerPath(): Path {
        val path = Path()
        val centerW = mControllerWidth / 2f
        val triangleHeight = 7f * Utils.density(context)
        val cornerRadius = 2.5f * mDensity
        val rectF = RectF()
        path.apply {
            moveTo(centerW, 0f)
            lineTo(mControllerWidth, triangleHeight)
            lineTo(mControllerWidth, mControllerHeight - cornerRadius)
            lineTo(0f, mControllerHeight - cornerRadius)
            lineTo(0f, triangleHeight)
            lineTo(centerW, 0f)
            moveTo(0f, mControllerHeight - cornerRadius)

            rectF.set(0f, mControllerHeight - 2 * cornerRadius, 2 * cornerRadius, mControllerHeight)
            arcTo(rectF, 180f, -90f, false)

            lineTo(mControllerWidth - cornerRadius, mControllerHeight)

            rectF.set(
                mControllerWidth - 2 * cornerRadius,
                mControllerHeight - 2 * cornerRadius,
                mControllerWidth,
                mControllerHeight
            )
            arcTo(rectF, 90f, -90f, false)

            close()
        }
        val dx = Utils.density(context) * 6.5f / 2f
        val dy = Utils.density(context) * (14f + 5.75f) / 2f
        path.addPath(getThreeLinePath(), dx, dy)
        path.fillType = Path.FillType.EVEN_ODD
        return path
    }

    private fun getThreeLinePath(): Path {
        val space = Utils.density(context) * 3.5f / 2f

        return Path().apply {
            fillType = Path.FillType.INVERSE_EVEN_ODD
            addPath(getLinePath(), 0f, 0f)
            addPath(getLinePath(), 0f, space + mLineInControllerHeight)
            addPath(getLinePath(), 0f, 2 * (space + mLineInControllerHeight))
            close()
        }
    }

    private fun getLinePath(): Path {
        val rectF = RectF()
        return Path().apply {
            fillType = Path.FillType.INVERSE_EVEN_ODD
            val cornerRadius = mLineInControllerHeight / 2f
            moveTo(cornerRadius, 0f)
            lineTo(mLineInControllerWidth - cornerRadius, 0f)
            lineTo(mLineInControllerWidth - cornerRadius, mLineInControllerHeight)
            lineTo(cornerRadius, mLineInControllerHeight)
            lineTo(cornerRadius, 0f)
            moveTo(cornerRadius, 0f)

            rectF.set(0f, 0f, mLineInControllerHeight, mLineInControllerHeight)
            arcTo(rectF, 90f, 180f, false)

            moveTo(mLineInControllerWidth - cornerRadius, 0f)

            rectF.set(
                mLineInControllerWidth - mLineInControllerHeight,
                0f,
                mLineInControllerWidth,
                mLineInControllerHeight
            )
            arcTo(rectF, 90f, -180f, false)

            close()
        }
    }

    private fun drawTextDuration(canvas: Canvas?) {
        val textStart =
            Utils.convertSecToTimeString((mStartProgress / 100 / 1000 * mMaxValue).roundToInt())
        val textEnd =
            Utils.convertSecToTimeString((mEndProgress / 100 / 1000 * mMaxValue).roundToInt())
        canvas?.drawText(textStart, 0f, mTextDurationHeight * 1.5f, mTextPaint)
        canvas?.drawText(
            textEnd,
            width - 10 - (getTextWidth(textEnd, mTextPaint)),
            mTextDurationHeight * 1.5f,
            mTextPaint
        )

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        mTenSecWidth = (10000 * mProgressLineDistance / mMaxValue)

        if (event?.action == MotionEvent.ACTION_DOWN) {


            if (mControlStartRegion.contains(
                    event.x.toInt(),
                    event.y.toInt()
                ) || event.rawX in mStartOffsetX - deltaRegion..mStartOffsetX + deltaRegion
            ) {
                isChangeLeft = true
                isChangeRight = false
            } else if (mControlEndRegion.contains(
                    event.x.toInt(),
                    event.y.toInt()
                ) || event.rawX in mEndOffsetX - deltaRegion..mEndOffsetX + deltaRegion
            ) {
                isChangeLeft = false
                isChangeRight = true
            } else {
                isChangeLeft = false
                isChangeRight = false
            }
        } else if (event?.action == MotionEvent.ACTION_UP) {
            if (isChangeLeft) {
                mOnChangeListener?.onLeftUp(mControllerStartDx / mProgressLineDistance)
            } else if (isChangeRight) {
                mOnChangeListener?.onRightUp(mControllerEndDx / mProgressLineDistance)
            }
            isChangeLeft = false
            isChangeRight = false
        }

        if (isChangeLeft) {
            if (event?.action == MotionEvent.ACTION_MOVE) {
                swipeStartController(event.rawX)
            }
        } else if (isChangeRight) {
            if (event?.action == MotionEvent.ACTION_MOVE) {
                swipeEndController(event.rawX)
            }
        }
        parent.requestDisallowInterceptTouchEvent(true)
        return true
    }

    private fun swipeStartController(rawX: Float) {
        mTenSecWidth = (10000 * mProgressLineDistance / mMaxValue)
        mControllerStartDx = mStartProgress * mProgressLineDistance / 100
        mControllerEndDx = mEndProgress * mProgressLineDistance / 100
        var dx = rawX - x
        if (dx <= 0) dx = 0f
        //if(dx>=(mControllerEndDx-mControllerWidth)) dx = mControllerEndDx-mControllerWidth
        if (dx >= (mControllerEndDx - mTenSecWidth)) dx = mControllerEndDx - mTenSecWidth

        mControllerStartDx = dx
        mStartProgress = mControllerStartDx * 100 / mProgressLineDistance
        mOnChangeListener?.onSwipeLeft(mControllerStartDx / mProgressLineDistance)
        invalidate()
    }

    private fun swipeEndController(rawX: Float) {
        mTenSecWidth = (10000 * mProgressLineDistance / mMaxValue)
        mControllerStartDx = mStartProgress * mProgressLineDistance / 100
        mControllerEndDx = mEndProgress * mProgressLineDistance / 100
        var dx = rawX - x
        if (dx <= (mControllerStartDx + mTenSecWidth)) dx = mControllerStartDx + mTenSecWidth
        if (dx >= width - mControllerWidth) dx = width - mControllerWidth
        mControllerEndDx = dx
        mEndProgress = mControllerEndDx * 100 / mProgressLineDistance
        mOnChangeListener?.onSwipeRight(mControllerEndDx / mProgressLineDistance)
        invalidate()
    }

    fun getStartOffset(): Int {
        return (mStartProgress * mMaxValue / 100).roundToInt()
    }

    fun getLength(): Int {
        return (mEndProgress * mMaxValue / 100).roundToInt() - getStartOffset()
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

    fun setMaxValue(timeMiniSec: Long) {
        mMaxValue = (timeMiniSec).toInt()
        invalidate()
    }


    fun setOnChangeListener(onChangeListener: OnChangeListener) {
        mOnChangeListener = onChangeListener
    }


    fun setStartAndEndProgress(startProgress: Float, endProgress: Float) {
        mStartProgress = startProgress
        mEndProgress = endProgress
        mControllerStartDx = mStartProgress * mProgressLineDistance / 100
        mControllerEndDx = mEndProgress * mProgressLineDistance / 100
        Logger.e("mProgressLineDistance = $mProgressLineDistance")
        invalidate()
    }


    interface OnChangeListener {
        fun onSwipeLeft(progress: Float)
        fun onLeftUp(progress: Float)
        fun onSwipeRight(progress: Float)
        fun onRightUp(progress: Float)
    }
}