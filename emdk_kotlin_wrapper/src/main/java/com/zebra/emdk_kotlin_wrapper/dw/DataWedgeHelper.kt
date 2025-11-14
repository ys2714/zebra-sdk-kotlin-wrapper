package com.zebra.emdk_kotlin_wrapper.dw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Date

object DataWedgeHelper {

    final val TAG = "DataWedgeHelper"

    interface ScanDataListener {
        fun onData(type: String, value: String, timestamp: String)
    }

    private val listeners = mutableListOf<ScanDataListener>()

    private val backgroundScope = CoroutineScope(Dispatchers.IO + Job())
    private val foregroundScope = CoroutineScope(Dispatchers.Main + Job())

    private var dataReceiver: DataReceiver? = null

    private fun notifyListeners(type: String, value: String, timestamp: String) {
        listeners.forEach {
            it.onData(type, value, timestamp)
        }
    }

    fun addScanDataListener(listener: ScanDataListener) {
        listeners.add(listener)
    }

    fun prepare(context: Context, callback: (Boolean) -> Unit) {
        registerReceiverIfNeeded(context)
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.enableDW(context, true)
            }.onSuccess { enabled ->
                val scannerList = DWIntentFactory.sendEnumerateScannersIntent(context)
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
                DWIntentFactory.sendGetDWStatusIntent(context)
            }.onSuccess { enabled ->
                foregroundScope.launch {
                    callback(enabled)
                }
            }.onFailure {
                Log.e(TAG, "DW NOT ENABLED. Exception: ${it.message}")
                foregroundScope.launch {
                    callback(false)
                }
            }
        }
    }

    fun enableDW(context: Context, callback: ((success: Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.enableDW(context, true)
            }.onSuccess {
                 foregroundScope.launch {
                     callback?.invoke(true)
                 }
            }.onFailure {
                if (it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_ENABLED.value) {
                    Log.d(TAG, "DATAWEDGE ALREADY ENABLED")
                    foregroundScope.launch {
                        callback?.invoke(true)
                    }
                } else {
                    Log.e(TAG, "ENABLE DW FAIL. Exception: ${it.message}")
                    foregroundScope.launch {
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun disableDW(context: Context, callback: ((success: Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.enableDW(context, false)
            }.onSuccess {
                foregroundScope.launch {
                    callback?.invoke(true)
                }
            }.onFailure {
                if (it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_DISABLED.value) {
                    Log.d(TAG, "DATAWEDGE ALREADY DISABLED")
                    foregroundScope.launch {
                        callback?.invoke(true)
                    }
                } else {
                    Log.e(TAG, "DISABLE DW FAIL. Exception: ${it.message}")
                    foregroundScope.launch {
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun createProfile(context: Context, name: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.sendCreateProfileIntent(context, name)
            }.onSuccess { success ->
                foregroundScope.launch {
                    callback?.invoke(success)
                }
            }.onFailure {
                if (it.message == DWAPI.ResultCodes.PROFILE_NOT_FOUND.value) {
                    Log.d(TAG, "PROFILE ALREADY DELETED")
                    foregroundScope.launch {
                        callback?.invoke(true)
                    }
                }
                if (it.message == DWAPI.ResultCodes.PROFILE_ALREADY_EXISTS.value) {
                    Log.d(TAG, "PROFILE ALREADY EXISTS")
                    foregroundScope.launch {
                        callback?.invoke(true)
                    }
                } else {
                    Log.e(TAG, "CREATE PROFILE FAIL. Exception: ${it.message}")
                    foregroundScope.launch {
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun deleteProfile(context: Context, name: String, callback: ((Boolean) -> Unit)? = null) {
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.sendDeleteProfileIntent(context, name)
            }.onSuccess { success ->
                foregroundScope.launch {
                    callback?.invoke(success)
                }
            }.onFailure {
                if (it.message == DWAPI.ResultCodes.PROFILE_NOT_FOUND.value) {
                    foregroundScope.launch {
                        callback?.invoke(true)
                    }
                } else {
                    Log.e(TAG, "DELETE PROFILE FAIL. Exception: ${it.message}")
                    foregroundScope.launch {
                        callback?.invoke(false)
                    }
                }
            }
        }
    }

    fun configProfileForBarcodeScan(context: Context, name: String, callback: (success: Boolean) -> Unit) {
        val scannerBundle = DWProfileProcessor.bundleForScannerPlugin(
            context,
            name,
            DWAPI.ScanInputModeOptions.SINGLE
        )

        val intentBundle = DWProfileProcessor.bundleForIntentPlugin(
            context,
            name,
            DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value,
            DWAPI.IntentDeliveryOptions.BROADCAST,
        )

        val bdfBundle = DWProfileProcessor.bundleForBDFPlugin(
            context,
            name,
            DWAPI.Plugin.Output.INTENT,
            true
        )

        val mainBundle = DWProfileProcessor.bundleForUpdateProfile(
            context,
            name
        )

        backgroundScope.launch {
            runCatching {
                val mainSuccess: Deferred<Boolean> = async {
                    DWIntentFactory.sendSetConfigIntent(context, mainBundle)
                }
                val mainSuccessFlag = mainSuccess.await()

                val scannerSuccess: Deferred<Boolean> = async {
                    DWIntentFactory.sendSetConfigIntent(context, scannerBundle)
                }
                val scannerSuccessFlag = scannerSuccess.await()

                val intentSuccess: Deferred<Boolean> = async {
                    DWIntentFactory.sendSetConfigIntent(context, intentBundle)
                }
                val intentSuccessFlag = intentSuccess.await()

                return@runCatching mainSuccessFlag && scannerSuccessFlag && intentSuccessFlag
            }.onSuccess {
                Log.d(TAG, "SET CONFIG SUCCESS")
                callback(true)
            }.onFailure {
                Log.e(TAG, "SET CONFIG FAIL. Exception: ${it.message}")
                callback(false)
                throw it
            }
        }
    }

    fun softScanTrigger(context: Context, option: DWAPI.SoftScanTriggerOptions) {
        DWIntentFactory.softScanTrigger(context, option)
    }

    fun getScannerInfo(context: Context, id: String): DWScannerMap.DWScannerInfo? {
        return DWScannerMap.getScannerInfo(id)
    }

    fun getScannerList(context: Context, callback: (List<DWScannerMap.DWScannerInfo>) -> Unit) {
        backgroundScope.launch {
            runCatching {
                DWIntentFactory.sendEnumerateScannersIntent(context)
            }.onSuccess {
                callback(it)
            }.onFailure {
                Log.e(TAG, "GET SCANNER LIST FAIL. Exception: ${it.message}")
                callback(emptyList())
            }
        }
    }

    private fun registerReceiverIfNeeded(context: Context) {
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