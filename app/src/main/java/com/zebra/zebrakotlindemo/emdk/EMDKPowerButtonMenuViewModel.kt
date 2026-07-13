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

    fun turnONWakeupKeyGunTrigger(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.GUN_TRIGGER, MXBase.WakeUpIndividualAction.ON) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnOFFWakeupKeyGunTrigger(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.GUN_TRIGGER, MXBase.WakeUpIndividualAction.OFF) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnONWakeupKeyLeftTrigger2(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.LEFT_TRIGGER_2, MXBase.WakeUpIndividualAction.ON) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnOFFWakeupKeyLeftTrigger2(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.LEFT_TRIGGER_2, MXBase.WakeUpIndividualAction.OFF) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnONWakeupKeyRightTrigger1(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.RIGHT_TRIGGER_1, MXBase.WakeUpIndividualAction.ON) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnOFFWakeupKeyRightTrigger1(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.RIGHT_TRIGGER_1, MXBase.WakeUpIndividualAction.OFF) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnONWakeupKeyScan(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.SCAN, MXBase.WakeUpIndividualAction.ON) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
        }
    }

    fun turnOFFWakeupKeyScan(context: Context) {
        MXHelper.setWakeUpKey(context, MXBase.KeyIdentifiers.SCAN, MXBase.WakeUpIndividualAction.OFF) { success ->
            showDebugToast(context, "Set Wakeup Key Success?", success.toString())
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