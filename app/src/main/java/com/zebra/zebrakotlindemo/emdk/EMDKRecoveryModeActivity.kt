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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKRecoveryModeActivity: ComponentActivity() {

    val viewModel = EMDKRecoveryModeViewModel()

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
            Text("Control Recovery Mode use Zebra MX API (>= A13)",
                modifier = Modifier
                    .padding()
            )
            Text("the change did not persist, you need to disable it again after second reboot\n " +
                    "* IMPORTANT PERSISTENCE NOTE: This setting persists on the device ONLY if the Enterprise Reset / Factory Reset is initiated by a barcode.\n" +
                    "* Resetting the device through the Android Settings panel or by any other means removes all settings created for this feature.",
                modifier = Modifier
                    .padding()
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                RoundButton("Temporary Disable Recovery Mode (Once)", Color(0xFFD10000)) {
                    viewModel.disableRecoveryModeAccess(context)
                }
                RoundButton("Enable Recovery Mode") {
                    viewModel.enableRecoveryModeAccess(context)
                }
            } else {
                Text("Sorry, the feature only supported on Android 13 and above",
                    modifier = Modifier
                        .padding()
                )
            }
        }
    }
}