//
//  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import Foundation
import SceneKit
import ARKit

@objc open class UiNodesManager: NSObject {
    @objc public static let instance = UiNodesManager(rootNode: TransformNode(), nodesById: [:], nodeByAnchorUuid: [:], focusedNode: nil, planeDetector: PlaneDetector())
    @objc public private (set) var rootNode: TransformNode
    
    var onInputFocused: ((_ input: DataProviding) -> (Void))?
    var onInputUnfocused: (() -> (Void))?
    
    fileprivate var nodesById: [String: TransformNode]
    fileprivate var nodeByAnchorUuid: [String: TransformNode]
    fileprivate var focusedNode: UiNode?
    fileprivate var longPressedNode: UiNode?
    fileprivate(set) var nodeSelector: UiNodeSelector!
    fileprivate weak var ARView: RCTARView?
    fileprivate let planeDetector: PlaneDetector!
    var dialogPresenter: DialogPresenting?

    init(rootNode: TransformNode, nodesById: [String: TransformNode], nodeByAnchorUuid: [String: TransformNode], focusedNode: UiNode?, planeDetector: PlaneDetector) {
        self.rootNode = rootNode
        self.nodesById = nodesById
        self.nodeByAnchorUuid = nodeByAnchorUuid
        self.focusedNode = focusedNode
        self.planeDetector = planeDetector
    }

    @objc public func registerARView(_ arView: RCTARView) {
        ARView = arView
        arView.scene.rootNode.addChildNode(rootNode)
        arView.register(self)
        arView.register(planeDetector)
        nodeSelector = UiNodeSelector(rootNode, { return self.planeDetector.detectedPlanes })
    }
    
    @objc public func handleNodeTap(_ node: TransformNode?) {
        focusedNode?.leaveFocus()
        if focusedNode != nil {
            onInputUnfocused?()
        }
        
        focusedNode = node as? UiNode
        if let uiNode = focusedNode {
            uiNode.enterFocus()
            uiNode.onActivate?(uiNode)
        }
        if let input = focusedNode as? DataProviding {
            onInputFocused?(input)
        }

        if let planeNode = focusedNode as? PlaneNode {
            positionRootNode(planeNode)
            ARView?.disablePlaneDetection()
            DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(250)) { [weak self] in
                self?.nodesById.forEach {
                    if $0.key.contains("planeNode_") {
                        $0.value.parent?.removeFromParentNode()
                        self?.nodesById.removeValue(forKey: $0.key)
                    }
                }
            }
        }
    }

    func positionRootNode(_ plane: PlaneNode) {
        if let position = plane.parent?.position {
            rootNode.position = position
            rootNode.layoutIfNeeded()
        }
    }
    
    @objc public func handleNodeLongPress(_ node: TransformNode?, _ state: UIGestureRecognizer.State) {
        switch state {
        case .began:
            longPressedNode = node as? UiNode
            longPressedNode?.longPressStarted()
        case .changed:
            break
        case .ended, .cancelled:
            longPressedNode?.longPressEnded()
            longPressedNode = nil
        default:
            print("LongPressGesture unsupported state.")
        }
    }
    
    @objc public func findNodeWithId(_ nodeId: String) -> TransformNode? {
        return nodesById[nodeId]
    }
    
    @objc public func findUiNodeWithId(_ nodeId: String) -> UiNode? {
        return nodesById[nodeId] as? UiNode
    }
    
    @objc public func findNodeWithAnchorUuid(_ nodeId: String) -> TransformNode? {
        return nodeByAnchorUuid[nodeId]
    }
    
    @objc public func registerNode(_ node: TransformNode, nodeId: String) {
        node.name = nodeId
        nodesById[nodeId] = node
        if (node.anchorUuid != "rootUuid") {
            nodeByAnchorUuid[node.anchorUuid] = node;
        }
    }
    
    @objc public func unregisterNode(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            node.removeFromParentNode()
            nodesById.removeValue(forKey: nodeId)
            if let dialog = node as? DialogDataProviding {
                dialogPresenter?.dismiss(dialog)
            }
            if let uiNode = node as? UiNode {
                uiNode.onDelete?(uiNode)
            }
        }
    }
    
    @objc public func addNode(_ nodeId: String, toParent parentId: String) {
        if let node = nodesById[nodeId] {
            if let parentNode = nodesById[parentId] {
                if parentNode.addChild(node) {
                    if let dialog = node as? DialogDataProviding {
                        dialogPresenter?.present(dialog)
                    }
                    return
                }
            }

            // Remove node which does not have a parent or
            // the parent does not want to add the node to its hierarchy.
            removeNodeWithDescendants(node)
        }
    }
    
    @objc public func addNodeToRoot(_ nodeId: String) {
        if let node = nodesById[nodeId] {
            rootNode.addChildNode(node)
        }
    }
    
    @objc public func removeNode(_ nodeId: String, fromParent parentId: String) {
        if let node = nodesById[nodeId],
            let parentNode = nodesById[parentId] {
            parentNode.removeChild(node)
            removeNodeWithDescendants(node)
        }
    }
    
    @objc public func removeNodeFromRoot(_ nodeId: String) {
        if let node = nodesById[nodeId],
            rootNode == node.parent {
            node.removeFromParentNode()
            removeNodeWithDescendants(node)
        }
    }
    
    @objc fileprivate func removeNodeWithDescendants(_ node: TransformNode) {
        node.enumerateTransformNodes { (item) in
            if let id = item.name {
                unregisterNode(id)
            }
        }
    }
    
    @objc public func clear() {
        nodesById.forEach { (key: String, value: SCNNode) in
            value.removeFromParentNode()
        }
        nodesById.removeAll()
    }
    
    @objc public func updateNode(_ nodeId: String, properties: [String: Any]) -> Bool {
        guard let node = nodesById[nodeId] else { return false }
        node.update(properties)
        if let uiNode = node as? UiNode {
            uiNode.onUpdate?(uiNode)
        }
        return true
    }
    
    @objc public func updateLayout() {
        assert(Thread.isMainThread, "updateLayout must be called in main thread!")
        rootNode.enumerateTransformNodes { node in
            node.layoutIfNeeded()
        }

        rootNode.enumerateTransformNodes { node in
            node.layoutContainerIfNeeded()
        }

        rootNode.enumerateTransformNodes { node in
            node.postUpdate()
        }
    }
    
    @objc func textFieldShouldReturn() {
        focusedNode?.leaveFocus()
        onInputUnfocused?()
    }
    
    @objc func textFieldDidChange(text: String?) {
        if let textEdit = focusedNode as? UiTextEditNode {
            textEdit.text = text
        }
    }
}

extension UiNodesManager: RCTARViewObserving {
    @objc internal func renderer(_ renderer: SCNSceneRenderer, didAdd node: SCNNode, for anchor: ARAnchor) {
        if let name = anchor.name, let transformNode = findNodeWithAnchorUuid(name) {
            transformNode.transform = node.convertTransform(node.transform, to: transformNode)
        }
    }

    @objc internal func renderer(_ renderer: SCNSceneRenderer, didUpdate node: SCNNode, for anchor: ARAnchor) {
        if let name = anchor.name, let transformNode = findNodeWithAnchorUuid(name) {
            transformNode.transform = node.convertTransform(node.transform, to: transformNode)
        }
    }
}
