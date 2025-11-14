package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import com.zebra.emdk_kotlin_wrapper.utils.PackageUtils
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMConst
import com.zebra.emdk_kotlin_wrapper.zdm.ZDMTokenStore

object DWProfileProcessor {

    final const val TAG = "DWProfileProcessor"

    // https://techdocs.zebra.com/datawedge/15-0/guide/api/createprofile/
    fun bundleForCreateProfile(context: Context,
                               profileName: String,
                               resultOption: DWAPI.SendResultOptions,
                               commandId: String): Bundle {
        val jsonString = AssetsReader.readFileToStringWithParams(
            context,
            DWConst.CreateProfileJSON,
            mapOf(
                DWConst.SEND_RESULT to resultOption.value,
                DWConst.COMMAND_IDENTIFIER to commandId,
                DWConst.PROFILE_NAME to profileName,
            )
        )
        return JsonUtils.jsonToBundle(jsonString)
    }

    fun bundleForUpdateProfile(context: Context,
                               profileName: String): Bundle {
        val jsonString = AssetsReader.readFileToStringWithParams(
            context,
            DWConst.UpdateProfileJSON,
            mapOf(
                DWConst.CONFIG_MODE to DWAPI.ConfigModeOptions.CREATE_IF_NOT_EXIST.value,
                DWConst.PROFILE_NAME to profileName,
                DWConst.PROFILE_ENABLED to "true",
                DWConst.PACKAGE_NAME to context.packageName,
            )
        )
        val bundle = JsonUtils.jsonToBundle(jsonString)
        return bundle
    }

    fun bundleForSetConfig(context: Context, name: String, intentAction: String): Bundle {
        val jsonString = AssetsReader.readFileToStringWithParams(
           context,
           "set_config.json",
            params = mapOf(
                DWConst.PROFILE_NAME to name,
                DWConst.PACKAGE_NAME to context.packageName,
                DWConst.intent_action to intentAction
            )
        )
        return JsonUtils.jsonToBundle(jsonString)
    }
}