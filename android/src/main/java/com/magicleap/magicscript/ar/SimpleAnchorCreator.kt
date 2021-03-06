/*
 * Copyright (c) 2020 Magic Leap, Inc. All Rights Reserved
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

package com.magicleap.magicscript.ar

import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import com.google.ar.core.TrackingState
import com.magicleap.magicscript.utils.DataResult
import com.magicleap.magicscript.utils.logMessage

class SimpleAnchorCreator(private val arResourcesProvider: ArResourcesProvider) : AnchorCreator {

    override fun createAnchor(pose: Pose): DataResult<Anchor> {
        if (arResourcesProvider.getCameraInfo().state != TrackingState.TRACKING) {
            val errorMessage = "Cannot create anchor, camera is not tracking"
            logMessage(errorMessage, warn = true)
            return DataResult.Error(IllegalStateException(errorMessage))
        }

        val session = arResourcesProvider.getSession()
        return if (session == null) {
            val errorMessage = "Cannot create anchor, session not ready yet"
            logMessage(errorMessage, warn = true)
            DataResult.Error(IllegalStateException(errorMessage))
        } else {
            try {
                val anchor = session.createAnchor(pose)
                DataResult.Success(anchor)
            } catch (exception: Exception) {
                val errorMessage = "Create anchor exception:  $exception"
                logMessage(errorMessage, warn = true)
                DataResult.Error(exception)
            }
        }
    }

}