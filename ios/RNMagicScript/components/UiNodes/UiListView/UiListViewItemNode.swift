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

import SceneKit

@objc open class UiListViewItemNode: UiNode {
    @objc var backgroundColor: UIColor = UIColor.clear {
        didSet {
            contentNode.geometry?.firstMaterial?.diffuse.contents = UIColor.red
        }
    }

    @objc override func setupNode() {
        super.setupNode()
    }

    fileprivate var childNode: TransformNode? = nil

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let backgroundColor = Convert.toColor(props["backgroundColor"]) {
            self.backgroundColor = backgroundColor
        }
    }

    @objc override func addChild(_ child: TransformNode) {
        super.addChild(child)
        childNode = child
        setNeedsLayout()
    }

    @objc override func removeChild(_ child: TransformNode) {
        super.removeChild(child)
        childNode = nil
        setNeedsLayout()
    }

    override func _calculateSize() -> CGSize {
        let childSize = childNode?.getSize() ?? CGSize.zero
        return CGSize(width: childSize.width + 0.025, height: childSize.height + 0.025)
    }

    @objc override func hitTest(ray: Ray) -> TransformNode? {
        guard let _ = selfHitTest(ray: ray) else { return nil }
        return childNode
    }
}
