/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import com.reactlibrary.utils.Vector2
import kotlin.math.min

class CustomButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class IconPosition {
        LEFT, RIGHT
    }

    var text = ""
        set(value) {
            field = value
            invalidate()
            requestLayout() // need to measure the view
        }

    var roundnessFactor = 1F // from 0 to 1 (fully rounded)
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Icon size in pixels
     */
    var iconSize: Vector2 = Vector2(0F, 0F)
        set(value) {
            field = value
            if (iconBitmap != null) {
                invalidate()
            }
        }

    var iconPosition = IconPosition.LEFT
        set(value) {
            field = value
            if (iconBitmap != null) {
                invalidate()
            }
        }

    // border width = shorter button dimension * borderWidthFactor
    private val borderWidthFactor = 0.07F
    private val defaultIconHeightFactor = 0.65F // when size not provided
    private val iconSpacingFactor = 0.3F // spacing offset from text (relative to icon width)
    private var textPaddingHorizontal = 0
    private var textPaddingVertical = 0
    private var iconBitmap: Bitmap? = null

    private val textPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, android.R.color.white)
        textSize = 12F
    }

    private val iconPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = getColor(context, android.R.color.white)
        colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private val bgPaint = Paint(ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = getColor(context, android.R.color.white)
    }

    private val textBounds = Rect()
    private val iconBounds = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        textPaint.getTextBounds(text, 0, text.length, textBounds)
        val defaultWidth = textBounds.width() + 2 * textPaddingHorizontal
        val defaultHeight = textBounds.height() + 2 * textPaddingVertical

        val width: Int = if (widthMode == MeasureSpec.EXACTLY) { // exact size
            widthSize
        } else { // WRAP_CONTENT
            defaultWidth
        }

        val height: Int = if (heightMode == MeasureSpec.EXACTLY) { // exact size
            heightSize
        } else { // WRAP_CONTENT
            defaultHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)

        // draw icon if provided
        val icon = iconBitmap
        if (icon != null) {
            if (iconSize.x == 0F && iconSize.y == 0F) {
                val iconHeight = defaultIconHeightFactor * this.height
                val iconWidth = icon.width / icon.height * iconHeight
                iconSize = Vector2(iconWidth, iconHeight)
            }
            drawIcon(canvas, icon)
        }

        // draw text
        val textOffsetX = when {
            icon == null -> 0F
            iconPosition == IconPosition.LEFT -> iconSize.x / 2F
            else -> -iconSize.x / 2F
        }
        drawText(canvas, textOffsetX)
    }

    fun setTextSize(textSizePx: Float) {
        textPaint.textSize = textSizePx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
        invalidate()
    }

    fun setIconColor(color: Int) {
        iconPaint.color = color
        iconPaint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        invalidate()
    }

    fun setTextPadding(paddingHorizontalPx: Int, paddingVerticalPx: Int) {
        textPaddingHorizontal = paddingHorizontalPx
        textPaddingVertical = paddingVerticalPx
        invalidate()
        requestLayout() // need to measure the view
    }

    fun setTypeface(typeface: Typeface) {
        textPaint.typeface = typeface
    }

    /**
     * Sets an icon for the button or removes current icon (when passed null)
     */
    fun setIcon(drawable: Drawable?) {
        if (drawable != null) {
            val icon = drawableToBitmap(drawable)
            this.iconBitmap = icon
        } else {
            this.iconBitmap = null
        }
        invalidate()
    }

    private fun drawBackground(canvas: Canvas) {
        val strokeSize = borderWidthFactor * min(width, height)
        val radius = (height.toFloat() - strokeSize) / 2 * roundnessFactor

        bgPaint.strokeWidth = strokeSize
        canvas.drawRoundRect(
                strokeSize / 2,
                strokeSize / 2,
                width.toFloat() - strokeSize / 2,
                height.toFloat() - strokeSize / 2,
                radius,
                radius,
                bgPaint
        )
    }

    private fun drawIcon(canvas: Canvas, icon: Bitmap) {
        val offsetX = if (text.isNotEmpty()) iconSpacingFactor * iconSize.x else 0F
        if (iconPosition == IconPosition.LEFT) {
            iconBounds.right = this.width / 2 - textBounds.width() / 2F + iconSize.x / 2 - offsetX
            iconBounds.left = iconBounds.right - iconSize.x
        } else {
            iconBounds.left = this.width / 2 + textBounds.width() / 2F - iconSize.x / 2 + offsetX
            iconBounds.right = iconBounds.left + iconSize.x
        }
        iconBounds.top = (this.height - iconSize.y) / 2F
        iconBounds.bottom = iconBounds.top + iconSize.y
        canvas.drawBitmap(icon, null, iconBounds, iconPaint)
    }

    private fun drawText(canvas: Canvas, offsetFromCenterX: Float) {
        val centerX = width / 2
        val centerY = height / 2
        val textX = centerX - textBounds.exactCenterX() + offsetFromCenterX
        val textY = centerY - textBounds.exactCenterY()
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }
        val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}