package com.easyocr.editor.ocr

import android.content.Context

/** Stores only the user's local model choice; no image or recognized text is retained. */
class OcrProfilePreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        "whocr_ocr_preferences",
        Context.MODE_PRIVATE,
    )

    fun load(): OcrProfile = preferences.getString(KEY_PROFILE, null)
        ?.let { saved -> OcrProfile.entries.firstOrNull { it.name == saved } }
        ?: OcrProfile.Fast

    fun save(profile: OcrProfile) {
        preferences.edit().putString(KEY_PROFILE, profile.name).apply()
    }

    private companion object {
        const val KEY_PROFILE = "ocr_profile"
    }
}
