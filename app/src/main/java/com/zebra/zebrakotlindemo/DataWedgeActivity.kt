package com.zebra.zebrakotlindemo

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zebra.emdk_kotlin_wrapper.mx.MXBase

class DataWedgeActivity : ComponentActivity() {

    val viewModel = DataWedgeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleOnPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.handleOnDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) return super.onKeyDown(keyCode, event)
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                MXBase.KeyCodes.BUTTON_SCAN.value -> {
                    Log.d("", "LEFT Scan PRESSED")
                    viewModel.startScanning(this)
                }
                MXBase.KeyCodes.BUTTON_R1.value -> {
                    Log.d("", "RIGHT Scan PRESSED")
                    viewModel.startScanning(this)
                }
                MXBase.KeyCodes.BUTTON_L2.value -> {
                    Log.d("", "PTT PRESSED")
                    viewModel.getScannerStatus(this)
                }
                MXBase.KeyCodes.BUTTON_R2.value -> {
                    Log.d("", "R2 PRESSED (if exist)")

                }
            }
        } else if (event.action == KeyEvent.ACTION_UP) {
            when (keyCode) {
                MXBase.KeyCodes.BUTTON_SCAN.value -> {
                    Log.d("", "LEFT Scan PRESSED")
                    viewModel.stopScanning(this)
                }
                MXBase.KeyCodes.BUTTON_R1.value -> {
                    Log.d("", "RIGHT Scan PRESSED")
                    viewModel.stopScanning(this)
                }
                MXBase.KeyCodes.BUTTON_L2.value -> {
                    Log.d("", "PTT PRESSED")
                }
                MXBase.KeyCodes.BUTTON_R2.value -> {
                    Log.d("", "R2 PRESSED (if exist)")
                }
            }
        } else {

        }

        return super.onKeyDown(keyCode, event)
    }

    @Composable
    fun RootView() {
        val newText = remember { viewModel.text }
        Column {
            Text("Scanner Status: " + viewModel.scannerStatus.value)
            StyledOutlinedTextField(newText.value) { newValue ->
               newText.value = newValue
            }
            RoundButton("Push Scan Button or Tap this") {
                viewModel.startScanning(this@DataWedgeActivity)
            }
            RoundButton("Refresh Scanner Status") {
                viewModel.getScannerStatus(this@DataWedgeActivity)
            }
        }
    }
}