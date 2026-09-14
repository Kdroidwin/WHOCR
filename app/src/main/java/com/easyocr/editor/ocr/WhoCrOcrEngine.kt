package com.easyocr.editor.ocr

import android.content.Context
import android.graphics.Bitmap

/** Uses PP-OCRv6_medium for every WHOCR OCR mode, entirely on-device. */
class WhoCrOcrEngine(context: Context) : OcrEngine {
    private val paddle = PaddleOcrV6MediumEngine(context)

    override suspend fun recognize(bitmap: Bitmap, language: OcrLanguage): OcrResult =
        paddle.recognize(bitmap, language)
}
