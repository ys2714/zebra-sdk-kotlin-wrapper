package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst

object MXHelper {

    private val profileManager: ProfileManager?
        get() {
            return EMDKHelper.shared.profileManager
        }

    fun whiteListApproveApp(context: Context, callback: (Boolean) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.getCallServicePermission(
                context,
                it,
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
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    fun setDeviceToSleep(context: Context) {
        profileManager?.also {
            MXProfileProcessor.callPowerManagerFeature(
                context,
                it,
                MXBase.PowerManagerOptions.SLEEP_MODE)
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    fun setSystemClock(context: Context, timeZone: String, date: String, time: String, callback: (Boolean) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.callClockSet(
                context,
                it,
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
                })
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    fun resetSystemClockToNTP(context: Context, ntpServer: String, syncInterval: String, callback: (Boolean) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.callClockSetAuto(
                context,
                it,
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
                })
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    fun setScreenLockType(context: Context, lockType: MXBase.ScreenLockType, callback: (Boolean) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.setScreenLockType(
                context,
                it,
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
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchSerialNumber(context: Context, callback: (String) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.fetchSerialNumberInBackground(
                context,
                it,
                object : MXBase.FetchOEMInfoCallback {
                    override fun onSuccess(result: String) {
                        callback(result)
                    }

                    override fun onError() {
                        callback("")
                    }
                })
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    /**
     *
     * @param context
     * @param isDevDevice indicate get PPID for development device or production device
     * @param callback: return empty string "" if failed
     * */
    fun fetchPPID(context: Context, isDevDevice: Boolean, callback: (String) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.fetchSerialNumberInBackground(
                context,
                it,
                object : MXBase.FetchOEMInfoCallback {
                    override fun onSuccess(result: String) {
                        val suffix = result.takeLast(5)
                        val prefix = if (isDevDevice) "619" else "610"
                        val yppid = "$prefix$suffix"
                        callback(yppid)
                    }

                    override fun onError() {
                        callback("")
                    }
                })
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }

    fun fetchIMEI(context: Context, callback: (String) -> Unit) {
        profileManager?.also {
            MXProfileProcessor.fetchIMEIInBackground(
                context,
                it,
                object : MXBase.FetchOEMInfoCallback {
                    override fun onSuccess(result: String) {
                        callback(result)
                    }

                    override fun onError() {
                        callback("")
                    }
                })
        } ?: run {
            throw Exception("ProfileManager is null. please run after EMDKHelper.shared.prepare()")
        }
    }
}