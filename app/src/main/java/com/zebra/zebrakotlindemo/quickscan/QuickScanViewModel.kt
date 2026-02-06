package com.zebra.zebrakotlindemo.quickscan

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.zebra.emdk_kotlin_wrapper.dw.DWBarcodeScanner
import java.util.Timer
import kotlin.concurrent.schedule

class QuickScanViewModel: ViewModel() {

    var scannerPrepared: MutableState<Boolean> = mutableStateOf(false)
    var barcodeText1: MutableState<String> = mutableStateOf("")
    var barcodeText2: MutableState<String> = mutableStateOf("")
    var barcodeText3: MutableState<String> = mutableStateOf("")
    var barcodeText4: MutableState<String> = mutableStateOf("")

    private var focus: Int = 1
    private var scanner: DWBarcodeScanner? = null

    fun handleOnCreate(context: Context) {
        scanner = DWQuickScanService.shared
            ?.findScanner(DWBarcodeScanner::class.simpleName!!) as DWBarcodeScanner
    }

    fun handleOnResume() {
        scannerPrepared.value = true
        scanner?.startListen {
            when (focus) {
                1 -> {
                    barcodeText1.value = it
                }
                2 -> {
                    barcodeText2.value = it
                }
                3 -> {
                    barcodeText3.value = it
                }
                4 -> {
                    barcodeText4.value = it
                }
            }
            Timer().schedule(100) {
                stopScanning()
            }
        }
        scanner?.resume()
    }

    fun handleOnPause() {
        scanner?.suspend()
        scanner?.stopListen()
    }

    fun startScanning() {
        scanner?.startScan()
    }

    fun stopScanning() {
        scanner?.stopScan()
    }

    fun selectSingleBarcode() {
        scanner?.switchAimType(DWBarcodeScanner.AimType.TRIGGER)
        focus = 1
    }

    fun selectContinuousBarcode() {
        scanner?.switchAimType(DWBarcodeScanner.AimType.TIMED_CONTINUOUS)
        focus = 2
    }

    fun select1DDecoders() {
        scanner?.switchDecoderType(
            arrayOf(
                DWBarcodeScanner.DecoderType.JAN_EAN_8,
                DWBarcodeScanner.DecoderType.UPCA,
                DWBarcodeScanner.DecoderType.JAN_EAN_13,
                DWBarcodeScanner.DecoderType.UPCE0,
                DWBarcodeScanner.DecoderType.UPCE1
            )
        )
        focus = 3
    }

    fun select2DDecoders() {
        scanner?.switchDecoderType(
            arrayOf(
                DWBarcodeScanner.DecoderType.DATA_MATRIX,
                DWBarcodeScanner.DecoderType.PDF_417,
                DWBarcodeScanner.DecoderType.QR
            )
        )
        focus = 4
    }
}