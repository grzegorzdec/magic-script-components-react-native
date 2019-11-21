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

package com.reactlibrary.scene.nodes

import android.content.Context
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.ar.ViewRenderableLoader
import com.reactlibrary.font.FontProvider
import com.reactlibrary.icons.IconsRepository

// As per documentation, UiTabNode is basically a limited button node.
class UiTabNode(
        props: ReadableMap,
        context: Context,
        viewRenderableLoader: ViewRenderableLoader,
        fontProvider: FontProvider,
        iconsRepository: IconsRepository
) : UiButtonNode(props, context, viewRenderableLoader, fontProvider, iconsRepository)
