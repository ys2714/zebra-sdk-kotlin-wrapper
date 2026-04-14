package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper

class EMDKBatteryManagerViewModel: ViewModel() {

    val criticalLowThreshold: MutableState<String> = mutableStateOf("")

    fun handleSetCriticalLowThreshold(context: Context) {
        try {
            val intValue = criticalLowThreshold.value.toInt()
            MXHelper.setBatteryCriticalLowThreshold(context, intValue) { success ->
                val message = if (success) "SUCCESS" else "FAILED"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "please input integer", Toast.LENGTH_LONG).show()
        }
    }
}