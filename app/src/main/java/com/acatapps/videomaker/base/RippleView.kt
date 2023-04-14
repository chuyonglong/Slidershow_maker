package com.acatapps.videomaker.base

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.acatapps.videomaker.R
import kotlin.math.max
import kotlin.math.sqrt

open class RippleView : View {

    constructor(context: Context?) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context?, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private var onClick: (() -> Unit)? = null

    private val mLamda = 30f
    private var mCurrentRadius = 0

    private var mMaxRadius = 0f
    private var mCurrentX = 0f
    private var mCurrentY = 0f

    private var mCornerRadius = 0f

    private val mFillPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#4DFFFFFF")
        isAntiAlias = true
    }


    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView)
        mCornerRadius = typedArray.getDimension(R.styleable.RippleView_cornerRadius, 0f)
        typedArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN || event?.action == MotionEvent.ACTION_POINTER_DOWN || event?.action == MotionEvent.ACTION_BUTTON_PRESS) {

            mCurrentX = event.x
            mCurrentY = event.y
            mCurrentRadius = 0
            mMaxRadius = max(
                (sqrt(mCurrentX * mCurrentX + mCurrentY * mCurrentY)),
                sqrt((width - mCurrentX) * (width - mCurrentX) + (height - mCurrentY) * (height - mCurrentY))
            ) + 100f
            drawRipple()


        }

        return true
    }

    fun getClipPath(): Path {
        val path = Path()
        path.reset()
        path.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            mCornerRadius,
            mCornerRadius,
            Path.Direction.CW
        )
        path.close()
        return path
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(getClipPath())
        super.onDraw(canvas)
        canvas?.drawCircle(mCurrentX, mCurrentY, mCurrentRadius.toFloat(), mFillPaint)
    }

    private fun drawRipple() {
        var isPress = true
        val animator = ValueAnimator.ofFloat(0f, mMaxRadius)
        animator.setDuration(350)
            .addUpdateListener {
                it.animatedFraction
                mCurrentRadius = (mMaxRadius * it.animatedFraction).toInt()
                if (isPress)
                    invalidate()
            }
        animator.interpolator = LinearOutSlowInInterpolator()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

                onClick?.invoke()
                mCurrentRadius = 0
                isPress = false
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.start()
    }

    fun setClick(onClick: () -> Unit) {
        this.onClick = onClick
    }


}