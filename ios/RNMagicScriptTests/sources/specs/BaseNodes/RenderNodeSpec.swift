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

import Quick
import Nimble
@testable import RNMagicScriptHostApplication

class RenderNodeSpec: QuickSpec {
    override func spec() {
        describe("RenderNode") {
            var node: RenderNode!

            beforeEach {
                node = RenderNode()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.color).to(beCloseTo(UIColor.white))
                }
            }

            context("update properties") {
                it("should update 'color' prop") {
                    node = RenderNode(props: ["color" : UIColor.red.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(UIColor.red))
                    node.update(["color" : UIColor.green.toArrayOfFloat])
                    expect(node.color).to(beCloseTo(UIColor.green))
                }
            }
        }
    }
}
