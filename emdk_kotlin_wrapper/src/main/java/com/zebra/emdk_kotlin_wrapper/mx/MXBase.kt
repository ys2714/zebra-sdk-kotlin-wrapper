package com.zebra.emdk_kotlin_wrapper.mx

import android.text.TextUtils

class MXBase {

    companion object {
        private val TAG = MXBase::class.java.simpleName
    }

    interface FetchOEMInfoCallback {
        fun onSuccess(result: String)
        fun onError()
    }

    interface ProcessProfileCallback {
        fun onSuccess(profileName: String)
        fun onError(errorInfo: ErrorInfo)
    }

    interface EventListener {
        fun onEMDKSessionOpened()
        fun onEMDKSessionClosed()
        fun onEMDKError(errorInfo: ErrorInfo)
    }

    data class ErrorInfo(
        // Contains the parm-error name (sub-feature that has error)
        var errorName: String = "",
        // Contains the characteristic-error type (Root feature that has error)
        var errorType: String = "",
        // contains the error description for parm or characteristic error.
        var errorDescription: String = ""
    ) {
        fun buildFailureMessage(): String {
            return when {
                !TextUtils.isEmpty(errorName) && !TextUtils.isEmpty(errorType) -> {
                    "$errorName :\n$errorType :\n$errorDescription"
                }
                !TextUtils.isEmpty(errorName) -> {
                    "$errorName :\n$errorDescription"
                }
                else -> {
                    "$errorType :\n$errorDescription"
                }
            }
        }
    }

    /**
     * Specifies the Power Manager action to be performed. The values correspond
     * to options available in the MX Power Manager profile.
     *
     * - `CREATE_PROFILE` (-1): Creates the profile without taking action.
     * - `DO_NOTHING` (0): No power management action is taken.
     * - `SLEEP_MODE` (1): Puts the device into sleep mode.
     * - `REBOOT` (4): Reboots the device.
     * - `ENTERPRISE_RESET` (5): Performs an enterprise reset.
     * - `FACTORY_RESET` (6): Performs a factory reset.
     * - `FULL_DEVICE_WIPE` (7): Performs a full device wipe.
     * - `OS_UPDATE` (8): Initiates an OS update.
     */
    enum class PowerManagerOptions(val value: Int) {
        CREATE_PROFILE(-1),
        DO_NOTHING(0),
        SLEEP_MODE(1),
        REBOOT(4),
        ENTERPRISE_RESET(5),
        FACTORY_RESET(6),
        FULL_DEVICE_WIPE(7),
        OS_UPDATE(8);

        fun valueString(): String {
            return value.toString()
        }
    }

    enum class EPermissionType(private val stringContent: String) {
        ACCESS_NOTIFICATIONS("android.permission.ACCESS_NOTIFICATIONS"),
        PACKAGE_USAGE_STATS("android.permission.PACKAGE_USAGE_STATS"),
        SYSTEM_ALERT_WINDOW("android.permission.SYSTEM_ALERT_WINDOW"),
        GET_APP_OPS_STATS("android.permission.GET_APP_OPS_STATS"),
        BATTERY_STATS("android.permission.BATTERY_STATS"),
        MANAGE_EXTERNAL_STORAGE("android.permission.MANAGE_EXTERNAL_STORAGE"),
        BIND_NOTIFICATION_LISTENER("android.permission.BIND_NOTIFICATION_LISTENER"),
        READ_LOGS("android.permission.READ_LOGS"),
        ALL_DANGEROUS_PERMISSIONS("ALL_DANGEROUS_PERMISSIONS"),
        ACCESS_RX_LOGGER("com.zebra.permission.ACCESS_RXLOGGER"),
        SCHEDULE_EXACT_ALARM("android.permission.SCHEDULE_EXACT_ALARM"),
        WRITE_SETTINGS("android.permission.WRITE_SETTINGS"),
        ACCESSIBILITY_SERVICE("ACCESSIBILITY_SERVICE_ACCESS");

        override fun toString(): String {
            return stringContent
        }

        companion object {
            private val lookup = EPermissionType.entries.associateBy { it.stringContent }
            fun fromString(permissionType: String): EPermissionType? {
                return lookup[permissionType]
            }
        }
    }
}
