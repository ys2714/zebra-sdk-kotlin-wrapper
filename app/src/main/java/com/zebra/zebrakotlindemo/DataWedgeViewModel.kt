package com.zebra.zebrakotlindemo

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.utils.FileUtils
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeViewModel : ViewModel() {

    var profileName: MutableState<String> = mutableStateOf("")
    var shouldConfigProfileByAPI: Boolean = false

    var barcodeText: MutableState<String> = mutableStateOf("")
    var ocrText: MutableState<String> = mutableStateOf("")

    var dataListener: DataWedgeHelper.ScanDataListener? = null
    var scannerStatus = mutableStateOf("")

    fun handleOnCreate(context: Context) {
        DataWedgeHelper.switchProfile(context, MainViewModel.barcodeProfileName) { success ->
            if (success) {
                profileName.value = MainViewModel.barcodeProfileName
            } else {
                showDebugToast(context, "Switch Profile", "Fail")
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
                if (profileName.value == MainViewModel.ocrProfileName) {
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
            if (shouldConfigProfileByAPI) {
                DataWedgeHelper.configBarcodePlugin(context, MainViewModel.barcodeProfileName, enable = true, hardTrigger = false)
                DataWedgeHelper.configWorkflowPlugin(context, MainViewModel.barcodeProfileName, enable = false)
            }
            getScannerStatus(context)
        }
    }

    fun handleOnPause(context: Context) {
        if (dataListener != null) {
            DataWedgeHelper.removeScanDataListener(dataListener!!)
            dataListener = null
        }
        if (shouldConfigProfileByAPI) {
            DataWedgeHelper.configBarcodePlugin(context, MainViewModel.barcodeProfileName, enable = false, hardTrigger = false)
        }
    }

    fun handleOnDestroy() {
    }

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

    fun switchToBarcodeProfile(context: Context) {
        if (shouldConfigProfileByAPI) {
            DataWedgeHelper.configWorkflowPlugin(context, MainViewModel.barcodeProfileName, enable = false)
            DataWedgeHelper.configBarcodePlugin(context, MainViewModel.barcodeProfileName, enable = true, hardTrigger = false)
        } else {
            DataWedgeHelper.switchProfile(context, MainViewModel.barcodeProfileName) { success ->
                if (success) {
                    profileName.value = MainViewModel.barcodeProfileName
                    //showDebugToast(context, "Switch Profile ${this.profileName.value}", "Success")
                } else {
                    showDebugToast(context, "Switch Profile", "Fail")
                }
            }
        }
    }

    fun switchToOCRProfile(context: Context) {
        if (shouldConfigProfileByAPI) {
            DataWedgeHelper.configBarcodePlugin(context, MainViewModel.barcodeProfileName, enable = false, hardTrigger = false)
            DataWedgeHelper.configWorkflowPlugin(context, MainViewModel.barcodeProfileName, enable = true)
        } else {
            DataWedgeHelper.switchProfile(context, MainViewModel.ocrProfileName) { success ->
                if (success) {
                    profileName.value = MainViewModel.ocrProfileName
                    //showDebugToast(context, "Switch Profile ${this.profileName.value}", "Success")
                } else {
                    showDebugToast(context, "Switch Profile", "Fail")
                }
            }
        }
    }

    fun toggleProfile(context: Context) {
        if (this.profileName.value == MainViewModel.ocrProfileName) {
            switchToBarcodeProfile(context)
        } else {
            switchToOCRProfile(context)
        }
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportProfile(context: Context, profileName: String) {
        DataWedgeHelper.switchProfile(context, profileName) { success ->
            if (success) {
                DataWedgeHelper.getProfile(context, profileName) { bundle ->
                    val json = JsonUtils.bundleToJson(bundle)
                    FileUtils.saveTextToDownloads(context, profileName, json)
                    // switch back
                    DataWedgeHelper.switchProfile(context, MainViewModel.barcodeProfileName) { success -> }
                }
            }
        }
    }
}