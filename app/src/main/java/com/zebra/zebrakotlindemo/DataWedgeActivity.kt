package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zebra.emdk_kotlin_wrapper.mx.MXBase

class DataWedgeActivity : ComponentActivity() {

    companion object {
        const val TAG = "DataWedgeActivity"

        fun start(context: Context) {
            context.startActivity(
                Intent(context, DataWedgeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

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
                MXBase.KeyCodes.SCAN.value -> {
                    Log.d("", "LEFT Scan PRESSED")
                    viewModel.startScanning(this)
                }
                MXBase.KeyCodes.RIGHT_TRIGGER_1.value -> {
                    Log.d("", "RIGHT Scan PRESSED")
                    viewModel.startScanning(this)
                }
                MXBase.KeyCodes.LEFT_TRIGGER_1.value -> {
                    Log.d("", "PTT PRESSED")
                    finish()
                }
                MXBase.KeyCodes.RIGHT_TRIGGER_2.value -> {
                    Log.d("", "R2 PRESSED (if exist)")
                }
            }
        } else if (event.action == KeyEvent.ACTION_UP) {
            when (keyCode) {
                MXBase.KeyCodes.SCAN.value -> {
                    Log.d("", "LEFT Scan PRESSED")
                    viewModel.stopScanning(this)
                }
                MXBase.KeyCodes.RIGHT_TRIGGER_1.value -> {
                    Log.d("", "RIGHT Scan PRESSED")
                    viewModel.stopScanning(this)
                }
                MXBase.KeyCodes.LEFT_TRIGGER_2.value -> {
                    Log.d("", "PTT PRESSED")
                }
                MXBase.KeyCodes.RIGHT_TRIGGER_2.value -> {
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
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Scanner Status: " + viewModel.scannerStatus.value)
            RoundButton("Refresh Scanner Status") {
                viewModel.getScannerStatus(this@DataWedgeActivity)
            }
            StyledOutlinedTextField("scan barcode or manually input", newText.value) { newValue ->
                newText.value = newValue
            }
            RoundButton("Push Scan Button or Tap this") {
                viewModel.startScanning(this@DataWedgeActivity)
            }
            Text("Current Profile: " + viewModel.profileName.value)
            RoundButton("Switch Profile") {
                viewModel.switchProfile(this@DataWedgeActivity)
            }
            StyledOutlinedTextField("scan expired date (OCR)", newText.value) { newValue ->
                newText.value = newValue
            }
        }
    }
}