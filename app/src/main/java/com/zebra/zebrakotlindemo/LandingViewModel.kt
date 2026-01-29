package com.zebra.zebrakotlindemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.zebra.zebrakotlindemo.quickscan.DWQuickScanService
import com.zebra.zebrakotlindemo.quickscan.DWQuickScanServiceConfig
import com.zebra.zebrakotlindemo.quickscan.QuickScanActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LandingViewModel: ViewModel() {

    enum class ServicePrepareStatus {
        INIT,
        STARTING,
        READY
    }

    var hasPostNotificationPermission = mutableStateOf(false)
    var servicePrepareStatus = mutableStateOf(ServicePrepareStatus.INIT)

    fun handleOnCreate(context: Context) {
        if (DWQuickScanService.isPrepared()) {
            servicePrepareStatus.value = ServicePrepareStatus.READY
        }
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
        servicePrepareStatus.value = ServicePrepareStatus.STARTING
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
        CoroutineScope(Dispatchers.IO + Job()).launch {
            while (!DWQuickScanService.isPrepared()) {
                // servicePrepareStatus.value = ServicePrepareStatus.STARTING
            }
            servicePrepareStatus.value = ServicePrepareStatus.READY
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