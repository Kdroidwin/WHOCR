package com.easyocr.editor.translation

import android.content.Context

class TranslationPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("translation_preferences", Context.MODE_PRIVATE)

    fun loadTarget(): TranslationTarget = preferences.getString(KEY_TARGET, null)
        ?.let { value -> TranslationTarget.entries.firstOrNull { it.name == value } }
        ?: TranslationTarget.Japanese

    fun loadProfile(): TranslationProfile = preferences.getString(KEY_PROFILE, null)
        ?.let { value -> TranslationProfile.entries.firstOrNull { it.name == value } }
        ?: TranslationProfile.Fast

    fun saveTarget(target: TranslationTarget) = preferences.edit().putString(KEY_TARGET, target.name).apply()

    fun saveProfile(profile: TranslationProfile) = preferences.edit().putString(KEY_PROFILE, profile.name).apply()

    private companion object {
        const val KEY_TARGET = "target"
        const val KEY_PROFILE = "profile"
    }
}
