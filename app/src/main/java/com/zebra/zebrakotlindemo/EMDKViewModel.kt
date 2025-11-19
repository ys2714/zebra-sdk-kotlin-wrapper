package com.zebra.zebrakotlindemo

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKBarcodeScannerHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKViewModel: ViewModel() {

    var text: MutableState<String> = mutableStateOf("")

    private var scannerHelper: EMDKBarcodeScannerHelper? = null

    fun handleOnCreate(context: Context) {
        scannerHelper = EMDKBarcodeScannerHelper(context)
    }

    fun startScan()  {
        scannerHelper?.startRead { type, data, timestamp ->
            text.value = data
        }
    }

    fun stopScan() {
        scannerHelper?.stopRead()
    }

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
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
        MXHelper.setScreenLockType(context, MXBase.ScreenLockType.PIN) { success ->
            showDebugToast(context, "Enable Lock Screen Success?", success.toString())
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