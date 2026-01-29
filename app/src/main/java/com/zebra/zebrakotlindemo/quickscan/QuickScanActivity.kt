package com.zebra.zebrakotlindemo.quickscan

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class QuickScanActivity : ComponentActivity() {

    private val viewModel = QuickScanViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == MXBase.KeyCodes.SCAN.value
            || keyCode == MXBase.KeyCodes.RIGHT_TRIGGER_1.value) {
            viewModel.startScanning()
        }
        if (keyCode == MXBase.KeyCodes.LEFT_TRIGGER_1.value) {
            viewModel.startScanning()
        }
        return super.onKeyDown(keyCode, event)
    }

    @Composable
    fun RootView() {
        val barcodeText1 = remember { viewModel.barcodeText1 }
        val barcodeText2 = remember { viewModel.barcodeText2 }
        val barcodeText3 = remember { viewModel.barcodeText3 }
        val barcodeText4 = remember { viewModel.barcodeText4 }

        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            if (viewModel.servicePrepared.value) {
                Text("AimType = 0.TRIGGER")
                StyledOutlinedTextField(
                    "single",
                    barcodeText1.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState ->
                            if (focusState.isFocused) {
                                viewModel.selectSingleBarcode()
                            }
                        })
                ) { newValue ->
                    barcodeText1.value = newValue
                }
                Text("AimType = 8.TIMED_CONTINUOUS")
                StyledOutlinedTextField(
                    "continuous",
                    barcodeText2.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState ->
                            if (focusState.isFocused) {
                                viewModel.selectContinuousBarcode()
                            }
                        })
                ) { newValue ->
                    barcodeText2.value = newValue
                }
                Text("1D Decoders = CODE_128, JAN_EAN_13")
                StyledOutlinedTextField(
                    "1D",
                    barcodeText3.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState ->
                            if (focusState.isFocused) {
                                viewModel.select1DDecoders()
                            }
                        })
                ) { newValue ->
                    barcodeText3.value = newValue
                }
                Text("2D Decoders = DATA_MATRIX, PDF_417, QR")
                StyledOutlinedTextField(
                    "2D",
                    barcodeText4.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState ->
                            if (focusState.isFocused) {
                                viewModel.select2DDecoders()
                            }
                        })
                ) { newValue ->
                    barcodeText4.value = newValue
                }
                RoundButton("Start Scan") {
                    viewModel.stopScanning()
                    viewModel.startScanning()
                }
                RoundButton("Stop Scan") {
                    viewModel.stopScanning()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}