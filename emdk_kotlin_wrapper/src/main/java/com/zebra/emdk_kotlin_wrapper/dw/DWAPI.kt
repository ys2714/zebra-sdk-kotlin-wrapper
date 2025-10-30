package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.content.Intent

object DWAPI {

    const val TRUE = "true"
    const val FALSE = "false"

    const val ACTION = "com.symbol.datawedge.api.ACTION"
    const val RESULT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
    const val CATEGORY_DEFAULT = "android.intent.category.DEFAULT"

    const val CREATE_PROFILE = "com.symbol.datawedge.api.CREATE_PROFILE"
    const val DELETE_PROFILE = "com.symbol.datawedge.api.DELETE_PROFILE"
    const val SET_CONFIG = "com.symbol.datawedge.api.SET_CONFIG"
    const val ENABLE_DATAWEDGE = "com.symbol.datawedge.api.ENABLE_DATAWEDGE"
    const val SOFT_SCAN_TRIGGER = "com.symbol.datawedge.api.SOFT_SCAN_TRIGGER"

    fun createDefaultIntent(): Intent {
        val intent = Intent()
        intent.setAction(ACTION)
        return intent
    }

    fun enableDW(appContext: Context) {
        val intent = createDefaultIntent()
        intent.putExtra(ENABLE_DATAWEDGE, true)
        appContext.sendOrderedBroadcast(intent, null)
    }

    fun disableDW(appContext: Context) {
        val intent = createDefaultIntent()
        intent.putExtra(ENABLE_DATAWEDGE, false)
        appContext.sendOrderedBroadcast(intent, null)
    }

    object SoftScanTriggerOptions {
        const val START = "START_SCANNING"
        const val STOP = "STOP_SCANNING"
        const val TOGGLE = "TOGGLE_SCANNING"
    }

    object ScanInputParams {
        const val MODE = "scanning_mode"
        const val MULTI_BARCODE_COUNT = "multi_barcode_count"
        const val SCANNER_SELECTION = "scanner_selection_by_identifier"
        const val TRIGGER_WAKEUP = "trigger-wakeup"
        const val ENABLED = "scanner_input_enabled"

        object ModeOptions {
            const val SINGLE = "0"
            const val UDI = "1"
            const val MULTI_BARCODE = "2"
        }
    }

    object WorkflowParams {
        const val SELECTED_NAME = "selected_workflow_name"

        object Input {
            const val ENABLED = "workflow_input_enabled"
            const val SOURCE = "workflow_input_source"

            object SourceOptions {
                const val IMAGER = "1"
                const val CAMERA = "2"
            }
        }

        object FreeFormOCR {
            const val ENABLED = "workflow_free_form_ocr_enabled"
        }
    }

    object OCRParams {

        const val TEMPLATE = "ocr_template"
        const val INVERSE = "inverse_ocr"
        const val ORIENTATION = "ocr_orientation"
        const val LINES = "ocr_lines"



        object Decoder {
            const val ENABLE_OCR_A = "decoder_ocr_a"
            const val ENABLE_OCR_B = "decoder_ocr_b"
            const val ENABLE_MICR = "decoder_micr"
            const val ENABLE_US_CURRENCY = "decoder_us_currency"
            const val OCR_A_VARIANT = "ocr_a_variant"
            const val OCR_B_VARIANT = "ocr_b_variant"
        }

        object InverseOptions {
            const val REGULAR_ONLY = "0"
            const val INVERSE_ONLY = "1"
            const val AUTO = "2"
        }

        object OCRAVariant {
            const val FULL_ASCII = "0"
            const val RESERVED_1 = "1"
            const val RESERVED_2 = "2"
            const val BANKING = "3"
        }

        object OCRBVariant {
            const val FULL_ASCII = "0"
            const val BANKING = "1"
            const val LIMITED = "2"
            const val TRAVEL_DOCUMENT_1 = "3"
            const val PASSPORT = "4"
            const val ISBN_1 = "6"
            const val ISBN_2 = "7"
            const val TRAVEL_DOCUMENT_2 = "8"
            const val VISA_TYPE_A = "9"
            const val VISA_TYPE_B = "10"
            const val ICAO_TRAVEL_DOCUMENT = "11"
        }

        object OrientationOptions {
            const val DEGREE_0 = "0"
            const val DEGREE_270 = "1"
            const val DEGREE_180 = "2"
            const val DEGREE_90 = "3"
            const val OMNIDIRECTIONAL = "4"
        }

        object LinesOptions {
            const val LINE_1 = "1"
            const val LINE_2 = "2"
            const val LINE_3 = "3"
        }

        object Chars {
            const val MIN_CHARS = "ocr_min_chars"
            const val MAX_CHARS = "ocr_max_chars"
            const val CHAR_SUBSET = "ocr_subset"
            // 20 to 99
            const val QUIET_ZONE = "ocr_quiet_zone"

            const val DEFAULT_SUBSET = "!\"#$%()*+,-./0123456789<>ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz\\^|"
        }

        object CheckDigit {
            // 1 to 9
            const val MODULUS = "ocr_check_digit_modulus"
            // Min length = 1, Max length = 100, default = 121212121212
            const val MULTIPLIER = "ocr_check_digit_multiplier"
            const val VALIDATION = "ocr_check_digit_validation"

            object ValidationOptions {
                const val NONE = "0"
                const val PRODUCT_ADD_RL = "1"
                const val DIGIT_ADD_RL = "2"
                const val PRODUCT_ADD_LR = "3"
                const val DIGIT_ADD_LR = "4"
                const val PRODUCT_ADD_RL_SIMPLE = "5"
                const val DIGIT_ADD_RL_SIMPLE = "6"
                const val HEALTH_INDUSTRY = "9"
            }
        }
    }

    object OCRType {
        const val IMAGER_OCR = ""
        const val CAM_OCR = ""
    }

    object ScanResult {
        const val SOURCE = "com.symbol.datawedge.source"
        const val TYPE = "com.symbol.datawedge.label_type"
        const val DATA = "com.symbol.datawedge.data_string"
        const val TIME = "com.symbol.datawedge.data_dispatch_time"
        const val SCANNER_ID = "com.symbol.datawedge.scanner_identifier"
        const val DECODE_MODE = "com.symbol.datawedge.decoded_mode"
    }

    object App {
        const val PACKAGE_NAME = "PACKAGE_NAME"
        const val ACTIVITY_LIST = "ACTIVITY_LIST"
        const val APP_LIST = "APP_LIST"
    }

    object Plugin {
        const val NAME = "PLUGIN_NAME"
        const val CONFIG= "PLUGIN_CONFIG"

        object Input {
            const val BARCODE = "BARCODE"
            const val MSR = "MSR"
            const val RFID = "RFID"
            const val SERIAL = "SERIAL"
            const val VOICE = "Voice"
            const val WORKFLOW = "Workflow"
        }

        object Processing {
            const val BDF = "BDF"
            const val ADF = "ADF"
            const val TOKEN = "TOKEN"
        }

        object Output {
            const val INTENT = "INTENT"
            const val KEYSTROKE = "KEYSTROKE"
            const val IP = "IP"
        }

        object Utilities {
            const val DCP = "DCP"
            const val EKB = "EKB"
        }
    }

    object DCPParams {
        const val ENABLED = "dcp_input_enabled"
        const val DOCK = "dcp_dock_button_on"
        const val MODE = "dcp_start_in"
        // 0-100
        const val HIGH_POS = "dcp_highest_pos"
        const val LOW_POS = "dcp_lowest_pos"
        // 0-1000
        const val DRAG = "dcp_drag_detect_time"

        object DockOptions {
            const val LEFT = "LEFT"
            const val RIGHT = "RIGHT"
            const val BOTH = "BOTH"
        }

        object ModeOptions {
            const val FULLSCREEN = "FULLSCREEN"
            const val BUTTON = "BUTTON"
            const val BUTTON_ONLY = "BUTTON_ONLY"
        }
    }

    object IntentParams {
        const val OUTPUT_ENABLED = "intent_output_enabled"
        const val ACTION = "intent_action"
        const val CATEGORY = "intent_category"
        const val DELIVERY = "intent_delivery"

        object DeliveryOptions {
            const val START_ACTIVITY = "0"
            const val START_SERVICE = "1"
            const val BROADCAST = "2"
        }
    }

    object Command {
        const val COMMAND = "COMMAND"
        const val COMMAND_IDENTIFIER = "COMMAND_IDENTIFIER"
    }

    object Result {
        const val SEND_RESULT = "SEND_RESULT"

        const val RESULT = "RESULT"
        const val RESULT_INFO = "RESULT_INFO"
        const val RESULT_CODE = "RESULT_CODE"

        object SendResultOptions {
            const val NONE = "NONE"
            const val LAST_RESULT = "LAST_RESULT"
            const val COMPLETE_RESULT = "COMPLETE_RESULT"
        }
    }

    object ResultCode {
        const val APP_ALREADY_ASSOCIATED = "APP_ALREADY_ASSOCIATED"
        const val BUNDLE_EMPTY = "BUNDLE_EMPTY"
        const val DATAWEDGE_ALREADY_DISABLED = "DATAWEDGE_ALREADY_DISABLED"
        const val DATAWEDGE_ALREADY_ENABLED = "DATAWEDGE_ALREADY_ENABLED"
        const val DATAWEDGE_DISABLED = "DATAWEDGE_DISABLED"
        const val INPUT_NOT_ENABLED = "INPUT_NOT_ENABLED"
        const val OPERATION_NOT_ALLOWED = "OPERATION_NOT_ALLOWED"
        const val PARAMETER_INVALID = "PARAMETER_INVALID"
        const val PLUGIN_NOT_SUPPORTED = "PLUGIN_NOT_SUPPORTED"
        const val PLUGIN_BUNDLE_INVALID = "PLUGIN_BUNDLE_INVALID"
        const val PROFILE_ALREADY_EXISTS = "PROFILE_ALREADY_EXISTS"
    }

    object Profile {
        const val NAME = "PROFILE_NAME"
        const val CONFIG_MODE = "CONFIG_MODE"
        const val ENABLED = "PROFILE_ENABLED"

        object ConfigModeOptions {
            const val CREATE_IF_NOT_EXIST = "CREATE_IF_NOT_EXIST"
            const val OVERWRITE = "OVERWRITE"
            const val UPDATE = "UPDATE"
        }
    }

    object Bundle {
        const val RESET_CONFIG = "RESET_CONFIG"
        const val PLUGIN_NAME = "PLUGIN_NAME"
        const val PARAM_LIST = "PARAM_LIST"
        const val OUTPUT_PLUGIN_NAME = "OUTPUT_PLUGIN_NAME"
    }

    object BDFParams {
        const val ENABLED = "bdf_enabled"
        const val SEND_DATA = "bdf_send_data"
    }

    object KeyStrokeParams {
        const val OUTPUT_ENABLED = "keystroke_output_enabled"
        const val ACTION_CHAR = "keystroke_action_char"
        const val DELAY_EXTENDED_ASCII = "keystroke_delay_extended_ascii"
        const val DELAY_CONTROL_CHARS = "keystroke_delay_control_chars"
    }


}