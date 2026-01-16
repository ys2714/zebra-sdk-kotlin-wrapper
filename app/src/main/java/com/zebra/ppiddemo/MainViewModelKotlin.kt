package com.zebra.ppiddemo

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModelKotlin {

    var emdkPrepared = mutableStateOf(false)

    var ppid35: MutableState<String> = mutableStateOf("")
    var ppid26: MutableState<String> = mutableStateOf("")
    var serial: MutableState<String> = mutableStateOf("")

    fun prepareEMDK(context: Context) {
        if (emdkPrepared.value == true) {
            return
        }
        EMDKHelper.shared.prepare(context) {
            emdkPrepared.value = true
        }
    }

    fun fetchPPID(context: Context, completion: () -> Unit) {
        MXHelper.fetchPPID(context, true) { result ->
            if (!result.isEmpty()) {
                ppid35.value = result
            } else {
                ppid35.value = "get ppid error"
                showDebugToast(context, "PPID", "get ppid error")
            }
            completion()
        }
    }

    fun generatePPID(context: Context, prefix: String, completion: () -> Unit) {
        MXHelper.generatePPID(context, prefix, 6) { result ->
            if (!result.isEmpty()) {
                ppid26.value = result
            } else {
                ppid26.value = "get ppid error"
                showDebugToast(context, "PPID", "get ppid error")
            }
            completion()
        }
    }

    fun fetchSerialNumber(context: Context, completion: () -> Unit) {
        MXHelper.fetchSerialNumber(context) { result ->
            if (!result.isEmpty()) {
                serial.value = result
            } else {
                serial.value = "get serial error"
                showDebugToast(context, "Serial", "get serial error")
            }
            completion()
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