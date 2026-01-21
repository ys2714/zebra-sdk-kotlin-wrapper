package com.zebra.zebrakotlindemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import java.util.Date
import java.util.Timer
import kotlin.concurrent.schedule

/*
please manually create a profile named "control_scan_trigger" with following settings:

1.Barcode Input

1.1 Enabled = true
1.2 Hardware Trigger = true
1.3 Config Scanner Settings
1.3.1 Reader Parameters
1.3.1.1 Aim Type = Timed Continuous (the last one in the list)

2.Keystroke Output

2.1 Enabled = false

3.Intent Output

3.1 Enabled = true
3.2 Intent Action = com.zebra.trigger.ACTION
3.3 Intent Category = com.zebra.trigger.CATEGORY
3.4 Intent Delivery = Broadcast Intent
*/
class DataWedgeTriggerBasicViewModel : ViewModel() {

    companion object {
        val profileName = "control_scan_trigger"
        val scanResultAction = "com.zebra.trigger.ACTION"
        val scanResultCategory = "com.zebra.trigger.CATEGORY"
    }

    var barcodeText: MutableState<String> = mutableStateOf("")
    var sessionStatus = mutableStateOf("")
    var scanDataIntentReceiver: BroadcastReceiver? = null

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
        DataWedgeHelper.switchProfile(context, profileName)
    }

    fun handleOnResume(context: Context) {
        registerDataListener(context)
    }

    fun handleOnPause(context: Context) {
        unregisterDataListener(context)
    }

    fun handleOnDestroy(context: Context) {
        handleOnPause(context)
    }

    fun handleOnScanData(context: Context, intent: Intent?) {
        if (intent == null) {
            return
        }
        if (intent.action != scanResultAction) {
            return
        }
        intent.extras?.let {
            val type = it.getString("com.symbol.datawedge.label_type") ?: ""
            val data = it.getString("com.symbol.datawedge.data_string") ?: ""
            val timestamp: Long = it.getLong("com.symbol.datawedge.data_dispatch_time")
            val date = Date(timestamp)
            barcodeText.value = data
        }
        Timer().schedule(20) {
            stopScanning(context)
        }
    }

    fun stopScanning(context: Context) {
        DataWedgeHelper.softScanTrigger(
            context,
            DWAPI.SoftScanTriggerOptions.STOP_SCANNING)
    }

    // Setup Listeners

    fun registerDataListener(context: Context) {
        val ctx = context.applicationContext
        if (scanDataIntentReceiver != null) {
            return
        }
        scanDataIntentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                handleOnScanData(ctx, intent)
            }
        }
        ContextCompat.registerReceiver(
            ctx,
            scanDataIntentReceiver,
            IntentFilter().apply {
                addAction(scanResultAction)
                addCategory(scanResultCategory)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun unregisterDataListener(context: Context) {
        val ctx = context.applicationContext
        if (scanDataIntentReceiver != null) {
            ctx.unregisterReceiver(scanDataIntentReceiver)
            scanDataIntentReceiver = null
        }
    }
}