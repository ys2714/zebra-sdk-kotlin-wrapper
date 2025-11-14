package com.zebra.zebrakotlindemo

import android.content.Context
import android.provider.ContactsContract
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeViewModel : ViewModel() {

    val profileName = "ScanByDW-20"
    var text: MutableState<String> = mutableStateOf("")
    private var isDataWedgePrepared = false

    fun setupDataWedgeIfNeeded(context: Context) {
        if (isDataWedgePrepared) {
            return
        }
        DataWedgeHelper.prepare(context) { enabled ->
            if (enabled) {
                isDataWedgePrepared = true
                DataWedgeHelper.addScanDataListener(object : DataWedgeHelper.ScanDataListener {
                    override fun onData(
                        type: String,
                        value: String,
                        timestamp: String
                    ) {
                        text.value = value
                    }
                })
                DataWedgeHelper.deleteProfile(context, profileName) { success ->
                    if (success) {
                        DataWedgeHelper.configProfileForBarcodeScan(context, profileName) { configSuccess ->

                        }
                    }
                }
            }
        }
    }

    fun teardownDataWedgeIfNeeded(context: Context) {
        DataWedgeHelper.deleteProfile(context, profileName)
        DataWedgeHelper.disableDW(context)
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_LONG).show()
        }
    }
}