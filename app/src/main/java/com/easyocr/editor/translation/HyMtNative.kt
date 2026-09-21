package com.easyocr.editor.translation

internal object HyMtNative {
    init {
        System.loadLibrary("hymt_jni")
    }

    external fun translate(modelPath: String, sourceText: String, targetLanguage: String): String
}
