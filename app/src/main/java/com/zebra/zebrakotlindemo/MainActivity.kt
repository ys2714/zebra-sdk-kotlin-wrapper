package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.emdk_kotlin_wrapper.utils.ZebraSystemEventMonitor
import com.zebra.zebrakotlindemo.datawedge.DataWedgeActivity
import com.zebra.zebrakotlindemo.emdk.EMDKActivity
import com.zebra.zebrakotlindemo.emdk.ScreenLockActivity
import com.zebra.zebrakotlindemo.quickscan.QuickScanActivity
import com.zebra.zebrakotlindemo.rxlogger.RXLoggerActivity
import com.zebra.zebrakotlindemo.ui.components.RoundButton

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
        viewModel.handleOnCreate(this)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
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

//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        if (keyCode == MXBase.KeyCodes.SCAN.value
//            || keyCode == MXBase.KeyCodes.RIGHT_TRIGGER_1.value) {
//            startActivity(Intent(this, QuickScanActivity::class.java))
//        }
//        if (keyCode == MXBase.KeyCodes.LEFT_TRIGGER_1.value) {
//            startActivity(Intent(this, QuickScanActivity::class.java))
//        }
//        return super.onKeyDown(keyCode, event)
//    }

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
                Text("Serial Number: " + viewModel.serial.value)
                if (viewModel.hasTelephonyFeature.value) {
                    Text("IMEI Number: " + viewModel.imei.value)
                }
                Text("Scanner Status: " + viewModel.scannerStatus.value)
                Text("API Token: " + viewModel.apiToken.value)
                RoundButton("Quick Scan", color = Color(0xFFF5B027)) {
                    startActivity(Intent(context, QuickScanActivity::class.java))
                }
                RoundButton("EMDK MX API") {
                    startActivity(Intent(context, EMDKActivity::class.java))
                }
                RoundButton("DataWedge Intent API") {
                    startActivity(Intent(context, DataWedgeActivity::class.java))
                }
                RoundButton("RXLogger API") {
                    startActivity(Intent(context, RXLoggerActivity::class.java))
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