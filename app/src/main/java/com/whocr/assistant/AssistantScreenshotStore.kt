package com.whocr.assistant

import android.graphics.Bitmap

/**
 * One-shot, process-memory handoff for the system-owned assistant screenshot.
 * No file, cache entry, database row, URI, or log is ever created for it.
 */
object AssistantScreenshotStore {
    private var screenshot: Bitmap? = null

    @Synchronized
    fun replace(bitmap: Bitmap?) {
        screenshot?.takeIf { it !== bitmap && !it.isRecycled }?.recycle()
        screenshot = bitmap
    }

    @Synchronized
    fun consume(): Bitmap? = screenshot.also { screenshot = null }

    @Synchronized
    fun clear() {
        screenshot?.takeIf { !it.isRecycled }?.recycle()
        screenshot = null
    }
}
