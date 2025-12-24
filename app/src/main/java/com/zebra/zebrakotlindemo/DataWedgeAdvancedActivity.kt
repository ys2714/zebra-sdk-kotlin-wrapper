package com.zebra.zebrakotlindemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp

class DataWedgeAdvancedActivity : ComponentActivity() {

    val viewModel = DataWedgeAdvancedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleOnPause(this)
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handleOnDestroy()
    }

    @Composable
    fun RootView() {
        val barcodeText = remember { viewModel.barcodeText }
        val ocrText = remember { viewModel.ocrText }

        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Scanner Status: " + viewModel.scannerStatus.value)
            Text("Current Profile: " + viewModel.currentProfileName.value)
            RoundButton("Refresh Scanner Status") {
                viewModel.getScannerStatus(this@DataWedgeAdvancedActivity)
            }
            StyledOutlinedTextField(
                "scan barcode or manually input",
                barcodeText.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged( { focusState ->
                        if (focusState.isFocused) {
                            viewModel.switchToBarcodePlugin(this@DataWedgeAdvancedActivity)
                        }
                    })
            ) { newValue ->
                barcodeText.value = newValue
            }
            StyledOutlinedTextField(
                "scan expired date (OCR)",
                ocrText.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged( { focusState ->
                        if (focusState.isFocused) {
                            viewModel.switchToOCRPlugin(this@DataWedgeAdvancedActivity)
                        }
                    })
            ) { newValue ->
                ocrText.value = newValue
            }
            RoundButton("Push Scan Button or Tap this") {
                viewModel.stopScanning(this@DataWedgeAdvancedActivity)
                viewModel.startScanning(this@DataWedgeAdvancedActivity)
            }
        }
    }
}