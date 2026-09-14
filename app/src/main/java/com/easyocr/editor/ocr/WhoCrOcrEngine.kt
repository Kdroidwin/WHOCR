package com.easyocr.editor.ocr

import android.content.Context
import android.graphics.Bitmap

/** Selects between two fully on-device OCR pipelines. */
class WhoCrOcrEngine(context: Context) : OcrEngine {
    private val fast = MlKitOcrEngine()
    private val paddle = PaddleOcrV6MediumEngine(context)

    override suspend fun recognize(
        bitmap: Bitmap,
        language: OcrLanguage,
        profile: OcrProfile,
    ): OcrResult = when (profile) {
        OcrProfile.Fast -> fast.recognize(bitmap, language, profile)
        OcrProfile.Accurate -> paddle.recognize(bitmap, language, profile)
    }
}
