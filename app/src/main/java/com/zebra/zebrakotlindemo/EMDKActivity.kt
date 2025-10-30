package com.zebra.zebrakotlindemo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.unit.dp
import com.zebra.emdk_kotlin_wrapper.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKActivity: ComponentActivity() {

    var text: MutableState<String> = mutableStateOf("")
    var serial: MutableState<String> = mutableStateOf("")
    var imei: MutableState<String> = mutableStateOf("")

    var emdkHelper: EMDKHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEMDKIfNeeded(this.applicationContext)
        setContent {
           RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        setupEMDKIfNeeded(this.applicationContext)
    }

    override fun onPause() {
        super.onPause()
        teardownEMDKIfNeeded(this.applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        teardownEMDKIfNeeded(this.applicationContext)
    }

    fun setupEMDKIfNeeded(context: Context) {
        if (emdkHelper != null) {
            return
        }
        emdkHelper = EMDKHelper(context)
        val config = EMDKHelper.Config()
        config.enableOCR = false
        emdkHelper?.prepare(config)
    }

    fun teardownEMDKIfNeeded(context: Context) {
        if (emdkHelper == null) {
            return
        }
        emdkHelper?.teardown()
        emdkHelper = null
    }

    fun showDebugToast(type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@EMDKActivity.applicationContext, "$type\n$data", Toast.LENGTH_LONG).show()
        }
    }

    @Composable
    fun RootView() {
        Column {
            StyledOutlinedTextField(text.value) { newValue ->
                text.value = newValue
            }
            RoundButton("Start Scan") {
                emdkHelper?.startRead { type, data, timestamp ->
                    text.value = data
                }
            }
            RoundButton("Stop Scan") {
                emdkHelper?.stopRead()
            }
            Text("OEM Info",
                modifier = Modifier
                    .padding()
            )
            Text(serial.value)
            RoundButton("Fetch Serial Number") {
                emdkHelper?.getProfileProcessor()?.fetchSerialNumberInBackground(this@EMDKActivity, object: MXBase.FetchOEMInfoCallback {
                    override fun onSuccess(result: String) {
                        serial.value = result
                    }

                    override fun onError() {
                        serial.value = "get serial number error"
                    }
                })
            }
            Text(imei.value)
            RoundButton("Fetch IMEI") {
                emdkHelper?.getProfileProcessor()?.fetchIMEIInBackground(this@EMDKActivity, object: MXBase.FetchOEMInfoCallback {
                    override fun onSuccess(result: String) {
                        imei.value = result
                    }

                    override fun onError() {
                        serial.value = "get serial number error"
                    }
                })
            }
        }
    }
}