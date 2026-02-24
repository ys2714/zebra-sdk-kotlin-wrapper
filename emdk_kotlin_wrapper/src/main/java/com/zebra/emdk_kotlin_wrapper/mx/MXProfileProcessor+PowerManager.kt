package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powermgr/
 *
 * Reset Action
 * */
@JvmOverloads
internal fun MXProfileProcessor.callPowerManagerFeature(
    context: Context,
    option: MXBase.PowerManagerOptions,
    zipFile: String? = null,
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
                    MXConst.ResetAction to option.string
                ),
                delaySeconds,
                callback
            )
        }
        MXBase.PowerManagerOptions.OS_UPDATE,
        MXBase.PowerManagerOptions.OS_UPGRADE,
        MXBase.PowerManagerOptions.OS_DOWNGRADE -> {
            val filePath = zipFile ?: ""
            processProfileWithCallback(
                context,
                MXBase.ProfileXML.PowerManagerResetOSUpdate,
                MXBase.ProfileName.PowerManagerResetOSUpdate,
                mapOf(
                    MXConst.ResetAction to option.string,
                    MXConst.ZipFile to filePath
                ),
                delaySeconds,
                callback
            )
        }
        MXBase.PowerManagerOptions.OS_UPDATE_VERIFY -> {
            val filePath = zipFile ?: ""
            processProfileWithCallback(
                context,
                MXBase.ProfileXML.PowerManagerResetOSVerify,
                MXBase.ProfileName.PowerManagerResetOSVerify,
                mapOf(
                    MXConst.ResetAction to option.string,
                    MXConst.OsupdateVerifyFile to filePath
                ),
                delaySeconds,
                callback
            )
        }
        MXBase.PowerManagerOptions.CREATE_PROFILE,
        MXBase.PowerManagerOptions.DO_NOTHING -> Unit
    }
}

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powermgr/
 *
 * Recovery Mode Access
 *
 * IMPORTANT PERSISTENCE NOTE: This setting persists on the device ONLY if the Enterprise Reset / Factory Reset is initiated by a barcode.
 * Resetting the device through the Android Settings panel or by any other means removes all settings created for this feature.
 * */
@JvmOverloads
internal fun MXProfileProcessor.callPowerManagerRecoveryModeControlFeature(
    context: Context,
    option: MXBase.PowerManagerRecoveryModeAccessOptions,
    delaySeconds: Long = 0,
    callback: (MXBase.ErrorInfo?) -> Unit) {
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.PowerManagerRecoveryModeAccess,
        MXBase.ProfileName.PowerManagerRecoveryModeAccess,
        mapOf(
            MXConst.RecoveryModeAccess to option.string,
        ),
        delaySeconds,
        callback
    )
}