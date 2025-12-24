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
        val barcodePluginName = "BARCODE"
        val workflowPluginName = "WORKFLOW"
    }

    var currentInputPluginName: MutableState<String> = mutableStateOf("")

    var barcodeText: MutableState<String> = mutableStateOf("")
    var ocrText: MutableState<String> = mutableStateOf("")

    var dataListener: DataWedgeHelper.ScanDataListener? = null
    var scannerStatus = mutableStateOf("")

    fun handleOnCreate(context: Context) {
        DataWedgeHelper.configWithJSON(
            context,
            "barcode_intent_advanced_create.json",
            mapOf(
                "scanner_input_enabled" to "true",
                "workflow_input_enabled" to "false"
            )
        ) { success ->
            if (success) {
                DataWedgeHelper.switchProfile(context, "barcode_intent_advanced")
                currentInputPluginName.value = barcodePluginName
            }
        }
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
                if (currentInputPluginName.value == workflowPluginName) {
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
        DataWedgeHelper.configWithJSON(
            context,
            "barcode_intent_advanced_update.json",
            mapOf(
                "scanner_input_enabled" to "true",
                "workflow_input_enabled" to "false"
            )
        ) { success ->
            if (success) {
                DataWedgeHelper.switchProfile(context, "barcode_intent_advanced") {
                    currentInputPluginName.value = barcodePluginName
                }
            }
            //showDebugToast(context, "", "switch to Barcode $success")
        }
    }

    fun switchToOCRPlugin(context: Context) {
        DataWedgeHelper.configWithJSON(
            context,
            "barcode_intent_advanced_update.json",
            mapOf(
                "scanner_input_enabled" to "false",
                "workflow_input_enabled" to "true"
            )
        ) { success ->
            if (success) {
                DataWedgeHelper.switchProfile(context, "barcode_intent_advanced") {
                    currentInputPluginName.value = workflowPluginName
                }
            }
            // showDebugToast(context, "", "switch to OCR $success")
        }
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }
}