package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powermgr/
 *
 * */
@JvmOverloads
internal fun MXProfileProcessor.callPowerManagerFeature(
    context: Context,
    option: MXBase.PowerManagerOptions,
    osZipFilePath: String? = null,
    delaySeconds: Long = 0,
    callback: (MXBase.ErrorInfo?) -> Unit) {
    when (option) {
        MXBase.PowerManagerOptions.SLEEP_MODE,
        MXBase.PowerManagerOptions.REBOOT,
        MXBase.PowerManagerOptions.ENTERPRISE_RESET,
        MXBase.PowerManagerOptions.FACTORY_RESET,
        MXBase.PowerManagerOptions.FULL_DEVICE_WIPE -> {
            processProfileWithCallback(
                context,
                MXBase.ProfileXML.PowerManagerReset,
                MXBase.ProfileName.PowerManagerReset,
                mapOf(
                    MXConst.ResetAction to option.string,
                    MXConst.ZipFile to MXConst.ignoredValue
                ),
                delaySeconds,
                callback
            )
        }
        MXBase.PowerManagerOptions.OS_UPDATE -> {
            val path: String = osZipFilePath?.also { it } ?: run { MXConst.ignoredValue }
            processProfileWithCallback(
                context,
                MXBase.ProfileXML.PowerManagerReset,
                MXBase.ProfileName.PowerManagerReset,
                mapOf(
                    MXConst.ResetAction to option.string,
                    MXConst.ZipFile to path
                ),
                delaySeconds,
                callback
            )
        }
        MXBase.PowerManagerOptions.CREATE_PROFILE,
        MXBase.PowerManagerOptions.DO_NOTHING -> Unit
    }
}