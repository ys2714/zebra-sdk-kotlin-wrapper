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
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

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
            RoundButton("Verify Upgrade OS Zip File") {
                val filePath = "/data/tmp/public/DeviceManifest.xml"
                //val filePath = "/data/tmp/public/HE_FULL_UPDATE_10-74-20.00-QG-U00-C473-HEL-04.zip"
                viewModel.checkOSZipFile(context, filePath)
            }
            RoundButton("Upgrade OS") {
                val filePath = "/data/tmp/public/AT_FULL_UPDATE_14-35-10.00-UG-U56-STD-ATH-04.zip"
                //val filePath = "/data/tmp/public/HE_FULL_UPDATE_10-74-20.00-QG-U00-C473-HEL-04.zip"
                viewModel.upgradeOS(context, filePath, true)
            }
            RoundButton("Downgrade OS") {
                val filePath = "/data/tmp/public/AT_FULL_UPDATE_14-35-10.00-UG-U00-STD-ATH-04.zip"
                //val filePath = "/data/tmp/public/HE_FULL_UPDATE_10-74-20.00-QG-U00-C472-HEL-04.zip"
                viewModel.downgradeOS(context, filePath, true)
            }
        }
    }
}