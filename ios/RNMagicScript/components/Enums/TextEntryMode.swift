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

import Foundation

@objc public enum TextEntryMode: Int {
    case email
    case none // Virtual Keyboard does not display
    case normal
    case numeric
    case url

    public typealias RawValue = String

    public var rawValue: RawValue {
        switch self {
        case .email:
            return "email"
        case .none:
            return "none"
        case .normal:
            return "normal"
        case .numeric:
            return "numeric"
        case .url:
            return "url"
        }
    }

    public init?(rawValue: RawValue) {
        switch rawValue {
        case "email":
            self = .email
        case "none":
            self = .none
        case "normal":
            self = .normal
        case "numeric":
            self = .numeric
        case "url":
            self = .url
        default:
            return nil
        }
    }
}

