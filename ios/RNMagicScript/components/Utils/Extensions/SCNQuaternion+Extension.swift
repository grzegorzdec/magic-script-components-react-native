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

let SCNQuaternionIdentity = SCNQuaternion(0, 0, 0, 1)

extension SCNQuaternion {
    public static let identity = SCNQuaternionIdentity
    
    public static func fromAxis(_ axis: SCNVector3, andAngle angle: Float) -> SCNQuaternion {
        let s: Float = sin(angle / 2)
        return SCNQuaternion(axis.x * s, axis.y * s, axis.z * s, cos(angle / 2))
    }
}