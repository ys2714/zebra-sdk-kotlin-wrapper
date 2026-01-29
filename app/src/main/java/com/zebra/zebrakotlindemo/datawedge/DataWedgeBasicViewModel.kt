package com.zebra.zebrakotlindemo.datawedge

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeBasicViewModel : ViewModel() {

    companion object {
        val barcodeProfileName = "barcode_intent"
        val ocrProfileName = "workflow_intent"
    }

    var currentProfileName: MutableState<String> = mutableStateOf("")

    var barcodeText: MutableState<String> = mutableStateOf("")
    var ocrText: MutableState<String> = mutableStateOf("")

    var dataListener: DataWedgeHelper.ScanDataListener? = null
    var statusListener: DataWedgeHelper.SessionStatusListener? = null

    var scannerStatus = mutableStateOf("")
    var sessionStatus = mutableStateOf("")

    fun isWaitingWorkflowSession(): Boolean {
        if (currentProfileName.value == ocrProfileName) {
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
        DataWedgeHelper.switchProfile(context, barcodeProfileName) { success ->
            if (success) {
                currentProfileName.value = barcodeProfileName
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
        }
        if (statusListener != null) {
            return
        }
        statusListener = object : DataWedgeHelper.SessionStatusListener {
            override fun onStatus(
                type: DWAPI.NotificationType,
                status: String,
                profileName: String
            ) {
                sessionStatus.value = "${type.value} $status"
            }

            override fun getID(): String {
                return hashCode().toString()
            }

            override fun onDisposal() {
                statusListener = null
            }
        }.also {
            DataWedgeHelper.addSessionStatusListener(it)
            DataWedgeHelper.enableScannerStatusNotification(context)
            DataWedgeHelper.enableWorkflowStatusNotification(context)
        }
    }

    fun handleOnPause(context: Context) {
        if (dataListener != null) {
            DataWedgeHelper.removeScanDataListener(dataListener!!)
            dataListener = null
        }
        if (statusListener != null) {
            DataWedgeHelper.removeSessionStatusListener(statusListener!!)
            DataWedgeHelper.disableScannerStatusNotification(context)
            DataWedgeHelper.disableWorkflowStatusNotification(context)
            statusListener = null
        }
    }

    fun handleOnDestroy(context: Context) {
        handleOnPause(context)
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
        DataWedgeHelper.switchProfile(context, barcodeProfileName) { success ->
            if (success) {
                currentProfileName.value = barcodeProfileName
                //showDebugToast(context, "Switch Profile ${this.profileName.value}", "Success")
            } else {
                showDebugToast(context, "Switch Profile", "Fail")
            }
        }
    }

    fun switchToOCRProfile(context: Context) {
        DataWedgeHelper.switchProfile(context, ocrProfileName) { success ->
            if (success) {
                currentProfileName.value = ocrProfileName
                //showDebugToast(context, "Switch Profile ${this.profileName.value}", "Success")
            } else {
                showDebugToast(context, "Switch Profile", "Fail")
            }
        }
    }

    fun toggleProfile(context: Context) {
        if (this.currentProfileName.value == ocrProfileName) {
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
}