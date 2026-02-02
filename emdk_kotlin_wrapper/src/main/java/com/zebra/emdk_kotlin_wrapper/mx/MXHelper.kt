package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.zebra.emdk_kotlin_wrapper.utils.DeviceInfoUtils
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst

object MXHelper {

    fun whiteListApproveApp(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.getCallServicePermission(
            context,
            ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API.value,
            delaySeconds,
            { error ->
                callback(error == null)
            }
        )
    }

    fun setDeviceToSleep(context: Context, delaySeconds: Long = 0) {
        MXProfileProcessor.callPowerManagerFeature(
            context,
            MXBase.PowerManagerOptions.SLEEP_MODE,
            delaySeconds = delaySeconds
        ) {}
    }

    fun setDeviceToReboot(context: Context, delaySeconds: Long = 0) {
        MXProfileProcessor.callPowerManagerFeature(
            context,
            MXBase.PowerManagerOptions.REBOOT,
            delaySeconds = delaySeconds
        ) {}
    }

    fun setSystemClock(context: Context, timeZone: String, date: String, time: String, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.callClockSet(
            context,
            true,
            timeZone,
            date,
            time,
            delaySeconds,
            { error ->
                callback(error == null)
            }
        )
    }

    fun resetSystemClockToNTP(context: Context, ntpServer: String, syncInterval: String, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.callClockResetAuto(
            context,
            true,
            ntpServer,
            syncInterval,
            delaySeconds,
            { error ->
                callback(error == null)
            }
        )
    }

    fun setScreenLockType(context: Context, lockType: MXBase.ScreenLockType, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setScreenLockType(
            context,
            lockType,
            delaySeconds,
            { error ->
                callback(error == null)
            }
        )
    }

    fun setScreenShotUsage(context: Context, usage: MXBase.ScreenShotUsage, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setScreenShotUsage(
            context,
            usage,
            delaySeconds,
            { error ->
                callback(error == null)
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun setPowerKeyMenuEnablePowerOffButton(context: Context, enable: Boolean, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyMenuEnablePowerOffButton(context, enable, delaySeconds) {
            callback(it == null)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setRecoveryModeAccess(context: Context, enable: Boolean, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        val option = if (enable) MXBase.PowerManagerRecoveryModeAccessOptions.FULL
            else MXBase.PowerManagerRecoveryModeAccessOptions.PARTIAL
        MXProfileProcessor.callPowerManagerRecoveryModeControlFeature(context, option, delaySeconds) {
            callback(it == null)
        }
    }

    fun setUSBClientModeChargingOnly(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setUSBClientModeDefault(context, MXBase.UsbClientModeDefaultOptions.CHARGING_ONLY, delaySeconds) {
            callback(it == null)
        }
    }

    fun setUSBClientModeFileTransfer(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setUSBClientModeDefault(context, MXBase.UsbClientModeDefaultOptions.FILE_TRANSFER, delaySeconds) {
            callback(it == null)
        }
    }

    fun setUSBClientModeTethering(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setUSBClientModeDefault(context, MXBase.UsbClientModeDefaultOptions.USB_TETHERING, delaySeconds) {
            callback(it == null)
        }
    }

    fun powerKeyTriggerAutoScreenLock(context: Context, enable: Boolean, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyTriggerAutoScreenLock(context, enable, delaySeconds) {
            callback(it == null)
        }
    }

    fun powerKeyAutoScreenLockSettingsOptionEnable(context: Context, enable: Boolean, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyAutoScreenLockSettingsOptionEnable(context, enable, delaySeconds) {
            callback(it == null)
        }
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchSerialNumber(context: Context, delaySeconds: Long = 0, callback: (String) -> Unit) {
        MXProfileProcessor.fetchSerialNumberInBackground(
            context,
            delaySeconds,
            callback
        )
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchPPID(context: Context, isDevDevice: Boolean, delaySeconds: Long = 0, callback: (String) -> Unit) {
        MXProfileProcessor.fetchSerialNumberInBackground(context, delaySeconds) { result ->
            if (result.isEmpty()) {
                callback("")
                return@fetchSerialNumberInBackground
            }
            val suffix = result.takeLast(5)
            val prefix = if (isDevDevice) "619" else "610"
            val yppid = "$prefix$suffix"
            callback(yppid)
        }
    }

    fun fetchIMEI(context: Context, delaySeconds: Long = 0, callback: (String) -> Unit) {
        if (!DeviceInfoUtils.hasTelephonyFeature(context)) {
            callback("")
        }
        MXProfileProcessor.fetchIMEIInBackground(context, delaySeconds) { result ->
            callback(result)
        }
    }

    fun setKeyMappingToSendIntent(context: Context, keyIdentifier: MXBase.KeyIdentifiers, intentAction: String, intentCategory: String, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.remappingKeyToSendIntent(
            context,
            keyIdentifier,
            intentAction,
            intentCategory,
            delaySeconds
        ) { error ->
            callback(error == null)
        }
    }

    fun setKeyMappingToDefault(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        MXProfileProcessor.remappingAllKeyToDefault(context, delaySeconds) { error ->
            callback(error == null)
        }
    }

    fun copyAndImportFreeFormOCRProfile(context: Context, delaySeconds: Long = 0, callback: (Boolean) -> Unit) {
        val target = "/data/tmp/public/dwprofile_ocr_workflow.db"
        val name = "dwprofile_ocr_workflow.db"
        MXProfileProcessor.copyEmbeddedFreeFormOCRProfile(context, target, delaySeconds) { errorInfo1 ->
            if (errorInfo1 != null) {
                callback(false)
            } else {
                MXProfileProcessor.importProfile(context, target, delaySeconds) { errorInfo2 ->
                    callback(errorInfo2 == null)
                }
            }
        }
    }
}