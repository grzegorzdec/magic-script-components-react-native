package com.reactlibrary.scene.nodes

import com.reactlibrary.scene.nodes.base.TransformNode

class GroupNode : TransformNode() {
    override fun loadRenderable(): Boolean {
        // it does not contain the view
        return false
    }
}

