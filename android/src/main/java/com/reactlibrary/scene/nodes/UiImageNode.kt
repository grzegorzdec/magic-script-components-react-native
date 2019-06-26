package com.reactlibrary.scene.nodes

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.facebook.react.bridge.ReadableMap
import com.reactlibrary.BuildConfig
import com.reactlibrary.R
import com.reactlibrary.scene.nodes.base.UiNode

class UiImageNode(context: Context) : UiNode(context) {

    override fun provideView(props: ReadableMap, context: Context): View {
        val view = LayoutInflater.from(context).inflate(R.layout.image, null) as ImageView
        val imagePath = getImagePath(props, context)

        // e.g. http://localhost:8081/assets/resources/DemoPicture1.jpg
        Glide.with(context)
                .load(imagePath)
                .into(view)

        return view
    }

    override fun setup(props: ReadableMap, update: Boolean) {
        super.setup(props, update)

    }

    private fun getImagePath(props: ReadableMap, context: Context): Uri {
        // e.g. resources\DemoPicture1.jpg
        val filePath = props.getString("filePath") ?: ""

        return if (BuildConfig.DEBUG) {
            Uri.parse("http://localhost:8081/assets/$filePath")
        } else {
            val packageName = context.packageName
            val resourcesPath = "android.resource://$packageName/drawable/"
            // convert string to format: resources_demopicture1
            val normalizedPath = filePath.toLowerCase().replace("/", "_")
            Uri.parse(normalizedPath)
        }
    }

}