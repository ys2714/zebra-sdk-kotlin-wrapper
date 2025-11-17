package com.zebra.zebrakotlindemo

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKBarcodeScannerHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor
import com.zebra.emdk_kotlin_wrapper.mx.callClockSet
import com.zebra.emdk_kotlin_wrapper.mx.callClockSetAuto
import com.zebra.emdk_kotlin_wrapper.mx.setScreenLockType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKViewModel: ViewModel() {

    var text: MutableState<String> = mutableStateOf("")


    private var scannerHelper: EMDKBarcodeScannerHelper? = null
    private var profileProcessor: MXProfileProcessor? = null

    fun handleOnCreate(context: Context) {
        scannerHelper = EMDKBarcodeScannerHelper(context)
        profileProcessor = MXProfileProcessor(context)
    }

    fun startScan()  {
        scannerHelper?.startRead { type, data, timestamp ->
            text.value = data
        }
    }

    fun stopScan() {
        scannerHelper?.stopRead()
    }

    fun setClockToAndroidReleaseDate(context: Context) {
        val timeZone = "GMT+9"
        val date = "2008-9-23"
        val time = "8:09:23"
        profileProcessor?.callClockSet(
            true,
            timeZone,
            date,
            time,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, date, time)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, errorInfo.errorName, errorInfo.errorDescription)
                }
            })
    }

    fun setClockToGoogleNTPTime(context: Context) {
        val ntpServer = "time.google.com"
        val syncInterval = "00:30:00"
        profileProcessor?.callClockSetAuto(
            true,
            ntpServer,
            syncInterval,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, "Set NTP Time Success", ntpServer)
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, "Set NTP Time Error", errorInfo.errorDescription)
                }
            })
    }

    fun disableLockScreen(context: Context) {
        profileProcessor?.setScreenLockType(
            context,
            MXBase.ScreenLockType.NONE,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, "Set Lock Screen", "disable lock screen success")
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, "Set Lock Screen", "disable lock screen failed")
                }
            }
        )
    }

    fun enableLockScreen(context: Context) {
        profileProcessor?.setScreenLockType(
            context,
            MXBase.ScreenLockType.PIN,
            object : MXBase.ProcessProfileCallback {
                override fun onSuccess(profileName: String) {
                    showDebugToast(context, "Set Lock Screen", "enable lock screen success")
                }

                override fun onError(errorInfo: MXBase.ErrorInfo) {
                    showDebugToast(context, "Set Lock Screen", "enable lock screen failed")
                }
            }
        )
    }

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$type\n$data",
                Toast.LENGTH_LONG).show()
        }
    }
}