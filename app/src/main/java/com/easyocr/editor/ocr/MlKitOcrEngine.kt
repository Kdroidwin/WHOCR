package com.easyocr.editor.ocr

import android.graphics.Bitmap
import android.graphics.Matrix
import com.easyocr.editor.geometry.RectF2
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MlKitOcrEngine : OcrEngine {
    private val latinRecognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }
    private val japaneseRecognizer by lazy {
        TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
    }

    override suspend fun recognize(
        bitmap: Bitmap,
        language: OcrLanguage,
    ): OcrResult = withContext(Dispatchers.Default) {
        val isVerticalJapanese = language == OcrLanguage.JapaneseVertical
        // ML Kit reads horizontal baselines. Counter-clockwise rotation turns
        // Japanese top-to-bottom columns into left-to-right lines. The result
        // rectangles are mapped back so overlays still align with the source.
        val inputBitmap = if (isVerticalJapanese) bitmap.rotateCounterClockwise() else bitmap
        val image = InputImage.fromBitmap(inputBitmap, 0)
        // The Japanese model also recognizes Latin glyphs, so it is the single
        // pass used for mixed Japanese/English input. Both models are bundled.
        val recognizer: TextRecognizer = when (language) {
            OcrLanguage.English -> latinRecognizer
            OcrLanguage.Japanese,
            OcrLanguage.JapaneseEnglish,
            OcrLanguage.JapaneseVertical,
            -> japaneseRecognizer
        }
        val text = recognizer.process(image).await()
        val blocks = text.textBlocks.mapNotNull { block ->
            val box = block.boundingBox ?: return@mapNotNull null
            RecognizedTextBlock(
                text = block.text.trim(),
                boundingBox = RectF2(
                    left = box.left.toFloat(),
                    top = box.top.toFloat(),
                    right = box.right.toFloat(),
                    bottom = box.bottom.toFloat(),
                ),
                lines = block.lines.mapNotNull { line ->
                    val lineBox = line.boundingBox ?: return@mapNotNull null
                    RecognizedTextLine(
                        text = line.text.trim(),
                        boundingBox = RectF2(
                            left = lineBox.left.toFloat(),
                            top = lineBox.top.toFloat(),
                            right = lineBox.right.toFloat(),
                            bottom = lineBox.bottom.toFloat(),
                        ),
                    )
                }.filter { it.text.isNotBlank() },
            )
        }.filter { it.text.isNotBlank() }

        val result = OcrResultMapper.fromBlocks(blocks)
        if (isVerticalJapanese) result.mapCounterClockwiseResultToSource(bitmap.width.toFloat()) else result
    }

    private fun Bitmap.rotateCounterClockwise(): Bitmap = Bitmap.createBitmap(
        this,
        0,
        0,
        width,
        height,
        Matrix().apply { postRotate(-90f) },
        true,
    )

    private fun OcrResult.mapCounterClockwiseResultToSource(sourceWidth: Float): OcrResult = copy(
        blocks = blocks.map { block ->
            block.copy(
                boundingBox = block.boundingBox.mapCounterClockwiseRectToSource(sourceWidth),
                lines = block.lines.map { line ->
                    line.copy(
                        boundingBox = line.boundingBox.mapCounterClockwiseRectToSource(sourceWidth),
                    )
                },
            )
        },
    )

    private fun RectF2.mapCounterClockwiseRectToSource(sourceWidth: Float): RectF2 = RectF2(
        left = sourceWidth - bottom,
        top = left,
        right = sourceWidth - top,
        bottom = right,
    )
}
