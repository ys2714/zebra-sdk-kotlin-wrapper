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
    ) : Throwable() {
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

    enum class ProfileXML(val value: String) {
        None("None"),
        AccessManagerAllowPermission("profile_access_manager_allow_permission.xml"),
        AccessManagerAllowCallService("profile_access_manager_allow_call_service.xml"),
        AppManagerInstallAndStart("profile_app_manager_install_and_start.xml"),
        PowerManagerReset("profile_power_manager_reset.xml"),
        ClockSet("profile_clock_set.xml"),
        ClockResetAuto("profile_clock_reset_auto.xml"),
        DevAdminManagerDisableLockScreen("profile_dev_admin_manager_disable_lock_screen.xml"),
        DisplayManagerDisableScreenShot("profile_display_manager_disable_screenshot.xml"),
        PowerKeyManagerSetPowerOffState("profile_powerkey_manager_set_poweroff_state.xml"),
        KeymappingManagerSetKeySendIntent("profile_keymapping_manager_set_key_send_intent.xml"),
        KeymappingManagerSetAllToDefault("profile_keymapping_manager_set_all_to_default.xml"),
        DataWedgeManagerImportProfile("profile_datawedge_manager_import_profile.xml"),
        FileManagerCopyEmbeddedFreeFormOCR("profile_file_manager_copy_embedded_free_form_ocr.xml");

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.WARNING)
        override fun toString(): String {
            // throw RuntimeException("Not Implemented")
            return value
        }

        val string: String
            get() = value
    }

    enum class ProfileName(private val value: String) {
        AccessManagerAllowPermission("AccessManagerAllowPermission"),
        AccessManagerAllowCallService("AccessManagerAllowCallService"),
        AppManagerInstallAndStart("AppManagerInstallAndStart"),
        PowerManagerReset("PowerManagerReset"),
        ClockSet("ClockSet"),
        ClockResetAuto("ClockResetAuto"),
        DevAdminManagerDisableLockScreen("DevAdminManagerDisableLockScreen"),
        DisplayManagerDisableScreenShot("DisplayManagerDisableScreenShot"),
        PowerKeyManagerSetPowerOffState("PowerKeyManagerSetPowerOffState"),
        KeymappingManagerSetKeySendIntent("KeymappingManagerSetKeySendIntent"),
        KeymappingManagerSetAllToDefault("KeymappingManagerSetAllToDefault"),
        DataWedgeManagerImportProfile("DataWedgeManagerImportProfile"),
        FileManagerCopyEmbeddedFreeFormOCR("FileManagerCopyEmbeddedFreeFormOCR");

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.WARNING)
        override fun toString(): String {
            // throw RuntimeException("Not Implemented")
            return value
        }

        val string: String
            get() = value
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

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()
    }

    enum class EPermissionType(private val value: String) {
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

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()

        companion object {
            private val lookup = EPermissionType.entries.associateBy { it.value }
            fun fromString(permissionType: String): EPermissionType? {
                return lookup[permissionType]
            }
        }
    }

    /**
     * Specifies the type of screen lock to be used.
     *
     * - `DO_NOT_CHANGE` (0): This value (or the absence of this parm from the XML) will cause no change to the Screen Lock Type; any previously selected setting will be retained.
     * - `SWIPE` (1): Causes the Swipe screen-lock to be displayed whenever the Lock Screen is invoked.
     * - `PATTERN` (2): Causes the Pattern screen-lock to be displayed whenever the Lock Screen is invoked.
     * - `PIN` (3): Causes the numerical "Pin" screen-lock to be displayed whenever the Lock Screen is invoked.
     * - `PASSWORD` (4): Causes the Password screen-lock to be displayed whenever the Lock Screen is invoked.
     * - `NONE` (5): Prevents the display of any Lock Screen at any time.
     */
    enum class ScreenLockType(val value: Int) {
        DO_NOT_CHANGE(0),
        SWIPE(1),
        PATTERN(2),
        PIN(3),
        PASSWORD(4),
        NONE(5);

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()
    }

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
    enum class ScreenShotUsage(val value: Int) {
        DO_NOTHING(0),
        ENABLE(1),
        DISABLE(2);

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()
    }

    /**
     * https://techdocs.zebra.com/emdk-for-android/14-0/mx/powerkeymgr/
     *
     * Power-off Button Show/Hide
     * Parm Name: PowerOffState
     *
     * Options:
     * 0 - Do not change: This value (or the absence of this parm from the XML) causes no change to the Power-off Menu; any previously selected setting is retained.
     * 1 - Show: Enables the Power-off button to be shown after the device power key is long-pressed.
     * 2 - Hide: Prevents the Power-off button from being shown after the device power key is long-pressed.
     * */
    enum class ShowHideState(val value: Int) {
        DO_NOT_CHANGE(0),
        SHOW(1),
        HIDE(2);

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()
    }

    /**
     * https://techdocs.zebra.com/mx/keymappingmgr/#key-identifier
     *
     * */
    enum class KeyIdentifiers(val value: String) {
        VOLUMEUP("VOLUMEUP"),
        VOLUMEDOWN("VOLUMEDOWN"),
        SCAN("SCAN"),
        GRIP_TRIGGER("GRIP_TRIGGER"),
        GRIP_TRIGGER_2("GRIP_TRIGGER_2"),
        LEFT_TRIGGER_1("LEFT_TRIGGER_1"),
        LEFT_TRIGGER_2("LEFT_TRIGGER_2"),
        RIGHT_TRIGGER_1("RIGHT_TRIGGER_1"),
        RIGHT_TRIGGER_2	("RIGHT_TRIGGER_2"),
        LEFT_TRIGGER("LEFT_TRIGGER"),
        RIGHT_TRIGGER("RIGHT_TRIGGER"),
        CENTER_TRIGGER("CENTER_TRIGGER"),
        GUN_TRIGGER("GUN_TRIGGER"),
        HEADSET_HOOK("HEADSET_HOOK"),
        BACK("BACK"),
        HOME("HOME"),
        MENU("MENU"),
        RECENT("RECENT"),
        POWER("POWER"),
        REAR_BUTTON("REAR_BUTTON"),
        LEFT_EXTERNAL_TRIGGER("LEFT_EXTERNAL_TRIGGER"),
        RIGHT_EXTERNAL_TRIGGER("RIGHT_EXTERNAL_TRIGGER"),
        BLUETOOTH_REMOTE_TRIGGER_1("BLUETOOTH_REMOTE_TRIGGER_1"),
        BLUETOOTH_REMOTE_TRIGGER_2("BLUETOOTH_REMOTE_TRIGGER_2"),
        DO_NOT_DISTURB("DO_NOT_DISTURB"), //WS50
        TOP_BUTTON("TOP_BUTTON"), //EM45
        RIGHT_BUTTON("RIGHT_BUTTON"), //EM45
        CHANNEL_SWITCH("CHANNEL_SWITCH"), //FR55
        ALERT_BUTTON("ALERT_BUTTON"), //FR55
        DURESS("DURESS"); //emergency call button

        override fun toString(): String {
            return string
        }

        val string: String
            get() = value.toString()
    }

    /**
     * https://techdocs.zebra.com/mx/keymappingmgr/#key-code
     *
     * */
    enum class KeyCodes(val value: Int) {
        VOLUMEUP(24),
        VOLUMEDOWN(25),
        SCAN(10036),
        LEFT_TRIGGER_1(102),
        RIGHT_TRIGGER_1(103),
        LEFT_TRIGGER_2(104),
        RIGHT_TRIGGER_2	(105),
        HEADSET_HOOK(79),
        BACK(4),
        HOME(3),
        MENU(82),
        POWER(26),
        DO_NOT_DISTURB(10043), //WS50
        CHANNEL_SWITCH(10044), //FR55
        DURESS(10045); //emergency call button

        @Deprecated("please use .string instead", ReplaceWith("string"), level = DeprecationLevel.ERROR)
        override fun toString(): String {
            throw RuntimeException("Not Implemented")
        }

        val string: String
            get() = value.toString()
    }
}

class DataWedgeManager {
}