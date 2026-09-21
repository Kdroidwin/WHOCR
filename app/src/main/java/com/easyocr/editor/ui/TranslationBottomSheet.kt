package com.easyocr.editor.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.easyocr.editor.translation.TranslationProfile
import com.easyocr.editor.translation.TranslationTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslationBottomSheet(
    sourceText: String,
    translatedText: String,
    isTranslating: Boolean,
    profile: TranslationProfile,
    target: TranslationTarget,
    onTranslate: () -> Unit,
    onProfileSelected: (TranslationProfile) -> Unit,
    onTargetSelected: (TranslationTarget) -> Unit,
    onCopyTranslation: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.Black, contentColor = MaterialTheme.colorScheme.onSurface) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("翻訳", style = MaterialTheme.typography.titleLarge)
            Text("翻訳モデル", style = MaterialTheme.typography.titleMedium)
            TranslationProfile.entries.forEach { option ->
                FilterChip(
                    selected = profile == option,
                    onClick = { onProfileSelected(option) },
                    label = { Column { Text(option.label); Text(option.description, style = MaterialTheme.typography.bodySmall) } },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Text("翻訳先: ${target.label}", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                TranslationTarget.entries.forEach { option ->
                    FilterChip(selected = target == option, onClick = { onTargetSelected(option) }, label = { Text(option.label) })
                }
            }
            Button(onClick = onTranslate, enabled = sourceText.isNotBlank() && !isTranslating, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.Translate, contentDescription = null)
                Text(if (isTranslating) "翻訳中…" else "${target.label} に翻訳")
            }
            if (translatedText.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("翻訳結果", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onCopyTranslation) {
                        Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                        Text("コピー")
                    }
                }
                SelectionContainer {
                    Text(translatedText, modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState()))
                }
            }
            Text("翻訳は端末内で実行されます。画像・OCR文字列・翻訳結果を送信または保存しません。", style = MaterialTheme.typography.bodySmall)
        }
    }
}
