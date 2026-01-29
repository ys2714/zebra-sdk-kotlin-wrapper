package com.zebra.zebrakotlindemo.datawedge

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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class DataWedgeTriggerActivity: ComponentActivity() {

    val viewModel = DataWedgeTriggerViewModel()

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        viewModel.handleOnKeyDown(this, keyCode, event)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        viewModel.handleOnKeyUp(this, keyCode, event)
        return super.onKeyUp(keyCode, event)
    }

    @Composable
    fun RootView() {
        val barcodeText = remember { viewModel.barcodeText }
        val ocrText = remember { viewModel.ocrText }

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
            Text("profile created by DataWedgeHelper.configWithJSON() no need to manually setup profile")
            Text("----------")
            Text("Session Status: " + viewModel.sessionStatus.value)
            Text(
                "Scan Session Remaining: ${viewModel.scanActivatingRemainSeconds.value}",
                modifier = Modifier
                    .padding(top = 10.dp),
                fontSize = 24.sp
            )
            StyledOutlinedTextField(
                "scan barcode or manually input",
                barcodeText.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged({ focusState ->
                        if (focusState.isFocused) {

                        }
                    })
            ) { newValue ->
                barcodeText.value = newValue
            }
            RoundButton("Star Continues Scan", color = Color(0xFF00F000)) {
                viewModel.startScanning(this@DataWedgeTriggerActivity)
            }
            RoundButton("Stop Continues Scan", color = Color(0xFFF00000)) {
                viewModel.stopScanning(this@DataWedgeTriggerActivity)
            }
        }
    }
}