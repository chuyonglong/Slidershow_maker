package com.acatapps.videomaker.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Utils
import kotlin.math.roundToInt

class RenderingProgressBar(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val mDensity = Utils.density(context)
    private var mProgressLineHeight = 8 * mDensity
    private var mPercentTextSize = 18 * mDensity
    private var mProgress = 0f
    private val mBgLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#B0BEC5")
    }
    private val mHighlightLinePaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#FFCC80")
    }
    private val mPercentTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.parseColor("#627388")
        typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
        textSize = mPercentTextSize
    }

    override fun onDraw(canvas: Canvas?) {
        drawBgLine(canvas)
        drawHighlightLine(canvas)
        drawPercentText(canvas)
    }

    private fun drawBgLine(canvas: Canvas?) {
        val path = Path().apply {
            addRoundRect(
                RectF(0f, height - mProgressLineHeight, width.toFloat(), height.toFloat()),
                (mProgressLineHeight / 2),
                mProgressLineHeight / 2,
                Path.Direction.CW
            )
        }
        canvas?.drawPath(path, mBgLinePaint)
    }

    private fun drawHighlightLine(canvas: Canvas?) {
        val path = Path().apply {
            val right = width * mProgress / 100
            addRoundRect(
                RectF(0f, height - mProgressLineHeight, right, height.toFloat()),
                mProgressLineHeight / 2,
                mProgressLineHeight / 2,
                Path.Direction.CW
            )
        }
        canvas?.drawPath(path, mHighlightLinePaint)
    }

    private fun drawPercentText(canvas: Canvas?) {
        if (!mProgress.isNaN()) {
            val text = mProgress.roundToInt().toString() + "%"
            val textW = getTextWidth(text, mPercentTextPaint)
            val textH = getTextHeight(text, mPercentTextPaint)
            canvas?.drawText(text, width / 2f - textW / 2f, textH + 5, mPercentTextPaint)
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

    fun setProgress(percent: Float) {
        mProgress = when {
            percent >= 100f -> 100f
            percent <= 0f -> 0f
            else -> percent
        }

        invalidate()
    }

    fun setProgress(percent: Int) {
        mProgress = when {
            percent >= 100 -> 100f
            percent <= 0 -> 0f
            else -> percent.toFloat()
        }

        invalidate()
    }

    fun addProgress(deltaProgress: Float) {


        mProgress += deltaProgress
        if (mProgress >= 99f) mProgress = 99f
        invalidate()


    }

}