package com.zebra.zebrakotlindemo.deeplink

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import kotlin.getValue

class DeeplinkActivity : ComponentActivity() {

    val viewModel: DeeplinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("----Deeplink----",
                modifier = Modifier
                    .padding()
            )
            RoundButton("SIM Info by intent", color = Color(0xFFF5B027)) {
                viewModel.openMobileNetworkSettings(context)
            }
            RoundButton("SIM Info by activity") {
                viewModel.openMobileNetworkSettingsByActivity(context)
            }
            RoundButton("SIM Info by hidden menu") {
                viewModel.openHiddenRadioInfoMenu(context)
            }
        }
    }
}