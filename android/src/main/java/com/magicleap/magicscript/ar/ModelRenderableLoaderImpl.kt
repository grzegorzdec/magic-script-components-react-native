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

package com.magicleap.magicscript.ar

import android.content.Context
import android.net.Uri
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.magicleap.magicscript.utils.Utils
import com.magicleap.magicscript.utils.logMessage

class ModelRenderableLoaderImpl(private val context: Context) : ModelRenderableLoader {

    override fun loadRenderable(
        modelUri: Uri,
        resultCallback: (result: RenderableResult<ModelRenderable>) -> Unit
    ) {
        val modelType = Utils.detectModelType(modelUri, context)
        if (modelType == ModelType.UNKNOWN) {
            val errorMessage = "Unresolved model type"
            logMessage(errorMessage, true)
            resultCallback(RenderableResult.Error(Exception(errorMessage)))
            return
        }

        ModelRenderable.builder()
            .setSource(modelUri, modelType)
            .setRegistryId(modelUri)
            .build()
            .thenAccept { renderable ->
                renderable.isShadowReceiver = false
                renderable.isShadowCaster = false
                resultCallback(RenderableResult.Success(renderable))
            }
            .exceptionally { throwable ->
                logMessage("error loading ModelRenderable: $throwable")
                resultCallback(RenderableResult.Error(throwable))
                null
            }
    }

    private fun ModelRenderable.Builder.setSource(uri: Uri, modelType: ModelType)
            : ModelRenderable.Builder {
        if (modelType == ModelType.GLB) {
            val source = RenderableSource.builder()
                .setSource(context, uri, RenderableSource.SourceType.GLB)
                .setRecenterMode(RenderableSource.RecenterMode.CENTER)
                .build()
            setSource(context, source)
        } else {
            setSource(context, uri)
        }
        return this
    }

}