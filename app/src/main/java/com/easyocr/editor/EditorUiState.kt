package com.easyocr.editor

import android.graphics.Bitmap
import android.net.Uri
import com.easyocr.editor.ocr.OcrLanguage
import com.easyocr.editor.ocr.OcrProfile
import com.easyocr.editor.ocr.OcrResult
import com.easyocr.editor.translation.TranslationProfile
import com.easyocr.editor.translation.TranslationTarget

data class EditorUiState(
    val sourceUri: Uri? = null,
    val isAssistantCapture: Boolean = false,
    val bitmap: Bitmap? = null,
    val ocrResult: OcrResult? = null,
    val language: OcrLanguage = OcrLanguage.JapaneseEnglish,
    val ocrProfile: OcrProfile = OcrProfile.Fast,
    val translationProfile: TranslationProfile = TranslationProfile.Fast,
    val translationTarget: TranslationTarget = TranslationTarget.Japanese,
    val translatedText: String = "",
    val isTranslating: Boolean = false,
    val overlaysEnabled: Boolean = true,
    val isLoadingImage: Boolean = false,
    val isRunningOcr: Boolean = false,
    val lastSavedUri: Uri? = null,
    val errorMessage: String? = null,
) {
    val hasImage: Boolean = bitmap != null
    val fullText: String = ocrResult?.fullText.orEmpty()
}
