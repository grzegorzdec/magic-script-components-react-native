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

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.bridge.JavaOnlyMap
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer
import com.google.ar.sceneform.ux.TransformationSystem
import com.magicleap.magicscript.NodeBuilder
import com.magicleap.magicscript.ar.ArResourcesProvider
import com.magicleap.magicscript.reactMapOf
import com.magicleap.magicscript.scene.nodes.Prism
import com.magicleap.magicscript.scene.nodes.base.TransformNode
import com.nhaarman.mockitokotlin2.*
import org.amshove.kluent.itReturns
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class UiNodesManagerTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val arResourcesProvider: ArResourcesProvider = mock()
    private lateinit var nodesManager: UiNodesManager
    private var arScene = spy<Scene>()

    @Before
    fun setUp() {
        whenever(arResourcesProvider.getArScene()).thenReturn(arScene)
        whenever(arResourcesProvider.getTransformationSystem()).thenReturn(getTransformationSystem())
        whenever(arResourcesProvider.isArLoaded()).thenReturn(true)

        nodesManager = UiNodesManager(arResourcesProvider)
    }

    @Test
    fun `should register node`() {
        val node = NodeBuilder().build()

        nodesManager.registerNode(node, "1")

        nodesManager.findNodeWithId("1") shouldEqual node
    }

    @Test
    fun `should remove node`() {
        val node = NodeBuilder().build()
        val id = "1"
        nodesManager.registerNode(node, id)

        nodesManager.removeNode(id)

        nodesManager.findNodeWithId(id) shouldBe null
    }

    @Test
    fun `child should be attached to content node is parent is a TransformNode`() {
        val parent = NodeBuilder().build()
        val parentId = "some parent id"
        nodesManager.registerNode(parent, parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.addNodeToParent("1", parentId)

        parent.contentNode.children.size shouldEqual 1
    }

    @Test
    fun `child should be detached from content node of parent when removed`() {
        val parent = NodeBuilder().build()
        val parentId = "some parent id"
        nodesManager.registerNode(parent, parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.addNodeToParent("1", parentId)

        nodesManager.removeNode("1")

        parent.contentNode.children.size shouldEqual 0
    }

    @Test
    fun `should remove children when parent removed`() {
        val parentId = "0"
        nodesManager.registerNode(NodeBuilder().build(), parentId)
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.registerNode(NodeBuilder().build(), "2")
        nodesManager.addNodeToParent("1", parentId)
        nodesManager.addNodeToParent("2", parentId)

        nodesManager.removeNode(parentId)

        nodesManager.findNodeWithId("1") shouldBe null
        nodesManager.findNodeWithId("2") shouldBe null
        nodesManager.findNodeWithId(parentId) shouldBe null
    }

    @Test
    fun `should remove all nodes on clear`() {
        nodesManager.registerNode(NodeBuilder().build(), "1")
        nodesManager.registerNode(NodeBuilder().build(), "2")
        nodesManager.registerNode(NodeBuilder().build(), "3")
        nodesManager.addNodeToParent("3", "2")

        nodesManager.clear()

        nodesManager.findNodeWithId("1") shouldBe null
        nodesManager.findNodeWithId("2") shouldBe null
        nodesManager.findNodeWithId("3") shouldBe null
    }

    @Test
    fun `should not attach renderable if ARCore library is not loaded`() {
        whenever(arResourcesProvider.isArLoaded()).thenReturn(false)
        val node1 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
        }

        nodesManager.registerNode(node1, "1")

        verify(node1, never()).attachRenderable()
    }

    @Test
    fun `should attach renderable for all nodes with renderable when ARCore is loaded`() {
        whenever(arResourcesProvider.isArLoaded()).thenReturn(true)
        val node1 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
            nodesManager.registerNode(this.mock, "1")
        }
        val node2 = mock<TransformNode> {
            on { hasRenderable }.doReturn(true)
            nodesManager.registerNode(this.mock, "2")
        }
        val nodeWithoutRenderable = mock<TransformNode> {
            on { hasRenderable } itReturns false
            nodesManager.registerNode(this.mock, "3")
        }

        verify(node1).attachRenderable()
        verify(node2).attachRenderable()
        verify(nodeWithoutRenderable, never()).attachRenderable()
    }

    @Test
    fun `AR scene should not have any children at the beginning`() {
        arScene.children.size shouldEqual 0
    }

    @Test
    fun `should attach Prisms to AR scene when ReactScene registered`() {
        val reactScene = ReactScene(reactMapOf(), arResourcesProvider)
        val prism1 = createPrism(reactMapOf())
        val prism2 = createPrism(reactMapOf())
        nodesManager.registerNode(reactScene, nodeId = "0")
        nodesManager.registerNode(prism1, nodeId = "1")
        nodesManager.registerNode(prism2, nodeId = "2")
        nodesManager.addNodeToParent(nodeId = "1", parentId = "0")
        nodesManager.addNodeToParent(nodeId = "2", parentId = "0")
        nodesManager.addNodeToRoot(nodeId = "0")

        arScene.children.size shouldEqual 2
        arScene.children[0] shouldEqual prism1
        arScene.children[1] shouldEqual prism2
    }

    @Test
    fun `should not add node to AR scene without Scene and Prism object and anchor UUID absent`() {
        val node = NodeBuilder().build()

        nodesManager.registerNode(node, "1")
        nodesManager.addNodeToRoot("1")

        arScene.children.size shouldEqual 0
    }

    @Test
    fun `should add node to specified anchor when anchor UUID present and anchor node exists`() {
        val uuid = UUID.randomUUID().toString()
        val anchorNode = AnchorNode()
        anchorNode.name = uuid
        arScene.addChild(anchorNode)
        val childNode = NodeBuilder()
            .withProps(reactMapOf(TransformNode.PROP_ANCHOR_UUID, uuid))
            .build()

        nodesManager.registerNode(childNode, "999")
        nodesManager.addNodeToParent("999", parentId = "")

        anchorNode.children.size shouldEqual 1
        anchorNode.children.first() shouldEqual childNode
    }

    @Test
    fun `should update node properties`() {
        val node = mock<TransformNode>()
        val props = JavaOnlyMap.of()
        nodesManager.registerNode(node, "1")

        nodesManager.updateNode("1", props)

        verify(node).update(props)
    }

    @Test
    fun `should resume all nodes when host resumed`() {
        val node1 = mock<TransformNode>()
        val node2 = mock<TransformNode>()
        val node3 = mock<TransformNode>()
        nodesManager.registerNode(node1, "1")
        nodesManager.registerNode(node2, "2")
        nodesManager.registerNode(node3, "3")

        nodesManager.onHostResume()

        verify(node1).onResume()
        verify(node2).onResume()
        verify(node3).onResume()
    }

    @Test
    fun `should pause all nodes when host paused`() {
        val node1 = mock<TransformNode>()
        val node2 = mock<TransformNode>()
        val node3 = mock<TransformNode>()
        nodesManager.registerNode(node1, "1")
        nodesManager.registerNode(node2, "2")
        nodesManager.registerNode(node3, "3")

        nodesManager.onHostPause()

        verify(node1).onPause()
        verify(node2).onPause()
        verify(node3).onPause()
    }

    private fun createPrism(props: JavaOnlyMap): Prism {
        return Prism(props, mock(), mock(), arResourcesProvider)
    }

    private fun getTransformationSystem(): TransformationSystem {
        val displayMetrics = context.resources.displayMetrics
        return TransformationSystem(displayMetrics, FootprintSelectionVisualizer())
    }

}
