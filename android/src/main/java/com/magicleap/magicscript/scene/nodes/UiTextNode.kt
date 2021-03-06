/*
 * Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.R
import com.magicleap.magicscript.ar.clip.Clipper
import com.magicleap.magicscript.ar.renderable.ViewRenderableLoader
import com.magicleap.magicscript.font.FontParams
import com.magicleap.magicscript.font.FontProvider
import com.magicleap.magicscript.scene.nodes.base.UiNode
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.Vector2
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.readColor

open class UiTextNode(
    initProps: ReadableMap,
    context: Context,
    viewRenderableLoader: ViewRenderableLoader,
    nodeClipper: Clipper,
    private val fontProvider: FontProvider
) : UiNode(initProps, context, viewRenderableLoader, nodeClipper) {

    companion object {
        // properties
        const val PROP_TEXT = "text"
        const val PROP_TEXT_SIZE = "textSize"
        const val PROP_BOUNDS_SIZE = "boundsSize"
        const val PROP_WRAP = "wrap"
        const val PROP_TEXT_ALIGNMENT = "textAlignment"
        const val PROP_TEXT_COLOR = "textColor"
        const val PROP_CHARACTERS_SPACING = "charSpacing"
        const val PROP_LINE_SPACING = "lineSpacing"

        const val DEFAULT_TEXT_SIZE = 0.025 // in meters
        const val DEFAULT_ALIGNMENT = "bottom-left" // view alignment (pivot)
        const val DEFAULT_WRAP = true
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_TEXT_SIZE, DEFAULT_TEXT_SIZE)
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.text, null) as TextView
    }

    override fun provideDesiredSize(): Vector2 {
        return readBoundsSize()
    }

    override fun setupView() {
        super.setupView()

        val wrap = readWrapProperty(properties)
        if (!wrap) {
            (view as TextView).setSingleLine(true)
        }

        val fontParams = FontParams.fromBundle(properties)
        if (fontParams.style == null && fontParams.weight == null) {
            // setting a default typeface
            (view as TextView).typeface = fontProvider.provideFont()
        }
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            setNeedsRebuild()
        }

        setText(props)
        setTextSize(props)
        setTextAlignment(props)
        setTextColor(props)
        setCharactersSpacing(props)
        setLineSpacing(props)
        setFontParams(props)
    }

    private fun readBoundsSize(): Vector2 {
        if (properties.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = properties.get(PROP_BOUNDS_SIZE) as Bundle
            val bounds = boundsData.getSerializable(PROP_BOUNDS_SIZE) as ArrayList<Double>
            val width = bounds[0].toFloat()
            val height = bounds[1].toFloat()
            return Vector2(width, height)
        } else {
            return Vector2(WRAP_CONTENT_DIMENSION, WRAP_CONTENT_DIMENSION)
        }
    }

    private fun readWrapProperty(props: Bundle): Boolean {
        if (props.containsKey(PROP_BOUNDS_SIZE)) {
            val boundsData = props.get(PROP_BOUNDS_SIZE) as Bundle
            return boundsData.getBoolean(PROP_WRAP, DEFAULT_WRAP)
        }
        return DEFAULT_WRAP
    }

    private fun canResizeOnContentChange(): Boolean {
        val bounds = readBoundsSize()
        return bounds.x == WRAP_CONTENT_DIMENSION || bounds.y == WRAP_CONTENT_DIMENSION
    }

    protected open fun setText(props: Bundle) {
        val text = props.getString(PROP_TEXT)
        if (text != null) {
            (view as TextView).text = text
            // rebuild only if size can be changed
            if (canResizeOnContentChange()) {
                setNeedsRebuild()
            }
        }
    }

    private fun setTextSize(props: Bundle) {
        if (props.containsKey(PROP_TEXT_SIZE)) {
            val sizeMeters = props.getDouble(PROP_TEXT_SIZE).toFloat()
            setTextSize(sizeMeters)
        }
    }

    private fun setTextSize(sizeMeters: Float) {
        val size = Utils.metersToFontPx(sizeMeters, view.context).toFloat()
        (view as TextView).setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
        // rebuild only if size can be changed
        if (canResizeOnContentChange()) {
            setNeedsRebuild()
        }
    }

    private fun setTextAlignment(props: Bundle) {
        when (props.getString(PROP_TEXT_ALIGNMENT)) {
            "left" -> {
                (view as TextView).gravity = Gravity.LEFT
            }
            "center" -> {
                (view as TextView).gravity = Gravity.CENTER_HORIZONTAL
            }
            "right" -> {
                (view as TextView).gravity = Gravity.RIGHT
            }
            "bottom-center" -> {
                (view as TextView).gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
        }
    }

    private fun setTextColor(props: Bundle) {
        val color = props.readColor(PROP_TEXT_COLOR)
        if (color != null) {
            (view as TextView).setTextColor(color)
        }
    }

    private fun setCharactersSpacing(props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_SPACING)) {
            val spacing = props.getDouble(PROP_CHARACTERS_SPACING)
            setCharactersSpacing(spacing)
        }
    }

    private fun setCharactersSpacing(spacing: Double) {
        (view as TextView).letterSpacing = spacing.toFloat()
        // rebuild only if size can be changed
        if (canResizeOnContentChange()) {
            setNeedsRebuild()
        }
    }

    private fun setLineSpacing(props: Bundle) {
        if (props.containsKey(PROP_LINE_SPACING)) {
            val spacingMultiplier = props.getDouble(PROP_LINE_SPACING).toFloat()
            (view as TextView).setLineSpacing(0F, spacingMultiplier)
        }
    }

    private fun setFontParams(props: Bundle) {
        val fontParams = FontParams.fromBundle(props)

        if (fontParams.style != null || fontParams.weight != null) {
            (view as TextView).typeface =
                fontProvider.provideFont(fontParams.style, fontParams.weight)
        }

        fontParams.allCaps?.let {
            (view as TextView).isAllCaps = it
        }

        fontParams.fontSize?.let {
            setTextSize(it.toFloat())
        }

        fontParams.tracking?.let {
            setCharactersSpacing(it)
        }
    }

}