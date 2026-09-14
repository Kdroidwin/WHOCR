package com.whocr.assistant

import android.content.Intent
import android.speech.RecognitionService

/**
 * Companion service required by some OEM assistant pickers.
 *
 * WHOCR does not accept voice input: OCR is image-only and completely local.
 * This service deliberately reports no speech result and performs no network
 * access, recording, or background work.
 */
class WhoCrRecognitionService : RecognitionService() {
    override fun onStartListening(intent: Intent?, callback: Callback?) = Unit

    override fun onStopListening(callback: Callback?) = Unit

    override fun onCancel(callback: Callback?) = Unit
}
