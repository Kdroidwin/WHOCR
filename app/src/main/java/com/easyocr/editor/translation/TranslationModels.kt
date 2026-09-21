package com.easyocr.editor.translation

enum class TranslationProfile(
    val label: String,
    val description: String,
) {
    Fast("FAST", "Hy-MT1.5 1.8B。端末内で高速に翻訳します。"),
    Quality("QUALITY", "TranslateGemma 4B。モデル追加後に利用できます。"),
}

enum class TranslationTarget(val label: String, val modelLanguage: String) {
    Japanese("日本語", "Japanese"),
    English("English", "English"),
    ChineseSimplified("简体中文", "Chinese"),
    Korean("한국어", "Korean"),
    French("Français", "French"),
    German("Deutsch", "German"),
    Spanish("Español", "Spanish"),
    Italian("Italiano", "Italian"),
    Portuguese("Português", "Portuguese"),
    Russian("Русский", "Russian"),
    Arabic("العربية", "Arabic"),
    Thai("ไทย", "Thai"),
    Vietnamese("Tiếng Việt", "Vietnamese"),
    Indonesian("Bahasa Indonesia", "Indonesian"),
}
