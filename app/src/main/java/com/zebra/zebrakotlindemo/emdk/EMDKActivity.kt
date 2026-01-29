package com.zebra.zebrakotlindemo.emdk

import android.os.Build
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
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKActivity: ComponentActivity() {

    private val viewModel = EMDKViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this.applicationContext)
        setContent {
           RootView()
        }
    }

    override fun onResume() {
        super.onResume()
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
            Text("Power Management",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Set Sleep") {
                viewModel.setSleep(this@EMDKActivity)
            }
            RoundButton("Reboot") {
                viewModel.setReboot(this@EMDKActivity)
            }
            Text("System Clock",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Set Clock to android release date 2008") {
                viewModel.setClockToAndroidReleaseDate(this@EMDKActivity)
            }
            RoundButton("Set Clock back to Google NTP time") {
                viewModel.setClockToGoogleNTPTime(this@EMDKActivity)
            }
            Text("Lock Screen",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Lock Screen") {
                viewModel.disableLockScreen(this@EMDKActivity)
            }
            RoundButton("Enable Lock Screen") {
                viewModel.enableLockScreen(this@EMDKActivity)
            }
            Text("Screenshot (Power + Volume Down)",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Screenshot") {
                viewModel.disableScreenShot(this@EMDKActivity)
            }
            RoundButton("Enable Screenshot") {
                viewModel.enableScreenShot(this@EMDKActivity)
            }
            Text("Power Key Menu (Long Press Power Key)",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Power Off Button") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewModel.disablePowerOff(this@EMDKActivity)
                } else {
                    viewModel.showDebugToast(
                        this@EMDKActivity,
                        "Not Supported",
                        "need android 11 or above"
                    )
                }
            }
            RoundButton("Enable Power Off Button") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    viewModel.enablePowerOff(this@EMDKActivity)
                } else {
                    viewModel.showDebugToast(
                        this@EMDKActivity,
                        "Not Supported",
                        "need android 11 or above"
                    )
                }
            }
        }
    }
}