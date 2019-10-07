/*
 *  Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.reactlibrary.font.external

import com.reactlibrary.font.ExternalFont
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight

class LominoFont : ExternalFont("LominoUIApp") {

    override fun getFileName(fontWeight: FontWeight, fontStyle: FontStyle): String {
        return when (fontWeight) {
            FontWeight.EXTRA_LIGHT,
            FontWeight.LIGHT -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Light")
            } else {
                getFullName("LightItalic")
            }
            FontWeight.REGULAR -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Regular")
            } else {
                getFullName("Italic")
            }
            FontWeight.MEDIUM -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Medium")
            } else {
                getFullName("MediumItalic")
            }
            FontWeight.BOLD -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("Bold")
            } else {
                getFullName("BoldItalic")
            }
            FontWeight.EXTRA_BOLD -> if (fontStyle == FontStyle.NORMAL) {
                getFullName("ExtraBold")
            } else {
                getFullName("ExtraBoldItalic")
            }
        }
    }

    private fun getFullName(suffix: String): String {
        return "$baseName-$suffix.ttf"
    }

}