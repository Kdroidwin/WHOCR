package com.easyocr.editor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.easyocr.editor.ocr.OcrProfile
import com.easyocr.editor.translation.TranslationProfile
import com.easyocr.editor.translation.TranslationTarget

/** Settings that must remain available even before the user has opened an image. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrSettingsBottomSheet(
    profile: OcrProfile,
    translationProfile: TranslationProfile,
    translationTarget: TranslationTarget,
    onProfileSelected: (OcrProfile) -> Unit,
    onTranslationProfileSelected: (TranslationProfile) -> Unit,
    onTranslationTargetSelected: (TranslationTarget) -> Unit,
    onOpenAssistantSettings: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Black,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("設定", style = MaterialTheme.typography.titleLarge)
            Text("OCRモデル", style = MaterialTheme.typography.titleMedium)
            OcrProfile.entries.forEach { option ->
                FilterChip(
                    selected = option == profile,
                    onClick = { onProfileSelected(option) },
                    label = {
                        Column {
                            Text(option.label)
                            Text(option.description, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Text(
                "高速モデルが標準です。どちらも端末内だけで実行され、画像や文字列を送信しません。",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text("翻訳", style = MaterialTheme.typography.titleMedium)
            TranslationProfile.entries.forEach { option ->
                FilterChip(
                    selected = option == translationProfile,
                    onClick = { onTranslationProfileSelected(option) },
                    label = { Text("${option.label}: ${option.description}") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Text("翻訳先", style = MaterialTheme.typography.titleMedium)
            TranslationTarget.entries.forEach { option ->
                FilterChip(
                    selected = option == translationTarget,
                    onClick = { onTranslationTargetSelected(option) },
                    label = { Text(option.label) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Button(
                onClick = onOpenAssistantSettings,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Outlined.OpenInNew, contentDescription = null)
                Text("既定のアシスタントを設定")
            }
        }
    }
}
