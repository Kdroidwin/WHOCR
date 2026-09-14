package com.easyocr.editor.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyScreen(
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
                .padding(horizontal = 24.dp, vertical = 18.dp),
        ) {
            Text("Privacy")
            Text(
                text = "OCR runs locally on your device. Images are not uploaded.\n\n" +
                    "WHOCR does not request internet access, does not use analytics, " +
                    "does not include ads, and does not send screenshots to a server. " +
                    "When invoked as the default assistant, Android delivers a screenshot " +
                    "directly in memory. WHOCR never writes that assistant screenshot to disk " +
                    "and erases the handoff buffer after it is consumed or the assistant closes.",
                modifier = Modifier.padding(top = 12.dp, bottom = 28.dp),
            )
        }
    }
}
