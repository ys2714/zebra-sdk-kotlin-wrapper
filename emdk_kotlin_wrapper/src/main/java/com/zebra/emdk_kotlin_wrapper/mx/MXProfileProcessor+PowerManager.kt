package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powermgr/
 *
 * */
@JvmOverloads
fun MXProfileProcessor.callPowerManagerFeature(
    context: Context,
    profileManager: ProfileManager,
    option: MXBase.PowerManagerOptions,
    osZipFilePath: String? = null,
    callback: MXBase.ProcessProfileCallback? = null) {
    val map = mutableMapOf<String, String>()
    when (option) {
        MXBase.PowerManagerOptions.SLEEP_MODE,
        MXBase.PowerManagerOptions.REBOOT,
        MXBase.PowerManagerOptions.ENTERPRISE_RESET,
        MXBase.PowerManagerOptions.FACTORY_RESET,
        MXBase.PowerManagerOptions.FULL_DEVICE_WIPE -> {
            map[MXConst.ResetAction] = option.valueString()
            processProfileWithCallback(
                context,
                profileManager,
                MXBase.ProfileXML.PowerManagerReset,
                MXBase.ProfileName.PowerManagerReset,
                map,
                callback
            )
        }
        MXBase.PowerManagerOptions.OS_UPDATE -> {
            map[MXConst.ResetAction] = option.valueString()
            osZipFilePath?.let { map[MXConst.ZipFile] = it }
            processProfileWithCallback(
                context,
                profileManager,
                MXBase.ProfileXML.PowerManagerReset,
                MXBase.ProfileName.PowerManagerReset,
                map,
                callback
            )
        }
        MXBase.PowerManagerOptions.CREATE_PROFILE,
        MXBase.PowerManagerOptions.DO_NOTHING -> Unit
    }
}