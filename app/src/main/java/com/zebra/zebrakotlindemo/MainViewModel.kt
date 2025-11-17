package com.zebra.zebrakotlindemo

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor
import com.zebra.emdk_kotlin_wrapper.mx.fetchIMEIInBackground
import com.zebra.emdk_kotlin_wrapper.mx.fetchSerialNumberInBackground
import com.zebra.emdk_kotlin_wrapper.mx.getCallServicePermission
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMAuthHelper
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel {

    companion object {
        val profileName = "ZebraKotlinDemo4"
    }

    private var profileProcessor: MXProfileProcessor? = null

    var appAuthenticated = mutableStateOf(false)
    var emdkPrepared = mutableStateOf(false)
    var scannerStatus = mutableStateOf("")

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
            profileProcessor = MXProfileProcessor(context)

//            authenticateApp(context) { whiteListSuccess ->
//                if (whiteListSuccess) {
//
//                } else {
//
//                }
//            }

//             fetchSerialNumber(context)
//             fetchIMEI(context)

            DataWedgeHelper.prepare(context) { enableSuccess ->
                if (enableSuccess) {
                    DataWedgeHelper.deleteProfile(context, profileName) { success ->
                        if (success) {
                            DataWedgeHelper.createProfile(context, profileName) { createSuccess ->
                                if (createSuccess) {
                                    DataWedgeHelper.bindProfileToApp(context, profileName, context.packageName) { configSuccess ->
                                        if (configSuccess) {
                                            getScannerStatus(context)
                                            emdkPrepared.value = true

                                            DataWedgeHelper.configBarcodePlugin(context, profileName, enable = false)
                                            DataWedgeHelper.configKeystrokePlugin(context, profileName, false)
                                            DataWedgeHelper.configIntentPlugin(context, profileName)

                                            Log.d("DataWedge", "Profile configured successfully")
                                        } else {
                                            Log.e("DataWedge", "Failed to configure profile")
                                        }
                                    }
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
            scannerStatus.value = status.toString()
            // showDebugToast(context, "Scanner Status", status.toString())
        }
    }

    fun fetchSerialNumber(context: Context) {
        profileProcessor?.fetchSerialNumberInBackground(
            context,
            object: MXBase.FetchOEMInfoCallback {
                override fun onSuccess(result: String) {
                    serial.value = result
                    // showDebugToast(context, "Serial Number", result)
                }

                override fun onError() {
                    serial.value = "get serial number error"
                    showDebugToast(context, "Serial Number", "get serial number error")
                }
            })
    }

    fun fetchIMEI(context: Context) {
        profileProcessor?.fetchIMEIInBackground(
            context,
            object: MXBase.FetchOEMInfoCallback {
                override fun onSuccess(result: String) {
                    imei.value = result
                    // showDebugToast(context, "IMEI", result)
                }

                override fun onError() {
                    serial.value = "get serial number error"
                    showDebugToast(context, "IMEI", "get serial number error")
                }
            })
    }

    fun authenticateApp(context: Context, callback: (Boolean) -> Unit) {
        whiteListApproveApp(context) { success ->
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

    fun whiteListApproveApp(context: Context, callback: (Boolean) -> Unit) {
        profileProcessor?.getCallServicePermission(
            context,
            ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API.value,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, "Approve App Success", profileName)
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, "Approve App Error", errorInfo.errorDescription)
                    callback(false)
                }
            }
        )
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