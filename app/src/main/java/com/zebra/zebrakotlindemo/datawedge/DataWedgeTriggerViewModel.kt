package com.zebra.zebrakotlindemo.datawedge

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class DataWedgeTriggerViewModel : ViewModel() {

    companion object {
        val barcodePluginName = "BARCODE"
        val workflowPluginName = "WORKFLOW"
    }

    var barcodeText: MutableState<String> = mutableStateOf("")
    var ocrText: MutableState<String> = mutableStateOf("")

    var dataListener: DataWedgeHelper.ScanDataListener? = null
    var statusListener: DataWedgeHelper.SessionStatusListener? = null

    var sessionStatus = mutableStateOf("")

    private var countDownTimer: CountDownTimer? = null
    private val countDownSeconds: Int = 6

    private var scanButtonDown: Boolean = false
    var scanActivatingRemainSeconds: MutableState<Int> = mutableStateOf(countDownSeconds - 1)

    /**
     * aim_type:
     *
     * 0 - Trigger
     * 1 - Timed Hold
     * 2 - Timed Release
     * 3 - Press And Release
     * 4 - Presentation
     * 5 - Continuous Read
     * 6 - Press and Sustain
     * 7 – Press and Continue
     * 8 - Timed Continuous
     * */
    fun handleOnCreate(context: Context) {
        DataWedgeHelper.configWithJSON(
            context,
            "barcode_intent_advanced_create.json",
            mapOf(
                "PROFILE_NAME" to "barcode_intent_advanced",
                "scanner_input_enabled" to "true",
                "workflow_input_enabled" to "false",
                "barcode_trigger_mode" to "1",
                "aim_type" to "8",
                "aim_timer" to "6000",
                "beam_timer" to "6000"
            )
        ) { success ->
            if (success) {
                // disable hardware scan trigger
                DataWedgeHelper.configWithJSON(
                    context,
                    "barcode_intent_advanced_update.json",
                    mapOf(
                        "scanner_input_enabled" to "true",
                        "workflow_input_enabled" to "false",
                        "barcode_trigger_mode" to "1",
                        "aim_type" to "8",
                        "aim_timer" to "6000",
                        "beam_timer" to "6000"
                    )
                ) { success ->
                    if (success) {
                        DataWedgeHelper.switchProfile(context, "barcode_intent_advanced")
                    }
                }
            }
        }
    }

    fun handleOnResume(context: Context) {
        registerDataListener(context)
        registerStatusListener(context)
    }

    fun handleOnPause(context: Context) {
        unregisterDataListener()
        unregisterStatusListener(context)
    }

    fun handleOnDestroy(context: Context) {
        handleOnPause(context)
    }

    fun handleOnKeyDown(context: Context, keyCode: Int, event: KeyEvent) {
        if (keyCode == MXBase.KeyCodes.RIGHT_TRIGGER_1.value) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (!scanButtonDown) {
                    startScanning(context)
                    scanButtonDown = true
                    Log.d("DataWedgeTriggerViewModel", "Scan Trigger Down")
                }
            }
        }
    }

    fun handleOnKeyUp(context: Context, keyCode: Int, event: KeyEvent) {
        if (keyCode == MXBase.KeyCodes.RIGHT_TRIGGER_1.value) {
            if (event.action == KeyEvent.ACTION_UP) {
                scanButtonDown = false
                Log.d("DataWedgeTriggerViewModel", "Scan Trigger Up")
            }
        }
    }

    fun startScanning(context: Context) {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.START_SCANNING)
        startCountdownTimer(context)
    }

    fun stopScanning(context: Context) {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.STOP_SCANNING)
        stopCountdownTimer()
    }

    fun stopScanningWithDelay(context: Context, delayMilliSeconds: Long) {
        stopCountdownTimer()
        Timer().schedule(delayMilliSeconds) {
            DataWedgeHelper.softScanTrigger(
                context,
                DWAPI.SoftScanTriggerOptions.STOP_SCANNING)
        }
    }

    // Setup Listeners

    fun registerDataListener(context: Context) {
        if (dataListener != null) {
            return
        }
        dataListener = object : DataWedgeHelper.ScanDataListener {
            override fun onData(
                type: String,
                value: String,
                timestamp: String
            ) {
                barcodeText.value = value
                stopScanningWithDelay(context, 20)
            }

            override fun getID(): String {
                return hashCode().toString()
            }

            override fun onDisposal() {
                dataListener = null
            }
        }.also {
            DataWedgeHelper.addScanDataListener(it)
        }
    }

    fun unregisterDataListener() {
        if (dataListener != null) {
            DataWedgeHelper.removeScanDataListener(dataListener!!)
            dataListener = null
        }
    }

    fun registerStatusListener(context: Context) {
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

    fun unregisterStatusListener(context: Context) {
        if (statusListener != null) {
            DataWedgeHelper.removeSessionStatusListener(statusListener!!)
            DataWedgeHelper.disableScannerStatusNotification(context)
            DataWedgeHelper.disableWorkflowStatusNotification(context)
            statusListener = null
        }
    }

    // Timers

    fun startCountdownTimer(context: Context) {
        stopCountdownTimer()
        countDownTimer = object : CountDownTimer((countDownSeconds) * 1000L, 1L * 1000L) {
            override fun onFinish() {
                scanActivatingRemainSeconds.value = countDownSeconds - 1
                // if the scan triggered by hardware button,
                // the scan session will end automatically when timeout.
                // but,
                // if the scan triggered by software button on UI,
                // it will never stop until we call this on timeout.
                stopScanningWithDelay(context, 200)
            }

            override fun onTick(millisUntilFinished: Long) {
                scanActivatingRemainSeconds.value = (millisUntilFinished / 1000L).toInt()
            }
        }
        countDownTimer?.start()
    }

    fun stopCountdownTimer() {
        scanActivatingRemainSeconds.value = countDownSeconds - 1
        countDownTimer?.cancel()
        countDownTimer = null
    }

    // Utilities

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }
}