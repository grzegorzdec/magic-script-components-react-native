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

package com.reactlibrary.scene.nodes.views

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CustomButtonTest {

    private lateinit var context: Context
    private lateinit var button: CustomButton

    @Before
    fun setUp() {
        this.context = ApplicationProvider.getApplicationContext()
        this.button = spy(CustomButton(context))
    }

    @Test
    fun shouldMeasureAndRedrawAfterSettingText() {
        button.text = "abc"

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun shouldMeasureAndRedrawAfterSettingTextSize() {
        button.setTextSize(18F)

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun shouldMeasureAndRedrawAfterSettingTextPadding() {
        button.setTextPadding(10, 10)

        verify(button).invalidate()
        verify(button).requestLayout()
    }

    @Test
    fun shouldRedrawAfterSettingRoundnessFactor() {
        button.roundnessFactor = 0.5f

        verify(button).invalidate()
    }

    @Test
    fun shouldRedrawAfterSettingTextColor() {
        button.setTextColor(Color.GREEN)

        verify(button).invalidate()
    }

}