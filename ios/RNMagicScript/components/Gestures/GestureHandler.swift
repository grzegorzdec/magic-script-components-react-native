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

//sourcery: AutoMockable
//sourcery: ObjcProtocol
@objc protocol GestureHandling {
    func handleTapGesture(_ sender: TapGestureRecognizer)
    func handleDragGesture(_ sender: DragGestureRecognizer)
    func handleLongPressGesture(_ sender: LongPressGestureRecognizer)
}

class GestureHandler: GestureHandling {
    @objc func handleTapGesture(_ sender: TapGestureRecognizer) {
        if let planeNode = sender.tappedNode as? PlaneNode {
            PlaneDetector.instance.handleNodeTap(planeNode, sender.initialTouchLocation)
            return
        }

        if sender.state == .ended {
            UiNodesManager.instance.handleNodeTap(sender.tappedNode)
        }
    }

    @objc func handleDragGesture(_ sender: DragGestureRecognizer) {
        if sender.state == UIGestureRecognizer.State.changed {
            sender.dragNode?.dragValue = sender.beginDragValue + sender.dragDelta
        }
    }

    @objc func handleLongPressGesture(_ sender: LongPressGestureRecognizer) {
        UiNodesManager.instance.handleNodeLongPress(sender.longPressedNode, sender.state)
    }
}
