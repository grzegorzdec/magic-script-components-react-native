/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.magicleap.magicscript.scene.nodes.layouts

import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.magicleap.magicscript.scene.nodes.base.UiLayout
import com.magicleap.magicscript.scene.nodes.layouts.manager.PageViewLayoutManager
import com.magicleap.magicscript.scene.nodes.props.Alignment
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.scene.nodes.props.Padding
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.putDefault
import com.magicleap.magicscript.utils.read

class PageViewNode(props: ReadableMap, layoutManager: LayoutManager) :
    UiLayout(props, layoutManager) {

    private var padding: Padding = Padding(0f, 0f, 0f, 0f)
    private var visiblePage: Int = 0

    companion object {
        // properties
        const val PROP_PADDING = "defaultPagePadding"
        const val PROP_CONTENT_ALIGNMENT = "defaultPageAlignment"
        const val PROP_VISIBLE_PAGE = "visiblePage"

        // default values
        const val DEFAULT_ALIGNMENT = "top-left"
        const val DEFAULT_CONTENT_ALIGNMENT = "top-left"
        const val DEFAULT_VISIBLE_PAGE = 0
        val DEFAULT_ITEM_PADDING = arrayListOf(0.0, 0.0, 0.0, 0.0)
    }

    init {
        // set default values of properties
        properties.putDefault(PROP_ALIGNMENT, DEFAULT_ALIGNMENT)
        properties.putDefault(PROP_CONTENT_ALIGNMENT, DEFAULT_CONTENT_ALIGNMENT)
        properties.putDefault(PROP_PADDING, DEFAULT_ITEM_PADDING)
        properties.putDefault(PROP_VISIBLE_PAGE, DEFAULT_VISIBLE_PAGE)
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)
        setItemPadding(props)
        setContentAlignment(props)
        setVisiblePage(props)
    }

    private fun setVisiblePage(props: Bundle) {
        props.read<Double>(PROP_VISIBLE_PAGE)?.let { page ->
            this.visiblePage = page.toInt()
            (layoutManager as PageViewLayoutManager).visiblePage = visiblePage
            requestLayout()
        }
    }

    override fun getContentBounding(): Bounding {
        val childBounds = Utils.calculateSumBounds(contentNode.children)
        val itemPadding = properties.read(PROP_PADDING) ?: Padding()
        val sizeX = if (width != WRAP_CONTENT_DIMENSION) width else childBounds.size().x
        val sizeY = if (height != WRAP_CONTENT_DIMENSION) height else childBounds.size().y

        return Bounding(
            -sizeX / 2 + contentNode.localPosition.x - itemPadding.left,
            -sizeY / 2 + contentNode.localPosition.y - itemPadding.bottom,
            sizeX / 2 + contentNode.localPosition.x + itemPadding.right,
            sizeY / 2 + contentNode.localPosition.y + itemPadding.top
        )
    }

    private fun setItemPadding(props: Bundle) {
        val padding = props.read<Padding>(PROP_PADDING)
        if (padding != null) {
            this.padding = padding
            (layoutManager as PageViewLayoutManager).itemPadding = padding
            requestLayout()
        }
    }

    private fun setContentAlignment(props: Bundle) {
        val alignment = props.read<Alignment>(PROP_CONTENT_ALIGNMENT)
        if (alignment != null) {
            val manager = layoutManager as PageViewLayoutManager
            manager.contentVerticalAlignment = alignment.vertical
            manager.contentHorizontalAlignment = alignment.horizontal
            requestLayout()
        }
    }
}