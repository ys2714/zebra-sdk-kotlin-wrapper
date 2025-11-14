package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import kotlinx.coroutines.launch

fun MXProfileProcessor.fetchSerialNumberInBackground(ctx: Context, callback: MXBase.FetchOEMInfoCallback) {
    fetchOEMInfoInBackground(ctx, MXConst.SERIAL_URI, callback)
}

fun MXProfileProcessor.fetchIMEIInBackground(ctx: Context, callback: MXBase.FetchOEMInfoCallback) {
    fetchOEMInfoInBackground(ctx, MXConst.IMEI_URI, callback)
}

fun MXProfileProcessor.fetchOEMInfoInBackground(ctx: Context, serviceId: String, callback: MXBase.FetchOEMInfoCallback) {
    backgroundScope.launch {
        val serial = OEMInfoHelper.getOEMInfo(ctx, serviceId)
        if (serial == null) {
            getCallServicePermission(ctx, serviceId, object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    val result = OEMInfoHelper.getOEMInfo(ctx, serviceId)
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