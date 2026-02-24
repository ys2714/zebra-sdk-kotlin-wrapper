package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper

class EMDKPowerManagementViewModel: ViewModel() {

    var osUpdateStatus: MutableState<String> = mutableStateOf("")
    var osUpdateDetail: MutableState<String> = mutableStateOf("")
    var osUpdateTimestamp: MutableState<String> = mutableStateOf("")

    fun fetchOSUpdateStatus(context: Context) {
        MXHelper.fetchOSUpdateStatus(context) { status, detail, timestamp ->
            osUpdateStatus.value = status
            osUpdateDetail.value = detail
            osUpdateTimestamp.value = timestamp
        }
    }

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
    }

    fun setReboot(context: Context) {
        MXHelper.setDeviceToReboot(context)
    }

    fun upgradeOS(context: Context, zipFilePath: String) {
        MXHelper.upgradeOS(context, zipFilePath)
    }

    fun downgradeOS(context: Context, zipFilePath: String) {
        MXHelper.downgradeOS(context, zipFilePath)
    }

    fun checkOSZipFile(context: Context, zipFilePath: String) {
        MXHelper.checkOSZipFile(context, zipFilePath)
    }
}