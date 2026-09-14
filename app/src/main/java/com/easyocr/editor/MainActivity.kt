package com.easyocr.editor

import android.graphics.Color
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.easyocr.editor.ui.ScreenshotEditorScreen
import com.easyocr.editor.ui.theme.EasyOcrTheme
import com.whocr.assistant.AssistantScreenshotStore

class MainActivity : ComponentActivity() {
    private val viewModel: EditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The OCR source and recognized text must not be capturable by another app.
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        viewModel.openIntent(intent)
        AssistantScreenshotStore.consume()?.let(viewModel::openBitmap)

        setContent {
            EasyOcrTheme {
                val state by viewModel.uiState.collectAsState()
                val imagePicker = rememberLauncherForActivityResult(
                    ActivityResultContracts.GetContent(),
                ) { uri: Uri? ->
                    uri?.let(viewModel::openUri)
                }
                val shareChooserTitle = remember { "Share image" }

                ScreenshotEditorScreen(
                    state = state,
                    onOpenPicker = { imagePicker.launch("image/*") },
                    onToggleOverlays = viewModel::toggleOverlays,
                    onRotateLeft = viewModel::rotateLeft,
                    onRotateRight = viewModel::rotateRight,
                    onCrop = viewModel::cropTo,
                    onApplyDrawing = viewModel::applyDrawing,
                    onSaveCopy = {
                        viewModel.saveCopy()
                    },
                    onShare = {
                        viewModel.share { shareIntent ->
                            startActivity(Intent.createChooser(shareIntent, shareChooserTitle))
                        }
                    },
                    onShowMessageConsumed = viewModel::clearMessage,
                    onCopyAll = {},
                    onRerunOcr = viewModel::rerunOcr,
                    onLanguageSelected = viewModel::setLanguage,
                    onOcrProfileSelected = viewModel::setOcrProfile,
                    onOpenAssistantSettings = {
                        startActivity(Intent(Settings.ACTION_VOICE_INPUT_SETTINGS))
                    },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        viewModel.openIntent(intent)
        AssistantScreenshotStore.consume()?.let(viewModel::openBitmap)
    }

}
