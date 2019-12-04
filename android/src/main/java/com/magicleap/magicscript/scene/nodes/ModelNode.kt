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

package com.magicleap.magicscript.scene.nodes

import android.content.Context
import android.os.Bundle
import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.magicleap.magicscript.ar.ModelRenderableLoader
import com.magicleap.magicscript.ar.RenderableResult
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.magicleap.magicscript.scene.nodes.props.Bounding
import com.magicleap.magicscript.utils.PropertiesReader

class ModelNode(initProps: ReadableMap,
                private val context: Context,
                private val modelRenderableLoader: ModelRenderableLoader)
    : TransformNode(initProps, hasRenderable = true, useContentNodeAlignment = true) {

    companion object {
        // properties
        const val PROP_MODEL_PATH = "modelPath"
        // initial resource scale applied when loading model (cannot be updated)
        const val PROP_IMPORT_SCALE = "importScale"

        const val DEFAULT_IMPORT_SCALE = 1.0
    }

    // localScale without importScale correction
    private var scale = localScale
    private var renderableCopy: Renderable? = null
    private var hidden = false

    override fun applyProperties(props: Bundle) {
        super.applyProperties(props)

        setModelPath(props)
        setImportScale(props)
    }

    override fun loadRenderable() {
        loadModel()
    }

    override fun setAlignment(props: Bundle) {
        // according to Lumin we cannot change alignment for Model
    }

    override fun setClipBounds(clipBounds: Bounding) {
        val contentPosition = getContentPosition()
        if (contentPosition.x in clipBounds.left..clipBounds.right
                && contentPosition.y in clipBounds.bottom..clipBounds.top) {
            if (contentNode.renderable == null) {
                contentNode.renderable = renderableCopy
            }
            hidden = false
        } else {
            contentNode.renderable = null
            hidden = true
        }
    }

    override fun onVisibilityChanged(visibility: Boolean) {
        super.onVisibilityChanged(visibility)
        if (visibility) {
            contentNode.renderable = renderableCopy
        } else {
            contentNode.renderable = null
        }
    }

    private fun setModelPath(props: Bundle) {
        if (props.containsKey(PROP_MODEL_PATH)) {
            // cannot update the ModelRenderable before [renderableRequested],
            // because Sceneform may be uninitialized yet
            // (loadRenderable may have not been called)
            if (renderableRequested) {
                loadModel()
            }
        }
    }

    private fun setImportScale(props: Bundle) {
        if (props.containsKey(PROP_IMPORT_SCALE)) {
            adjustLocalScale()
        }
    }

    override fun setLocalScale(props: Bundle) {
        val localScale = PropertiesReader.readVector3(props, PROP_LOCAL_SCALE)
        if (localScale != null) {
            this.scale = localScale
            adjustLocalScale()
        }
    }

    private fun loadModel() {
        val modelUri = PropertiesReader.readFilePath(properties, PROP_MODEL_PATH, context)
        if (modelUri != null) {
            modelRenderableLoader.loadRenderable(modelUri) { result ->
                if (result is RenderableResult.Success) {
                    this.renderableCopy = result.renderable
                    if (!hidden) { // model can be (re)loaded after setting clip bounds
                        contentNode.renderable = renderableCopy
                    }
                }
            }
        }
    }

    private fun adjustLocalScale() {
        val importScale = properties.getDouble(PROP_IMPORT_SCALE, DEFAULT_IMPORT_SCALE).toFloat()
        localScale = Vector3(scale.x * importScale, scale.y * importScale, scale.z * importScale)
    }

}