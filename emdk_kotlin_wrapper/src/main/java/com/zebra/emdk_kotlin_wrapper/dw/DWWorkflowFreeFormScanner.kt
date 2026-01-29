package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context

class DWWorkflowFreeFormScanner(override val context: Context): DWVirtualScanner(context) {

    override val configJSONFileName: String
        get() = "barcode_intent_advanced_create.json"

    override val parameters: Map<String, String>
        get() = mapOf(
            "PROFILE_NAME" to "workflow_intent_advanced",
            "scanner_input_enabled" to "false",
            "workflow_input_enabled" to "true",
            "barcode_trigger_mode" to "0",
            "aim_type" to "8",
            "aim_timer" to "6000",
            "beam_timer" to "6000"
        )
}