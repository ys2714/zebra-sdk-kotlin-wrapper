package com.zebra.emdk_kotlin_wrapper.mx

object MXConst {
    private val TAG = MXConst::class.java.simpleName

    // Content Provider Keys
    private const val IMEI = "imei"
    private const val BUILD_SERIAL = "build_serial"

    const val SERIAL_URI = "content://oem_info/oem.zebra.secure/build_serial"
    const val IMEI_URI = "content://oem_info/wan/imei"

    // Profile XML file names in assets
    const val AccessManagerAllowPermissionXML = "profile_access_manager_allow_permission.xml"
    const val AccessManagerAllowCallServiceXML = "profile_access_manager_allow_call_service.xml"
    const val AppManagerInstallAndStartXML = "profile_app_manager_install_and_start.xml"
    const val PowerManagerResetXML = "profile_power_manager_reset.xml"

    // Profile Names
    const val AccessManagerAllowPermission = "AccessManagerAllowPermission"
    const val AccessManagerAllowCallService = "AccessManagerAllowCallService"
    const val AppManagerInstallAndStart = "AppManagerInstallAndStart"
    const val PowerManagerReset = "PowerManagerReset"

    // Profile Parameters
    const val resetAction = "resetAction"
    const val zipFile = "zipFile"
    const val serviceIdentifier = "serviceIdentifier"
    const val callerPackageName = "callerPackageName"
    const val callerSignature = "callerSignature"
    const val apkFilePath = "apkFilePath"
    const val appPackageName = "appPackageName"
    const val mainActivityClass = "mainActivityClass"

    /**
     * Defines the action to be taken for a permission request.
     * - **0: Do Nothing** - No change; prior settings are retained. (MX 10.0+, Android API 26+)
     * - **1: Allow** - Grants the permission to the app. (MX 10.0+, Android API 26+)
     * - **2: Deny** - Denies the permission to the app. (MX 10.0+, Android API 26+)
     * - **3: Allow User to choose** - Prompts the user to grant or deny the permission. This is the Android default. (MX 10.0+, Android API 26+)
     * - **4: Verify** - Verifies whether the permission is granted.
     */
    const val permissionAccessAction = "permissionAccessAction"
    const val permissionAccessPackageName = "permissionAccessPackageName"
    const val applicationClassName = "applicationClassName"
    const val permissionAccessPermissionName = "permissionAccessPermissionName"
    const val permissionAccessSignature = "permissionAccessSignature"
}
