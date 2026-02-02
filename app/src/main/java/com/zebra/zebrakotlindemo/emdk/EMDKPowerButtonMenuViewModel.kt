package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKPowerButtonMenuViewModel: ViewModel() {

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