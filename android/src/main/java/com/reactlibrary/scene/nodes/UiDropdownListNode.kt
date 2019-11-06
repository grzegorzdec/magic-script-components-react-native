/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.icons.IconsRepository
import com.reactlibrary.scene.nodes.base.Layoutable
import com.reactlibrary.scene.nodes.layouts.UiLinearLayout
import com.reactlibrary.scene.nodes.layouts.manager.LinearLayoutManagerImpl
import com.reactlibrary.scene.nodes.views.CustomButton
import com.reactlibrary.utils.logMessage

class UiDropdownListNode(initProps: ReadableMap,
                         context: Context,
                         viewRenderableLoader: ViewRenderableLoader,
                         fontProvider: FontProvider,
                         iconsRepo: IconsRepository
) : UiButtonNode(initProps, context, viewRenderableLoader, fontProvider, iconsRepo), Layoutable {

    companion object {
        const val PROP_LIST_MAX_HEIGHT = "listMaxHeight"
        const val PROP_LIST_TEXT_SIZE = "listTextSize"
        const val PROP_CHARACTERS_LIMIT = "maxCharacterLimit" // for list item
        const val PROP_MULTI_SELECT = "multiSelect"
        const val PROP_SHOW_LIST = "showList"
        const val PROP_SELECTED = "selected"
    }

    // Events
    var onSelectionChangedListener: ((itemIndex: Int) -> Unit)? = null
    var onListVisibilityChanged: ((isVisible: Boolean) -> Unit)? = null

    private val listNode: UiLinearLayout
    private var lastSelectedItem: UiDropdownListItemNode? = null

    init {
        val listProps = JavaOnlyMap()
        listProps.putString(PROP_ALIGNMENT, "top-left")
        listProps.putString(UiLinearLayout.PROP_ORIENTATION, "vertical")
        listProps.putString(UiLinearLayout.PROP_DEFAULT_ITEM_ALIGNMENT, "top-left")
        listNode = UiLinearLayout(listProps, LinearLayoutManagerImpl())

        properties.putString(PROP_ICON, "arrow-down")
    }

    override fun build() {
        super.build()
        listNode.build()
        (view as CustomButton).iconPosition = CustomButton.IconPosition.RIGHT
    }

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        val listItems = contentNode.children.filterIsInstance<UiDropdownListItemNode>()
        configureListItems(listItems, props)
        setShowList(props)
    }

    override fun addContent(child: Node) {
        if (child is UiDropdownListItemNode) {
            configureListItems(listOf(child), properties)
            child.onSelectedListener = {
                lastSelectedItem?.isSelected = false
                lastSelectedItem = child
                val index = listNode.contentNode.children.size
                onSelectionChangedListener?.invoke(index)
                logMessage("on item selected, index= $index")
            }
            listNode.addContent(child)
        } else {
            super.addContent(child)
        }
    }

    override fun removeContent(child: Node) {
        if (child is UiDropdownListItemNode) {
            listNode.removeContent(child)
            if (child == lastSelectedItem) {
                lastSelectedItem = null
            }
        } else {
            super.removeContent(child)
        }
    }

    override fun onViewClick() {
        super.onViewClick()
        if (isListVisible()) {
            hideList()
        } else {
            showList()
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        super.onUpdate(frameTime)

        val bounding = getContentBounding()
        val listX = bounding.left
        val listY = bounding.bottom - (bounding.top - bounding.bottom) / 3
        listNode.localPosition = Vector3(listX, listY, 0F)
    }

    private fun configureListItems(items: List<UiDropdownListItemNode>, props: Bundle) {
        setListTextSize(items, props)
        setCharactersLimit(items, props)
    }

    private fun setListTextSize(items: List<UiDropdownListItemNode>, props: Bundle) {
        if (props.containsKey(PROP_LIST_TEXT_SIZE)) {
            val textSize = props.getDouble(PROP_LIST_TEXT_SIZE)
            items.forEach { item ->
                item.update(JavaOnlyMap.of(UiTextNode.PROP_TEXT_SIZE, textSize))
            }
        }
    }

    private fun setCharactersLimit(items: List<UiDropdownListItemNode>, props: Bundle) {
        if (props.containsKey(PROP_CHARACTERS_LIMIT)) {
            val charsLimit = props.getDouble(PROP_CHARACTERS_LIMIT).toInt()
            items.forEach { item ->
                item.maxCharacters = charsLimit
            }
        }
    }

    private fun setShowList(props: Bundle) {
        if (props.containsKey(PROP_SHOW_LIST)) {
            val show = props.getBoolean(PROP_SHOW_LIST)
            if (show && !isListVisible()) {
                showList()
            } else if (isListVisible()) {
                hideList()
            }
        }
    }

    private fun isListVisible(): Boolean {
        return contentNode.children.contains(listNode)
    }

    private fun showList() {
        addContent(listNode)
        onListVisibilityChanged?.invoke(true)
    }

    private fun hideList() {
        removeContent(listNode)
        onListVisibilityChanged?.invoke(false)
    }

}