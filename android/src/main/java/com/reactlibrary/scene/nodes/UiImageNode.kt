package com.reactlibrary.scene.nodes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode
import com.reactlibrary.utils.Utils

class UiImageNode(props: ReadableMap, private val context: Context) : UiNode(props, context) {

    companion object {
        // properties
        private const val PROP_FILE_PATH = "filePath"
    }

    override fun applyProperties(properties: Bundle, update: Boolean) {
        super.applyProperties(properties, update)
        setImagePath(properties)
    }

    override fun provideView(context: Context): View {
        return LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView
    }

    private fun setImagePath(properties: Bundle) {
        // path to folder in react project
        val path = properties.getString(PROP_FILE_PATH)
        if (path != null) {
            val androidPath = Utils.getImagePath(path, context)
            // e.g. http://localhost:8081/assets/resources/DemoPicture1.jpg
            Glide.with(context)
                    .load(androidPath)
                    .into(view as ImageView)

        }
    }

}