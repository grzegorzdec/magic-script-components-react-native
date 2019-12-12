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

import Quick
import Nimble
@testable import RNMagicScriptHostApplication

import SceneKit

class UiToggleGroupNodeSpec: QuickSpec {
    override func spec() {
        describe("UiToggleGroupNode") {
            var node: UiToggleGroupNode!

            beforeEach {
                node = UiToggleGroupNode()
                node.layoutIfNeeded()
            }

            context("initial properties") {
                it("should have set default values") {
                    expect(node.allowMultipleOn).to(beFalse())
                    expect(node.allowAllOff).to(beFalse())
                    expect(node.allTogglesOff).to(beFalse())
                    expect(node.canHaveFocus).to(beFalse())
                }
            }

            context("update properties") {
                it("should update 'allowMultipleOn' prop") {
                    node.update(["allowMultipleOn" : true])
                    expect(node.allowMultipleOn).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'allowAllOff' prop") {
                    node.update(["allowAllOff" : true])
                    expect(node.allowAllOff).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())
                }

                it("should update 'allTogglesOff' prop") {
                    node.update(["allTogglesOff" : true])
                    expect(node.allTogglesOff).to(beTrue())
                    expect(node.isLayoutNeeded).to(beFalse())
                }
            }

            context("when child added") {
                context("when child is UiToggleNode") {
                    it("should add it to the list") {
                        let toggleNode = UiToggleNode()
                        node.addChild(toggleNode)
                        expect(node.itemsList.count).to(equal(1))


                        let otherNode = TransformNode()
                        node.addChild(otherNode)
                        expect(node.itemsList.count).to(equal(1))
                    }
                }

                context("when child is TransformNodeContainer (toggles container)") {
                    it("should manage it internally") {
                        let layoutNode = UiLinearLayoutNode()
                        node.addChild(layoutNode)
                        expect(node.innerLayout).to(equal(layoutNode))
                    }
                }
            }

            context("when child register his presence") {
                context("when it's managed by this toggle group") {
                    it("should add it to the list") {
                        let toggleNode = UiToggleNode()
                        node.childPresent(toggleNode: toggleNode)
                        expect(node.itemsList.count).to(equal(1))
                    }
                }

                context("when it's already managed by this toggle group") {
                    it("should do nothing") {
                        let toggleNode = UiToggleNode()
                        node.addChild(toggleNode)
                        expect(node.itemsList.count).to(equal(1))

                        node.childPresent(toggleNode: toggleNode)
                        expect(node.itemsList.count).to(equal(1))
                    }
                }
            }

            context("when child removed") {
                context("when child is UiToggleNode") {
                    it("should remove it from the list") {
                        let toggleNode = UiToggleNode()
                        node.addChild(toggleNode)
                        expect(node.itemsList.count).to(equal(1))

                        node.removeChild(toggleNode)
                        expect(node.itemsList.count).to(equal(0))
                    }
                }

                context("when child is TransformNodeContainer (toggles container)") {
                    it("should manage it internally") {
                        let layoutNode = UiLinearLayoutNode()
                        node.addChild(layoutNode)
                        expect(node.innerLayout).to(equal(layoutNode))

                        node.removeChild(layoutNode)
                        expect(node.innerLayout).to(beNil())
                    }
                }
            }

            context("when toggle selection requested") {
                context("when MultipleOn & AllOff allowed") {
                    it("should toggle state") {
                        node.allowAllOff = true
                        node.allowMultipleOn = true

                        let toggleNode = UiToggleNode()
                        toggleNode.on = true

                        node.toggleSelection(toggleNode)
                        expect(toggleNode.on).to(beFalse())

                        node.toggleSelection(toggleNode)
                        expect(toggleNode.on).to(beTrue())
                    }
                }

                context("when MultipleOn allowed AllOff forbidden") {
                    context("when requestes toggle is last selected") {
                        it("should leave toggle selected") {
                            node.allowAllOff = false
                            node.allowMultipleOn = true

                            let toggleNode1 = UiToggleNode()
                            toggleNode1.on = true

                            let toggleNode2 = UiToggleNode()
                            toggleNode2.on = false

                            node.childPresent(toggleNode: toggleNode1)
                            node.childPresent(toggleNode: toggleNode2)

                            node.toggleSelection(toggleNode1)
                            expect(toggleNode1.on).to(beTrue())
                            expect(toggleNode2.on).to(beFalse())
                        }
                    }

                    context("when requestes toggle is NOT last selected") {
                        it("should set toggle deselected") {
                            node.allowAllOff = false
                            node.allowMultipleOn = true

                            let toggleNode1 = UiToggleNode()
                            toggleNode1.on = true

                            let toggleNode2 = UiToggleNode()
                            toggleNode2.on = true

                            node.childPresent(toggleNode: toggleNode1)
                            node.childPresent(toggleNode: toggleNode2)

                            node.toggleSelection(toggleNode1)
                            expect(toggleNode1.on).to(beFalse())
                            expect(toggleNode2.on).to(beTrue())
                        }
                    }
                }

                context("when MultipleOn forbidden AllOff allowed") {
                    context("when requestes toggle is NOT selected (other is selected)") {
                        it("should set toggle selected (deselect other)") {
                            node.allowAllOff = true
                            node.allowMultipleOn = false

                            let toggleNode1 = UiToggleNode()
                            toggleNode1.on = false

                            let toggleNode2 = UiToggleNode()
                            toggleNode2.on = true

                            node.childPresent(toggleNode: toggleNode1)
                            node.childPresent(toggleNode: toggleNode2)

                            node.toggleSelection(toggleNode1)
                            expect(toggleNode1.on).to(beTrue())
                            expect(toggleNode2.on).to(beFalse())
                        }
                    }
                }

                context("when MultipleOn forbidden AllOff forbidden") {
                    it("should set toggle to selected (other to deselected)") {
                        node.allowAllOff = false
                        node.allowMultipleOn = false

                        let toggleNode1 = UiToggleNode()
                        toggleNode1.on = false

                        let toggleNode2 = UiToggleNode()
                        toggleNode2.on = false

                        let toggleNode3 = UiToggleNode()
                        toggleNode3.on = true

                        node.childPresent(toggleNode: toggleNode1)
                        node.childPresent(toggleNode: toggleNode2)
                        node.childPresent(toggleNode: toggleNode3)

                        node.toggleSelection(toggleNode1)
                        expect(toggleNode1.on).to(beTrue())
                        expect(toggleNode2.on).to(beFalse())
                        expect(toggleNode3.on).to(beFalse())
                    }
                }
            }
        }
    }
}
