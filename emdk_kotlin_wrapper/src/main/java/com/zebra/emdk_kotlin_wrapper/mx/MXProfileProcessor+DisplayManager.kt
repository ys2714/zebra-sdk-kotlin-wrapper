package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/displaymgr/
 *
 * Screen Shot Enable/Disable
 * Parm Name: ScreenShotUsage
 *
 * Options:
 * 0 - Do Nothing
 * 1 - Enable
 * 2 - Disable
 * */
internal fun MXProfileProcessor.setScreenShotUsage(context: Context, usage: MXBase.ScreenShotUsage, callback: MXBase.ProcessProfileCallback) {
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.DisplayManagerDisableScreenShot,
        MXBase.ProfileName.DisplayManagerDisableScreenShot,
        mapOf(
            MXConst.ScreenShotUsage to usage.string
        ),
        callback
    )
}

internal fun MXProfileProcessor.enableScreenShotFeature(context: Context, callback: MXBase.ProcessProfileCallback) {
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.DisplayManagerDisableScreenShot,
        MXBase.ProfileName.DisplayManagerDisableScreenShot,
        mapOf(
            MXConst.ScreenShotUsage to MXBase.ScreenShotUsage.ENABLE.string
        ),
        callback
    )
}