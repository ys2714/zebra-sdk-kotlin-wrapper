package com.zebra.emdk_kotlin_wrapper.emdk

import android.content.Context
import com.symbol.emdk.ProfileManager
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerConfig
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.StatusData

class EMDKBarcodeScannerHelper(private val context: Context) {

    private var barcodeScanner: Scanner? = null
    private var eventHandler: BarcodeScanEventHandler? = null
    private lateinit var dataCallback: (type: String, value: String, timestamp: String) -> Unit

    inner class BarcodeScanEventHandler: Scanner.DataListener, Scanner.StatusListener {
        override fun onData(collection: ScanDataCollection?) {
            if (collection == null) {
                return
            }
            val type = collection.scanData.last().labelType.name
            val text = collection.scanData.last().data
            val timestamp = collection.scanData.last().timeStamp
            dataCallback(type, text, timestamp)
        }

        override fun onStatus(data: StatusData?) {
            data?.state?.let {
                when(it) {
                    StatusData.ScannerStates.IDLE -> {}
                    StatusData.ScannerStates.WAITING -> {}
                    StatusData.ScannerStates.SCANNING -> {

                    }
                    StatusData.ScannerStates.DISABLED -> {}
                    StatusData.ScannerStates.ERROR -> {}
                }
            }
        }
    }

    private var config: Config = Config()

    class Config {
        var enableOCR: Boolean = false
    }

    init {
        EMDKHelper.shared.prepareBarcodeManager(context) {
            eventHandler = BarcodeScanEventHandler()
            barcodeScanner = EMDKHelper.shared.barcodeManager?.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            barcodeScanner?.let { scanner ->
                scanner.addDataListener(eventHandler)
                scanner.addStatusListener(eventHandler)
                scanner.enable()
                setupOCR(scanner, config)
            }
        }
    }

    fun teardown() {
        barcodeScanner?.cancelRead()
        barcodeScanner?.disable()
        barcodeScanner = null
    }

    fun startRead(callback: (type: String, value: String, timestamp: String) -> Unit) {
        try {
            dataCallback = callback
            barcodeScanner?.let {
                it.cancelRead()
                it.disable()
                it.enable()
                it.triggerType = Scanner.TriggerType.SOFT_ONCE
                it.read()
            }
        } catch (e: ScannerException) {

        }
    }

    fun stopRead() {
        barcodeScanner?.let {
            it.cancelRead()
            it.disable()
        }
    }

    private fun setupOCR(scanner: Scanner, config: Config) {
        if (!config.enableOCR) {
            return
        }
        val newConfig = scanner.config
        // Decoder
        newConfig.decoderParams.ocrA.enabled = true
        newConfig.decoderParams.ocrB.enabled = true
        newConfig.decoderParams.ocrA.ocrAVariant = ScannerConfig.OcrAVariant.FULL_ASCII
        newConfig.decoderParams.ocrB.ocrBVariant = ScannerConfig.OcrBVariant.FULL_ASCII
        // Param
        newConfig.ocrParams.inverseOcr = ScannerConfig.InverseOcr.REGULAR_ONLY
        newConfig.ocrParams.checkDigitModulus = 1
        newConfig.ocrParams.checkDigitMultiplier = "121212121212"
        newConfig.ocrParams.checkDigitValidation = ScannerConfig.OcrCheckDigitValidation.NONE
        newConfig.ocrParams.ocrLines = ScannerConfig.OcrLines.ONE_LINE
        newConfig.ocrParams.maxCharacters = 100
        newConfig.ocrParams.minCharacters = 3
        newConfig.ocrParams.orientation = ScannerConfig.OcrOrientation.DEGREE_0
        newConfig.ocrParams.quietZone = 50
        newConfig.ocrParams.subset = "!\"#$%()*+,-./0123456789<>ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\\^|"
        newConfig.ocrParams.template = "99999999"
        // set back config
        scanner.config = newConfig
    }
}