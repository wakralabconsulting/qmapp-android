package com.qatarmuseums.qatarmuseumsapp.customview

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class VerticleTextView(context: Context, attrs: AttributeSet) : TextView(context, attrs) {

    internal val topDown: Boolean

    init {
        val gravity = getGravity()
        if (Gravity.isVertical(gravity) && gravity and Gravity.VERTICAL_GRAVITY_MASK === Gravity.BOTTOM) {
            setGravity(gravity and Gravity.HORIZONTAL_GRAVITY_MASK or Gravity.TOP)
            topDown = false
        } else
            topDown = true
    }

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth())
    }

    protected override fun onDraw(canvas: Canvas) {
        val textPaint = getPaint()
        textPaint.setColor(getCurrentTextColor())
        textPaint.drawableState = getDrawableState()

        canvas.save()
        if (topDown) {
            canvas.translate(getWidth().toFloat(), 0F)
            canvas.rotate((90).toFloat())
        } else {
            canvas.translate(0F, getHeight().toFloat())
            canvas.rotate((-90).toFloat())
        }

        canvas.translate(getCompoundPaddingLeft().toFloat(), getExtendedPaddingTop().toFloat())

        getLayout().draw(canvas)
        canvas.restore()
    }
}