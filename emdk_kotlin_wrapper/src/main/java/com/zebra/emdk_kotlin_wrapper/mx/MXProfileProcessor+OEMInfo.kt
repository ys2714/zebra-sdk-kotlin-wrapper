package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import kotlinx.coroutines.launch

internal fun MXProfileProcessor.fetchSerialNumberInBackground(
    context: Context,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.SERIAL_URI, callback)
}

internal fun MXProfileProcessor.fetchIMEIInBackground(
    context: Context,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.IMEI_URI, callback)
}

internal fun MXProfileProcessor.fetchOEMInfoInBackground(
    context: Context,
    serviceId: String,
    callback: (String) -> Unit) {
    backgroundScope.launch {
        val serial = OEMInfoHelper.getOEMInfo(context, serviceId)
        if (serial == null) {
            getCallServicePermission(context, serviceId, object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    val result = OEMInfoHelper.getOEMInfo(context, serviceId) ?: ""
                    foregroundScope.launch {
                        callback(result)
                    }
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback("")
                }
            })
        } else {
            foregroundScope.launch {
                callback(serial)
            }
        }
    }
}