package com.easyocr.editor.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.easyocr.editor.geometry.RectF2
import com.paddle.ocr.PaddleOCR
import com.paddle.ocr.PaddleOCRConfig
import com.paddle.ocr.util.OpenCVUtils
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Offline PP-OCRv6_medium pipeline. Both ONNX models are packaged as APK
 * assets; this code performs no model download and has no network path.
 */
class PaddleOcrV6MediumEngine(context: Context) : OcrEngine {
    private val appContext = context.applicationContext
    private val createMutex = Mutex()
    private var pipeline: PaddleOCR? = null

    override suspend fun recognize(
        bitmap: Bitmap,
        language: OcrLanguage,
        profile: OcrProfile,
    ): OcrResult {
        val ocr = try {
            pipeline ?: createMutex.withLock {
                check(OpenCVUtils.init(appContext)) { "OpenCV native runtime failed to initialize" }
                pipeline ?: PaddleOCR.create(
                    context = appContext,
                    config = PaddleOCRConfig(
                        // Preserve faint manga type and small furigana candidates.
                        detBoxThresh = 0.35f,
                        recScoreThresh = 0.0f,
                        recBatchSize = 1,
                    ),
                ).also { pipeline = it }
            }
        } catch (error: Throwable) {
            Log.e("WHOCR-Paddle", "PP-OCRv6_medium model initialization failed", error)
            throw error
        }
        val vertical = language == OcrLanguage.JapaneseVertical
        // PP-OCR recognition reads text lines horizontally. Turning a vertical
        // Japanese page counter-clockwise makes columns readable left-to-right.
        val input = if (vertical) bitmap.rotateCounterClockwise() else bitmap
        val run = ocr.recognize(input)
        Log.i(
            "WHOCR-Paddle",
            "PP-OCRv6_medium completed ${run.lineCount} lines in ${run.totalTimeMs} ms",
        )
        val blocks = run.results.mapNotNull { result ->
            val text = result.text.trim()
            val points = result.box.points
            if (text.isBlank() || points.isEmpty()) return@mapNotNull null
            var rect = RectF2(
                left = points.minOf { it.x },
                top = points.minOf { it.y },
                right = points.maxOf { it.x },
                bottom = points.maxOf { it.y },
            )
            if (vertical) rect = rect.mapCounterClockwiseRectToSource(bitmap.width.toFloat())
            RecognizedTextBlock(
                text = text,
                boundingBox = rect,
                lines = listOf(RecognizedTextLine(text, rect)),
            )
        }
        return OcrResultMapper.fromBlocks(blocks)
    }

    private fun Bitmap.rotateCounterClockwise(): Bitmap = Bitmap.createBitmap(
        this, 0, 0, width, height, Matrix().apply { postRotate(-90f) }, true,
    )

    private fun RectF2.mapCounterClockwiseRectToSource(sourceWidth: Float): RectF2 = RectF2(
        left = sourceWidth - bottom,
        top = left,
        right = sourceWidth - top,
        bottom = right,
    )
}
