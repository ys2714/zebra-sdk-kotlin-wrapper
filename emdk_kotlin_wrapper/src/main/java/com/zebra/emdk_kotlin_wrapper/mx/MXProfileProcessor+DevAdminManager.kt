package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager


/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/mx/devadmin/
 *
 * Screen Lock Type
 * Parm Name: ScreenLockType
 *
 * Options:
 * 0 - Do not change: This value (or the absence of this parm from the XML) will cause no change to the Screen Lock Type; any previously selected setting will be retained.
 * 1 - Swipe: Causes the Swipe screen-lock to be displayed whenever the Lock Screen is invoked.
 * 2 - Pattern: Causes the Pattern screen-lock to be displayed whenever the Lock Screen is invoked.
 * 3 - Pin: Causes the numerical "Pin" screen-lock to be displayed whenever the Lock Screen is invoked.
 * 4 - Password: Causes the Password screen-lock to be displayed whenever the Lock Screen is invoked.
 * 5 - None: Prevents the display of any Lock Screen at any time.
 * */

fun MXProfileProcessor.setScreenLockType(
    context: Context,
    profileManager: ProfileManager,
    type: MXBase.ScreenLockType,
    callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.ScreenLockType to type.string,
        MXConst.DevAdminPkg to context.applicationContext.packageName,
        MXConst.DevAdminClass to MXProfileProcessor.DevAdminReceiver::class.simpleName!!
    )
    processProfileWithCallback(
        context,
        profileManager,
        MXBase.ProfileXML.DevAdminManagerDisableLockScreen,
        MXBase.ProfileName.DevAdminManagerDisableLockScreen,
        map,
        callback
    )
}