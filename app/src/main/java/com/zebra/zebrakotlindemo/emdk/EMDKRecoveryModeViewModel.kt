package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EMDKRecoveryModeViewModel: ViewModel() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun enableRecoveryModeAccess(context: Context) {
        MXHelper.setRecoveryModeAccess(context, true) {
            showDebugToast(context, "Recovery Mode Access", "Enabled")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun disableRecoveryModeAccess(context: Context) {
        MXHelper.setRecoveryModeAccess(context, false) {
            showDebugToast(context, "Recovery Mode Access", "Disabled")
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