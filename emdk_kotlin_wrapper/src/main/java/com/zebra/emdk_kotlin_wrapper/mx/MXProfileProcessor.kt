package com.zebra.emdk_kotlin_wrapper.mx

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.util.Log
import android.util.Xml
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.ProfileManager
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParserException
import java.io.StringReader
import kotlinx.coroutines.*

/**
 * https://techdocs.zebra.com/emdk-for-android/14-0/guide/profile-manager-guides/
 *
 * */
object MXProfileProcessor {

    private val TAG = MXProfileProcessor::class.java.simpleName

    var foregroundScope = CoroutineScope(Dispatchers.Main + Job())
    var backgroundScope = CoroutineScope(Dispatchers.IO + Job())

    fun processProfileWithCallback(context: Context,
                                   profileManager: ProfileManager,
                                   fileName: MXBase.ProfileXML,
                                   profileName: MXBase.ProfileName,
                                   params: Map<String, String>? = null,
                                   callback: MXBase.ProcessProfileCallback?) {
        backgroundScope.launch {
            val errorInfo: Deferred<MXBase.ErrorInfo?> = processProfile(
                context,
                profileManager,
                fileName,
                profileName,
                params)
            val info = errorInfo.await()
            foregroundScope.launch {
                if (callback != null) {
                    if (info == null) {
                        callback.onSuccess(profileName.toString())
                    } else {
                        callback.onError(info)
                    }
                }
            }
        }
    }

    private suspend fun processProfile(context: Context,
                                       profileManager: ProfileManager,
                                       fileName: MXBase.ProfileXML,
                                       profileName: MXBase.ProfileName,
                                       params: Map<String, String>? = null) : Deferred<MXBase.ErrorInfo?> {
        return backgroundScope.async {
            // create profile if not exist
            val createProfileResult = profileManager.processProfile(
                profileName.toString(),
                ProfileManager.PROFILE_FLAG.SET,
                null as Array<String>?
            )

            val command = AssetsReader.readFileToStringWithParams(context, fileName.toString(), params)

            val results = profileManager.processProfile(
                profileName.toString(),
                ProfileManager.PROFILE_FLAG.SET,
                arrayOf(command)
            ) ?: return@async MXBase.ErrorInfo(
                errorName = MXProfileProcessor.TAG,
                errorType = "ProfileError",
                errorDescription = "Operation returned null result."
            )
            when (results.statusCode) {
                EMDKResults.STATUS_CODE.SUCCESS -> {
                    return@async null
                }
                EMDKResults.STATUS_CODE.CHECK_XML -> {
                    try {
                        val parser = Xml.newPullParser().apply {
                            setInput(StringReader(results.statusString))
                        }
                        return@async XMLParser.parseXML(parser)
                    } catch (e: XmlPullParserException) {
                        Log.e(MXProfileProcessor.TAG, "Failed to parse XML response", e)
                        return@async MXBase.ErrorInfo(
                            MXProfileProcessor.TAG,
                            "XmlPullParserException",
                            e.message ?: "Unknown parser exception")
                    }
                }
                else -> {
                    Log.e(MXProfileProcessor.TAG, "failed with status code: ${results.statusCode}")
                    return@async MXBase.ErrorInfo(
                        MXProfileProcessor.TAG,
                        results.statusString,
                        results.extendedStatusMessage)
                }
            }
        }
    }

    /**
     * https://developer.android.com/reference/android/app/admin/DeviceAdminReceiver.html#DeviceAdminReceiver()
     *
     * */
    class DevAdminReceiver: DeviceAdminReceiver() {

    }
}
