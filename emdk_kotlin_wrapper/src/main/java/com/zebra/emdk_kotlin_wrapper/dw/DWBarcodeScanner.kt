package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle

/**
 * https://techdocs.zebra.com/datawedge/7-5/guide/api/setconfig/#scannerinputparameters
 * */
class DWBarcodeScanner(override val context: Context): DWVirtualScanner(context) {

    /**
     * aim_type:
     *
     * 0 - Trigger
     * 1 - Timed Hold
     * 2 - Timed Release
     * 3 - Press And Release
     * 4 - Presentation
     * 5 - Continuous Read
     * 6 - Press and Sustain
     * 7 – Press and Continue
     * 8 - Timed Continuous
     * */
    enum class AimType(val value: Int) {
        TRIGGER(0),
        TIMED_HOLD(1),
        TIMED_RELEASE(2),
        PRESS_AND_RELEASE(3),
        PRESENTATION(4),
        CONTINUOUS_READ(5),
        PRESS_AND_SUSTAIN(6),
        PRESS_AND_CONTINUE(7),
        TIMED_CONTINUOUS(8);

        val string: String = value.toString()
    }

    override val configJSONFileName: String
        get() = "barcode_intent_advanced_create.json"


    override val parameters: Map<String, String>
        get() = mapOf(
            "PROFILE_NAME" to "barcode_single_intent_advanced",
            "scanner_input_enabled" to "true",
            "workflow_input_enabled" to "false",
            "barcode_trigger_mode" to "0",
            "aim_type" to "0",
            "aim_timer" to "6000",
            "beam_timer" to "6000"
        )

    fun switchAimType(aimType: AimType): DWBarcodeScanner {
        DataWedgeHelper.switchScannerParams(
            context,
            Bundle().apply {
                putString("aim_type", aimType.string)
            }
        )
        return this
    }
}
