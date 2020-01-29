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

import UIKit
import ARKit
import SceneKit

class ViewController: UIViewController {

    fileprivate var arView: RCTARView!
    fileprivate var lastTime: TimeInterval = 0

    override var shouldAutorotate: Bool { return true }
    override var prefersStatusBarHidden: Bool { return true }

    fileprivate var rootNode: SCNNode {
        return arView.scene.rootNode
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        setupARView()
        setupScene()
        arView.register(self)
    }

    deinit {
        arView.unregister(self)
    }

    let groupId: String = "group"
    fileprivate func setupScene() {
        let _: UiGroupNode = createComponent(["localScale": [0.5, 0.5, 0.5]], nodeId: groupId)

        setupDropdownListTest()
//        setupScrollViewScene()

        UiNodesManager.instance.updateLayout()
    }

    fileprivate func setupModelScene() {
        let filenames = [
            "static.obj",
            "static.obj",
            "static.gltf",
            "animated.gltf",
            "static.glb",
            "animated.glb"
        ]
        for (index, filename) in filenames.enumerated() {
            if let path = Bundle.main.path(forResource: filename, ofType: nil),
                FileManager.default.fileExists(atPath: path) {
                loadModel(path, index: index)
            } else {
                debugPrint("Unable to load \(filename) model.")
            }
        }
    }

    fileprivate func loadModel(_ filePath: String, index: Int) {
        let columns: Int = 2
        let x: CGFloat = -0.3 + CGFloat(index % columns) * 0.3
        let y: CGFloat = 0.3 - CGFloat(index / columns) * 0.3
        let scale: CGFloat = 0.1
        let nodeId = "model_id_\(Date().timeIntervalSince1970)"
        let _: UiModelNode = createComponent([
            "modelPath": "file://\(filePath)",
            "debug": true,
            "localPosition": [x, y, 0],
            "localScale": [scale, scale, scale]
        ], nodeId: nodeId, parentId: groupId)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        arView.reset()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        arView.pause()
    }

    fileprivate func setupARView() {
        arView = RCTARView()
        arView.backgroundColor = UIColor(white: 55.0 / 255.0, alpha: 1.0)
        arView.debug = true
        arView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(arView)
        NSLayoutConstraint.activate([
            arView.leftAnchor.constraint(equalTo: view.leftAnchor),
            arView.topAnchor.constraint(equalTo: view.topAnchor),
            arView.rightAnchor.constraint(equalTo: view.rightAnchor),
            arView.bottomAnchor.constraint(equalTo: view.bottomAnchor)
        ])
    }

    fileprivate func setupDropdownListTest() {
        // Dropdown list
        let dropdownListId: String = "dropdown_list_id"
        let dropdown: UiDropdownListNode = createComponent([
            "alignment": "top-center",
            "localPosition": [0, 0.8, 0],
            "text": "DropDownList",
//            "textSize": 0.015,
            "multiSelect": true,
            "maxHeight": 0.5
//            "maxCharacterLimit": 4
        ], nodeId: dropdownListId, parentId: groupId)

        for i in 0..<10 {
            let _: UiDropdownListItemNode = createComponent([
                "id": i,
                "label": "item \(i + 1)",
                "selected": i % 2 == 0
            ], nodeId: "item_\(i)", parentId: dropdownListId)
        }

        let toggle: UiToggleNode = createComponent([
            "localPosition": [0.4, 1.0, 0],
            "text": "Multi select mode",
            "textSize": 0.08,
            "height": 0.1,
        ], nodeId: "toggle_id", parentId: groupId)
        toggle.onChanged = { sender, on in
            dropdown.multiSelect = on
        }
    }

    fileprivate var scrollView: UiScrollViewNode!
    fileprivate var scrollBar: UiScrollBarNode!
    fileprivate var layout: UiLinearLayoutNode!
    fileprivate var isVertical: Bool = true
    fileprivate func setupScrollViewScene() {
        let position: [CGFloat] = [0, 0.1, 0]
        let size: CGFloat = 1.0
        let aabb = [
            "min": [-0.5 * size, -0.5 * size, -0.1 * size],
            "max": [0.5 * size, 0.5 * size, 0.1 * size]
        ]
        // Scroll view
        let scrollViewId: String = "scroll_view_id"
        scrollView = createComponent([
            "alignment": "top-center",
            "debug": true,
            "localPosition": position,
            "scrollBarVisibility": "auto",
            "scrollBounds": aabb
        ], nodeId: scrollViewId, parentId: groupId)

        scrollBar = createComponent([
            "length": size,
            "thickness": 0.04
        ], nodeId: "scroll_bar_id", parentId: scrollViewId)

        let layoutId = "layout_id"
        layout = createComponent([
            "alignment": "'center-center'",
            "defaultItemAlignment": "center-center",
        ], nodeId: layoutId, parentId: scrollViewId)
        let colors: [Array<CGFloat>] = [
            [1, 0, 0, 1],
            [1, 1, 0, 1],
            [1, 1, 1, 1],
            [0, 1, 0, 1],
            [0, 1, 1, 1],
            [0, 0, 1, 1],
            [0, 0, 0, 1],
            [0.5, 0.5, 0.5, 1],
        ]

        for i in 0..<colors.count {
            let _: UiImageNode = createComponent([
                "color": colors[i],
                "height": size,
                "width": size
            ], nodeId: "item_\(i)", parentId: layoutId)
        }

        let togglePosition: [CGFloat] = [position[0] + 0.4, position[1] - 0.6 * size, position[2]]
        let toggle: UiToggleNode = createComponent([
            "localPosition": togglePosition,
            "text": "Horizontal",
            "textSize": 0.08,
            "height": 0.1
        ], nodeId: "toggle_id", parentId: groupId)
        toggle.onChanged = { [weak self] sender, on in
            guard let strongSelf = self else { return }
            strongSelf.isVertical = !strongSelf.isVertical
            strongSelf.updateOrientation()
        }

        updateOrientation()
    }

    fileprivate func rnd() -> CGFloat {
        return (CGFloat(arc4random()) / 0xFFFFFFFF) - 0.5
    }

    fileprivate func updateOrientation() {
        let size = scrollView.getSize()
        let barWidth = scrollBar.thickness
        scrollView.scrollDirection = isVertical ? ScrollDirection.vertical : ScrollDirection.horizontal
        scrollBar.scrollOrientation = isVertical ? Orientation.vertical : Orientation.horizontal
        layout?.layoutOrientation = isVertical ? Orientation.vertical : Orientation.horizontal
        scrollBar.localPosition = isVertical ? SCNVector3(0.5 * (size.width + barWidth), 0, 0) : SCNVector3(0, -0.5 * (size.height + barWidth), 0)
        UiNodesManager.instance.updateLayout()
    }

    @discardableResult
    fileprivate func createComponent<T: TransformNode>(_ props: [String: Any], nodeId: String, parentId: String? = nil) -> T {
        let node = T.init(props: props)
        UiNodesManager.instance.registerNode(node, nodeId: nodeId)
        if let parentId = parentId {
            UiNodesManager.instance.addNode(nodeId, toParent: parentId)
        } else {
            UiNodesManager.instance.addNodeToRoot(nodeId)
        }
        return node
    }
}

extension ViewController: RCTARViewObserving {
    func renderer(_ renderer: SCNSceneRenderer, updateAtTime time: TimeInterval) {
        let deltaTime = time - lastTime
        lastTime = time
        guard deltaTime < 0.5 else { return }

//        DispatchQueue.main.async() { [weak self] in
//            UiNodesManager.instance.updateLayout()
//        }
    }
}
