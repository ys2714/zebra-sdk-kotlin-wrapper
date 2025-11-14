package com.zebra.emdk_kotlin_wrapper.mx

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powermgr/
 *
 * */
@JvmOverloads
fun MXProfileProcessor.callPowerManagerFeature(option: MXBase.PowerManagerOptions, osZipFilePath: String? = null, callback: MXBase.ProcessProfileCallback? = null) {
    val map = mutableMapOf<String, String>()
    when (option) {
        MXBase.PowerManagerOptions.SLEEP_MODE,
        MXBase.PowerManagerOptions.REBOOT,
        MXBase.PowerManagerOptions.ENTERPRISE_RESET,
        MXBase.PowerManagerOptions.FACTORY_RESET,
        MXBase.PowerManagerOptions.FULL_DEVICE_WIPE -> {
            map[MXConst.ResetAction] = option.valueString()
            processProfileWithCallback(
                MXConst.PowerManagerResetXML,
                MXConst.PowerManagerReset,
                map,
                callback
            )
        }
        MXBase.PowerManagerOptions.OS_UPDATE -> {
            map[MXConst.ResetAction] = option.valueString()
            osZipFilePath?.let { map[MXConst.ZipFile] = it }
            processProfileWithCallback(
                MXConst.PowerManagerResetXML,
                MXConst.PowerManagerReset,
                map,
                callback
            )
        }
        MXBase.PowerManagerOptions.CREATE_PROFILE,
        MXBase.PowerManagerOptions.DO_NOTHING -> Unit
    }
}