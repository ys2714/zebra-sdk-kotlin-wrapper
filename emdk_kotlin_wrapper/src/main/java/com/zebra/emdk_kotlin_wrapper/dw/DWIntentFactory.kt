package com.zebra.emdk_kotlin_wrapper.dw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.core.content.ContextCompat
import com.zebra.emdk_kotlin_wrapper.dw.DWScannerMap.DWScannerInfo
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import kotlinx.coroutines.suspendCancellableCoroutine

internal object DWIntentFactory {

    const val TAG = "DWIntentFactory"

    fun callDWAPI(context: Context, extraKey: DWAPI.ActionExtraKeys, extraValue: Any, callback: (Result<Intent>) -> Unit) {
        fun completion(ctx: Context, receiver: BroadcastReceiver, result: Result<Intent>) {
            callback(result)
            ctx.unregisterReceiver(receiver)
        }
        val ctx = context.applicationContext
        val commandIdentifier = "${extraKey.value}_${(0..99999999).random()}"
        // dynamically register a broadcast receiver to receive the result. unregister it after the result is received.
        ContextCompat.registerReceiver(
            ctx,
            object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context?,
                    intent: Intent?
                ) {
                    intent?.also {
                        // command identifier optional
                        val receivedCommandIdentifier = it.getStringExtra(DWConst.COMMAND_IDENTIFIER)
                        if (receivedCommandIdentifier != null && receivedCommandIdentifier != commandIdentifier) {
                            // please don't call completion() here, because this maybe caused by concurrently calling same API.
                            Log.w(TAG, "SKIP. intent command didn't match $commandIdentifier")
                            return
                        }
                        if (it.action != DWAPI.ResultActionNames.RESULT_ACTION.value) {
                            Log.e(TAG, "intent action is not ${DWAPI.ResultActionNames.RESULT_ACTION.value}")
                            completion(ctx, this, Result.failure(Exception(DWAPI.ResultCodes.ERROR_INTENT_ACTION_NOT_MATCH.value)))
                            return
                        }
                        // result info optional
                        val infoBundle = it.getBundleExtra(DWAPI.Result.RESULT_INFO)
                        if (infoBundle != null) {
                            val code = infoBundle.getString(DWAPI.Result.RESULT_CODE)
                            if (code == null) {
                                completion(ctx, this, Result.success(it))
                            } else {
                                if (code == DWAPI.ResultCodes.PLUGIN_BUNDLE_INVALID.value ||
                                    code == DWAPI.ResultCodes.PLUGIN_NOT_SUPPORTED.value ||
                                    code == DWAPI.ResultCodes.PLUGIN_DISABLED_IN_CONFIG.value||
                                    code == DWAPI.ResultCodes.PARAMETER_INVALID.value
                                    ) {
                                    val bundle = extraValue as Bundle
                                    val dump = JsonUtils.bundleToJson(bundle)
                                    completion(ctx, this, Result.failure(Exception("$code $receivedCommandIdentifier ${dump}")))
                                }
                                else {
                                    completion(ctx, this, Result.failure(Exception(code)))
                                }
                            }
                            return
                        } else {
                            Log.d(TAG, "no RESULT_INFO in intent")
                        }
                        // passed all check
                        completion(ctx, this, Result.success(it))
                    } ?: run {
                        Log.e(TAG, "intent is null")
                        completion(ctx, this, Result.failure(Exception(DWAPI.ResultCodes.ERROR_INTENT_NULL.value)))
                    }
                }
            },
            IntentFilter().apply {
                // please do not set category to the DW API calling intent !!!
                // DW API result intent come with category "android.intent.category.DEFAULT"
                addAction(DWAPI.ResultActionNames.RESULT_ACTION.value)
                addCategory(DWAPI.ResultCategoryNames.CATEGORY_DEFAULT.value)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
        ctx.sendOrderedBroadcast(
            Intent().apply {
                // please do not set category to the DW API calling intent !!!
                // DW API result intent come with category "android.intent.category.DEFAULT"
                setAction(DWAPI.ActionNames.ACTION.value)
                when (extraValue) {
                    is String -> {
                        putExtra(extraKey.value, extraValue as String)
                    }
                    is Boolean -> {
                        putExtra(extraKey.value, extraValue as Boolean)
                    }
                    is Int -> {
                        putExtra(extraKey.value, extraValue as Int)
                    }
                    is Bundle -> {
                        putExtra(extraKey.value, extraValue as Bundle)
                    }
                    is Parcelable -> {
                        putExtra(extraKey.value, extraValue as Parcelable)
                    }
                    is Array<*> -> {
                        if (extraValue.all { it is Parcelable })
                            putExtra(extraKey.value, extraValue as Array<Parcelable>)
                    }
                }
                putExtra(DWConst.SEND_RESULT, DWAPI.SendResultOptions.LAST_RESULT.value)
                putExtra(DWConst.COMMAND_IDENTIFIER, commandIdentifier)
            },
            null
        )
    }

    /**
     * https://techdocs.zebra.com/datawedge/latest/guide/api/setconfig/
     *
     * - Result Codes:
     * PLUGIN_NOT_SUPPORTED - An attempt was made to configure a plug-in that is not supported by DataWedge intent APIs
     * BUNDLE_EMPTY - The bundle contains no data
     * PROFILE_NAME_EMPTY - An attempt was made to configure a Profile name with no data
     * PROFILE_NOT_FOUND - An attempt was made to perform an operation on a Profile that does not exist
     * PLUGIN_BUNDLE_INVALID - A passed plug-in parameter bundle is empty or contains insufficient information
     * PARAMETER_INVALID - The passed parameters were empty, null or invalid
     * APP_ALREADY_ASSOCIATED - An attempt was made to associate an app that was already associated with another Profile
     * OPERATION_NOT_ALLOWED - An attempt was made to rename or delete a protected Profile or to associate an app with Profile0
     * RESULT_ACTION_RESULT_CODE_EMPTY_RULE_NAME - Rule name empty or undefined in ADF_RULE bundle
     * UNLICENSED_FEATURE - An attempt was made to call SetConfig or Switch Scanner Params API to change the scanning mode to MultiBarcode or NextGen SimulScan on an unlicensed Zebra Professional-series device.
     * */
    suspend fun sendSetConfigIntent(context: Context, extra: Bundle): Boolean = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.SET_CONFIG, extra) { result ->
            result.onSuccess {
                continuation.resumeWith(Result.success(true))
            }.onFailure {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

    /**
     * Enables or disables the DataWedge service.
     *
     * See [DataWedge API documentation](https://techdocs.zebra.com/datawedge/latest/guide/api/enabledatawedge/) for more details.
     *
     * The function sends an intent with the following properties:
     * - **ACTION**: `com.symbol.datawedge.api.ACTION`
     * - **EXTRA_DATA**: `com.symbol.datawedge.api.ENABLE_DATAWEDGE`
     * - **Value**: `true` or `false`
     *
     * https://techdocs.zebra.com/datawedge/latest/guide/api/enabledatawedge/
     * @param context The application context.
     * @param enabled `true` to enable DataWedge, `false` to disable it.
     * @return `true` if the operation was successful (including cases where DataWedge was already in the desired state), `false` on failure.
     * @throws Exception with a message from [DWAPI.ResultCodes] on failure. Note that `DATAWEDGE_ALREADY_ENABLED` and `DATAWEDGE_ALREADY_DISABLED` are treated as success conditions.
     */
    suspend fun enableDW(context: Context, enabled: Boolean): Boolean = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.ENABLE_DATAWEDGE, enabled) { result ->
            result.onSuccess {
                continuation.resumeWith(Result.success(true))
            }.onFailure {
                if (enabled && it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_ENABLED.value) {
                    continuation.resumeWith(Result.success(true))
                }
                else if (!enabled && it.message == DWAPI.ResultCodes.DATAWEDGE_ALREADY_DISABLED.value) {
                    continuation.resumeWith(Result.success(true))
                }
                else {
                    continuation.resumeWith(Result.failure(it))
                }
            }
        }
    }

    /**
     * https://techdocs.zebra.com/datawedge/latest/guide/api/getdatawedgestatus/
     *
     * - Parameters:
     * ACTION [String]: "com.symbol.datawedge.api.ACTION"
     * EXTRA_DATA [String]: "com.symbol.datawedge.api.GET_DATAWEDGE_STATUS"
     *
     * - Result Codes:
     * EXTRA VALUE: Empty string
     * Returns the status of DataWedge as "enabled" or "disabled" as a string extra.
     * EXTRA NAME: "com.symbol.datawedge.api.RESULT_GET_DATAWEDGE_STATUS"
     * EXTRA TYPE: Bundle
     * */
    suspend fun sendGetDWStatusIntent(context: Context): Boolean = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.GET_DATAWEDGE_STATUS, "") { result ->
            result.onSuccess {
                if (it.action == DWAPI.ResultActionNames.RESULT_ACTION.value &&
                    it.hasExtra(DWAPI.ResultExtraKeys.GET_DATAWEDGE_STATUS.value))
                {
                    val result = it.getStringExtra(DWAPI.ResultExtraKeys.GET_DATAWEDGE_STATUS.value)
                    val enabled = result == DWAPI.StringEnabled.ENABLED.value
                    Log.d(DataWedgeHelper.TAG, "CHECK DW STATUS PROFILE RESULT: $enabled")
                    continuation.resumeWith(Result.success(enabled))
                } else {
                    Log.e(DataWedgeHelper.TAG, "CHECK DW STATUS PROFILE FAILED: no dw status in intent")
                    continuation.resumeWith(Result.failure(Exception(DWAPI.ResultCodes.ERROR_NO_DATAWEDGE_STATUS_IN_RESULT.value)))
                }
            }.onFailure {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

    /**
     * https://techdocs.zebra.com/datawedge/latest/guide/api/createprofile/
     *
     * - Parameters:
     * ACTION [String]: com.symbol.datawedge.api.ACTION
     * EXTRA_DATA [String]: com.symbol.datawedge.api.CREATE_PROFILE
     * String <value>: Name of Profile to be created
     *
     * - Result Codes:
     * PROFILE_ALREADY_EXIST - FAILURE
     * PROFILE_NAME_EMPTY - FAILURE
     * */
    suspend fun sendCreateProfileIntent(context: Context, name: String): Boolean = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.CREATE_PROFILE,name) { result ->
            result.onSuccess {
                continuation.resumeWith(Result.success(true))
            }.onFailure {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

    suspend fun sendDeleteProfileIntent(context: Context, name: String): Boolean = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, name) { result ->
            result.onSuccess {
                continuation.resumeWith(Result.success(true))
            }.onFailure {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

    /**
     * https://techdocs.zebra.com/datawedge/15-0/guide/api/enumeratescanners/
     *
     * Wrong:
     * com.symbol.datawedge.api.ACTION_ENUMERATEDSCANNERLIST
     * Right:
     * com.symbol.datawedge.api.RESULT_ENUMERATE_SCANNERS
     * */
    suspend fun sendEnumerateScannersIntent(context: Context): List<DWScannerInfo> = suspendCancellableCoroutine { continuation ->
        callDWAPI(context, DWAPI.ActionExtraKeys.ENUMERATE_SCANNERS, "") { result ->
            result.onSuccess { intent ->
                callDWAPIEnumerateScannersHandler(intent) {
                    continuation.resumeWith(Result.success(it))
                }
            }.onFailure {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }

    private fun callDWAPIEnumerateScannersHandler(intent: Intent, callback: (List<DWScannerInfo>) -> Unit) {
        if (intent.action == DWAPI.ResultActionNames.RESULT_ACTION.value &&
            intent.hasExtra(DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value)
        )
        {
            val bundles: ArrayList<Bundle>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableArrayListExtra(
                    DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value,
                    Bundle::class.java) as ArrayList<Bundle>
            } else {
                intent.getParcelableArrayListExtra<Bundle>(DWAPI.ResultExtraKeys.ENUMERATE_SCANNERS.value)
            }
            bundles?.forEach { bundle ->
                val name = bundle.getString("SCANNER_NAME")
                val connected = bundle.getBoolean("SCANNER_CONNECTION_STATE")
                val index = bundle.getInt("SCANNER_INDEX")
                val id = bundle.getString("SCANNER_IDENTIFIER")
                if (id != null && name != null) {
                    DWScannerMap.setScannerInfo(id, name, index, connected)
                    Log.d(DataWedgeHelper.TAG, "ScannerInfo: [name: $name, connected: $connected, index: $index, id: $id]")
                }
            }
            Log.d(DataWedgeHelper.TAG, "LIST SCANNER SUCCESS")
            callback(DWScannerMap.getScannerList())
        } else {
            Log.e(DataWedgeHelper.TAG, "LIST SCANNER FAILED: no dw status in intent")
            callback(emptyList())
        }
    }

    /**
     * https://techdocs.zebra.com/datawedge/latest/guide/api/softscantrigger/
     *
     * DATAWEDGE_DISABLED - FAILURE
     * INPUT_NOT_ENABLED - FAILURE
     * PARAMETER_INVALID - FAILURE
     * PROFILE_DISABLED - FAILURE
     */
    fun softScanTrigger(context: Context, option: DWAPI.SoftScanTriggerOptions) {
        callDWAPI(context,DWAPI.ActionExtraKeys.SOFT_SCAN_TRIGGER, option.value) { result ->
            result.onSuccess {
                Log.d(TAG, "softScanTrigger success")
            }.onFailure {
                Log.e(TAG, "softScanTrigger failed: ${it.message}")
            }
        }
    }

    fun sendSetDCPButtonIntent(context: Context, name: String, enabled: Boolean) {
        // DCP
        val dcpParams = Bundle()
        if (enabled) {
            dcpParams.putString(DWAPI.DCPParams.ENABLED, DWAPI.StringBoolean.TRUE.value)
        } else {
            dcpParams.putString(DWAPI.DCPParams.ENABLED, DWAPI.StringBoolean.FALSE.value)
        }
        dcpParams.putString(DWAPI.DCPParams.DOCK, DWAPI.DCPParams.DockOptions.BOTH)
        dcpParams.putString(DWAPI.DCPParams.MODE, DWAPI.DCPParams.ModeOptions.BUTTON)
        dcpParams.putString(DWAPI.DCPParams.HIGH_POS, "30")
        dcpParams.putString(DWAPI.DCPParams.LOW_POS, "70")
        dcpParams.putString(DWAPI.DCPParams.DRAG, "501")
        val dcpPluginBundle = Bundle()
        dcpPluginBundle.putString(DWAPI.Plugin.NAME, DWAPI.Plugin.Utilities.DCP.value)
        dcpPluginBundle.putString(DWAPI.BundleParams.RESET_CONFIG, DWAPI.StringBoolean.TRUE.value)
        dcpPluginBundle.putBundle(DWAPI.BundleParams.PARAM_LIST, dcpParams)
        // Main Config Bundle
        val bundle = Bundle()
        bundle.putString(DWAPI.Profile.NAME, name)
        bundle.putString(DWAPI.Profile.CONFIG_MODE, DWAPI.ConfigModeOptions.UPDATE.value)
        bundle.putString(DWAPI.Profile.ENABLED, DWAPI.StringBoolean.TRUE.value)
        if (enabled) {
            bundle.putBundle(DWAPI.Plugin.Utilities.DCP.value, dcpPluginBundle)
        } else {
            bundle.putBundle(DWAPI.Plugin.Utilities.DCP.value, null)
        }
        // Send Broadcast
        val intent = Intent(DWAPI.ActionNames.ACTION.value)
        intent.putExtra(DWConst.SET_CONFIG, bundle)
        context.sendOrderedBroadcast(intent, null)
    }

    private fun configOCRParams(bundle: Bundle): Bundle {
        // Decoder
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_OCR_A, DWAPI.StringBoolean.TRUE.value)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_OCR_B, DWAPI.StringBoolean.TRUE.value)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_MICR, DWAPI.StringBoolean.TRUE.value)
        bundle.putString(DWAPI.OCRParams.Decoder.ENABLE_US_CURRENCY, DWAPI.StringBoolean.TRUE.value)
        bundle.putString(DWAPI.OCRParams.Decoder.OCR_A_VARIANT, DWAPI.OCRParams.OCRAVariant.FULL_ASCII.string)
        bundle.putString(DWAPI.OCRParams.Decoder.OCR_B_VARIANT, DWAPI.OCRParams.OCRBVariant.ISBN_1.string)
        // Orientation
        bundle.putString(DWAPI.OCRParams.ORIENTATION, DWAPI.OCRParams.OrientationOptions.DEGREE_0.string)
        // Lines
        bundle.putString(DWAPI.OCRParams.LINES, DWAPI.OCRParams.LinesOptions.LINE_1.string)
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
        bundle.putString(DWAPI.OCRParams.CheckDigit.VALIDATION, DWAPI.OCRParams.CheckDigit.ValidationOptions.PRODUCT_ADD_LR.string)
        // Inverse
        bundle.putString(DWAPI.OCRParams.INVERSE, DWAPI.OCRParams.InverseOptions.AUTO.string)
        return bundle
    }



    fun setDCPButton(context: Context, name: String, enabled: Boolean) {
        sendSetDCPButtonIntent(context, name, enabled)
    }


}
