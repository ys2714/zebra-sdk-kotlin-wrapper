package com.zebra.zebrakotlindemo.emdk

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper


class EMDKTouchPanelViewModel: ViewModel() {

    // use ProductModels.valueOf() to create from string
    // different devices supporting different set of touch modes
    // please refer to https://techdocs.zebra.com/mx/touchmgr/
    enum class ProductModels {
        TC26,
        TC27,
        TC201,
        TC52
    }

    var systemTouchMode: MutableState<String> = mutableStateOf("")
    var vendorTouchMode: MutableState<String> = mutableStateOf("")
    var appTouchMode: MutableState<String> = mutableStateOf("")
    var productModel: MutableState<ProductModels> = mutableStateOf(ProductModels.TC26)

    fun handleOnCreate(context: Context) {

    }

    fun handleOnResume(context: Context) {
        EMDKHelper.shared.prepare(context) {
            MXHelper.fetchProductModel(context) { model ->
                productModel.value = ProductModels.valueOf(model)
                fetchTouchMode(context)
            }
        }
    }

    //persist.sys.touch_mode
    //adb shell getprop persist.sys.touch_mode
    //
    //persist.vendor.sys.touch_mode
    //adb shell getprop persist.vendor.sys.touch_mode
    //
    fun fetchTouchMode(context: Context) {
        when(productModel.value) {
            ProductModels.TC26 -> {
                MXHelper.fetchVendorTouchMode(context) { mode ->
                    vendorTouchMode.value = mode
                    appTouchMode.value = mode
                }
            }
            ProductModels.TC27 -> {
                MXHelper.fetchTouchMode(context) { mode ->
                    systemTouchMode.value = mode
                    appTouchMode.value = mode
                }
            }
            ProductModels.TC201 -> {
                MXHelper.fetchTouchMode(context) { mode ->
                    systemTouchMode.value = mode
                    appTouchMode.value = mode
                }
            }

            ProductModels.TC52 -> {
                MXHelper.fetchTouchMode(context) { mode ->
                    systemTouchMode.value = mode
                    appTouchMode.value = mode
                }
            }
        }
    }

    fun setTouchPanelToFingerOnly(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.FINGER_ONLY) {
            appTouchMode.value = MXBase.TouchPanelSensitivityOptions.FINGER_ONLY.xmlValue
        }
    }

    fun setTouchPanelToGloveAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.GLOVE_AND_FINGER) {
            appTouchMode.value = MXBase.TouchPanelSensitivityOptions.GLOVE_AND_FINGER.xmlValue
        }
    }

    fun setTouchPanelToStylusAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.STYLUS_AND_FINGER) {
            appTouchMode.value = MXBase.TouchPanelSensitivityOptions.STYLUS_AND_FINGER.xmlValue
        }
    }

    fun setTouchPanelToStylusGloveAndFinger(context: Context) {
        MXHelper.setTouchPanelSensitivity(context, MXBase.TouchPanelSensitivityOptions.STYLUS_GLOVE_AND_FINGER) {
            appTouchMode.value = MXBase.TouchPanelSensitivityOptions.STYLUS_GLOVE_AND_FINGER.xmlValue
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