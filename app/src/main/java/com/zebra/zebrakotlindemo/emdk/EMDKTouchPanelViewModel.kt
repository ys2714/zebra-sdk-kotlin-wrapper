package com.zebra.zebrakotlindemo.emdk

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper.getMainLooper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper


class EMDKTouchPanelViewModel: ViewModel() {

    var touchMode: MutableState<String> = mutableStateOf("")
    var vendorTouchMode: MutableState<String> = mutableStateOf("")

    fun handleOnCreate(context: Context) {
        OEMInfoHelper.observeOEMInfo(context, MXConst.VENDOR_TOUCH_MODE_URI) { newValue ->
            vendorTouchMode.value = newValue ?: ""
        }
        OEMInfoHelper.observeOEMInfo(context, MXConst.TOUCH_MODE_URI) { newValue ->
            touchMode.value = newValue ?: ""
        }
    }

    fun handleOnResume(context: Context) {
        fetchTouchMode(context)
        // observe(context)
    }

    //persist.sys.touch_mode
    //adb shell getprop persist.sys.touch_mode
    //
    //persist.vendor.sys.touch_mode
    //adb shell getprop persist.vendor.sys.touch_mode
    //
    fun fetchTouchMode(context: Context) {
        MXHelper.fetchTouchMode(context) { systemMode ->
            touchMode.value = systemMode
            MXHelper.fetchVendorTouchMode(context) { vendorMode ->
                vendorTouchMode.value = vendorMode
            }
        }
    }

    fun setTouchPanelToFingerOnly(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.FINGER_ONLY) {
            fetchTouchMode(context)
        }
    }

    fun setTouchPanelToGloveAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.GLOVE_AND_FINGER) {
            fetchTouchMode(context)
        }
    }

    fun setTouchPanelToStylusAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.STYLUS_AND_FINGER) {
            fetchTouchMode(context)
        }
    }

    fun setTouchPanelToStylusGloveAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.STYLUS_GLOVE_AND_FINGER) {
            fetchTouchMode(context)
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