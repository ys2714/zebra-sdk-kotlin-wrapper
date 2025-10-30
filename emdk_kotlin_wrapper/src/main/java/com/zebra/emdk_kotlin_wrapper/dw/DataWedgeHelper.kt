package com.zebra.emdk_kotlin_wrapper.dw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import java.util.Date

class DataWedgeHelper(val appContext: Context) {

    public final inner class ResultReceiver: BroadcastReceiver() {

        override fun onReceive(
            context: Context?,
            intent: Intent?
        ) {
            if (intent == null) {
                Toast.makeText(appContext, "intent is null", Toast.LENGTH_LONG).show()
                return
            }
            if (intent.action == DWAPI.RESULT_ACTION) {
                handleCreateProfileResultAction(intent)
            } else if (intent.action == scanResultAction) {
                handleDWScanOutputAction(intent)
            }
        }

        fun dumpBundleInfo(bundle: Bundle) : String {
            var resultText = ""
            val keys = bundle.keySet()
            for (key in keys) {
                val line = bundle.getString(key)
                resultText += "$key:$line\n"
            }
            return resultText
        }

        fun handleCreateProfileResultAction(intent: Intent) {
            val command = intent.getStringExtra(DWAPI.Command.COMMAND)
            val commandID = intent.getStringExtra(DWAPI.Command.COMMAND_IDENTIFIER)
            val result = intent.getStringExtra(DWAPI.Result.RESULT)

            var bundle: Bundle = Bundle()
            if (intent.hasExtra(DWAPI.Result.RESULT_INFO)) {
                bundle = intent.getBundleExtra(DWAPI.Result.RESULT_INFO)!!
                val code = bundle.getString(DWAPI.Result.RESULT_CODE)
                if (code == null) {
                    // success
                    Toast.makeText(appContext, "CREATE PROFILE SUCCESS", Toast.LENGTH_LONG).show()
                    return
                }
                if (code == DWAPI.ResultCode.PROFILE_ALREADY_EXISTS) {
                    // success
                    Toast.makeText(appContext, "CREATE PROFILE SUCCESS", Toast.LENGTH_LONG).show()
                    return
                } else {
                    // failure
                    // Toast.makeText(appContext, code, Toast.LENGTH_LONG).show()
                }
            }
            // Toast.makeText(appContext, dumpBundleInfo(bundle), Toast.LENGTH_LONG).show()
        }

        fun handleDWScanOutputAction(intent: Intent) {
            if (intent.action != scanResultAction) { return }
            intent.extras?.let {
                val type = it.getString(DWAPI.ScanResult.TYPE) ?: ""
                val data = it.getString(DWAPI.ScanResult.DATA) ?: ""
                val timestamp: Long = it.getLong(DWAPI.ScanResult.TIME)
                val date = Date(timestamp)
                dataCallback(type, data, date.toString())
            }
        }
    }

    var receiver: ResultReceiver
    var hostAppPackageName: String
    var scanResultAction: String
    private lateinit var dataCallback: (type: String, value: String, timestamp: String) -> Unit

    init {
        hostAppPackageName = appContext.packageName
        scanResultAction = "$hostAppPackageName.DW_SCAN_OUTPUT"

        val filter = IntentFilter()
        filter.addAction(DWAPI.RESULT_ACTION)
        filter.addCategory(DWAPI.CATEGORY_DEFAULT)
        filter.addAction(scanResultAction)

        receiver = ResultReceiver()

        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    fun createProfile(name: String) {
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.CREATE_PROFILE, name)
        intent.putExtra(DWAPI.Result.SEND_RESULT, DWAPI.Result.SendResultOptions.LAST_RESULT)
        intent.putExtra(DWAPI.Command.COMMAND_IDENTIFIER, "12345678")
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun configProfileForBarcodeScan(name: String, enableOCR: Boolean, useCamera: Boolean, callback: (type: String, value: String, timestamp: String) -> Unit) {
        dataCallback = callback
        // App
        val appParams = Bundle()
        appParams.putString(DWAPI.App.PACKAGE_NAME, hostAppPackageName)
        appParams.putStringArray(DWAPI.App.ACTIVITY_LIST, arrayOf("*"))
        // Input
        var barcodeParams = Bundle()
        barcodeParams.putString(DWAPI.ScanInputParams.ENABLED, DWAPI.TRUE)
        val inputPluginBundle = Bundle()
        if (enableOCR) {
            barcodeParams = configOCRParams(barcodeParams)
            barcodeParams.putString(DWAPI.WorkflowParams.Input.ENABLED, DWAPI.TRUE)
            // barcodeParams.putString(DWAPI.WorkflowParams.SELECTED_NAME, "free_form_ocr")
            barcodeParams.putString(DWAPI.WorkflowParams.FreeFormOCR.ENABLED, DWAPI.TRUE)
            barcodeParams.putString(DWAPI.WorkflowParams.Input.SOURCE, DWAPI.WorkflowParams.Input.SourceOptions.CAMERA)
            inputPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Input.WORKFLOW)
            inputPluginBundle.putString(DWAPI.Bundle.RESET_CONFIG, DWAPI.TRUE)
        } else {
            inputPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Input.BARCODE)
        }
        inputPluginBundle.putBundle(DWAPI.Bundle.PARAM_LIST, barcodeParams)
        // Output
        val intentParams = Bundle()
        intentParams.putString(DWAPI.IntentParams.OUTPUT_ENABLED, DWAPI.TRUE)
        intentParams.putString(DWAPI.IntentParams.ACTION, scanResultAction)
        intentParams.putString(DWAPI.IntentParams.CATEGORY, DWAPI.CATEGORY_DEFAULT)
        intentParams.putString(DWAPI.IntentParams.DELIVERY, DWAPI.IntentParams.DeliveryOptions.BROADCAST)
        val outputPluginBundle = Bundle()
        outputPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Output.INTENT)
        outputPluginBundle.putBundle(DWAPI.Bundle.PARAM_LIST, intentParams)
        // Processing
        val bdfParams = Bundle()
        bdfParams.putString(DWAPI.BDFParams.ENABLED, DWAPI.FALSE)
        bdfParams.putString(DWAPI.BDFParams.SEND_DATA, DWAPI.FALSE)
        val processingPluginBundle = Bundle()
        processingPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Processing.BDF)
        processingPluginBundle.putBundle(DWAPI.Bundle.PARAM_LIST, bdfParams)
        // Keystroke
        val keyStrokeParams = Bundle()
        keyStrokeParams.putString(DWAPI.KeyStrokeParams.OUTPUT_ENABLED, DWAPI.FALSE)
        keyStrokeParams.putString(DWAPI.KeyStrokeParams.ACTION_CHAR, "9")
        keyStrokeParams.putString(DWAPI.KeyStrokeParams.DELAY_EXTENDED_ASCII, "500")
        keyStrokeParams.putString(DWAPI.KeyStrokeParams.DELAY_CONTROL_CHARS, "800")
        val keyStrokePluginBundle = Bundle()
        keyStrokePluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Output.KEYSTROKE)
        keyStrokePluginBundle.putBundle(DWAPI.Bundle.PARAM_LIST, keyStrokePluginBundle)
        // Plugins Array
        val pluginArray: ArrayList<Bundle> = ArrayList()
        pluginArray.add(inputPluginBundle)
        pluginArray.add(outputPluginBundle)
        pluginArray.add(processingPluginBundle)
        // Main Config Bundle
        val bundle = Bundle()
        bundle.putString(DWAPI.Profile.NAME, name)
        bundle.putString(DWAPI.Profile.CONFIG_MODE, DWAPI.Profile.ConfigModeOptions.OVERWRITE)
        bundle.putString(DWAPI.Profile.ENABLED, DWAPI.TRUE)
        bundle.putParcelableArray(DWAPI.App.APP_LIST, arrayOf(appParams))
        bundle.putParcelableArrayList(DWAPI.Plugin.CONFIG, pluginArray)
        // Send Broadcast
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.SET_CONFIG, bundle)
        appContext.sendOrderedBroadcast(intent, null)
    }

    private fun configOCRParams(bundle: Bundle): Bundle {
        // Decoder
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_OCR_A, DWAPI.TRUE)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_OCR_B, DWAPI.TRUE)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_MICR, DWAPI.TRUE)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_US_CURRENCY, DWAPI.TRUE)
        bundle.putString(DWAPI.OCRParams.Decoder.OCR_A_VARIANT, DWAPI.OCRParams.OCRAVariant.FULL_ASCII)
        bundle.putString(DWAPI.OCRParams.Decoder.OCR_B_VARIANT, DWAPI.OCRParams.OCRBVariant.ISBN_1)
        // Orientation
        bundle.putString(DWAPI.OCRParams.ORIENTATION, DWAPI.OCRParams.OrientationOptions.DEGREE_0)
        // Lines
        bundle.putString(DWAPI.OCRParams.LINES, DWAPI.OCRParams.LinesOptions.LINE_1)
        // Chars
        bundle.putString(DWAPI.OCRParams.Chars.MIN_CHARS, "3")
        bundle.putString(DWAPI.OCRParams.Chars.MAX_CHARS, "100")
        bundle.putString(DWAPI.OCRParams.Chars.CHAR_SUBSET, DWAPI.OCRParams.Chars.DEFAULT_SUBSET)
        bundle.putString(DWAPI.OCRParams.Chars.QUIET_ZONE, "60")
        // Template
        bundle.putString(DWAPI.OCRParams.TEMPLATE, "99999999")
        // Check Digit
        bundle.putString(DWAPI.OCRParams.CheckDigit.MODULUS, "10")
        bundle.putString(DWAPI.OCRParams.CheckDigit.MULTIPLIER, "121212121212")
        bundle.putString(DWAPI.OCRParams.CheckDigit.VALIDATION, DWAPI.OCRParams.CheckDigit.ValidationOptions.PRODUCT_ADD_LR)
        // Inverse
        bundle.putString(DWAPI.OCRParams.INVERSE, DWAPI.OCRParams.InverseOptions.AUTO)
        return bundle
    }

    fun deleteProfile(name: String) {
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.DELETE_PROFILE, arrayOf(name))
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun softScanTriggerStart() {
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.SOFT_SCAN_TRIGGER, DWAPI.SoftScanTriggerOptions.START)
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun softScanTriggerStop() {
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.SOFT_SCAN_TRIGGER, DWAPI.SoftScanTriggerOptions.STOP)
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun softScanTriggerToggle() {
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.SOFT_SCAN_TRIGGER, DWAPI.SoftScanTriggerOptions.TOGGLE)
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun setDCPButton(name: String, enabled: Boolean) {
        // DCP
        val dcpParams = Bundle()
        if (enabled) {
            dcpParams.putString(DWAPI.DCPParams.ENABLED, DWAPI.TRUE)
        } else {
            dcpParams.putString(DWAPI.DCPParams.ENABLED, DWAPI.FALSE)
        }
        dcpParams.putString(DWAPI.DCPParams.DOCK, DWAPI.DCPParams.DockOptions.BOTH)
        dcpParams.putString(DWAPI.DCPParams.MODE, DWAPI.DCPParams.ModeOptions.BUTTON)
        dcpParams.putString(DWAPI.DCPParams.HIGH_POS, "30")
        dcpParams.putString(DWAPI.DCPParams.LOW_POS, "70")
        dcpParams.putString(DWAPI.DCPParams.DRAG, "501")
        val dcpPluginBundle = Bundle()
        dcpPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Utilities.DCP)
        dcpPluginBundle.putString(DWAPI.Bundle.RESET_CONFIG, DWAPI.FALSE)
        dcpPluginBundle.putBundle(DWAPI.Bundle.PARAM_LIST, dcpParams)
        // Main Config Bundle
        val bundle = Bundle()
        bundle.putString(DWAPI.Profile.NAME, name)
        bundle.putString(DWAPI.Profile.CONFIG_MODE, DWAPI.Profile.ConfigModeOptions.UPDATE)
        bundle.putString(DWAPI.Profile.ENABLED, DWAPI.TRUE)
        if (enabled) {
            bundle.putBundle(DWAPI.Plugin.Utilities.DCP, dcpPluginBundle)
        } else {
            bundle.putBundle(DWAPI.Plugin.Utilities.DCP, null)
        }
        // Send Broadcast
        val intent = DWAPI.createDefaultIntent()
        intent.putExtra(DWAPI.SET_CONFIG, bundle)
        appContext.sendOrderedBroadcast(intent, null)
    }
}