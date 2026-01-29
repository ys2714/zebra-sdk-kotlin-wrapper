package com.zebra.zebrakotlindemo.datawedge

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

/*
please manually create a profile named "control_scan_trigger" with following settings:

1.Barcode Input

1.1 Enabled = true
1.2 Hardware Trigger = true
1.3 Config Scanner Settings
1.3.1 Reader Parameters
1.3.1.1 Aim Type = Timed Continuous (the last one in the list)

2.Keystroke Output

2.1 Enabled = false

3.Intent Output

3.1 Enabled = true
3.2 Intent Action = com.zebra.trigger.ACTION
3.3 Intent Category = com.zebra.trigger.CATEGORY
3.4 Intent Delivery = Broadcast Intent
*/
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
