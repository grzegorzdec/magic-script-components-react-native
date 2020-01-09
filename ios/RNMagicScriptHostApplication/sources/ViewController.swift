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
        UiNodesManager.instance.updateLayout()
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
        // Rect layout
        let dropdownListId: String = "dropdown_list_id"
        let _: UiDropdownListNode = createComponent([
            "alignment": "top-center",
            "debug": true,
            "localPosition": [0, 0.7, 0],
            "text": "DropDownList",
//            "textSize": 0.015,
//            "multiSelectMode": true,
//            "maxCharacterLimit": 4
        ], nodeId: dropdownListId, parentId: groupId)

        for i in 0..<10 {
            let _: UiDropdownListItemNode = createComponent([
                "id": i,
                "label": "item \(i + 1)",
            ], nodeId: "item_\(i)", parentId: dropdownListId)
        }
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
