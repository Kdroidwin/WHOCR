package com.easyocr.editor.ocr

import com.easyocr.editor.geometry.RectF2

data class OcrResult(
    val fullText: String,
    val blocks: List<OcrTextBlock>,
)

data class OcrTextBlock(
    val id: String,
    val text: String,
    val boundingBox: RectF2,
    val lines: List<OcrTextLine> = emptyList(),
)

data class OcrTextLine(
    val id: String,
    val text: String,
    val boundingBox: RectF2,
)

enum class OcrLanguage(val label: String) {
    JapaneseEnglish("Japanese + English"),
    English("English"),
    Japanese("Japanese"),
    JapaneseVertical("Japanese vertical (manga / novel)"),
}

/**
 * Selects the fully local OCR pipeline. Fast is deliberately the default:
 * it uses the bundled ML Kit Japanese/Latin recognizers and is best for an
 * assistant invocation. Accurate uses the larger PP-OCRv6_medium ONNX model.
 */
enum class OcrProfile(
    val label: String,
    val description: String,
) {
    Fast(
        label = "高速（標準）",
        description = "ML Kit。日本語・英語を素早く認識します。",
    ),
    Accurate(
        label = "高精度（遅い）",
        description = "PP-OCRv6_medium。小さな文字・漫画ではより正確ですが時間がかかります。",
    ),
}
