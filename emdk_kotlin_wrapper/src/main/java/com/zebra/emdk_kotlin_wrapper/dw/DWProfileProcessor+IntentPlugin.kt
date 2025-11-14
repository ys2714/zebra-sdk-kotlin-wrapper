package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils

fun DWProfileProcessor.bundleForIntentPlugin(context: Context,
                          profileName: String,
                          intentAction: String,
                          deliveryOptions: DWAPI.IntentDeliveryOptions): Bundle {
    val jsonString = AssetsReader.readFileToStringWithParams(
        context,
        DWConst.IntentPluginJSON,
        mapOf(
            DWConst.CONFIG_MODE to DWAPI.ConfigModeOptions.UPDATE.value,
            DWConst.PROFILE_NAME to profileName,
            DWConst.PROFILE_ENABLED to "true",
            DWConst.PACKAGE_NAME to context.packageName,
            DWConst.intent_output_enabled to "true",
            DWConst.intent_action to intentAction,
            DWConst.intent_category to "android.intent.category.DEFAULT",
            DWConst.intent_delivery to deliveryOptions.string
        )
    )
    return JsonUtils.jsonToBundle(jsonString)
}
