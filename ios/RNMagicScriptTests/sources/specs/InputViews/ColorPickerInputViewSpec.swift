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

import ChromaColorPicker

@testable import RNMagicScriptHostApplication

class ColorPickerInputViewSpec: QuickSpec {
    override func spec() {
        describe("ColorPickerInputView") {
            var inputView: ColorPickerInputView!
            var simpleColorPickerDataProviding: SimpleColorPickerDataProviding!
            
            beforeEach() {
                inputView = ColorPickerInputView()
                simpleColorPickerDataProviding = SimpleColorPickerDataProviding()
                inputView.pickerData = simpleColorPickerDataProviding
            }
            
            context("when user select value") {
                let referenceColor = UIColor.lightGray
                
                it("should update pickerData") {
                    inputView.colorPickerDidChooseColor(ChromaColorPicker(frame: CGRect.zero), color: referenceColor)
                    expect(simpleColorPickerDataProviding.colorPickerValue).to(beCloseTo(referenceColor))
                }
                
                it("should notify with callback (colorChanged)") {
                    inputView.colorPickerDidChooseColor(ChromaColorPicker(frame: CGRect.zero), color: referenceColor)
                    expect(simpleColorPickerDataProviding.colorChangedTriggered).to(beTrue())
                }
            }
            
            context("when user confirm selected value") {
                it("should notify with callback (colorConfirmed)") {
                    inputView.doneButtonAction(UIButton())
                    expect(simpleColorPickerDataProviding.colorConfirmedTriggered).to(beTrue())
                }
            }
            
            context("when user cancel selecting") {
                it("should notify with callback (colorCanceled)") {
                    inputView.cancelButtonAction(UIButton())
                    expect(simpleColorPickerDataProviding.colorCanceledTriggered).to(beTrue())
                }
                
                it("should notify with callback (onFinish)") {
                    var result = false
                    inputView.onFinish = {
                        result = true
                    }
                    inputView.cancelButtonAction(UIButton())
                    expect(result).to(beTrue())
                }
            }
        }
    }
}

fileprivate class SimpleColorPickerDataProviding: ColorPickerDataProviding {
    var colorPickerValue: UIColor = .red
    
    func colorChanged() { colorChangedTriggered = true }
    
    func colorConfirmed() { colorConfirmedTriggered = true }
    
    func colorCanceled() { colorCanceledTriggered = true }
    
    var colorChangedTriggered = false
    var colorConfirmedTriggered = false
    var colorCanceledTriggered = false
}
