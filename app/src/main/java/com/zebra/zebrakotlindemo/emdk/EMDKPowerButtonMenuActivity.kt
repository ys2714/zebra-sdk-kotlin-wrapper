package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKPowerButtonMenuActivity: ComponentActivity() {

    val viewModel = EMDKPowerButtonMenuViewModel()

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
            Text("Power Key Menu (Long Press Power Key)",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Power Off Button") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewModel.disablePowerOff(context)
                } else {
                    viewModel.showDebugToast(
                        context,
                        "Not Supported",
                        "need android 11 or above"
                    )
                }
            }
            RoundButton("Enable Power Off Button") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewModel.enablePowerOff(context)
                } else {
                    viewModel.showDebugToast(
                        context,
                        "Not Supported",
                        "need android 11 or above"
                    )
                }
            }
            Text("Screenshot (Power + Volume Down)",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Screenshot") {
                viewModel.disableScreenShot(context)
            }
            RoundButton("Enable Screenshot") {
                viewModel.enableScreenShot(context)
            }
        }
    }
}