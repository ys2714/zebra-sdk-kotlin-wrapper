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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.zebra.emdk_kotlin_wrapper.DWAPI
import com.zebra.emdk_kotlin_wrapper.DataWedgeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeActivity : ComponentActivity() {

    val profileName = "ScanByDW"
    var dataWedgeHelper: DataWedgeHelper? = null

    var text: MutableState<String> = mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDataWedgeIfNeeded(this.applicationContext)
        setContent {
            RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        setupDataWedgeIfNeeded(this.applicationContext)
    }

    override fun onPause() {
        super.onPause()
        teardownDataWedgeIfNeeded(this.applicationContext)
    }

    override fun onDestroy() {
        super.onDestroy()
        teardownDataWedgeIfNeeded(this.applicationContext)
    }

    fun setupDataWedgeIfNeeded(context: Context) {
        if (dataWedgeHelper != null) {
            return
        }
        DWAPI.enableDW(context)
        dataWedgeHelper = DataWedgeHelper(context)
        dataWedgeHelper?.createProfile(profileName)
        dataWedgeHelper?.configProfileForBarcodeScan(profileName,  enableOCR = true, useCamera = false) { type, value, timestamp ->
            text.value = value
        }
    }

    fun teardownDataWedgeIfNeeded(context: Context) {
        if (dataWedgeHelper == null) {
            return
        }
        // dataWedgeHelper?.setDCPButton(profileName, false)
        dataWedgeHelper?.deleteProfile(profileName)
        dataWedgeHelper = null
        DWAPI.disableDW(applicationContext)
    }

    fun showDebugToast(type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@DataWedgeActivity.applicationContext, "$type\n$data", Toast.LENGTH_LONG).show()
        }
    }

    @Composable
    fun RootView() {
        val newText = remember { text }
        Column {
            StyledOutlinedTextField(newText.value) { newValue ->
               newText.value = newValue
            }
            RoundButton("Push Scan Button or Tap this") {
                dataWedgeHelper?.softScanTriggerStart()
            }
        }
    }
}