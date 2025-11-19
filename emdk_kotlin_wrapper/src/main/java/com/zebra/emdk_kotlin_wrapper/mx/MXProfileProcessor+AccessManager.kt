package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.utils.PackageUtils

/**
 * 
 *
 * */
internal fun MXProfileProcessor.grantZebraBluetoothInsightServiceAccessPermission() {

}

internal fun MXProfileProcessor.getAllDangerousPermissions(
    context: Context,
    callback: MXBase.ProcessProfileCallback) {
    val base64 = PackageUtils.getPackageSignature(context)
    val name = context.packageName
    callAccessManagerAllowPermission(
        context,
        MXBase.EPermissionType.ALL_DANGEROUS_PERMISSIONS.string,
        name,
        "",
        base64,
        callback
    )
}

internal fun MXProfileProcessor.getCallServicePermission(
    context: Context,
    serviceId: String,
    callback: MXBase.ProcessProfileCallback) {
    val base64 = PackageUtils.getPackageSignature(context)
    val name = context.packageName
    callAccessManagerAllowCallService(
        context,
        serviceId,
        name,
        base64,
        callback)
}

internal fun MXProfileProcessor.callAccessManagerAllowCallService(
    context: Context,
    serviceIdentifier: String,
    callerPackageName: String,
    callerSignature: String,
    callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.ServiceIdentifier to serviceIdentifier,
        MXConst.CallerPackageName to callerPackageName,
        MXConst.CallerSignature to callerSignature
    )
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.AccessManagerAllowCallService,
        MXBase.ProfileName.AccessManagerAllowCallService,
        map,
        callback
    )
}

internal fun MXProfileProcessor.callAccessManagerAllowPermission(
    context: Context,
    permissionName: String,
    appPackageName: String,
    appClassName: String,
    appSignature: String,
    callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.PermissionAccessPermissionName to permissionName,
        MXConst.PermissionAccessAction to "1", // 1: allow
        MXConst.PermissionAccessPackageName to appPackageName,
        MXConst.ApplicationClassName to appClassName,
        MXConst.PermissionAccessSignature to appSignature
    )
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.AccessManagerAllowPermission,
        MXBase.ProfileName.AccessManagerAllowPermission,
        map,
        callback
    )
}

internal fun MXProfileProcessor.callAppManagerInstallAndStart(
    context: Context,
    apkPath: String,
    packageName: String,
    mainActivity: String,
    callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.APK to apkPath,
        MXConst.Package to packageName,
        MXConst.Class to mainActivity
    )
    processProfileWithCallback(
        context,
        MXBase.ProfileXML.AppManagerInstallAndStart,
        MXBase.ProfileName.AppManagerInstallAndStart,
        map,
        callback
    )
}