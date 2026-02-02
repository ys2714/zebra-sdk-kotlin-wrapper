package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKUSBManagerActivity: ComponentActivity() {

    val viewModel = EMDKUSBManagerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Control USB Settings use Zebra MX API",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Set Client Mode to Charging Only") {
                viewModel.setUSBClientModeToChargingOnly(context)
            }
            RoundButton("Set Client Mode to File Transfer") {
                viewModel.setUSBClientModeToFileTransfer(context)
            }
            RoundButton("Set Client Mode to USB Tethering") {
                viewModel.setUSBClientModeToUSBTethering(context)
            }
            RoundButton("Open Settings to check", Color(0xFF00D100)) {
                viewModel.openSettings(context)
            }
        }
    }
}