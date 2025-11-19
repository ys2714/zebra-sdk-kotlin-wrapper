package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst

object MXHelper {

    private val profileManager: ProfileManager
        get() {
            if (EMDKHelper.shared.profileManager == null) {
                throw RuntimeException("please call EMDKHelper.prepare() before get profileManager")
            }
            return EMDKHelper.shared.profileManager!!
        }

    fun whiteListApproveApp(context: Context, callback: (Boolean) -> Unit) {
        MXProfileProcessor.getCallServicePermission(
            context,
            this.profileManager,
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
            this.profileManager,
            MXBase.PowerManagerOptions.SLEEP_MODE
        )
    }

    fun setDeviceToReboot(context: Context) {
        MXProfileProcessor.callPowerManagerFeature(
            context,
            this.profileManager,
            MXBase.PowerManagerOptions.REBOOT
        )
    }

    fun setSystemClock(context: Context, timeZone: String, date: String, time: String, callback: (Boolean) -> Unit) {
        MXProfileProcessor.callClockSet(
            context,
            this.profileManager,
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
            this.profileManager,
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
            this.profileManager,
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

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchSerialNumber(context: Context, callback: (String) -> Unit) {
        MXProfileProcessor.fetchSerialNumberInBackground(
            context,
            this.profileManager,
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
        MXProfileProcessor.fetchSerialNumberInBackground(context, this.profileManager) { result ->
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
        MXProfileProcessor.fetchIMEIInBackground(context, this.profileManager) { result ->
            callback(result)
        }
    }
}