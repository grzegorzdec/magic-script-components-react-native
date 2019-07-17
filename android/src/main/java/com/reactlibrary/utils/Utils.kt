package com.reactlibrary.utils

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.widget.EditText
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.collision.Box
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.Renderable
import com.reactlibrary.BuildConfig
import com.reactlibrary.scene.nodes.base.TransformNode
import java.io.Serializable

// By default, every 250dp for the view becomes 1 meter for the renderable
// https://developers.google.com/ar/develop/java/sceneform/create-renderables
private const val DP_TO_METER_RATIO = 250

private const val DEBUG_ASSETS_PATH = "http://localhost:8081/assets/"

class Utils {
    companion object {

        /**
         * Converts React project's image path to path
         * that can be accessed from android code.
         */
        fun getImagePath(imagePath: String, context: Context): Uri {
            // e.g. resources\DemoPicture1.jpg
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + imagePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/drawable/"
                // resources\DemoPicture1.jpg is copied to
                // res/drawable with file name = "resources_demopicture1"
                val fileName = imagePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        /**
         *
         * Converts React project's file path (other than image) to path
         * that can be accessed from android code.
         *
         * TODO (currently working only in debug when device is connected to PC)
         */
        fun getFilePath(filePath: String, context: Context): Uri {
            // e.g. resources\model.glb
            return if (BuildConfig.DEBUG) {
                Uri.parse(DEBUG_ASSETS_PATH + filePath)
            } else {
                val packageName = context.packageName
                val basePath = "android.resource://$packageName/raw/"
                // TODO check if resources\model.glb is copied to
                // TODO res/raw with file name = "resources_model"
                val fileName = filePath.toLowerCase().replace("/", "_")
                Uri.parse(basePath + fileName)
            }
        }

        /**
         *  Converts ARCore's meters to pixels
         *  (Uses an average of horizontal and vertical density -
         *  usually they are almost the same)
         */
        fun metersToPx(meters: Double, context: Context): Int {
            val xdpi = context.resources.displayMetrics.xdpi
            val ydpi = context.resources.displayMetrics.ydpi
            val densityAvgFactor = (xdpi + ydpi) / 320
            return (meters * DP_TO_METER_RATIO * densityAvgFactor).toInt()
        }
    }

}

/**
 * ==========Extension methods============
 */

fun Any.logMessage(message: String, warn: Boolean = false) {
    if (BuildConfig.DEBUG) {
        if (warn) {
            Log.w("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
        } else {
            Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
        }
    }
}

fun Serializable.toVector3(): Vector3? {
    this as ArrayList<Double>
    return if (size == 3) {
        val x = get(0).toFloat()
        val y = get(1).toFloat()
        val z = get(2).toFloat()
        Vector3(x, y, z)
    } else {
        null
    }
}

fun Serializable.toVector4(): List<Double>? {
    return if ((this as ArrayList<Double>).size == 4) {
        this
    } else {
        null
    }
}

fun List<Double>.toColor(): Int? {
    return if (this.size == 4) {
        val r = get(0) * 255
        val g = get(1) * 255
        val b = get(2) * 255
        val a = get(3) * 255
        Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    } else {
        null
    }
}

fun Serializable.toQuaternion(): Quaternion? {
    this as ArrayList<Double>
    return if (size == 4) {
        val x = get(0).toFloat()
        val y = get(1).toFloat()
        val z = get(2).toFloat()
        val w = get(3).toFloat()
        Quaternion(x, y, z, w) // Quaternion.axisAngle
    } else {
        null
    }
}

data class Bounding(
        var left: Float = 0f,
        var bottom: Float = 0f,
        var right: Float = 0f,
        var top: Float = 0f
)

/**
 * Calculates the bounds of a [Renderable]
 */
fun Renderable.calculateBounds(): Bounding {
    // TODO add Sphere collision shape support
    val collisionShape = collisionShape as? Box
    return if (collisionShape != null) {
        val left = collisionShape.center.x - collisionShape.size.x / 2
        val right = collisionShape.center.x + collisionShape.size.x / 2
        val top = collisionShape.center.y - collisionShape.size.y / 2
        val bottom = collisionShape.center.y + collisionShape.size.y / 2
        Bounding(left, bottom, right, top)
    } else {
        logMessage("Renderable.calculateBounding(): collision shape is null!", true)
        Bounding(0f, 0f, 0f, 0f)
    }
}

fun List<Node>.calculateBounds(): Bounding {
    val bounds = Bounding(0f, 0f, 0f, 0f)

    for (node in this) {
        val childBounds = if (node is TransformNode) node.getBounding() ?: Bounding() else Bounding()
        if (childBounds.left < bounds.left) {
            bounds.left = childBounds.left
        }
        if (childBounds.right > bounds.right) {
            bounds.right = childBounds.right
        }
        if (childBounds.top < bounds.top) {
            bounds.top = childBounds.top
        }
        if (childBounds.bottom > bounds.bottom) {
            bounds.bottom = childBounds.bottom
        }
    }

    return bounds
}

fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}