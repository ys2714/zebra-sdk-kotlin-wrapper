package com.zebra.emdk_kotlin_wrapper.mx

import android.bluetooth.BluetoothClass
import android.content.Context
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import com.zebra.emdk_kotlin_wrapper.utils.DeviceInfoUtils
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

internal fun MXProfileProcessor.fetchTouchModeInBackground(
    context: Context,
    delaySeconds: Long,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.TOUCH_MODE_URI, delaySeconds, callback)
}

internal fun MXProfileProcessor.fetchVendorTouchModeInBackground(
    context: Context,
    delaySeconds: Long,
    callback: (String) -> Unit) {
    fetchOEMInfoInBackground(context,MXConst.VENDOR_TOUCH_MODE_URI, delaySeconds, callback)
}

internal fun MXProfileProcessor.fetchOEMInfoInBackground(
    context: Context,
    serviceId: String,
    delaySeconds: Long = 0,
    callback: (String) -> Unit) {
    backgroundScope.launch {
        val info = OEMInfoHelper.getOEMInfo(context, serviceId)
        if (info == null) {
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
                callback(info)
            }
        }
    }
}