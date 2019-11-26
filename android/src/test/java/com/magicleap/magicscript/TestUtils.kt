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

package com.magicleap.magicscript

import android.os.Bundle
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyMap
import com.magicleap.magicscript.scene.nodes.base.TransformNode

fun createProperty(vararg keysAndValues: Any): Bundle =
        Arguments.toBundle(JavaOnlyMap.of(*keysAndValues)) ?: Bundle()

fun View.createActionDownEvent(): MotionEvent {
    val coordinates = IntArray(2)
    getLocationOnScreen(coordinates)
    return MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            MotionEvent.ACTION_DOWN,
            coordinates[0].toFloat(),
            coordinates[1].toFloat(),
            0)
}

fun <T: TransformNode> T.update(vararg keysAndValues: Any) {
    update(JavaOnlyMap.of(*keysAndValues))
}