package com.zebra.emdk_kotlin_wrapper.dw

import android.content.Context
import android.os.Bundle
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils

internal object DWProfileProcessor {

    final const val TAG = "DWProfileProcessor"

    fun bundleForBindProfile(context: Context, profileName: String, packageName: String): Bundle {
        val jsonString = AssetsReader.readFileToStringWithParams(
            context,
            DWConst.BindProfileJSON,
            mapOf(
                DWConst.PROFILE_NAME to profileName,
                DWConst.PROFILE_ENABLED to "true",
                DWConst.PACKAGE_NAME to packageName
            )
        )
        val bundle = JsonUtils.jsonToBundle(jsonString)
        return bundle
    }
}