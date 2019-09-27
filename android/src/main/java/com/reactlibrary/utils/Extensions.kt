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

import android.os.Bundle
import android.util.Log
import android.widget.EditText

/**
 * ==========Extension methods============
 */

fun Any.logMessage(message: String, warn: Boolean = false) {
    if (warn) {
        Log.w("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
    } else {
        Log.d("AR_LOG_" + this.javaClass.name, message) //this.javaClass.name
    }
}

fun EditText.setTextAndMoveCursor(text: String) {
    this.setText("")
    this.append(text)
}

fun Bundle.putDefaulDouble(name: String, value: Double){
    if (!containsKey(name)) {
        putDouble(name, value)
    }
}

fun Bundle.putDefaulString(name: String, value: String){
    if (!containsKey(name)) {
        putString(name, value)
    }
}
