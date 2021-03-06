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
import SceneKit

@objc open class UiGridLayoutNode: UiLayoutNode {
    @objc override var width: CGFloat {
        get { return gridLayout.width }
        set { gridLayout.width = newValue; setNeedsLayout() }
    }
    @objc override var height: CGFloat {
        get { return gridLayout.height }
        set { gridLayout.height = newValue; setNeedsLayout() }
    }
    @objc var columns: Int {
        get { return gridLayout.columns }
        set { gridLayout.columns = newValue; setNeedsLayout() }
    }
    @objc var rows: Int {
        get { return gridLayout.rows }
        set { gridLayout.rows = newValue; setNeedsLayout() }
    }
    @objc var defaultItemAlignment: Alignment {
        get { return gridLayout.defaultItemAlignment }
        set { gridLayout.defaultItemAlignment = newValue; setNeedsLayout() }
    }
    @objc var defaultItemPadding: UIEdgeInsets {
        get { return gridLayout.defaultItemPadding }
        set { gridLayout.defaultItemPadding = newValue; setNeedsLayout() }
    }
    var itemAlignment: [(column: Int, row: Int, alignment: Alignment)] = [] {
        didSet { setNeedsLayout() }
    }
    var itemPadding: [(column: Int, row: Int, padding: UIEdgeInsets)] = [] {
        didSet { setNeedsLayout() }
    }
    @objc var skipInvisibleItems: Bool {
        get { return gridLayout.skipInvisibleItems }
        set { gridLayout.skipInvisibleItems = newValue; setNeedsLayout() }
    }

    fileprivate var gridLayout = GridLayout()

    @objc override func setupNode() {
        super.setupNode()
        contentNode.addChildNode(gridLayout.container)
    }

    override func hitTest(ray: Ray) -> HitTestResult? {
        return gridLayout.hitTest(ray: ray, node: self)
    }

    @objc override func update(_ props: [String: Any]) {
        super.update(props)

        if let columns = Convert.toInt(props["columns"]) {
            self.columns = columns
        }

        if let rows = Convert.toInt(props["rows"]) {
            self.rows = rows
        }

        if let defaultItemAlignment = Convert.toAlignment(props["defaultItemAlignment"]) {
            self.defaultItemAlignment = defaultItemAlignment
        }

        if let defaultItemPadding = Convert.toPadding(props["defaultItemPadding"]) {
            self.defaultItemPadding = defaultItemPadding
        }
        
        if let itemAlignment = Convert.toItemAlignmentColumnRow(props["itemAlignment"]) {
            self.itemAlignment = itemAlignment
        }

        if let itemPadding = Convert.toItemPaddingColumnRow(props["itemPadding"]) {
            self.itemPadding = itemPadding
        }

        if let skipInvisibleItems = Convert.toBool(props["skipInvisibleItems"]) {
            self.skipInvisibleItems = skipInvisibleItems
        }
    }

    @discardableResult
    @objc override func addChild(_ child: TransformNode) -> Bool {
        gridLayout.addItem(child)
        setNeedsLayout()
        return true
    }

    @objc override func removeChild(_ child: TransformNode) {
        if gridLayout.removeItem(child) {
            setNeedsLayout()
        }
    }

    @objc override func _calculateSize() -> CGSize {
        updatePaddingAndAlignmentPerItem()
        return gridLayout.getSize()
    }

    @objc override func updateLayout() {
        // Invoke getSize to make sure the grid's sizes are calcualted and cached in gridDesc.
        _ = getSize()
        gridLayout.updateLayout()
    }

    @objc override func setNeedsLayout() {
        super.setNeedsLayout()
        gridLayout.invalidate()
    }
    
    fileprivate func updatePaddingAndAlignmentPerItem() {
        let flowDirection = gridLayout.getFlowDirection()
        let currentRows = gridLayout.getCurrentRows()
        let currentColumns = gridLayout.getCurrentColumns()
        
        var paddingByIndex: [Int: UIEdgeInsets] = [:]
        itemPadding.forEach {
            let index: Int = (flowDirection == .vertical) ? ($0.row * currentColumns + $0.column) : ($0.column * currentRows + $0.row)
            paddingByIndex[index] = $0.padding
        }
        gridLayout.paddingByIndex = paddingByIndex
        
        var alignmentByIndex: [Int: Alignment] = [:]
        itemAlignment.forEach {
            let index: Int = (flowDirection == .vertical) ? ($0.row * currentColumns + $0.column) : ($0.column * currentRows + $0.row)
            alignmentByIndex[index] = $0.alignment
        }
        gridLayout.alignmentByIndex = alignmentByIndex
    }
}

extension UiGridLayoutNode: TransformNodeContainer {
    var itemsCount: Int { return gridLayout.itemsCount }
}
