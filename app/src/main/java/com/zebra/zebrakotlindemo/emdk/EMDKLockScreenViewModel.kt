package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKLockScreenViewModel: ViewModel() {

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

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$type\n$data",
                Toast.LENGTH_LONG).show()
        }
    }
}