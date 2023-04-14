package com.acatapps.videomaker.custom_view
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.acatapps.videomaker.R
import com.acatapps.videomaker.models.TextStickerAttrData
import com.acatapps.videomaker.utils.Logger
import com.acatapps.videomaker.utils.Utils
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

class EditTextSticker(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {

    private val mDensity = Utils.density(context)
    private val mTextSize = 100 * mDensity


    private val mDotHeight = mDensity

    private var mBitmap: Bitmap? = null
    private val mMatrix = Matrix()
    private val mArrayOfFloat = FloatArray(9)

    private val mScaleBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_scale_sticker)
    private val mDeleteBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_cancel_transform)
    private val mEditTextBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_edit_sticker)
    private var mButtonRadius = 12f * mDensity
    private val mTextLineArray = ArrayList<String>()
    private var mMainText = ""

    private var mHintText = context.getString(R.string.type_your_text)

    private var mInEdit = true
    val inEdit get() = mInEdit

    private var mIsAdded = false
    private val mRotateButtonRegion = Region()
    private val mDeleteButtonRegion = Region()
    private val mEditButtonRegion = Region()
    private val mImageRegion = Region()

    private var mAlignMode = AlignMode.CENTER

    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var mIsRotateMode = false
    private var mIsMotion = false
    private var mDiagonalLength = 0f
    private var mMidPointX = 0f
    private var mMidPointY = 0f
    private var mLastDegrees = 0f

    private var mFontId = R.font.roboto_regular
    private var mTextStyle = Typeface.NORMAL
    private var mFlag = Paint.ANTI_ALIAS_FLAG
    private var mTextColor = Color.WHITE

    var deleteCallback: (() -> Unit)? = null
    var editCallback: ((EditTextSticker) -> Unit)? = null

    private val mTextPaint = Paint().apply {
        isAntiAlias = true
        textSize = mTextSize
        color = mTextColor
        typeface = Typeface.create(ResourcesCompat.getFont(context, mFontId), mTextStyle)
    }

    private val mDotBoundPaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        pathEffect = DashPathEffect(floatArrayOf(5f * mDensity, 5f * mDensity), 0f)
        strokeWidth = mDotHeight
    }

    init {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        mMatrix.postScale(0.25f, 0.25f, mMidPointX, mMidPointY)
        val h = getTextHeight(mTextPaint) / 4
        val w = getTextWidth(mHintText, mTextPaint) / 4
        val translateX = (Utils.screenWidth(context) - w) / 2f
        val translateY = 2 * 56 * Utils.density()
        mMatrix.postTranslate(translateX, translateY)
        background = null
        calculatorLineText()
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mMainText = s.toString()
                Logger.e("text length = ${s.toString().length}")
                calculatorLineText()
            }

        })
    }

    private fun calculatorLineText() {
        mTextLineArray.clear()
        if (mMainText.isEmpty()) {
            drawHint()
            return
        }

        var line = ""
        for (char in mMainText) {
            line = if (char == '\n') {
                mTextLineArray.add(line)
                ""
            } else {
                "$line$char"
            }
        }
        mTextLineArray.add(line)
        var maxW = 0
        for (item in mTextLineArray) {
            val lineW = getTextWidth(item, mTextPaint)
            if (lineW > maxW) maxW = lineW.roundToInt()
        }

        val maxH = getTextHeight(mTextPaint) * mTextLineArray.size
        val bitmap = Bitmap.createBitmap(maxW + 1, maxH.roundToInt() + 10, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        when (mAlignMode) {
            AlignMode.LEFT -> {
                for (index in 0 until mTextLineArray.size) {
                    val currentLine = mTextLineArray[index]
                    canvas.drawText(
                        currentLine,
                        0f,
                        getTextHeight(mTextPaint) * (index + 1) - getTextHeight(mTextPaint) / 4,
                        mTextPaint
                    )
                }
            }
            AlignMode.CENTER -> {
                for (index in 0 until mTextLineArray.size) {
                    val currentLine = mTextLineArray[index]
                    canvas.drawText(
                        currentLine,
                        maxW / 2f - getTextWidth(currentLine, mTextPaint) / 2,
                        getTextHeight(mTextPaint) * (index + 1) - getTextHeight(mTextPaint) / 4,
                        mTextPaint
                    )
                }
            }
            AlignMode.RIGHT -> {
                for (index in 0 until mTextLineArray.size) {
                    val currentLine = mTextLineArray[index]
                    canvas.drawText(
                        currentLine,
                        maxW - getTextWidth(currentLine, mTextPaint),
                        getTextHeight(mTextPaint) * (index + 1) - getTextHeight(mTextPaint) / 4,
                        mTextPaint
                    )
                }
            }
        }
        mBitmap = bitmap
        invalidate()

    }

    private fun drawHint() {
        val h = getTextHeight(mTextPaint)
        val w = getTextWidth(mHintText, mTextPaint)
        val bitmap =
            Bitmap.createBitmap(w.roundToInt(), h.roundToInt() + 10, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(
            mHintText,
            0f,
            getTextHeight(mTextPaint) - getTextHeight(mTextPaint) / 4,
            mTextPaint
        )
        mBitmap = bitmap
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        mBitmap?.let {
            val topLeftX = getMatrixTranslateX()
            val topLeftY = getMatrixTranslateY()

            val topRightX = getMatrixScaleX() * this.mBitmap!!.width + getMatrixTranslateX()
            val topRightY = getMatrixTranslateY() + getMatrixSkewY() * mBitmap!!.width

            val bottomLeftX = getMatrixTranslateX() + getMatrixSkewX() * this.mBitmap!!.height
            val bottomLeftY = getMatrixScaleY() * this.mBitmap!!.height + getMatrixTranslateY()

            val bottomRightX =
                getMatrixScaleX() * this.mBitmap!!.width + getMatrixTranslateX() + getMatrixSkewX() * this.mBitmap!!.height
            val bottomRightY =
                getMatrixScaleY() * this.mBitmap!!.height + getMatrixTranslateY() + getMatrixSkewY() * this.mBitmap!!.width

            canvas.save()
            canvas.drawBitmap(mBitmap!!, mMatrix, null)
            if (mInEdit) {
                val path = Path().apply {
                    moveTo(topLeftX, topLeftY)
                    lineTo(topRightX, topRightY)
                    lineTo(bottomRightX, bottomRightY)
                    lineTo(bottomLeftX, bottomLeftY)
                    lineTo(topLeftX, topLeftY)
                    close()
                }
                getBoundRegion(mImageRegion, path)
                canvas.drawPath(path, mDotBoundPaint)
                canvas.restore()

                drawRotateButton(canvas, bottomRightX, bottomRightY)
                if (mIsAdded) {
                    drawDeleteButton(canvas, topLeftX, topLeftY)
                    drawEditStickerButton(canvas, topRightX, topRightY)
                }

            }

        }
    }

    fun getOutBitmap(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, mMatrix, null)
    }

    private fun drawRotateButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        Path().apply {
            addRect(0f, 0f, mButtonRadius * 2, mButtonRadius * 2, Path.Direction.CCW)
            offset(offsetX - mButtonRadius, offsetY - mButtonRadius)
            getBoundRegion(mRotateButtonRegion, this)
        }
        val recf = RectF(
            offsetX - mButtonRadius,
            offsetY - mButtonRadius,
            offsetX + mButtonRadius,
            offsetY + mButtonRadius
        )
        canvas?.drawBitmap(mScaleBitmap, null, recf, null)
    }

    private fun drawDeleteButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        Path().apply {
            addRect(0f, 0f, mButtonRadius * 2, mButtonRadius * 2, Path.Direction.CCW)
            offset(offsetX - mButtonRadius, offsetY - mButtonRadius)
            getBoundRegion(mDeleteButtonRegion, this)
        }
        val recf = RectF(
            offsetX - mButtonRadius,
            offsetY - mButtonRadius,
            offsetX + mButtonRadius,
            offsetY + mButtonRadius
        )
        canvas?.drawBitmap(mDeleteBitmap, null, recf, null)
    }

    private fun drawEditStickerButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        Path().apply {
            addRect(0f, 0f, mButtonRadius * 2, mButtonRadius * 2, Path.Direction.CCW)
            offset(offsetX - mButtonRadius, offsetY - mButtonRadius)
            getBoundRegion(mEditButtonRegion, this)
        }
        val recf = RectF(
            offsetX - mButtonRadius,
            offsetY - mButtonRadius,
            offsetX + mButtonRadius,
            offsetY + mButtonRadius
        )
        canvas?.drawBitmap(mEditTextBitmap, null, recf, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!mInEdit) return false
        requestFocus()
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchPointX = event.rawX
                mTouchPointY = event.rawY
                when {
                    mRotateButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        mIsRotateMode = true

                        val arrayOfFloat = FloatArray(9)
                        mMatrix.getValues(arrayOfFloat)

                        val topLeftX = getMatrixTranslateX()
                        val topLeftY = getMatrixTranslateY()
                        val bottomRightX = event.x
                        val bottomRightY = event.y
                        mMidPointX = (topLeftX + bottomRightX) / 2
                        mMidPointY = (topLeftY + bottomRightY) / 2
                        mDiagonalLength =
                            hypot(bottomRightX - mMidPointX, bottomRightY - mMidPointY)
                        mLastDegrees = Math.toDegrees(
                            atan2(
                                (bottomRightY - mMidPointY),
                                (bottomRightX - mMidPointX)
                            ).toDouble()
                        ).toFloat()
                        return true
                    }
                    mDeleteButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        if (!mIsAdded) return false
                        deleteCallback?.invoke()
                    }
                    mEditButtonRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        if (!mIsAdded) return false
                        editCallback?.invoke(this)
                    }
                    mImageRegion.contains(event.x.toInt(), event.y.toInt()) -> {
                        mIsMotion = true
                        return true
                    }
                    else -> return false
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsRotateMode) {
                    rotate(event.x, event.y)
                } else if (mIsMotion) {
                    motionView(event.rawX, event.rawY)
                }
                return true

            }
            MotionEvent.ACTION_UP -> {
                mIsRotateMode = false
                mIsMotion = false
                return true
            }
        }

        return false
    }

    private fun showKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInputFromWindow(applicationWindowToken, InputMethodManager.SHOW_FORCED, 0)
    }

    private fun motionView(rawX: Float, rawY: Float) {
        mMatrix.postTranslate(rawX - mTouchPointX, rawY - mTouchPointY)
        mTouchPointX = rawX
        mTouchPointY = rawY
        invalidate()
    }

    private fun rotate(rawX: Float, rawY: Float) {
        val toDiagonalLength = hypot((rawX) - mMidPointX, (rawY) - mMidPointY)
        val scale = toDiagonalLength / mDiagonalLength
        mMatrix.postScale(scale, scale, mMidPointX, mMidPointY)
        mDiagonalLength = toDiagonalLength

        val toDegrees =
            Math.toDegrees(atan2((rawY - mMidPointY), (rawX - mMidPointX)).toDouble()).toFloat()

        val degrees = toDegrees - mLastDegrees
        mMatrix.postRotate(degrees, mMidPointX, mMidPointY)
        mLastDegrees = toDegrees
        mMatrix.getValues(mArrayOfFloat)

        invalidate()
    }

    fun setInEdit(inEdit: Boolean) {
        mInEdit = inEdit
        invalidate()
    }

    private fun getTextWidth(text: String, paint: Paint): Float {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width().toFloat() + 80
    }

    private fun getTextHeight(paint: Paint): Float {
        val text =
            "qwertyuiop[]asdfghjkl;'\\zxcvbnm,./QWERTYUIOP{}ASDFGHJKL:\"|ZXCVBNM<>?1234567890-=!@#$%^&*()_+`~"
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.height().toFloat()
    }

    private fun getMatrixTranslateX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MTRANS_X]
    }

    private fun getMatrixTranslateY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MTRANS_Y]
    }

    private fun getMatrixScaleX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSCALE_X]
    }

    private fun getMatrixScaleY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSCALE_Y]
    }

    private fun getMatrixSkewX(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSKEW_X]
    }

    private fun getMatrixSkewY(): Float {
        mMatrix.getValues(mArrayOfFloat)
        return mArrayOfFloat[Matrix.MSKEW_Y]
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

    fun changeFonts(fontId: Int) {
        if (mFontId == fontId) return
        mFontId = fontId
        mTextPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, mFontId), mTextStyle)
        calculatorLineText()
    }

    fun changeAlign(align: AlignMode) {
        if (mAlignMode == align) return
        mAlignMode = align
        calculatorLineText()
    }

    fun changeTextStyle(textStyle: Int) {
        if (mTextStyle == textStyle) return
        mTextStyle = textStyle
        mTextPaint.typeface = Typeface.create(ResourcesCompat.getFont(context, mFontId), mTextStyle)
        calculatorLineText()
    }

    fun changeTextFlag(flag: Int) {
        mFlag = if (mFlag == flag) {
            Paint.ANTI_ALIAS_FLAG
        } else {
            flag
        }

        mTextPaint.apply {
            isAntiAlias = true
            flags = mFlag
        }
        calculatorLineText()
    }

    fun changeColor(color: Int) {
        if (mTextColor != color) {
            mTextColor = color
            mTextPaint.color = mTextColor
            calculatorLineText()
        }
    }

    fun changeIsAdded(isAdded: Boolean) {
        mIsAdded = isAdded
        invalidate()
    }

    fun getMainText(): String {
        return mMainText
    }

    enum class AlignMode {
        LEFT, CENTER, RIGHT
    }

    fun getTextAttrData(): TextStickerAttrData {
        return TextStickerAttrData(mMainText, mTextColor, mFontId, mAlignMode, mTextStyle, mFlag)
    }

    fun setAttr(textStickerAttrData: TextStickerAttrData) {
        textStickerAttrData.apply {
            setText("")
            append(text)
            mMainText = text
            changeColor(textColor)
            changeAlign(alignMode)
            changeTextStyle(textStyle)
            changeFonts(fontId)
            changeTextFlag(flag)
        }

    }

}