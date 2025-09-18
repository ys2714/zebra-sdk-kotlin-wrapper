package com.zebra.zebrakotlindemo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.zebra.emdk_kotlin_wrapper.EMDKHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKActivity: ComponentActivity() {

    var text: MutableState<String> = mutableStateOf("")
    var scannerHelper: EMDKHelper? = null

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
        if (scannerHelper != null) {
            return
        }
        scannerHelper = EMDKHelper(context)
        val config = EMDKHelper.Config()
        config.enableOCR = false
        scannerHelper?.prepare(config)
    }

    fun teardownEMDKIfNeeded(context: Context) {
        if (scannerHelper == null) {
            return
        }
        scannerHelper?.teardown()
        scannerHelper = null
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
                scannerHelper?.startRead { type, data, timestamp ->
                    text.value = data
                }
            }
            RoundButton("Stop Scan") {
                scannerHelper?.stopRead()
            }
        }
    }
}