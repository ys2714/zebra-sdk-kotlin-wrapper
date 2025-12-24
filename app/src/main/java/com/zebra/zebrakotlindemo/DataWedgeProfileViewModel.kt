package com.zebra.zebrakotlindemo

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.utils.FileUtils
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataWedgeProfileViewModel : ViewModel() {

    fun showDebugToast(context: Context, type: String, data: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "$type\n$data", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportProfile(context: Context, profileName: String) {
        DataWedgeHelper.getProfile(context, profileName) { bundle ->
            val json = JsonUtils.bundleToJson(bundle)
            FileUtils.saveTextToDownloads(context, profileName, json)
        }
    }
}