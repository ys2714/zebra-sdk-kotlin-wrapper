package com.zebra.zebrakotlindemo.quickscan

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWBarcodeScanner
import com.zebra.emdk_kotlin_wrapper.dw.DWQuickScanService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import kotlin.concurrent.schedule

class QuickScanViewModel: ViewModel() {

    var servicePrepared: MutableState<Boolean> = mutableStateOf(false)
    var barcodeText1: MutableState<String> = mutableStateOf("")
    var barcodeText2: MutableState<String> = mutableStateOf("")

    private var aimType: DWBarcodeScanner.AimType = DWBarcodeScanner.AimType.TRIGGER

    fun handleOnCreate(context: Context) {

    }

    fun handleOnResume() {
        CoroutineScope(Dispatchers.IO).launch {
            while (!DWQuickScanService.isPrepared()) {
                servicePrepared.value = false
            }
            servicePrepared.value = true

            DWQuickScanService.shared
                ?.selectScanner(DWQuickScanService.ScannerKey.KEY_SCANNER_BARCODE)
                ?.startListen {
                    if (aimType == DWBarcodeScanner.AimType.TRIGGER) {
                        barcodeText1.value = it
                    }
                    if (aimType == DWBarcodeScanner.AimType.TIMED_CONTINUOUS) {
                        barcodeText2.value = it
                    }
                    Timer().schedule(100) {
                        stopScanning()
                    }
                }
        }
    }

    fun handleOnPause() {
        DWQuickScanService.shared
            ?.stopListen()
    }

    fun startScanning() {
        DWQuickScanService.shared
            ?.startScan()
    }

    fun stopScanning() {
        DWQuickScanService.shared
            ?.stopScan()
    }

    fun selectSingleBarcode() {
        val scanner = DWQuickScanService.shared?.currentScanner as DWBarcodeScanner
        scanner.switchAimType(DWBarcodeScanner.AimType.TRIGGER)
        aimType = DWBarcodeScanner.AimType.TRIGGER
    }

    fun selectContinuousBarcode() {
        val scanner = DWQuickScanService.shared?.currentScanner as DWBarcodeScanner
        scanner.switchAimType(DWBarcodeScanner.AimType.TIMED_CONTINUOUS)
        aimType = DWBarcodeScanner.AimType.TIMED_CONTINUOUS
    }
}