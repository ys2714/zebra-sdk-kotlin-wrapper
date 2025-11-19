package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst

object MXHelper {

    fun whiteListApproveApp(context: Context, callback: (Boolean) -> Unit) {
        MXProfileProcessor.getCallServicePermission(
            context,
            ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API.value,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback(false)
                }
            }
        )
    }

    fun setDeviceToSleep(context: Context) {
        MXProfileProcessor.callPowerManagerFeature(
            context,
            MXBase.PowerManagerOptions.SLEEP_MODE
        )
    }

    fun setDeviceToReboot(context: Context) {
        MXProfileProcessor.callPowerManagerFeature(
            context,
            MXBase.PowerManagerOptions.REBOOT
        )
    }

    fun setSystemClock(context: Context, timeZone: String, date: String, time: String, callback: (Boolean) -> Unit) {
        MXProfileProcessor.callClockSet(
            context,
            true,
            timeZone,
            date,
            time,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback(false)
                }
            }
        )
    }

    fun resetSystemClockToNTP(context: Context, ntpServer: String, syncInterval: String, callback: (Boolean) -> Unit) {
        MXProfileProcessor.callClockResetAuto(
            context,
            true,
            ntpServer,
            syncInterval,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback(false)
                }
            }
        )
    }

    fun setScreenLockType(context: Context, lockType: MXBase.ScreenLockType, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setScreenLockType(
            context,
            lockType,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    callback(true)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    callback(false)
                }
            }
        )
    }

    fun setScreenShotUsage(context: Context, usage: MXBase.ScreenShotUsage, callback: (Boolean) -> Unit) {
        MXProfileProcessor.setScreenShotUsage(context, usage, object : MXBase.ProcessProfileCallback {
            override fun onSuccess(profileName: String) {
                callback(true)
            }

            override fun onError(errorInfo: MXBase.ErrorInfo) {
                callback(false)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun setPowerKeyMenuEnablePowerOffButton(context: Context, enable: Boolean, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyMenuEnablePowerOffButton(context, enable, callback)
    }

    fun powerKeyTriggerAutoScreenLock(context: Context, enable: Boolean, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyTriggerAutoScreenLock(context, enable, callback)
    }

    fun powerKeyAutoScreenLockSettingsOptionEnable(context: Context, enable: Boolean, callback: (Boolean) -> Unit) {
        MXProfileProcessor.powerKeyAutoScreenLockSettingsOptionEnable(context, enable, callback)
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchSerialNumber(context: Context, callback: (String) -> Unit) {
        MXProfileProcessor.fetchSerialNumberInBackground(
            context,
            callback
        )
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchPPID(context: Context, isDevDevice: Boolean, callback: (String) -> Unit) {
        MXProfileProcessor.fetchSerialNumberInBackground(context) { result ->
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

    fun fetchIMEI(context: Context, callback: (String) -> Unit) {
        MXProfileProcessor.fetchIMEIInBackground(context) { result ->
            callback(result)
        }
    }
}