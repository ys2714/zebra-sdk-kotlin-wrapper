package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import com.zebra.emdk_kotlin_wrapper.utils.ZebraKeyEventMonitor
import com.zebra.emdk_kotlin_wrapper.utils.ZebraSystemEventMonitor
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMAuthHelper
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel {

    companion object {
        val profileName = "ZebraKotlinDemo4"
    }

    var appAuthenticated = mutableStateOf(false)
    var emdkPrepared = mutableStateOf(false)
    var scannerStatus = mutableStateOf("")

    var emdkVersion: MutableState<String> = mutableStateOf("")
    var mxVersion: MutableState<String> = mutableStateOf("")
    var dwVersion: MutableState<String> = mutableStateOf("")

    var ppid: MutableState<String> = mutableStateOf("")
    var serial: MutableState<String> = mutableStateOf("")
    var imei: MutableState<String> = mutableStateOf("")

    /**
     *
     * com.zebra.emdk_kotlin_wrapper.SCAN_RESULT_ACTION
     * android.intent.category.DEFAULT
     * com.zebra.zebrakotlindemo
     *
     * */
    fun prepare(context: Context) {
        if (emdkPrepared.value == true) {
            return
        }
        EMDKHelper.shared.prepare(context) {
            // get versions
            emdkVersion.value = EMDKHelper.shared.emdkVersion
            mxVersion.value = EMDKHelper.shared.mxVersion
            dwVersion.value = EMDKHelper.shared.dwVersion
            // white list app
            authenticateApp(context) { whiteListSuccess ->
                if (whiteListSuccess) {
                    fetchSerialNumber(context) {
                        fetchPPID(context) {
                            fetchIMEI(context) {}
                        }
                    }
                } else {

                }
            }
            // prepare dw
            DataWedgeHelper.prepare(context) { enableSuccess ->
                if (enableSuccess) {
                    DataWedgeHelper.deleteProfile(context, profileName) { success ->
                        if (success) {
                            DataWedgeHelper.createProfile(context, profileName) { createSuccess ->
                                if (createSuccess) {
                                    // DataWedgeHelper.bindProfileToApp(context, profileName, context.packageName) { configSuccess ->
                                        // if (configSuccess) {
                                            getScannerStatus(context)

//                                            DataWedgeHelper.configBarcodePlugin(context, profileName, enable = false, hardTrigger = false)
//                                            DataWedgeHelper.configKeystrokePlugin(context, profileName, false)
//                                            DataWedgeHelper.configIntentPlugin(context, profileName)

//                                            MXHelper.setScreenLockType(context, MXBase.ScreenLockType.NONE) { success ->
//                                                // will show customized lock screen
//                                            }

//                                            ZebraKeyEventMonitor.resetAllKeyDownToDefault(context, delaySeconds = 1) {
//                                                ZebraKeyEventMonitor.registerKeyDownListener(context, MXBase.KeyIdentifiers.LEFT_TRIGGER_2, delaySeconds = 1) {
//                                                    showDebugToast(context, "Push To Talk", "press the PTT key to talk")
//                                                }
//                                                emdkPrepared.value = true
//                                            }

//                                            MXHelper.copyAndImportFreeFormOCRProfile(context, delaySeconds = 3) { success ->
//                                                showDebugToast(context, "Profile", "Free Form OCR Profile configured successfully? $success")
//                                            }

                                            Log.d("DataWedge", "Profile configured successfully")
                                        //} else {
                                        //    Log.e("DataWedge", "Failed to configure profile")
                                        //}
                                    //}
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun getScannerStatus(context: Context) {
        DataWedgeHelper.getScannerStatus(context, 1) { status ->
            scannerStatus.value = status.string
            // showDebugToast(context, "Scanner Status", status.string)
        }
    }

    fun fetchPPID(context: Context, completion: () -> Unit) {
        MXHelper.fetchPPID(context, true) { result ->
            if (!result.isEmpty()) {
                ppid.value = result
            } else {
                ppid.value = "get ppid error"
                showDebugToast(context, "PPID", "get ppid error")
            }
            completion()
        }
    }

    fun fetchSerialNumber(context: Context, completion: () -> Unit) {
        MXHelper.fetchSerialNumber(context) { result ->
            if (!result.isEmpty()) {
                serial.value = result
            } else {
                serial.value = "get serial error"
                showDebugToast(context, "Serial", "get serial error")
            }
            completion()
        }
    }

    fun fetchIMEI(context: Context, completion: () -> Unit) {
        MXHelper.fetchIMEI(context) { result ->
            if (!result.isEmpty()) {
                imei.value = result
            } else {
                imei.value = "get imei error"
                showDebugToast(context, "IMEI", "get imei error")
            }
            completion()
        }
    }

    fun authenticateApp(context: Context, callback: (Boolean) -> Unit) {
        MXHelper.whiteListApproveApp(context) { success ->
            if (success) {
                // Token saved in ZDMTokenStore
                getDWToken(context)
                appAuthenticated.value = true
                callback(true)
            } else {
                appAuthenticated.value = false
                callback(false)
            }
        }
    }

    fun getDWToken(context: Context) {
        val token = ZDMAuthHelper.acquireToken(context, ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API) ?: "UNKNOWN"
        showDebugToast(context, "Token", token)
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$type\n$data",
                Toast.LENGTH_LONG).show()
        }
    }
}