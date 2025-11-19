package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils

internal fun DWProfileProcessor.bundleForWorkflow(context: Context,
                      profileName: String): Bundle {
    val jsonString = AssetsReader.readFileToStringWithParams(
        context,
        DWConst.WorkflowPluginJSON,
        mapOf(
            DWConst.PROFILE_NAME to profileName,
            DWConst.PROFILE_ENABLED to "true",
            DWConst.PACKAGE_NAME to context.packageName
        )
    )
    return JsonUtils.jsonToBundle(jsonString)
}