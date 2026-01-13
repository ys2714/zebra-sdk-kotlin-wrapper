package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.utils.ZebraSystemEventMonitor

class MainActivity : ComponentActivity() {

    private val viewModel = MainViewModel()

    companion object {
        const val TAG = "MainActivity"

        fun start(context: Context) {
            context.startActivity(
                Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ZebraSystemEventMonitor.registerAppPauseCallback {
            viewModel.showDebugToast(this, "App", "Pause")
        }
        ZebraSystemEventMonitor.registerAppStopCallback {
            viewModel.showDebugToast(this, "App", "Stop")
        }
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.prepare(this)
        ZebraSystemEventMonitor.registerScreenOFFListener(this) { isScreenOff ->
            if (isScreenOff) {
                ScreenLockActivity.start(this)
                Log.d("", "Screen OFF")
            } else {
                Log.d("", "Screen ON")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        ZebraSystemEventMonitor.registerScreenOFFListener(this) { isScreenOff ->
            if (isScreenOff) {
                Log.d("", "Screen OFF")
            } else {
                ScreenLockActivity.start(this)
                Log.d("", "Screen ON")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == MXBase.KeyCodes.SCAN.value
            || keyCode == MXBase.KeyCodes.RIGHT_TRIGGER_1.value) {
            DataWedgeBasicActivity.start(this)
        }
        return super.onKeyDown(keyCode, event)
    }

    @Composable
    fun RootView(context: Context) {
        if (viewModel.emdkPrepared.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                ,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("----Version Info----",
                    modifier = Modifier
                        .padding()
                )
                Text("EMDK Version: " + viewModel.emdkVersion.value)
                Text("MX Version: " + viewModel.mxVersion.value)
                Text("DW Version: " + viewModel.dwVersion.value)
                Text("----OEM Info----",
                    modifier = Modifier
                        .padding()
                )
                Text("PPID: " + viewModel.ppid.value)
                Text("Serial Number: " + viewModel.serial.value)
                if (viewModel.hasTelephonyFeature.value) {
                    Text("IMEI Number: " + viewModel.imei.value)
                }
                Text("Scanner Status: " + viewModel.scannerStatus.value)
                RoundButton("EMDK") {
                    startActivity(Intent(context, EMDKActivity::class.java))
                }
                RoundButton("DataWedge Basic") {
                    startActivity(Intent(context, DataWedgeBasicActivity::class.java))
                }
                RoundButton("DataWedge Advanced") {
                    startActivity(Intent(context, DataWedgeAdvancedActivity::class.java))
                }
                RoundButton("DataWedge Profile") {
                    startActivity(Intent(context, DataWedgeProfileActivity::class.java))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        }
    }
}