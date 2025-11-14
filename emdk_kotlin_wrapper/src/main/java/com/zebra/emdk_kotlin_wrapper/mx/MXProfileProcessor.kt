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
class MXProfileProcessor(private val context: Context) {

    private var profileManager: ProfileManager? = null

    var foregroundScope = CoroutineScope(Dispatchers.Main + Job())
    var backgroundScope = CoroutineScope(Dispatchers.IO + Job())

    init {
        EMDKHelper.shared.prepare(context) {
            profileManager = EMDKHelper.shared.profileManager
        }
    }

    fun processProfileWithCallback(fileName: String,
                                           profileName: String,
                                           params: Map<String, String>? = null,
                                           callback: MXBase.ProcessProfileCallback?) {
        backgroundScope.launch {
            val errorInfo: Deferred<MXBase.ErrorInfo?> = processProfile(
                fileName,
                profileName,
                params)
            errorInfo.await()?.let {
                callback?.onError(it)
            } ?: callback?.onSuccess(profileName)
        }
    }

    private suspend fun processProfile(fileName: String, profileName: String, params: Map<String, String>? = null) : Deferred<MXBase.ErrorInfo?> {
        return backgroundScope.async {
            val command = AssetsReader.readFileToStringWithParams(context, fileName, params)
            val results = profileManager?.processProfile(
                profileName,
                ProfileManager.PROFILE_FLAG.SET,
                arrayOf(command)
            ) ?: return@async MXBase.ErrorInfo(
                errorName = MXProfileProcessor.Companion.TAG,
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
                        Log.e(MXProfileProcessor.Companion.TAG, "Failed to parse XML response", e)
                        return@async MXBase.ErrorInfo(
                            MXProfileProcessor.Companion.TAG,
                            "XmlPullParserException",
                            e.message ?: "Unknown parser exception")
                    }
                }
                else -> {
                    Log.e(MXProfileProcessor.Companion.TAG, "Profile processing failed with status code: ${results.statusCode}")
                    return@async MXBase.ErrorInfo(
                        MXProfileProcessor.Companion.TAG,
                        "ProfileError",
                        "Profile processing failed with status code: ${results.statusCode}")
                }
            }
        }
    }

    companion object {
        private val TAG = MXProfileProcessor::class.java.simpleName
    }

    /**
     * https://developer.android.com/reference/android/app/admin/DeviceAdminReceiver.html#DeviceAdminReceiver()
     *
     * */
    class DevAdminReceiver: DeviceAdminReceiver() {

    }
}
