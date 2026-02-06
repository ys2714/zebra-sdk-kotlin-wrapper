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

class EMDKTouchPanelActivity: ComponentActivity() {

    val viewModel = EMDKTouchPanelViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Control Touch Panel Sensitivity use Zebra MX API",
                modifier = Modifier
                    .padding()
            )
            Text("System Touch Mode: " + viewModel.touchMode.value)
            Text("Vendor Touch Mode: " + viewModel.vendorTouchMode.value)
            RoundButton("Refresh Touch Mode") {
                viewModel.fetchTouchMode(context)
            }
            RoundButton("Finger Only") {
                viewModel.setTouchPanelToFingerOnly(context)
            }
            RoundButton("Glove and Finger") {
                viewModel.setTouchPanelToGloveAndFinger(context)
            }
            RoundButton("Stylus and Finger") {
                viewModel.setTouchPanelToStylusAndFinger(context)
            }
            RoundButton("Stylus and Glove and Finger") {
                viewModel.setTouchPanelToStylusGloveAndFinger(context)
            }
            RoundButton("Open Settings to check", Color(0xFF00D100)) {
                viewModel.openSettings(context)
            }
        }
    }
}