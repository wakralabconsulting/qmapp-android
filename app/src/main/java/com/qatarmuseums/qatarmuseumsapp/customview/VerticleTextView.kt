package com.qatarmuseums.qatarmuseumsapp.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class VerticleTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    private val topDown: Boolean

    init {
        val gravity = gravity
        topDown = if (Gravity.isVertical(gravity) && gravity and Gravity.VERTICAL_GRAVITY_MASK === Gravity.BOTTOM) {
            setGravity(gravity and Gravity.HORIZONTAL_GRAVITY_MASK or Gravity.TOP)
            false
        } else
            true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(canvas: Canvas) {
        val textPaint = paint
        textPaint.color = currentTextColor
        textPaint.drawableState = drawableState

        canvas.save()
        if (topDown) {
            canvas.translate(width.toFloat(), 0F)
            canvas.rotate((90).toFloat())
        } else {
            canvas.translate(0F, height.toFloat())
            canvas.rotate((-90).toFloat())
        }

        canvas.translate(compoundPaddingLeft.toFloat(), extendedPaddingTop.toFloat())

        layout.draw(canvas)
        canvas.restore()
    }
}