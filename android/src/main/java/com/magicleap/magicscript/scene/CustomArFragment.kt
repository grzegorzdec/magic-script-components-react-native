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

package com.magicleap.magicscript.scene

import android.os.Bundle
import android.view.View
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.ux.ArFragment
import com.magicleap.magicscript.plane.ARPlaneDetectorBridge

class CustomArFragment : ArFragment() {

    private var onReadyCalled = false
    private lateinit var session: Session
    private var lastTimestamp: Long = 0L


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        session = Session(context)
        arSceneView.scene.addOnUpdateListener {
            if (!onReadyCalled && arSceneView.arFrame?.camera?.trackingState == TrackingState.TRACKING) {
                // We can add AR objects after session is ready and camera is in tracking mode
                UiNodesManager.INSTANCE.onArFragmentReady()
                onReadyCalled = true
//                arSceneView.setupSession(session)
            }
//            val newFrame = session.update()
//            if (newFrame.timestamp != lastTimestamp) {
//                lastTimestamp = newFrame.timestamp
//                ARPlaneDetectorBridge.INSTANCE.onPlaneUpdate(newFrame.getUpdatedTrackables(Plane::class.java))
//            }
        }
        setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            val anchor = hitResult.createAnchor()
            UiNodesManager.INSTANCE.onTapArPlane(anchor)
        }

        // Hide the instructions
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
    }
}