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

package com.magicleap.magicscript.scene

import com.facebook.react.bridge.ReadableMap
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.magicleap.magicscript.scene.nodes.base.TransformNode

interface NodesManager {

    fun registerScene(scene: Scene)

    fun findNodeWithId(nodeId: String): Node?

    fun registerNode(node: TransformNode, nodeId: String)

    fun addNodeToRoot(nodeId: String)

    fun addNodeToParent(nodeId: String, parentId: String)

    fun updateNode(nodeId: String, properties: ReadableMap): Boolean

    fun removeNode(nodeId: String)

    fun clear()

}