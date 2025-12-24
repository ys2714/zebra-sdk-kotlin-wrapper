package com.zebra.zebrakotlindemo

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeAdvancedViewModel : ViewModel() {

    companion object {
        val barcodeProfileName = "barcode_intent_advanced"
        val ocrProfileName = "workflow_intent_advanced"
    }

    var currentProfileName: MutableState<String> = mutableStateOf("")

    var barcodeText: MutableState<String> = mutableStateOf("")
    var ocrText: MutableState<String> = mutableStateOf("")

    var dataListener: DataWedgeHelper.ScanDataListener? = null
    var scannerStatus = mutableStateOf("")

    fun handleOnCreate(context: Context) {
//        createProfileWithHelper(context) {
//            switchToBarcodePlugin(context)
//        }

        DataWedgeHelper.configWithJSON(context, "profile_barcode_input_intent_output.json") {}
    }

    fun handleOnResume(context: Context) {
        if (dataListener != null) {
            return
        }
        dataListener = object : DataWedgeHelper.ScanDataListener {
            override fun onData(
                type: String,
                value: String,
                timestamp: String
            ) {
                if (currentProfileName.value == ocrProfileName) {
                    ocrText.value = value
                } else {
                    barcodeText.value = value
                }
            }

            override fun getID(): String {
                return hashCode().toString()
            }

            override fun onDisposal() {
                dataListener = null
            }
        }.also {
            DataWedgeHelper.addScanDataListener(it)
            getScannerStatus(context)
            //enablePlugins(context)
        }
    }

    fun handleOnPause(context: Context) {
        if (dataListener != null) {
            DataWedgeHelper.removeScanDataListener(dataListener!!)
            dataListener = null
        }
        //disablePlugins(context)
    }

    fun handleOnDestroy() {}

    fun startScanning(context: Context) {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.START_SCANNING)
    }

    fun stopScanning(context: Context) {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.STOP_SCANNING)
    }

    fun getScannerStatus(context: Context) {
        DataWedgeHelper.getScannerStatus(context, 1) { status ->
            scannerStatus.value = status.string
            // showDebugToast(context, "Scanner Status", status.string)
        }
    }

    fun switchToBarcodePlugin(context: Context) {
        //DataWedgeHelper.configWorkflowPlugin(context, barcodeProfileName, enable = false)
        //DataWedgeHelper.configBarcodePlugin(context, barcodeProfileName, enable = true, hardTrigger = false)
    }

    fun switchToOCRPlugin(context: Context) {
        DataWedgeHelper.configWithJSON(context, "profile_workflow_input_intent_output.json") {
            success ->
            showDebugToast(context, "", "switch to OCR $success")
        }

        //DataWedgeHelper.configBarcodePlugin(context, barcodeProfileName, enable = false, hardTrigger = false)
        //DataWedgeHelper.configWorkflowPlugin(context, barcodeProfileName, enable = true)
    }

    fun disablePlugins(context: Context) {
        DataWedgeHelper.configBarcodePlugin(context, barcodeProfileName, enable = false, hardTrigger = false)
        DataWedgeHelper.configWorkflowPlugin(context, barcodeProfileName, enable = false)
    }

    fun enablePlugins(context: Context) {
        DataWedgeHelper.configBarcodePlugin(context, barcodeProfileName, enable = true, hardTrigger = false)
        DataWedgeHelper.configWorkflowPlugin(context, barcodeProfileName, enable = true)
    }

    fun createProfileWithHelper(context: Context, completion: () -> Unit) {
        DataWedgeHelper.deleteProfile(context, barcodeProfileName) { success ->
            if (!success) {
                throw RuntimeException("MainViewModel - delete profile failed: $barcodeProfileName")
            }
            DataWedgeHelper.createProfile(context, barcodeProfileName) { createSuccess ->
                if (!createSuccess) {
                    throw RuntimeException("MainViewModel - create profile failed: $barcodeProfileName")
                }
                DataWedgeHelper.switchProfile(context, barcodeProfileName) { switchSuccess ->
                    if (!switchSuccess) {
                        throw RuntimeException("MainViewModel - switch profile failed: $barcodeProfileName")
                    }
                    getScannerStatus(context)
                    DataWedgeHelper.configBarcodePlugin(context, barcodeProfileName, enable = false, hardTrigger = false)
                    DataWedgeHelper.configWorkflowPlugin(context, barcodeProfileName, false)
                    DataWedgeHelper.configKeystrokePlugin(context, barcodeProfileName, false)
                    DataWedgeHelper.configIntentPlugin(context, barcodeProfileName)
                    Log.d("DataWedge", "Profile configured successfully")
                    completion()
                }
            }
        }
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }
}