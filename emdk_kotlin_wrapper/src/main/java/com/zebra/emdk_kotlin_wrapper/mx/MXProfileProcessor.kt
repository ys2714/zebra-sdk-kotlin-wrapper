package com.zebra.emdk_kotlin_wrapper.mx

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Xml
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.oeminfo.OEMInfoHelper
import com.zebra.emdk_kotlin_wrapper.utils.PackageManagerHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParserException
import java.io.StringReader
import kotlinx.coroutines.*

class MXProfileProcessor(
    private val context: Context,
    private val profileManager: ProfileManager) {

    fun fetchSerialNumberInBackground(ctx: Context, callback: MXBase.FetchOEMInfoCallback) {
        fetchOEMInfoInBackground(ctx, MXConst.SERIAL_URI, callback)
    }

    fun fetchIMEIInBackground(ctx: Context, callback: MXBase.FetchOEMInfoCallback) {
        fetchOEMInfoInBackground(ctx, MXConst.IMEI_URI, callback)
    }

    fun fetchOEMInfoInBackground(ctx: Context, serviceId: String, callback: MXBase.FetchOEMInfoCallback) {
        Thread {
            val serial = OEMInfoHelper.getOEMInfo(ctx, serviceId)
            if (serial == null) {
                getCallServicePermission(ctx, serviceId, object : MXBase.ProcessProfileCallback {
                    override fun onSuccess(profileName: String) {
                        val result = OEMInfoHelper.getOEMInfo(ctx, serviceId)
                        if (result != null) {
                            callback.onSuccess(result)
                        } else {
                            callback.onError()
                        }
                    }

                    override fun onError(errorInfo: MXBase.ErrorInfo) {
                        callback.onError()
                    }
                })
            } else {
                Handler(Looper.getMainLooper()).post { callback.onSuccess(serial) }
            }
        }.start()
    }

    fun getAllDangerousPermissions(ctx: Context, callback: MXBase.ProcessProfileCallback) {
        val base64 = PackageManagerHelper.getPackageSignature(ctx)
        val name = ctx.packageName
        callAccessManagerAllowPermission(
            MXBase.EPermissionType.ALL_DANGEROUS_PERMISSIONS.toString(),
            name,
            "",
            base64,
            callback
        )
    }

    fun getCallServicePermission(ctx: Context, serviceId: String, callback: MXBase.ProcessProfileCallback) {
        val base64 = PackageManagerHelper.getPackageSignature(ctx)
        val name = ctx.packageName
        callAccessManagerAllowCallService(serviceId, name, base64, callback)
    }

    fun callAccessManagerAllowCallService(serviceIdentifier: String, callerPackageName: String, callerSignature: String, callback: MXBase.ProcessProfileCallback) {
        val map = mapOf(
            MXConst.serviceIdentifier to serviceIdentifier,
            MXConst.callerPackageName to callerPackageName,
            MXConst.callerSignature to callerSignature
        )
        processProfile(MXConst.AccessManagerAllowCallServiceXML, MXConst.AccessManagerAllowCallService, map, callback)
    }

    fun callAccessManagerAllowPermission(permissionName: String, appPackageName: String, appClassName: String, appSignature: String, callback: MXBase.ProcessProfileCallback) {
        val map = mapOf(
            MXConst.permissionAccessPermissionName to permissionName,
            MXConst.permissionAccessAction to "1", // 1: allow
            MXConst.permissionAccessPackageName to appPackageName,
            MXConst.applicationClassName to appClassName,
            MXConst.permissionAccessSignature to appSignature
        )
        processProfile(MXConst.AccessManagerAllowPermissionXML, MXConst.AccessManagerAllowPermission, map, callback)
    }

    fun callAppManagerInstallAndStart(apkPath: String, packageName: String, mainActivity: String, callback: MXBase.ProcessProfileCallback) {
        val map = mapOf(
            MXConst.apkFilePath to apkPath,
            MXConst.appPackageName to packageName,
            MXConst.mainActivityClass to mainActivity
        )
        processProfile(MXConst.AppManagerInstallAndStartXML, MXConst.AppManagerInstallAndStart, map, callback)
    }

    @JvmOverloads
    fun callPowerManagerFeature(option: MXBase.PowerManagerOptions, osZipFilePath: String? = null, callback: MXBase.ProcessProfileCallback? = null) {
        val map = mutableMapOf<String, String>()
        when (option) {
            MXBase.PowerManagerOptions.SLEEP_MODE,
            MXBase.PowerManagerOptions.REBOOT,
            MXBase.PowerManagerOptions.ENTERPRISE_RESET,
            MXBase.PowerManagerOptions.FACTORY_RESET,
            MXBase.PowerManagerOptions.FULL_DEVICE_WIPE -> {
                map[MXConst.resetAction] = option.valueString()
                processProfile(MXConst.PowerManagerResetXML, MXConst.PowerManagerReset, map, callback)
            }
            MXBase.PowerManagerOptions.OS_UPDATE -> {
                map[MXConst.resetAction] = option.valueString()
                osZipFilePath?.let { map[MXConst.zipFile] = it }
                processProfile(MXConst.PowerManagerResetXML, MXConst.PowerManagerReset, map, callback)
            }
            MXBase.PowerManagerOptions.CREATE_PROFILE,
            MXBase.PowerManagerOptions.DO_NOTHING -> Unit
        }
    }

    @JvmOverloads
    fun processProfile(profileResId: String, profileName: String, params: Map<String, String>? = null, callback: MXBase.ProcessProfileCallback? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val command = XMLReader.readXmlFileToStringWithParams(context, profileResId, params)
            val results = profileManager.processProfile(profileName, ProfileManager.PROFILE_FLAG.SET, arrayOf(command))
            if (results == null) {
                callback?.onError(MXBase.ErrorInfo(errorName = MXProfileProcessor.Companion.TAG, errorType = "ProfileError", errorDescription = "Operation returned null result."))
                return@launch
            }
            when (results.statusCode) {
                EMDKResults.STATUS_CODE.SUCCESS -> callback?.onSuccess(profileName)
                EMDKResults.STATUS_CODE.CHECK_XML -> {
                    try {
                        val parser = Xml.newPullParser().apply {
                            setInput(StringReader(results.statusString))
                        }
                        val errorInfo = XMLReader.parseXML(parser)
                        if (errorInfo == null) {
                            callback?.onSuccess(profileName)
                        } else {
                            callback?.onError(errorInfo)
                        }
                    } catch (e: XmlPullParserException) {
                        Log.e(MXProfileProcessor.Companion.TAG, "Failed to parse XML response", e)
                        callback?.onError(MXBase.ErrorInfo(MXProfileProcessor.Companion.TAG, "XmlPullParserException", e.message ?: "Unknown parser exception"))
                    }
                }
                else -> {
                    Log.e(MXProfileProcessor.Companion.TAG, "Profile processing failed with status code: ${results.statusCode}")
                    callback?.onError(MXBase.ErrorInfo(MXProfileProcessor.Companion.TAG, "ProfileError", "Profile processing failed with status code: ${results.statusCode}"))
                }
            }
        }
    }

    companion object {
        private val TAG = MXProfileProcessor::class.java.simpleName
    }
}
