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
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.reactlibrary.utils.Vector2
import com.reactlibrary.utils.onLayoutListener

class CustomScrollView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val SCROLL_VERTICAL = "vertical"
        const val SCROLL_HORIZONTAL = "horizontal"
    }

    var contentSize = Vector2()
        set(value) {
            field = value
            updateScrollbars()
        }

    var onScrollChangeListener: ((on: Vector2) -> Unit)? = null

    var hBar: CustomScrollBar? = null
        set(value) {
            field = value
            updateScrollbars()
        }

    var vBar: CustomScrollBar? = null
        set(value) {
            field = value
            updateScrollbars()
        }

    var scrollDirection = ""

    var position = Vector2()
        private set

    private var isBeingDragged = false
    private var previousTouch = Vector2()

    init {
        this.onLayoutListener {
            updateScrollbars()
        }
    }

    override fun stopNestedScroll() {
        isBeingDragged = false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE) {
            stopNestedScroll()
            return false
        }

        val touch = Vector2(event.x, event.y)
        if (!isBeingDragged) {
            isBeingDragged = true
            previousTouch = touch
        }

        if (action == MotionEvent.ACTION_MOVE) {
            val movePx = previousTouch - touch
            val viewSize = Vector2(width.toFloat(), height.toFloat())
            val maxTravel = contentSize - viewSize
            val move = movePx / maxTravel

            when (scrollDirection) {
                SCROLL_VERTICAL -> move.x = 0F
                SCROLL_HORIZONTAL -> move.y = 0F
            }

            position = (position + move).coerceIn(0F, 1F)
            hBar?.thumbPosition = position.x
            vBar?.thumbPosition = position.y
            onScrollChangeListener?.invoke(position)
        }
        previousTouch = touch

        return true
    }

    // Update scrollbars when content size has changed.
    private fun updateScrollbars() {
        this.hBar?.thumbSize = width.toFloat() / contentSize.x
        this.vBar?.thumbSize = height.toFloat() / contentSize.y
    }
}