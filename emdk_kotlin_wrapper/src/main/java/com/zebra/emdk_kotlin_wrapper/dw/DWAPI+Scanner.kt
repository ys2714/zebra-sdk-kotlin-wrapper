package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.zebra.emdk_kotlin_wrapper.dw.DWScannerMap.DWScannerInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.collections.forEach

/**
 * https://techdocs.zebra.com/datawedge/15-0/guide/api/enumeratescanners/
 *
 * Wrong:
 * com.symbol.datawedge.api.ACTION_ENUMERATEDSCANNERLIST
 * Right:
 * com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS
 * */
suspend fun DWAPI.sendEnumerateScannersIntent(context: Context): List<DWScannerInfo> = suspendCancellableCoroutine { continuation ->
    DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.ENUMERATE_SCANNERS, "") { result ->
        result.onSuccess { intent ->
            callDWAPIEnumerateScannersHandler(intent) {
                continuation.resumeWith(Result.success(it))
            }
        }.onFailure {
            continuation.resumeWith(Result.failure(it))
        }
    }
}

/**
 * https://techdocs.zebra.com/datawedge/6-8/guide/api/getscannerstatus/
 *
 * - Parameters:
 * ACTION [String]: "com.symbol.datawedge.api.ACTION"
 * EXTRA_DATA [String]: "com.symbol.datawedge.api.GET_SCANNER_STATUS"
 * EXTRA VALUE: Empty string
 *
 * - Result Codes:
 * EXTRA NAME: "com.symbol.datawedge.api.RESULT_SCANNER_STATUS"
 * EXTRA TYPE [String]: [ ] Possible values:
 *
 * WAITING - Scanner is ready to be triggered
 * SCANNING - Scanner is emitting a scanner beam
 * DISABLED - Scanner is disabled
 * CONNECTED - An external (Bluetooth or serial) scanner is connected
 * DISCONNECTED - The external scanner is disconnected
 * Error and debug messages are logged to the Android logging system, which can be viewed and filtered by the logcat command. Use logcat from an ADB shell to view the log messages:
 * $ adb logcat -s DWAPI
 * */
suspend fun DWAPI.sendGetSelectedScannerStatusIntent(context: Context): DWAPI.ScannerStatus = suspendCancellableCoroutine { continuation ->
    DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.GET_SCANNER_STATUS, "") { result ->
        result.onSuccess {
            callDWAPIGetSelectedScannerStatusHandler(it) { status ->
                if (status != null) {
                    continuation.resumeWith(Result.success(status))
                } else {
                    continuation.resumeWith(Result.failure(java.lang.Exception("Scanner status is null")))
                }
            }
        }.onFailure {
            continuation.resumeWith(Result.failure(it))
        }
    }
}

private fun callDWAPIEnumerateScannersHandler(intent: Intent, callback: (List<DWScannerInfo>) -> Unit) {
    if (intent.action == DWAPI.ResultActionNames.RESULT_ACTION.value &&
        intent.hasExtra(DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value)
    )
    {
        val bundles: ArrayList<Bundle>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(
                DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value,
                Bundle::class.java) as ArrayList<Bundle>
        } else {
            intent.getParcelableArrayListExtra<Bundle>(DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value)
        }
        bundles?.forEach { bundle ->
            val name = bundle.getString("SCANNER_NAME")
            val connected = bundle.getBoolean("SCANNER_CONNECTION_STATE")
            val index = bundle.getInt("SCANNER_INDEX")
            val id = bundle.getString("SCANNER_IDENTIFIER")
            if (id != null && name != null) {
                DWScannerMap.setScannerInfo(id, name, index, connected)
                Log.d(DataWedgeHelper.TAG, "ScannerInfo: [name: $name, connected: $connected, index: $index, id: $id]")
            }
        }
        Log.d(DataWedgeHelper.TAG, "LIST SCANNER SUCCESS")
        callback(DWScannerMap.getScannerList())
    } else {
        Log.e(DataWedgeHelper.TAG, "LIST SCANNER FAILED: no dw status in intent")
        callback(emptyList())
    }
}

private fun callDWAPIGetSelectedScannerStatusHandler(intent: Intent, callback: (DWAPI.ScannerStatus?) -> Unit) {
    if (intent.action == DWAPI.ResultActionNames.RESULT_ACTION.value &&
        intent.hasExtra(DWAPI.ResultExtraKeys.SCANNER_STATUS.value)
    )
    {
        val status = intent.getStringExtra(DWAPI.ResultExtraKeys.SCANNER_STATUS.value)
        val statusEnum = DWAPI.ScannerStatus.entries.find { it.value == status }
        if (statusEnum != null) {
            callback(statusEnum)
        } else {
            callback(null)
        }
    } else {
        Log.e(DataWedgeHelper.TAG, "LIST SCANNER FAILED: no dw status in intent")
        callback(null)
    }
}