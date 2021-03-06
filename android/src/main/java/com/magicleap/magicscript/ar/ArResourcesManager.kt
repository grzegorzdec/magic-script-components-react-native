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

import com.google.ar.core.HitResult
import com.google.ar.core.Pose
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.TransformationSystem
import java.lang.ref.WeakReference

class ArResourcesManager : ArResourcesProvider() {

    var planeDetection = false

    private var sceneRef = WeakReference<Scene>(null)
    private var sessionRef = WeakReference<Session>(null)
    private var transformationSystemRef = WeakReference<TransformationSystem>(null)
    private var cameraState: TrackingState? = null
    private var cameraPose: Pose? = null
    private var arLoaded = false

    init {
        INSTANCE = this
    }

    fun clearArReferences() {
        sceneRef = WeakReference<Scene>(null)
        sessionRef = WeakReference<Session>(null)
        transformationSystemRef = WeakReference<TransformationSystem>(null)
        cameraState = null
        cameraPose = null
        arLoaded = false
    }

    /**
     * Scene is changed when fragment is recreated
     */
    fun setupScene(scene: Scene) {
        this.sceneRef = WeakReference(scene)
        notifySceneChanged(scene)
    }

    fun setupSession(session: Session) {
        this.sessionRef = WeakReference(session)
    }

    fun setupTransformationSystem(transformationSystem: TransformationSystem) {
        this.transformationSystemRef = WeakReference(transformationSystem)
        notifyTransformationSystemChanged(transformationSystem)
    }

    fun onArCoreLoaded() {
        arLoaded = true
        notifyArLoaded(firstTime = arLoadCounter == 0)
        arLoadCounter++
    }

    fun onCameraUpdated(cameraPose: Pose, trackingState: TrackingState) {
        this.cameraState = trackingState
        this.cameraPose = cameraPose
        notifyCameraUpdated(cameraPose, trackingState)
    }

    fun onPlaneTapped(hitResult: HitResult) {
        notifyPlaneTapped(hitResult)
    }

    override fun getSession(): Session? {
        return sessionRef.get()
    }

    override fun getArScene(): Scene? {
        return sceneRef.get()
    }

    override fun isArLoaded(): Boolean {
        return arLoaded
    }

    override fun getTransformationSystem(): TransformationSystem? {
        return transformationSystemRef.get()
    }

    override fun getCameraInfo(): CameraInfo {
        return CameraInfo(cameraState, cameraPose)
    }

    override fun isPlaneDetectionEnabled(): Boolean {
        return planeDetection
    }

    companion object {
        private var arLoadCounter = 0
        var INSTANCE: ArResourcesManager? = null
    }
}