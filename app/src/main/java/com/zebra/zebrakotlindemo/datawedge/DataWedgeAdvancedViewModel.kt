package com.zebra.zebrakotlindemo.datawedge

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWBarcodeScanner
import com.zebra.zebrakotlindemo.quickscan.DWQuickScanService
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

    var scannerStatus = mutableStateOf("")
    var sessionStatus = mutableStateOf("")

    private var scanner: DWBarcodeScanner? = null

    fun isWaitingWorkflowSession(): Boolean {
        if (currentInputPluginName.value == workflowPluginName) {
            if (sessionStatus.value == "WORKFLOW_STATUS SESSION_STARTED") {
                return false
            }
            else if (sessionStatus.value == "WORKFLOW_STATUS DISABLED") {
                return false
            }
            else {
                return true
            }
        } else {
            return false
        }
    }

    fun handleOnCreate(context: Context) {
        scanner = DWQuickScanService.shared?.selectScanner(DWBarcodeScanner::class.simpleName!!) as DWBarcodeScanner
    }

    fun handleOnResume(context: Context) {
        scanner?.select { instance ->
            instance.startListen { data ->
                if (currentInputPluginName.value == workflowPluginName) {
                    ocrText.value = data
                } else {
                    barcodeText.value = data
                }
            }
            instance.startListenStatus { type, status ->
                sessionStatus.value = "${type.value} $status"
            }
            instance.resume()
            getScannerStatus(context)
        }
    }

    fun handleOnPause(context: Context) {
        scanner?.stopListen()
        scanner?.stopListenStatus()
        scanner?.suspend()
    }

    fun handleOnDestroy(context: Context) {
        handleOnPause(context)
    }

    fun startScanning(context: Context) {
        scanner?.startScan()
    }

    fun stopScanning(context: Context) {
        scanner?.stopScan()
    }

    fun getScannerStatus(context: Context) {
        DataWedgeHelper.getScannerStatus(context, 1) { status ->
            scannerStatus.value = status.string
            // showDebugToast(context, "Scanner Status", status.string)
        }
    }

    fun switchToBarcodePlugin(context: Context) {
        scanner?.update(
            mapOf(
                "PROFILE_NAME" to scanner!!.profileName!!,
                "scanner_input_enabled" to "true",
                "workflow_input_enabled" to "false",
                "barcode_trigger_mode" to "1",
                "aim_type" to "8",
                "aim_timer" to "6000",
                "beam_timer" to "6000"
            )
        ) { instance ->
            instance.select {
                currentInputPluginName.value = barcodePluginName
            }
        }
    }

    fun switchToOCRPlugin(context: Context) {
        scanner?.update(
            mapOf(
                "PROFILE_NAME" to scanner!!.profileName!!,
                "scanner_input_enabled" to "false",
                "workflow_input_enabled" to "true",
                "barcode_trigger_mode" to "1",
                "aim_type" to "8",
                "aim_timer" to "6000",
                "beam_timer" to "6000"
            )
        ) { instance ->
            instance.select {
                currentInputPluginName.value = workflowPluginName
            }
        }
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }
}