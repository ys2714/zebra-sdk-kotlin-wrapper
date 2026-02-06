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
            Text("Product Model: " + viewModel.productModel.value)
            Text("System Touch Mode: " + viewModel.systemTouchMode.value)
            Text("Vendor Touch Mode: " + viewModel.vendorTouchMode.value)
            Text("App Touch Mode: " + viewModel.appTouchMode.value)
            RoundButton("Refresh Touch Mode", Color(0xFF00D100)) {
                viewModel.fetchTouchMode(context)
            }
            when(viewModel.productModel.value) {
                EMDKTouchPanelViewModel.ProductModels.TC26 -> {
                    RoundButton("Finger Only") {
                        viewModel.setTouchPanelToFingerOnly(context)
                    }
                    RoundButton("Glove and Finger") {
                        viewModel.setTouchPanelToGloveAndFinger(context)
                    }
                }
                EMDKTouchPanelViewModel.ProductModels.TC27 -> {
                    RoundButton("Finger Only") {
                        viewModel.setTouchPanelToFingerOnly(context)
                    }
                    RoundButton("Glove and Finger") {
                        viewModel.setTouchPanelToGloveAndFinger(context)
                    }
                }
                EMDKTouchPanelViewModel.ProductModels.TC201 -> {
                    RoundButton("Finger Only") {
                        viewModel.setTouchPanelToFingerOnly(context)
                    }
                    RoundButton("Glove and Finger") {
                        viewModel.setTouchPanelToGloveAndFinger(context)
                    }
                }
                EMDKTouchPanelViewModel.ProductModels.TC52 -> {
                    RoundButton("Finger Only") {
                        viewModel.setTouchPanelToFingerOnly(context)
                    }
                    RoundButton("Glove and Finger") {
                        viewModel.setTouchPanelToGloveAndFinger(context)
                    }
                    RoundButton("Stylus and Finger") {
                        viewModel.setTouchPanelToStylusAndFinger(context)
                    }
                }
            }
            RoundButton("Open Settings to check", Color(0xFF00D100)) {
                viewModel.openSettings(context)
            }
        }
    }
}