package com.zebra.zebrakotlindemo

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKViewModel: ViewModel() {

    var text: MutableState<String> = mutableStateOf("")

    fun handleOnCreate(context: Context) {

    }

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
    }

    fun setReboot(context: Context) {
        MXHelper.setDeviceToReboot(context)
    }

    fun setClockToAndroidReleaseDate(context: Context) {
        val timeZone = "GMT+9"
        val date = "2008-09-23"
        val time = "08:09:23"
        MXHelper.setSystemClock(
            context,
            timeZone,
            date,
            time) { success ->
            showDebugToast(context, "Set Clock Success?", success.toString())
        }
    }

    fun setClockToGoogleNTPTime(context: Context) {
        val ntpServer = "time.google.com"
        val syncInterval = "00:30:00"
        MXHelper.resetSystemClockToNTP(
            context,
            ntpServer,
            syncInterval) { success ->
            showDebugToast(context, "Reset Clock Success?", success.toString())
        }
    }

    fun disableLockScreen(context: Context) {
        MXHelper.setScreenLockType(context, MXBase.ScreenLockType.NONE) { success ->
            showDebugToast(context, "Disable Lock Screen Success?", success.toString())
        }
    }

    fun enableLockScreen(context: Context) {
        MXHelper.powerKeyAutoScreenLockSettingsOptionEnable(context, enable = true, delaySeconds = 0) { success ->
            showDebugToast(context, "Screen Lock settings enabled?", success.toString())

            MXHelper.powerKeyTriggerAutoScreenLock(context, enable = true, delaySeconds = 0) { success ->
                showDebugToast(context, "Power Key Screen Lock enabled?", success.toString())

                MXHelper.setScreenLockType(context, MXBase.ScreenLockType.PIN) { success ->
                    showDebugToast(context, "Enable Lock Screen Success?", success.toString())
                }
            }
        }
    }

    fun disableScreenShot(context: Context) {
        MXHelper.setScreenShotUsage(context, MXBase.ScreenShotUsage.DISABLE) { success ->
            showDebugToast(context, "Disable Screen Shot Success?", success.toString())
        }
    }

    fun enableScreenShot(context: Context) {
        MXHelper.setScreenShotUsage(context, MXBase.ScreenShotUsage.ENABLE) { success ->
            showDebugToast(context, "Enable Screen Shot Success?", success.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun disablePowerOff(context: Context) {
        MXHelper.setPowerKeyMenuEnablePowerOffButton(context, false) { success ->
            showDebugToast(context, "Disable Power Off Success?", success.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun enablePowerOff(context: Context) {
        MXHelper.setPowerKeyMenuEnablePowerOffButton(context, true) { success ->
            showDebugToast(context, "Enable Power Off Success?", success.toString())
        }
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$type\n$data",
                Toast.LENGTH_LONG).show()
        }
    }
}