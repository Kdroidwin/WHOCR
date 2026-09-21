package com.easyocr.editor.translation

import android.content.Context
import java.io.File

/** Keeps only the model binary locally; source images and translated text are never persisted. */
class TranslationRepository(private val context: Context) {
    fun translate(source: String, profile: TranslationProfile, target: TranslationTarget): String {
        require(source.isNotBlank()) { "翻訳する文字がありません" }
        require(profile == TranslationProfile.Fast) {
            "QUALITY（TranslateGemma 4B）のモデルはまだ同梱されていません"
        }
        return HyMtNative.translate(ensureFastModel().absolutePath, source, target.modelLanguage).trim()
            .ifBlank { throw IllegalStateException("翻訳結果を生成できませんでした") }
    }

    private fun ensureFastModel(): File {
        val models = File(context.noBackupFilesDir, "models").apply { mkdirs() }
        val destination = File(models, FAST_MODEL)
        if (destination.length() == FAST_MODEL_SIZE) return destination
        destination.delete()
        context.assets.open(FAST_MODEL).use { input ->
            destination.outputStream().use { output -> input.copyTo(output) }
        }
        check(destination.length() == FAST_MODEL_SIZE) { "Hy-MT モデルの展開に失敗しました" }
        return destination
    }

    private companion object {
        const val FAST_MODEL = "Hy-MT1.5-1.8B-1.25bit.gguf"
        const val FAST_MODEL_SIZE = 461_860_704L
    }
}
