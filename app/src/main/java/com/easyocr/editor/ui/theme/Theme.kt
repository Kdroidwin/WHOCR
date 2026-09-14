package com.easyocr.editor.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmoledBlackColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFC3CCFF),
    onPrimary = Color(0xFF101535),
    secondary = Color(0xFFD1C2FF),
    onSecondary = Color(0xFF1A1030),
    surface = Color.Black,
    onSurface = Color(0xFFE6E1E9),
    surfaceContainer = Color(0xFF080808),
    surfaceContainerLow = Color(0xFF050505),
    surfaceContainerHigh = Color(0xFF101010),
    surfaceContainerHighest = Color(0xFF181818),
    surfaceVariant = Color(0xFF151515),
    background = Color.Black,
    onBackground = Color(0xFFE6E1E9),
    outline = Color(0xFFAAA5AD),
)

@Composable
fun EasyOcrTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AmoledBlackColors,
        content = content,
    )
}
