package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import java.util.Timer
import java.util.TimerTask

class EMDKPowerManagementViewModel: ViewModel() {

    val device_manifest = "DeviceManifest.xml"
    val tc26_downgrade = "HE_FULL_UPDATE_10-74-20.00-QG-U00-C472-HEL-04.zip"
    val tc26_upgrade = "HE_FULL_UPDATE_10-74-20.00-QG-U00-C473-HEL-04.zip"

    val tc27_downgrade = "AT_FULL_UPDATE_14-35-10.00-UG-U00-STD-ATH-04.zip"
    val tc27_upgrade = "AT_FULL_UPDATE_14-35-10.00-UG-U56-STD-ATH-04.zip"

    val tc201_downgrade = "ER_FULL_UPDATE_15-13-21.00-VG-U00-PRD-ERS-04.zip"
    val tc201_upgrade = "ER_FULL_UPDATE_15-14-10.00-VG-U00-PRD-ERS-04.zip"

    val tc27_upgrade_stream = "https://drive.google.com/uc?export=download&id=1HN8l0y5R9mOVpHJKT-ZXIfmJayBqQYO7"
    val tc27_downgrade_stream = "https://drive.google.com/uc?export=download&id=1IZ_HUaKdEhXclLf92mmqUdWPCwqbglDa"

    var osUpdateStatus: MutableState<String> = mutableStateOf("")
    var osUpdateDetail: MutableState<String> = mutableStateOf("")
    var osUpdateTimestamp: MutableState<String> = mutableStateOf("")

    var manifestPath: MutableState<String> = mutableStateOf("/sdcard/Download/" + device_manifest)
    var upgradeBSPPath: MutableState<String> = mutableStateOf("/sdcard/Download/" + tc27_upgrade)
    var downgradeBSPPath: MutableState<String> = mutableStateOf("/sdcard/Download/" + tc27_downgrade)

    var upgradeBSPStream: MutableState<String> = mutableStateOf(tc27_upgrade_stream)
    var downgradeBSPStream: MutableState<String> = mutableStateOf(tc27_downgrade_stream)

    var flagShouldShowRebootConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    fun startFetchOSUpdateStatus(context: Context) {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MXHelper.fetchOSUpdateStatus(context) { status, detail, timestamp ->
                    osUpdateStatus.value = status.string
                    osUpdateDetail.value = detail
                    osUpdateTimestamp.value = timestamp

                    if (status == MXBase.OSUpdateStatus.WAITING_FOR_REBOOT
                        && !flagShouldShowRebootConfirmDialog.value) {
                        flagShouldShowRebootConfirmDialog.value = true
                    }
                }
            }
        }, 0, 10 * 1000) // every 10 sec
    }

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
    }

    fun setReboot(context: Context) {
        MXHelper.setDeviceToReboot(context)
    }

    fun upgradeOS(context: Context, zipFilePath: String, suppressReboot: Boolean) {
        Toast.makeText(context, "verify package ...", Toast.LENGTH_LONG).show()
        checkOSZipFile(context, zipFilePath) { success ->
            if (success) {
                MXHelper.upgradeOS(context, zipFilePath, suppressReboot)
                startFetchOSUpdateStatus(context)
                Toast.makeText(context, "verify success. starting upgrade", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "verify failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun downgradeOS(context: Context, zipFilePath: String) {
        Toast.makeText(context, "verify package ...", Toast.LENGTH_LONG).show()
        checkOSZipFile(context, zipFilePath) { success ->
            if (success) {
                MXHelper.downgradeOS(context, zipFilePath)
                startFetchOSUpdateStatus(context)
                Toast.makeText(context, "verify success. starting downgrade", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "verify failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun streamUpgradeOS(context: Context, url: String, token: String) {
        val authInfo = MXBase.AuthInfo.createWithCustomHeader(token)
        MXHelper.streamUpgradeOS(
            context,
            url,
            authInfo,
            true,
            0)
    }

    fun streamDowngradeOS(context: Context, url: String, token: String) {
        val authInfo = MXBase.AuthInfo.createWithCustomHeader(token)
        MXHelper.streamDowngradeOS(
            context,
            url,
            authInfo,
            0)
    }

    fun checkOSZipFile(context: Context, zipFilePath: String, callback: (Boolean) -> Unit) {
        MXHelper.checkOSZipFile(context, zipFilePath) { success ->
            callback(success)
        }
    }

    fun cancelUpdate(context: Context) {
        MXHelper.cancelOngoingUpdate(context)
    }
}