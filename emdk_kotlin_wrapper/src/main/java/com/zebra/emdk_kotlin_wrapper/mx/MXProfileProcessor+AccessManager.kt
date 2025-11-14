package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import com.zebra.emdk_kotlin_wrapper.utils.PackageUtils

/**
 * 
 *
 * */
fun MXProfileProcessor.grantZebraBluetoothInsightServiceAccessPermission() {

}

fun MXProfileProcessor.getAllDangerousPermissions(ctx: Context, callback: MXBase.ProcessProfileCallback) {
    val base64 = PackageUtils.getPackageSignature(ctx)
    val name = ctx.packageName
    callAccessManagerAllowPermission(
        MXBase.EPermissionType.ALL_DANGEROUS_PERMISSIONS.toString(),
        name,
        "",
        base64,
        callback
    )
}

fun MXProfileProcessor.getCallServicePermission(ctx: Context, serviceId: String, callback: MXBase.ProcessProfileCallback) {
    val base64 = PackageUtils.getPackageSignature(ctx)
    val name = ctx.packageName
    callAccessManagerAllowCallService(
        serviceId,
        name,
        base64,
        callback)
}

fun MXProfileProcessor.callAccessManagerAllowCallService(serviceIdentifier: String, callerPackageName: String, callerSignature: String, callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.ServiceIdentifier to serviceIdentifier,
        MXConst.CallerPackageName to callerPackageName,
        MXConst.CallerSignature to callerSignature
    )
    processProfileWithCallback(
        MXConst.AccessManagerAllowCallServiceXML,
        MXConst.AccessManagerAllowCallService,
        map,
        callback
    )
}

fun MXProfileProcessor.callAccessManagerAllowPermission(permissionName: String, appPackageName: String, appClassName: String, appSignature: String, callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.PermissionAccessPermissionName to permissionName,
        MXConst.PermissionAccessAction to "1", // 1: allow
        MXConst.PermissionAccessPackageName to appPackageName,
        MXConst.ApplicationClassName to appClassName,
        MXConst.PermissionAccessSignature to appSignature
    )
    processProfileWithCallback(
        MXConst.AccessManagerAllowPermissionXML,
        MXConst.AccessManagerAllowPermission,
        map,
        callback
    )
}

fun MXProfileProcessor.callAppManagerInstallAndStart(apkPath: String, packageName: String, mainActivity: String, callback: MXBase.ProcessProfileCallback) {
    val map = mapOf(
        MXConst.APK to apkPath,
        MXConst.Package to packageName,
        MXConst.Class to mainActivity
    )
    processProfileWithCallback(
        MXConst.AppManagerInstallAndStartXML,
        MXConst.AppManagerInstallAndStart,
        map,
        callback
    )
}