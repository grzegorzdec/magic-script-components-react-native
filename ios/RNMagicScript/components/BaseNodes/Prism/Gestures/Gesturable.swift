//
//  Copyright (c) 2019-2020 Magic Leap, Inc. All Rights Reserved
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
protocol GestureManaging: class {
    var allowsCameraGestures: Bool { get set }

    var recognizers: [GestureRecognizing]? { get }
    func addGestureRecognizer(_ gestureRecognizer: GestureRecognizing)
    func removeGestureRecognizer(_ gestureRecognizer: GestureRecognizing)
}

extension RCTARView: GestureManaging {
    var allowsCameraGestures: Bool {
        set { arView.allowsCameraControl = newValue }
        get { return arView.allowsCameraControl }
    }

    var recognizers: [GestureRecognizing]? {
        return gestureRecognizers?.filter { $0 is GestureRecognizing }.map { $0 as! GestureRecognizing }
    }

    func addGestureRecognizer(_ gestureRecognizer: GestureRecognizing) {
        if let gr = gestureRecognizer as? UIGestureRecognizer {
            addGestureRecognizer(gr)
        }
    }

    func removeGestureRecognizer(_ gestureRecognizer: GestureRecognizing) {
        if let gr = gestureRecognizer as? UIGestureRecognizer {
            removeGestureRecognizer(gr)
        }
    }
}
