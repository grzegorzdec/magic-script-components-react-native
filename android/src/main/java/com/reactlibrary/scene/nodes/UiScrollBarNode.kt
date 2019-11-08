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

package com.reactlibrary.scene.nodes

import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.scene.nodes.base.TransformNode
import com.reactlibrary.utils.putDefaultDouble
import com.reactlibrary.utils.putDefaultString

/**
 * It is only a node that hold information needed by ScrollViewNode
 * (it's mapped to a native scroll bar view)
 */
class UiScrollBarNode(initProps: ReadableMap) :
        TransformNode(initProps, false, useContentNodeAlignment = false) {

    companion object {
        // properties
        const val PROP_WIDTH = "width"
        const val PROP_HEIGHT = "height"
        const val PROP_THUMB_POSITION = "thumbPosition"
        const val PROP_THUMB_SIZE = "thumbSize"
        const val PROP_ORIENTATION = "orientation"

        const val ORIENTATION_VERTICAL = "vertical"
        const val ORIENTATION_HORIZONTAL = "horizontal"

        const val DEFAULT_ORIENTATION = ORIENTATION_VERTICAL
        const val DEFAULT_WIDTH = 0.04
        const val DEFAULT_HEIGHT = 1.2
        const val DEFAULT_THUMB_SIZE = 0.0
        const val DEFAULT_THUMB_POSITION = 0.0
    }

    fun getWidth(): Float {
        return properties.getDouble(PROP_WIDTH, DEFAULT_WIDTH).toFloat()
    }

    fun getHeight(): Float {
        return properties.getDouble(PROP_HEIGHT, DEFAULT_HEIGHT).toFloat()
    }

    fun getThumbPosition(): Float {
        return properties.getDouble(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION).toFloat()
    }

    fun getThumbSize(): Float {
        return properties.getDouble(PROP_THUMB_SIZE, DEFAULT_THUMB_SIZE).toFloat()
    }

    fun getOrientation(): String {
        return properties.getString(PROP_ORIENTATION, DEFAULT_ORIENTATION)
    }

    init {
        // set default properties values
        properties.putDefaultDouble(PROP_WIDTH, DEFAULT_WIDTH)
        properties.putDefaultDouble(PROP_HEIGHT, DEFAULT_HEIGHT)
        properties.putDefaultDouble(PROP_THUMB_POSITION, DEFAULT_THUMB_POSITION)
        properties.putDefaultDouble(PROP_THUMB_SIZE, DEFAULT_THUMB_SIZE)
        properties.putDefaultString(PROP_ORIENTATION, ORIENTATION_VERTICAL)
    }

}
