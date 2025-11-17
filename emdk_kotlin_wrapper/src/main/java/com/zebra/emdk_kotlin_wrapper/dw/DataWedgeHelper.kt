package com.zebra.emdk_kotlin_wrapper.dw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Date

object DataWedgeHelper {

    final val TAG = "DataWedgeHelper"

    interface ScanDataListener {
        fun onData(type: String, value: String, timestamp: String)
        fun onDestroy()
    }

    private var dataReceiver: DataReceiver? = null

    private val listeners: ArrayList<ScanDataListener> = arrayListOf()
    private const val LISTENER_LIMIT = 10

    private val backgroundScope = CoroutineScope(Dispatchers.IO + Job())
    private val foregroundScope = CoroutineScope(Dispatchers.Main + Job())

    private fun notifyListeners(type: String, value: String, timestamp: String) {
        listeners.forEach {
            it.onData(type, value, timestamp)
        }
    }

    fun addScanDataListener(listener: ScanDataListener) {
        if (listeners.size < LISTENER_LIMIT) {
            listeners.add(listener)
        } else {
            val first = listeners.removeAt(0)
            first.onDestroy()
            listeners.add(listener)
        }
    }

    fun removeScanDataListener(listener: ScanDataListener) {
        listeners.remove(listener)
    }

    fun prepare(context: Context, callback: (Boolean) -> Unit) {
        registerReceiverIfNeeded(context)
        backgroundScope.launch {
            runCatching {
                DWAPI.enableDW(context, true)
            }.onSuccess { enabled ->
                val scannerList = DWAPI.sendEnumerateScannersIntent(context)
                if (scannerList.isEmpty()) {
                    Log.e(TAG, "Scanner List empty. device should at least have one internal camera")
                    foregroundScope.launch {
                        callback(false)
                    }
                } else {
                    foregroundScope.launch {
                        callback(enabled)
                    }
                }
            }.onFailure {
                Log.e(TAG, "DW NOT ENABLED. Exception: ${it.message}")
                foregroundScope.launch {
                    callback(false)
                }
            }
        }
    }

    fun checkDWStatus(context: Context, callback: (enabled: Boolean) -> Unit) {
        backgroundScope.launch {
            runCatching {
                DWAPI.sendGetDWStatusIntent(context)
            }.onSuccess { enabled ->
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback(enabled)
                }
            }.onFailure {
                Log.e(TAG, "DW NOT ENABLED. Exception: ${it.message}")
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback(false)
                }
            }
        }
    }

    fun enableDW(context: Context, callback: ((success: Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWAPI.enableDW(context, true)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                     callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    if (it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_ENABLED.value) {
                        Log.d(TAG, "DATAWEDGE ALREADY ENABLED")
                        callback?.invoke(true)
                    } else {
                        Log.e(TAG, "ENABLE DW FAIL. Exception: ${it.message}")
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun disableDW(context: Context, callback: ((success: Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWAPI.enableDW(context, false)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    if (it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_DISABLED.value) {
                        Log.d(TAG, "DATAWEDGE ALREADY DISABLED")
                        callback?.invoke(true)
                    } else {
                        Log.e(TAG, "DISABLE DW FAIL. Exception: ${it.message}")
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun createProfile(context: Context, name: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWAPI.sendCreateProfileIntent(context, name)
            }.onSuccess { success ->
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(success)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    if (it.message == DWAPI.ResultCodes.PROFILE_NOT_FOUND.value) {
                        Log.d(TAG, "PROFILE ALREADY DELETED")
                        callback?.invoke(true)
                    }
                    if (it.message == DWAPI.ResultCodes.PROFILE_ALREADY_EXISTS.value) {
                        Log.d(TAG, "PROFILE ALREADY EXISTS")
                        callback?.invoke(true)
                    } else {
                        Log.e(TAG, "CREATE PROFILE FAIL. Exception: ${it.message}")
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun deleteProfile(context: Context, name: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWAPI.sendDeleteProfileIntent(context, name)
            }.onSuccess { success ->
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(success)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    if (it.message == DWAPI.ResultCodes.PROFILE_NOT_FOUND.value) {
                        callback?.invoke(true)
                    } else {
                        Log.e(TAG, "DELETE PROFILE FAIL. Exception: ${it.message}")
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun bindProfileToApp(context: Context, name: String, packageName: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                val bundle = DWProfileProcessor.bundleForBindProfile(context, name, packageName)
                DWAPI.sendSetConfigIntent(context, bundle)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(false)
                }
            }
        }
    }

    fun configBarcodePlugin(context: Context, name: String, enable: Boolean, hardTrigger: Boolean,callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                val bundle = DWProfileProcessor.bundleForBarcodePlugin(
                    context,
                    name,
                    enable,
                    hardTrigger,
                    DWAPI.ScanInputModeOptions.SINGLE)
                DWAPI.sendSetConfigIntent(context, bundle)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(false)
                }
            }
        }
    }

    fun configKeystrokePlugin(context: Context, name: String, enable: Boolean, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                val bundle = DWProfileProcessor.bundleForKeystrokePlugin(context, name, enable)
                DWAPI.sendSetConfigIntent(context, bundle)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(false)
                }
            }
        }
    }

    fun configIntentPlugin(context: Context, name: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                val bundle = DWProfileProcessor.bundleForIntentPlugin(
                    context,
                    name,
                    DWAPI.ResultActionNames.SCAN_RESULT_ACTION,
                    DWAPI.ResultCategoryNames.CATEGORY_DEFAULT,
                    DWAPI.IntentDeliveryOptions.BROADCAST
                )
                DWAPI.sendSetConfigIntent(context, bundle)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback?.invoke(false)
                }
            }
        }
    }

    fun softScanTrigger(context: Context, option: DWAPI.SoftScanTriggerOptions) {
        DWAPI.softScanTrigger(context, option)
    }

    fun getScannerInfo(context: Context, id: String): DWScannerMap.DWScannerInfo? {
        return DWScannerMap.getScannerInfo(id)
    }

    fun getScannerStatus(context: Context, delaySeconds: Int, callback: (DWAPI.ScannerStatus) -> Unit) {
        backgroundScope.launch {
            runCatching {
                delay(delaySeconds * 1000L)
                DWAPI.sendGetSelectedScannerStatusIntent(context)
            }.onSuccess {
                foregroundScope.launch {
                    callback(it)
                }
            }.onFailure {
                Log.e(TAG, "GET SCANNER STATUS FAIL. Exception: ${it.message}")
                foregroundScope.launch {
                    callback(DWAPI.ScannerStatus.UNKNOWN)
                }
            }
        }
    }

    fun getScannerList(context: Context, callback: (List<DWScannerMap.DWScannerInfo>) -> Unit) {
        backgroundScope.launch {
            runCatching {
                DWAPI.sendEnumerateScannersIntent(context)
            }.onSuccess {
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback(it)
                }
            }.onFailure {
                Log.e(TAG, "GET SCANNER LIST FAIL. Exception: ${it.message}")
                delay(DWAPI.MILLISECONDS_DELAY_BETWEEN_API_CALLS)
                foregroundScope.launch {
                    callback(emptyList())
                }
            }
        }
    }

    fun registerReceiverIfNeeded(context: Context) {
        if (dataReceiver != null) {
            Log.d(TAG, "DataReceiver already registered. skip")
            return
        }
        dataReceiver = DataReceiver()
        ContextCompat.registerReceiver(
            context.applicationContext,
            dataReceiver,
            IntentFilter().apply {
                addCategory(DWAPI.ResultCategoryNames.CATEGORY_DEFAULT.value)
                addAction(DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    class DataReceiver: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null) {
                Toast.makeText(context, "intent is null", Toast.LENGTH_LONG).show()
                return
            }
            if (intent.action == DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value) {
                handleDWScanOutputAction(intent)
            } else {
                Toast.makeText(context, "Unknown action", Toast.LENGTH_LONG).show()
            }
        }

        fun handleDWScanOutputAction(intent: Intent) {
            if (intent.action != DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value) {
                return
            }
            intent.extras?.let {
                val type = it.getString(DWAPI.ScanResult.TYPE) ?: ""
                val data = it.getString(DWAPI.ScanResult.DATA) ?: ""
                val timestamp: Long = it.getLong(DWAPI.ScanResult.TIME)
                val date = Date(timestamp)
                notifyListeners(type, data, date.toString())
            }
        }
    }
}