package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import kotlinx.coroutines.launch

fun MXProfileProcessor.fetchSerialNumberInBackground(
    context: Context,
    profileManager: ProfileManager,
    callback: MXBase.FetchOEMInfoCallback) {
    fetchOEMInfoInBackground(context, profileManager,MXConst.SERIAL_URI, callback)
}

fun MXProfileProcessor.fetchIMEIInBackground(
    context: Context,
    profileManager: ProfileManager,
    callback: MXBase.FetchOEMInfoCallback) {
    fetchOEMInfoInBackground(context, profileManager,MXConst.IMEI_URI, callback)
}

fun MXProfileProcessor.fetchOEMInfoInBackground(
    context: Context,
    profileManager: ProfileManager,
    serviceId: String,
    callback: MXBase.FetchOEMInfoCallback) {
    backgroundScope.launch {
        val serial = OEMInfoHelper.getOEMInfo(context, serviceId)
        if (serial == null) {
            getCallServicePermission(context, profileManager, serviceId, object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    val result = OEMInfoHelper.getOEMInfo(context, serviceId)
                    if (result != null) {
                        callback.onSuccess(result)
                    } else {
                        callback.onError()
                    }
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback.onError()
                }
            })
        } else {
            foregroundScope.launch {
                callback.onSuccess(serial)
            }
        }
    }
}