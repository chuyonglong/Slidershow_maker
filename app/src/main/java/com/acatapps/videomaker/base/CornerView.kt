package com.acatapps.videomaker.base

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.acatapps.videomaker.R
import com.acatapps.videomaker.utils.Utils

class CornerView : View {
    private var mShadowRadius = 0f
    private var mShadowBottomOnly = false
    private var mCornerRadius = 0f
    private var mBgColor = Color.WHITE
    private val mBgPaint = Paint().apply {

    }

    constructor(context: Context?) : super(context) {
        initAttrs(null)
        init()

    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
        init()
    }

    private fun init() {
        mShadowRadius = Utils.density(context!!) * 3
        mBgPaint.apply {
            setShadowLayer(mShadowRadius, 1f, 1f, Color.parseColor("#e5e5e5"))
            isAntiAlias = true
            style = Paint.Style.FILL
            color = mBgColor
        }

    }

    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerView)
        mCornerRadius = typedArray.getDimension(R.styleable.CornerView_cornerRadiusView, 0f)
        mBgColor = typedArray.getColor(R.styleable.CornerView_bgColor, Color.GRAY)
        mShadowBottomOnly = typedArray.getBoolean(R.styleable.CornerView_shadowBottomOnly, false)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(getClipPath(), mBgPaint)
    }


    fun getClipPath(): Path {
        val path = Path()
        path.reset()
        if (mShadowBottomOnly) {
            path.addRoundRect(
                RectF(0f, 0f, width.toFloat(), height.toFloat() - mShadowRadius),
                mCornerRadius,
                mCornerRadius,
                Path.Direction.CW
            )
        } else {
            path.addRoundRect(
                RectF(
                    0f + mShadowRadius,
                    0f + mShadowRadius,
                    width.toFloat() - mShadowRadius,
                    height.toFloat() - mShadowRadius
                ), mCornerRadius, mCornerRadius, Path.Direction.CW
            )
        }

        path.close()
        return path
    }


}