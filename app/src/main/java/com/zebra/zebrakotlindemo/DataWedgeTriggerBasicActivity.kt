package com.zebra.zebrakotlindemo

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class DataWedgeTriggerBasicActivity: ComponentActivity() {

    val viewModel = DataWedgeTriggerBasicViewModel()

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
        viewModel.handleOnDestroy(this)
    }

    @Composable
    fun RootView() {
        val barcodeText = remember { viewModel.barcodeText }
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("日本語")
            Text("English")
            Text("中文")
            Text("Tiếng Việt")
            Text("แบบไทย")
            Text("हिंदी")
            Text("----------")
            Text("please check DataWedgeTriggerBasicViewModel.kt file to find about how to setup profile")
            Text("----------")
            Text("Session Status: " + viewModel.sessionStatus.value)
            StyledOutlinedTextField(
                "scan barcode or manually input",
                barcodeText.value,
                modifier = Modifier
                    .fillMaxWidth()
            ) { newValue ->
                barcodeText.value = newValue
            }
            RoundButton("Stop Continues Scan", color = Color(0xFFF00000)) {
                viewModel.stopScanning(this@DataWedgeTriggerBasicActivity)
            }
        }
    }
}
