package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import kotlinx.coroutines.suspendCancellableCoroutine

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
suspend fun DWAPI.sendSetConfigIntent(context: Context, extra: Bundle): Boolean = suspendCancellableCoroutine { continuation ->
    DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.SET_CONFIG, extra) { result ->
        result.onSuccess {
            continuation.resumeWith(Result.success(true))
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
suspend fun DWAPI.sendCreateProfileIntent(context: Context, name: String): Boolean = suspendCancellableCoroutine { continuation ->
    DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.CREATE_PROFILE,name) { result ->
        result.onSuccess {
            continuation.resumeWith(Result.success(true))
        }.onFailure {
            continuation.resumeWith(Result.failure(it))
        }
    }
}

suspend fun DWAPI.sendDeleteProfileIntent(context: Context, name: String): Boolean = suspendCancellableCoroutine { continuation ->
    DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, name) { result ->
        result.onSuccess {
            continuation.resumeWith(Result.success(true))
        }.onFailure {
            if (it.message == DWAPI.ResultCodes.PROFILE_NOT_FOUND.value) {
                continuation.resumeWith(Result.success(true))
            } else {
                continuation.resumeWith(Result.failure(it))
            }
        }
    }
}
