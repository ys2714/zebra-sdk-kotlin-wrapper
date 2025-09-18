package com.zebra.emdk_kotlin_wrapper

import android.content.Context
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.ScannerConfig
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.StatusData

class EMDKHelper(val appContext: Context) {

    private var manager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var barcodeScanner: Scanner? = null

    private var config: Config = Config()
    private lateinit var dataCallback: (type: String, value: String, timestamp: String) -> Unit

    class Config {
        var enableOCR: Boolean = false
    }

    private inner class EMDKEventHandler: EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener {
        override fun onOpened(p0: EMDKManager?) {
            manager = p0
            barcodeManager = manager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as BarcodeManager
            barcodeScanner = barcodeManager?.getDevice(BarcodeManager.DeviceIdentifier.DEFAULT)
            barcodeScanner?.let { scanner ->
                scanner.addDataListener(this)
                scanner.addStatusListener(this)
                scanner.enable()
                setupOCR(scanner, config)
            }
        }

        override fun onClosed() {
            barcodeScanner?.cancelRead()
            barcodeScanner?.disable()
            manager?.release()
            manager = null
            barcodeManager = null
            barcodeScanner = null
        }

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

    fun prepare(config: Config) : EMDKHelper? {
        this.config = config
        val emdkEventHandler = EMDKEventHandler()
        val results = EMDKManager.getEMDKManager(appContext, emdkEventHandler)
        when(results.statusCode) {
            EMDKResults.STATUS_CODE.SUCCESS -> return this
            EMDKResults.STATUS_CODE.FAILURE -> return null
            EMDKResults.STATUS_CODE.UNKNOWN -> return null
            EMDKResults.STATUS_CODE.NULL_POINTER -> return null
            EMDKResults.STATUS_CODE.EMPTY_PROFILENAME -> return null
            EMDKResults.STATUS_CODE.EMDK_NOT_OPENED -> return null
            EMDKResults.STATUS_CODE.CHECK_XML -> return null
            EMDKResults.STATUS_CODE.PREVIOUS_REQUEST_IN_PROGRESS -> return null
            EMDKResults.STATUS_CODE.PROCESSING -> return null
            EMDKResults.STATUS_CODE.NO_DATA_LISTENER -> return null
            EMDKResults.STATUS_CODE.FEATURE_NOT_READY_TO_USE -> return null
            EMDKResults.STATUS_CODE.FEATURE_NOT_SUPPORTED -> return null
        }
    }

    fun teardown() {
        barcodeScanner?.cancelRead()
        barcodeScanner?.disable()
        manager?.release()
        manager = null
        barcodeManager = null
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