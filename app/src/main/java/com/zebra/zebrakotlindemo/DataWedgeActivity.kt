package com.zebra.zebrakotlindemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

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