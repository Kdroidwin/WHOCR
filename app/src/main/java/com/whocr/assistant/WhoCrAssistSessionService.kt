package com.whocr.assistant

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.voice.VoiceInteractionSession
import android.service.voice.VoiceInteractionSessionService
import android.view.WindowManager
import com.easyocr.editor.MainActivity

class WhoCrAssistSessionService : VoiceInteractionSessionService() {
    override fun onNewSession(args: Bundle?): VoiceInteractionSession = WhoCrSession(this)
}

private class WhoCrSession(private val appContext: Context) : VoiceInteractionSession(appContext) {
    override fun onShow(args: Bundle?, showFlags: Int) {
        super.onShow(args, showFlags)
        // This session has no visible UI and cannot intercept the target app.
        window?.window?.addFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        )
    }

    override fun onHandleScreenshot(screenshot: Bitmap?) {
        super.onHandleScreenshot(screenshot)
        if (screenshot == null) return
        AssistantScreenshotStore.replace(screenshot)
        appContext.startActivity(
            Intent(appContext, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra(EXTRA_ASSISTANT_CAPTURE, true)
            },
        )
    }

    override fun onHide() {
        // Give the launched activity a short chance to consume the one-shot
        // reference, then erase it even if activity launch was rejected.
        Handler(Looper.getMainLooper()).postDelayed({
            AssistantScreenshotStore.clear()
        }, HANDOFF_EXPIRY_MS)
        super.onHide()
    }

    private companion object {
        const val EXTRA_ASSISTANT_CAPTURE = "com.whocr.extra.ASSISTANT_CAPTURE"
        const val HANDOFF_EXPIRY_MS = 3_000L
    }
}
