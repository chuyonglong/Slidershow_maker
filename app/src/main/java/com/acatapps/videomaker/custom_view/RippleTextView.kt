package com.acatapps.videomaker.custom_view

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import com.acatapps.videomaker.R
import kotlin.math.max
import kotlin.math.sqrt

class RippleTextView : AppCompatTextView {

    private var mCornerRadius = 0f
    private var mBgColor = Color.TRANSPARENT
    protected var onClick:(()->Unit)? = null
    protected var instanClick:(()->Unit)? = null
    private var isPress = false
    private val mBgPaint = Paint()
    private val mRipplePaint = Paint()
    private var mCurrentRadius = 0
    private var mMaxRadius = 0f
    private var mCurrentX = 0f
    private var mCurrentY = 0f
    constructor(context: Context) : super(context) {
        initAttrs(null)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initAttrs(attributes)
    }

    private fun initAttrs(attrs:AttributeSet?) {
        if(attrs == null) return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleTextView)
        mCornerRadius = typedArray.getDimension(R.styleable.RippleTextView_cornerRadiusTextView, 0f)
        mBgColor = typedArray.getColor(R.styleable.RippleTextView_bgColorTextView, Color.TRANSPARENT)
        typedArray.recycle()

        mBgPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = mBgColor
        }

        mRipplePaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = Color.parseColor("#4DFFFFFF")
        }

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(getClipPath())
        canvas?.drawPath(getClipPath(), mBgPaint)
        super.onDraw(canvas)

        if(isPress) {
            canvas?.drawCircle(mCurrentX, mCurrentY, mCurrentRadius.toFloat(), mRipplePaint)
        }
    }



    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_UP) {
            instanClick?.invoke()
            mCurrentX = event.x
            mCurrentY = event.y
            mCurrentRadius = 0
            mMaxRadius = max((sqrt(mCurrentX*mCurrentX+mCurrentY*mCurrentY)), sqrt((width-mCurrentX) *(width-mCurrentX)+(height-mCurrentY)*(height-mCurrentY))) + 100f
            drawRipple()
        }



        return true
    }

    fun getClipPath(): Path {
        val path = Path()
        path.reset()
        path.addRoundRect(RectF(0f,0f,width.toFloat(), height.toFloat()), mCornerRadius, mCornerRadius, Path.Direction.CW)
        path.close()
        return path
    }

    private fun drawRipple() {
        isPress = true
        val animator = ValueAnimator.ofFloat(0f,mMaxRadius)
        animator.addUpdateListener {
            it.animatedFraction
            mCurrentRadius = (mMaxRadius*it.animatedFraction).toInt()
            invalidate()
        }
        animator.duration = 200
        animator.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                onClick?.invoke()
                isPress = false
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        animator.start()
    }

    fun setClick(onClick:()->Unit) {
        this.onClick = onClick
    }
    fun setInstanceClick(onClick:()->Unit) {
        this.instanClick = onClick
    }

}