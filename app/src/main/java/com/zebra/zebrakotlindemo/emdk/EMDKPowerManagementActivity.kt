package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class EMDKPowerManagementActivity: ComponentActivity() {

    val viewModel = EMDKPowerManagementViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Power Management",
                modifier = Modifier
                    .padding()
            )
            Text("----OS Update Info----",
                modifier = Modifier
                    .padding()
            )
            Text("OS Update Status: " + viewModel.osUpdateStatus.value)
            Text("OS Update Detail: " + viewModel.osUpdateDetail.value)
            Text("OS Update Timestamp: " + viewModel.osUpdateTimestamp.value)
            RoundButton("Start Refresh Status") {
                viewModel.startFetchOSUpdateStatus(context)
            }
            RoundButton("Force Sleep") {
                viewModel.setSleep(context)
            }
            RoundButton("Force Reboot") {
                viewModel.setReboot(context)
            }
            Text("Manifest path to verify OS")
            StyledOutlinedTextField(
                "Manifest path",
                viewModel.manifestPath.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged({ focusState -> })
            ) { newValue ->
                viewModel.manifestPath.value = newValue
            }
            RoundButton("Verify Upgrade OS Manifest File") {
                viewModel.checkOSZipFile(context, viewModel.manifestPath.value) { success ->
                    Toast.makeText(context, "verify success ? $success", Toast.LENGTH_SHORT).show()
                }
            }
            Text("BSP path for OS upgrade")
            StyledOutlinedTextField(
                "BSP path",
                viewModel.upgradeBSPPath.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged({ focusState -> })
            ) { newValue ->
                viewModel.upgradeBSPPath.value = newValue
            }
            RoundButton("Upgrade OS") {
                viewModel.upgradeOS(context, viewModel.upgradeBSPPath.value, true)
            }
            Text("BSP path for OS downgrade")
            StyledOutlinedTextField(
                "BSP path",
                viewModel.downgradeBSPPath.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged({ focusState -> })
            ) { newValue ->
                viewModel.downgradeBSPPath.value = newValue
            }
            RoundButton("Downgrade OS") {
                viewModel.downgradeOS(context, viewModel.downgradeBSPPath.value, true)
            }
        }
    }
}