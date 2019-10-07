/*
 * Copyright (c) 2019 Magic Leap, Inc. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactlibrary.utils

import android.content.Context
import android.graphics.Typeface
import com.reactlibrary.font.FontStyle
import com.reactlibrary.font.FontWeight
import com.reactlibrary.font.external.LominoFont

/**
 * Typeface returned from this provider should be used by every
 * node that contains a text
 */
class FontProvider {

    companion object {

        private const val FONTS_DIR = "fonts"
        private val externalFont = LominoFont()
        private val DEFAULT_WEIGHT = FontWeight.REGULAR
        private val DEFAULT_STYLE = FontStyle.NORMAL

        private val fontsCache = hashMapOf<String, FontState>()

        /**
         * Returns a typeface for a given [weight] and [style].
         * If external font files are present in assets, this function returns
         * a proper typeface based on these files, else it returns a default typeface.
         */
        fun provideFont(context: Context, weight: FontWeight? = null, style: FontStyle? = null): Typeface {
            val fontWeight = weight ?: DEFAULT_WEIGHT
            val fontStyle = style ?: DEFAULT_STYLE
            val fontName = externalFont.getFileName(fontWeight, fontStyle)
            val fontState = fontsCache[fontName]
            when {
                fontState == null -> { // need to load
                    val fontsCatalogExists = context.assets.list("")?.contains(FONTS_DIR) ?: false
                    if (!fontsCatalogExists) {
                        fontsCache[fontName] = FontState(true, null)
                        return DefaultFontProvider.provideFont(fontWeight, fontStyle)
                    }

                    val fontExists = context.assets.list(FONTS_DIR)?.contains(fontName) ?: false
                    if (!fontExists) {
                        fontsCache[fontName] = FontState(true, null)
                        return DefaultFontProvider.provideFont(fontWeight, fontStyle)
                    }

                    val fontPath = "$FONTS_DIR/$fontName"
                    val typeface = Typeface.createFromAsset(context.assets, fontPath)
                    fontsCache[fontName] = FontState(false, typeface)
                    logMessage("External font loaded: $fontName")
                    return typeface

                }

                fontState.loadError -> // already tried to load this font, but it's absent
                    return DefaultFontProvider.provideFont(fontWeight, fontStyle)

                else -> return fontState.typeface!!
            }
        }

    }

    private class FontState(val loadError: Boolean, val typeface: Typeface?)
}