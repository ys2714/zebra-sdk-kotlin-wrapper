package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper

class EMDKPowerManagementViewModel: ViewModel() {

    fun setSleep(context: Context) {
        MXHelper.setDeviceToSleep(context)
    }

    fun setReboot(context: Context) {
        MXHelper.setDeviceToReboot(context)
    }
}