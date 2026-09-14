package com.easyocr.editor.ocr

import android.graphics.Bitmap

interface OcrEngine {
    suspend fun recognize(
        bitmap: Bitmap,
        language: OcrLanguage = OcrLanguage.JapaneseEnglish,
        profile: OcrProfile = OcrProfile.Fast,
    ): OcrResult
}
