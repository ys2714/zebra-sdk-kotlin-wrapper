package com.zebra.zebrakotlindemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWQuickScanService
import com.zebra.emdk_kotlin_wrapper.dw.DWQuickScanServiceConfig
import com.zebra.zebrakotlindemo.quickscan.QuickScanActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LandingViewModel: ViewModel() {

    var hasPostNotificationPermission = mutableStateOf(false)
    var servicePrepared = mutableStateOf(false)

    fun handleOnCreate(context: Context) {
        servicePrepared.value = DWQuickScanService.isPrepared()
    }

    fun handleOnResume(context: Context) {
        hasPostNotificationPermission.value = checkPostNotificationPermission(context)
        waitServiceReady {
            context.startActivity(
                Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            )
        }
    }

    fun startServiceIfNeeded(context: Context) {
        hasPostNotificationPermission.value = true
        val ctx = context.applicationContext
        if (!DWQuickScanService.isRunning()) {
            DWQuickScanService.start(
                ctx,
                DWQuickScanServiceConfig(
                    R.drawable.local_shipping_24px,
                    "Scanner Ready",
                    "push scan button"
                ),
                QuickScanActivity::class.java)
        }
    }

    private fun waitServiceReady(onReady: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            while (!DWQuickScanService.isPrepared()) {
                servicePrepared.value = false
            }
            servicePrepared.value = true
            onReady()
        }
    }

    private fun checkPostNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }
}