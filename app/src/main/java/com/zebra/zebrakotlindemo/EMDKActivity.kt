package com.zebra.zebrakotlindemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class EMDKActivity: ComponentActivity() {

    private val viewModel = EMDKViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupEMDKIfNeeded(this.applicationContext)
        setContent {
           RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setupEMDKIfNeeded(this.applicationContext)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Composable
    fun RootView() {
        val newText = remember { viewModel.text }
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
//            StyledOutlinedTextField(newText.value) { newValue ->
//                newText.value = newValue
//            }
//            RoundButton("Start Scan") {
//                viewModel.startScan()
//            }
//            RoundButton("Stop Scan") {
//                viewModel.stopScan()
//            }
            Text("OEM Info",
                modifier = Modifier
                    .padding()
            )
            Text(viewModel.serial.value)
            RoundButton("Fetch Serial Number") {
                viewModel.fetchSerialNumber(this@EMDKActivity)
            }
            Text(viewModel.imei.value)
            RoundButton("Fetch IMEI") {
                viewModel.fetchIMEI(this@EMDKActivity)
            }
            Text("Set System Clock",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Set Clock to android release date 2008") {
                viewModel.setClockToAndroidReleaseDate(this@EMDKActivity)
            }
            RoundButton("Set Clock back to Google NTP time") {
                viewModel.setClockToGoogleNTPTime(this@EMDKActivity)
            }
            RoundButton("Disable Lock Screen") {
                viewModel.disableLockScreen(this@EMDKActivity)
            }
            RoundButton("Enable Lock Screen") {
                viewModel.enableLockScreen(this@EMDKActivity)
            }
        }
    }
}