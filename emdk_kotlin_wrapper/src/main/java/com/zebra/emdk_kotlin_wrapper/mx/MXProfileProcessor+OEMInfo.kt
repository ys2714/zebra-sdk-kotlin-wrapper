package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import kotlinx.coroutines.launch

internal fun MXProfileProcessor.fetchSerialNumberInBackground(
    context: Context,
    delaySeconds: Long,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.SERIAL_URI, delaySeconds, callback)
}

internal fun MXProfileProcessor.fetchIMEIInBackground(
    context: Context,
    delaySeconds: Long,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.IMEI_URI, delaySeconds, callback)
}

internal fun MXProfileProcessor.fetchOEMInfoInBackground(
    context: Context,
    serviceId: String,
    delaySeconds: Long = 0,
    callback: (String) -> Unit) {
    backgroundScope.launch {
        val serial = OEMInfoHelper.getOEMInfo(context, serviceId)
        if (serial == null) {
            getCallServicePermission(context, serviceId, delaySeconds, { errorInfo ->
                if (errorInfo != null) {
                    callback("")
                } else {
                    val result = OEMInfoHelper.getOEMInfo(context, serviceId) ?: ""
                    foregroundScope.launch {
                        callback(result)
                    }
                }
            })
        } else {
            foregroundScope.launch {
                callback(serial)
            }
        }
    }
}