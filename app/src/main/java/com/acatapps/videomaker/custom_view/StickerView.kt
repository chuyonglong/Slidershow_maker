package com.acatapps.videomaker.custom_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Utils
import kotlin.math.atan2
import kotlin.math.hypot

class StickerView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {

    private val mMatrix = Matrix()
    private var mBitmap: Bitmap? = null
    private val mPaint = Paint()
    private val mBluePaint = Paint()
    private val mArrayOfFloat = FloatArray(9)
    private val mDotBoundPaint = Paint()
    private var mDotHeight = 1f
    private var mDensity = 1f
    private var mButtonRadius = 12f
    private val mButtonPaint = Paint()
    private val mRotateButtonRegion = Region()
    private val mDeleteButtonRegion = Region()
    private val mImageRegion = Region()
    private var mInEdit = false
    val inEdit get() = mInEdit

    private val mScaleBitmap: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_scale_sticker)
    private val mDeleteBitmap =
        BitmapFactory.decodeResource(resources, R.drawable.ic_cancel_transform)

    var deleteCallback: (() -> Unit)? = null

    init {
        FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        mDensity = Utils.density(context)
        mDotHeight *= mDensity
        mButtonRadius *= mDensity
        mPaint.apply {
            isAntiAlias = true
            color = Color.BLACK

        }

        mBluePaint.apply {
            isAntiAlias = true
            color = Color.BLUE
        }

        mDotBoundPaint.apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(5f * mDensity, 5f * mDensity), 0f)
            strokeWidth = mDotHeight
        }

        mButtonPaint.apply {
            color = Color.GREEN
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }


    override fun onDraw(canvas: Canvas) {
        mBitmap?.let {
            val arrayOfFloat = FloatArray(9)
            mMatrix.getValues(arrayOfFloat)

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
                drawDeleteButton(canvas, topLeftX, topLeftY)
            }

        }

    }

    fun getOutBitmap(canvas: Canvas) {
        canvas.drawBitmap(mBitmap!!, mMatrix, null)
    }

    private fun drawBoundPath(canvas: Canvas, path: Path) {

    }

    private fun drawRotateButton(canvas: Canvas?, offsetX: Float, offsetY: Float) {
        val path = Path().apply {
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
        val path = Path().apply {
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

    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            mMatrix.postTranslate(100f, 100f)
            mBitmap = it
            requestLayout()
        }

    }

    fun setBitmap(bitmap: Bitmap?, inEdit: Boolean) {
        bitmap?.let {
            mMatrix.postTranslate(100f, 100f)
            mBitmap = it
            requestLayout()
            setInEdit(inEdit)
        }

    }

    fun setBitmap(bitmap: Bitmap?, inEdit: Boolean, parentWidth: Int, parentHeight: Int) {
        bitmap?.let {
            var limitX = parentWidth - bitmap.width
            if (limitX <= 0) limitX = 1
            var limitY = parentWidth - bitmap.height
            if (limitY <= 0) limitY = 1
            mMatrix.postTranslate((0..limitX).random().toFloat(), (0..limitY).random().toFloat())
            mBitmap = it
            requestLayout()
            setInEdit(inEdit)
        }

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


    private var mTouchPointX = 0f
    private var mTouchPointY = 0f
    private var mIsRotateMode = false
    private var mIsMotion = false
    var mDiagonalLength = 0f
    var mMidPointX = 0f
    var mMidPointY = 0f
    var mLastDegrees = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!mInEdit) return false
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mTouchPointX = event.rawX
            mTouchPointY = event.rawY
            if (mRotateButtonRegion.contains(event.x.toInt(), event.y.toInt())) {
                mIsRotateMode = true

                val arrayOfFloat = FloatArray(9)
                mMatrix.getValues(arrayOfFloat)

                val topLeftX = getMatrixTranslateX()
                val topLeftY = getMatrixTranslateY()
                val bottomRightX = event.x
                val bottomRightY = event.y
                mMidPointX = (topLeftX + bottomRightX) / 2
                mMidPointY = (topLeftY + bottomRightY) / 2
                mDiagonalLength = hypot(bottomRightX - mMidPointX, bottomRightY - mMidPointY)
                mLastDegrees = Math.toDegrees(
                    atan2(
                        (bottomRightY - mMidPointY),
                        (bottomRightX - mMidPointX)
                    ).toDouble()
                ).toFloat()
                return true
            } else if (mDeleteButtonRegion.contains(event.x.toInt(), event.y.toInt())) {
                deleteCallback?.invoke()
            } else if (mImageRegion.contains(event.x.toInt(), event.y.toInt())) {
                mIsMotion = true
                return true
            } else return false

        } else if (event?.action == MotionEvent.ACTION_MOVE) {
            if (mIsRotateMode) {
                rotate(event.x, event.y)
            } else if (mIsMotion) {
                motionView(event.rawX, event.rawY)
            }
            return true

        } else if (event?.action == MotionEvent.ACTION_UP) {
            mIsRotateMode = false
            mIsMotion = false
            return true
        }

        return false
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

    fun getBitmap(): Bitmap? = mBitmap
}