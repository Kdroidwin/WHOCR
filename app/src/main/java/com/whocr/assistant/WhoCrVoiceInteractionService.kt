package com.whocr.assistant

import android.service.voice.VoiceInteractionService

class WhoCrVoiceInteractionService : VoiceInteractionService() {
    override fun onReady() {
        super.onReady()
        // Deliberately no background capture, accessibility service, or network work.
    }

    override fun onShutdown() {
        AssistantScreenshotStore.clear()
        super.onShutdown()
    }
}
