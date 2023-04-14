package com.acatapps.videomaker.custom_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.utils.Utils
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.roundToInt
import kotlin.math.sqrt

class CustomEditTextSticker : AppCompatEditText {

    private var mDensity = 0f

    private var mMainText = ""
    private var mHintText = "Type your text"
    private var mNormalTextSize = 24f
    private var mNormalTextPaint = Paint()

    private val mDotBoundPaint = Paint()

    private val mMainTextPaint = Paint()
    private var mMainTextSize = 24f

    private var mPadding = 4f

    private var mDotHeight = 1f


    private var mButtonRadius = 12f
    private val mButtonPaint = Paint()

    private var mTranslationX = 0f
    private var mTranslationY = 0f
    private var mScale = 1f
    private var mRotate = 0f

    private val mRotateButtonRegion = Region()

    private var mTouchPointX = 0f
    private var mTouchPointY = 0f

    private var mRotateMode = false

    private var mCenterX = 0f
    private var mCenterY = 0f

    private var mOriginWidth = 0f
    private var mOriginHeight = 0f
    private val mFloatArray = FloatArray(9)

    private var mLineList = ArrayList<String>()

    constructor(context: Context) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }


    private fun initAttrs(attributes: AttributeSet?) {
        mDensity = Utils.density(context)
        movementMethod = null
        mNormalTextSize *= mDensity
        mMainTextSize *= mDensity
        mPadding *= mDensity
        mDotHeight *= mDensity
        mButtonRadius *= mDensity
        setBackgroundColor(Color.TRANSPARENT)

        mDotBoundPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f * mDensity, 5f * mDensity), 0f)
            strokeWidth = mDotHeight
        }

        mMainTextPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mMainTextSize
            alpha = 255
        }

        mNormalTextPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = mNormalTextSize

        }

        mButtonPaint.apply {
            color = Color.GREEN
            isAntiAlias = true
            style = Paint.Style.FILL
        }


        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mMainText = s.toString()
                requestLayout()
            }

        })


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mMainText.isNotEmpty()) {

            val h = getHeightForText() + 4 * mButtonRadius
            val w = getWidthForText() + 4 * (mPadding + mButtonRadius)

            setMeasuredDimension(w.roundToInt(), h.roundToInt())
        } else {
            val w = (getTextWidth(
                mHintText,
                mNormalTextPaint
            ) + mPadding * 4 + 4 * mButtonRadius).roundToInt()
            val h = ((getTextHeight(
                mHintText,
                mNormalTextPaint
            ) * 1.75f) + mButtonRadius * 2).roundToInt()
            setMeasuredDimension(w, h)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        drawMainText(canvas)
        drawDotBound(canvas)

        canvas?.save()
        drawDeleteIcon(canvas)
        drawRotateButton(canvas)
        canvas?.restore()
    }

    private fun drawMainText(canvas: Canvas?) {
        if (mMainText.isNotEmpty()) {
            val offset = mButtonRadius * 2 + mPadding
            var currentHeight = mButtonRadius + mPadding
            for (text in mLineList) {
                val lineHeight = getTextHeight("ky", mMainTextPaint)
                currentHeight += lineHeight
                canvas?.drawText(text, offset, currentHeight, mMainTextPaint)
            }

        } else {
            drawHintText(canvas)
        }
    }


    private fun drawHintText(canvas: Canvas?) {
        drawText(canvas, mHintText, mNormalTextPaint)
    }

    private fun drawText(canvas: Canvas?, text: String, paint: Paint) {
        val offset = mButtonRadius * 2 + mPadding
        canvas?.drawText(text, offset, height * 2 / 3f, paint)

    }

    private fun drawDotBound(canvas: Canvas?) {
        val offset = mDotHeight + mButtonRadius
        canvas?.drawRect(
            offset,
            offset,
            width.toFloat() - offset,
            height.toFloat() - offset,
            mDotBoundPaint
        )
    }

    private fun drawDeleteIcon(canvas: Canvas?) {
        canvas?.drawCircle(
            mButtonRadius / mScale,
            mButtonRadius / mScale,
            mButtonRadius / mScale,
            mButtonPaint
        )
    }

    private fun drawRotateButton(canvas: Canvas?) {
        val path = Path().apply {
            addCircle(
                width - (mButtonRadius / mScale),
                height - (mButtonRadius / mScale),
                (mButtonRadius / mScale),
                Path.Direction.CCW
            )
            getBoundRegion(mRotateButtonRegion, this)
        }
        canvas?.drawPath(path, mButtonPaint)
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

    var mPreRotation = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mRotateMode = mRotateButtonRegion.contains(event?.x.roundToInt(), event.y.roundToInt())
            if (mRotateMode) {
                mPreRotation = rotation
            }
            mTouchPointX = event.rawX
            mTouchPointY = event.rawY
        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            if (mRotateMode) {
                onRotate(event.rawX, event.rawY)
            } else {
                motionView(event.rawX, event.rawY)
            }

        } else if (event?.action == MotionEvent.ACTION_UP) {
            mRotateMode = false
            mPreRotation = rotation
        }

        return true
    }

    private fun motionView(rawX: Float, rawY: Float) {
        mTranslationX += (rawX - mTouchPointX)
        mTranslationY += (rawY - mTouchPointY)
        mTouchPointX = rawX
        mTouchPointY = rawY
        translationX = mTranslationX
        translationY = mTranslationY

    }

    private fun onRotate(rawX: Float, rawY: Float) {
        val location = IntArray(2)
        getLocationOnScreen(location)
        mCenterX = (rawX - location[0]) / 2f
        mCenterY = (rawY - location[1]) / 2f

        val dDegree =
            (atan((rawY - mCenterY) / (rawX - mCenterX)) - atan((mTouchPointY - mCenterY) / (mTouchPointX - mCenterX))) * 180 / PI
        rotation = mPreRotation + dDegree.toFloat()


        val originCross = sqrt(width * width.toFloat() + height * height)
        val d1 = rawX - location[0]
        val d2 = rawY - location[1]
        val newCross = sqrt((d1 * d1) + (d2 * d2))
        var newScale = newCross / originCross
        mScale = newScale
        scaleX = mScale
        scaleY = mScale
        invalidate()
    }

    private fun containSpecialCharacter(text: String): Boolean {
        val regex = "[q|y|p|g|j]".toRegex()
        return regex.containsMatchIn(text)
    }

    private fun showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInputFromWindow(applicationWindowToken, InputMethodManager.SHOW_FORCED, 1)
    }

    private fun getBoundRegion(region: Region, path: Path) {
        val boundRecF = RectF()
        path.computeBounds(boundRecF, true)
        region.setPath(
            path,
            Region(
                boundRecF.left.toInt(),
                boundRecF.top.toInt(),
                boundRecF.right.toInt(),
                boundRecF.bottom.toInt()
            )
        )
    }

    private fun getHeightForText(): Int {
        var numberLine = 1
        var line = ""
        var outHeight = 0f
        mLineList.clear()
        for (c in mMainText) {
            if (c.toString() == "\n") {
                numberLine++
                mLineList.add(line)
                outHeight += (getTextHeight(
                    "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM",
                    mMainTextPaint
                ))
                line = ""
            } else {
                line = "$line$c"
            }
        }
        mLineList.add(line)
        outHeight += getTextHeight(
            "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM",
            mMainTextPaint
        )
        return outHeight.roundToInt()
    }

    private fun getWidthForText(): Int {
        var maxWidth = 0f
        for (text in mLineList) {
            if (getTextWidth(text, mMainTextPaint) >= maxWidth) {
                maxWidth = getTextWidth(text, mMainTextPaint)
            }
        }
        return maxWidth.roundToInt()
    }

    fun setToCenter(parentW: Int, parentH: Int) {

    }

    fun changeFonts(fontId: Int) {
        mMainTextPaint.typeface = ResourcesCompat.getFont(context, fontId)
        requestLayout()
        invalidate()
    }
}