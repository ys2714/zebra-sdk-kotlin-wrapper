package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import java.util.Timer
import java.util.TimerTask

class EMDKPowerManagementViewModel: ViewModel() {

    var osUpdateStatus: MutableState<String> = mutableStateOf("")
    var osUpdateDetail: MutableState<String> = mutableStateOf("")
    var osUpdateTimestamp: MutableState<String> = mutableStateOf("")

    fun startFetchOSUpdateStatus(context: Context) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MXHelper.fetchOSUpdateStatus(context) { status, detail, timestamp ->
                    osUpdateStatus.value = status
                    osUpdateDetail.value = detail
                    osUpdateTimestamp.value = timestamp

                    // recursive
                    startFetchOSUpdateStatus(context)
                }
            }
        }, 0, 1000)
    }

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
    }

    fun setReboot(context: Context) {
        MXHelper.setDeviceToReboot(context)
    }

    fun upgradeOS(context: Context, zipFilePath: String, suppressReboot: Boolean) {
        MXHelper.upgradeOS(context, zipFilePath, suppressReboot)
    }

    fun downgradeOS(context: Context, zipFilePath: String, suppressReboot: Boolean) {
        MXHelper.downgradeOS(context, zipFilePath, suppressReboot)
    }

    fun checkOSZipFile(context: Context, zipFilePath: String) {
        MXHelper.checkOSZipFile(context, zipFilePath)
    }
}