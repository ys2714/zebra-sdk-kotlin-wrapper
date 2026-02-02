package com.zebra.zebrakotlindemo.emdk

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKUSBManagerViewModel: ViewModel() {

    fun setUSBClientModeToChargingOnly(context: Context) {
        MXHelper.setUSBClientModeChargingOnly(context) {
            showDebugToast(context, "USB Client Mode", "Charging Only")
        }
    }

    fun setUSBClientModeToFileTransfer(context: Context) {
        MXHelper.setUSBClientModeFileTransfer(context) {
            showDebugToast(context, "USB Client Mode", "File Transfer")
        }
    }

    fun setUSBClientModeToUSBTethering(context: Context) {
        MXHelper.setUSBClientModeTethering(context) {
            showDebugToast(context, "USB Client Mode", "Tethering")
        }
    }

    fun showDebugToast(context: Context, title: String, desc: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context,
                "$title\n$desc",
                Toast.LENGTH_LONG).show()
        }
    }

    fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_SETTINGS)
        // intent.component = ComponentName("com.android.settings", "com.android.settings.UsbSettings")
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // This means the activity does not exist on the device.
            // You can show a toast or a dialog to the user.
            Toast.makeText(context, "USB settings not found", Toast.LENGTH_SHORT).show()

            // As a fallback, you can open the general settings page
            // startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }
}