package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils

fun DWProfileProcessor.bundleForSimpleScannerConfig(context: Context, name: String, intentAction: String): Bundle {
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