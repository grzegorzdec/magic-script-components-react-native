package com.reactlibrary.scene.nodes.layouts.manager

import com.google.ar.sceneform.Node
import com.nhaarman.mockitokotlin2.mock
import com.reactlibrary.scene.nodes.UiButtonNode
import com.reactlibrary.scene.nodes.UiTextNode
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.scene.nodes.layouts.UiRectLayout
import com.reactlibrary.scene.nodes.props.Alignment
import com.reactlibrary.scene.nodes.props.Padding
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
class RectLayoutManagerTest {

    private lateinit var rectLayout: UiRectLayout
    private lateinit var rectLayoutManager: RectLayoutManager

    @Before
    fun setUp() {
        this.rectLayout = mock()
        this.rectLayoutManager = RectLayoutManagerImpl()
        rectLayoutManager.contentHorizontalAlignment = Alignment.HorizontalAlignment.LEFT
        rectLayoutManager.contentVerticalAlignment = Alignment.VerticalAlignment.TOP
        rectLayoutManager.itemPadding = Padding(1F, 1F, 1F, 1F)
    }

    @Test(expected = Exception::class)
    fun shouldThrowErrorWhenThereAreMoreThanOneChildren() {
        val children: List<Node> = arrayListOf(Node(), Node())

        rectLayoutManager.layoutChildren(children, emptyMap())
    }

}