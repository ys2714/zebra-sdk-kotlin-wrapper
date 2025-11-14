package com.zebra.emdk_kotlin_wrapper.dw

import com.squareup.moshi.Json
import java.io.Serializable

/*
"scanner_input_enabled": "${scannerInputEnabled}",
"scanner_selection_by_identifier": "${scannerSelectionByIdentifier}",
"scanning_mode": "${scanningMode}",
"barcode_trigger_mode": "1",
"scanner_selection" : "auto",
"decoder_upca" : "true",
"decoder_ean13" : "true",
"decoder_code128" : "true",
"decoder_code39" : "true"
*/

public class PluginScannerParam: Serializable {

    @Json(name = "scanner_input_enabled")
    val enabled: String = DWAPI.StringBoolean.TRUE.value

    @Json(name = "scanner_selection_by_identifier")
    val scannerSelectionByIdentifier: String = DWAPI.ScannerIdentifiers.AUTO.value

    @Json(name = "scanning_mode")
    val scanningMode: String = DWAPI.ScanInputModeOptions.SINGLE.string

}
