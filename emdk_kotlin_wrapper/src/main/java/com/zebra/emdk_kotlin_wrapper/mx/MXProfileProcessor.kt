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
internal object MXProfileProcessor {

    private val TAG = MXProfileProcessor::class.java.simpleName

    var foregroundScope = CoroutineScope(Dispatchers.Main + Job())
    var backgroundScope = CoroutineScope(Dispatchers.IO + Job())

    internal val profileManager: ProfileManager
        get() {
            if (EMDKHelper.shared.profileManager == null) {
                throw RuntimeException("please call EMDKHelper.prepare() before get profileManager")
            }
            return EMDKHelper.shared.profileManager!!
        }

    fun processProfileWithCallback(context: Context,
                                   fileName: MXBase.ProfileXML,
                                   profileName: MXBase.ProfileName,
                                   params: Map<String, String>? = null,
                                   callback: MXBase.ProcessProfileCallback?) {
        processProfile(
            context,
            fileName,
            profileName,
            params) { error ->
            foregroundScope.launch {
                if (callback != null) {
                    if (error == null) {
                        callback.onSuccess(profileName.string)
                    } else {
                        callback.onError(error)
                    }
                }
            }
        }
    }

    private fun addResultListener(
        profileName: MXBase.ProfileName,
        callback: (MXBase.ErrorInfo?) -> Unit) {
        profileManager.addDataListener(object : ProfileManager.DataListener {
            override fun onData(resultData: ProfileManager.ResultData?) {
                resultData?.also { data ->
                    if (data.profileName == profileName.string) {

                        when (data.result.statusCode) {
                            EMDKResults.STATUS_CODE.SUCCESS -> {
                                callback(null)
                            }
                            EMDKResults.STATUS_CODE.CHECK_XML -> {
                                try {
                                    val parser = Xml.newPullParser().apply {
                                        setInput(StringReader(data.result.statusString))
                                    }
                                    val error = XMLParser.parseXML(parser)
                                    callback(error)
                                } catch (e: XmlPullParserException) {
                                    Log.e(MXProfileProcessor.TAG, "Failed to parse XML response", e)
                                    val error = MXBase.ErrorInfo(
                                        MXProfileProcessor.TAG,
                                        "XmlPullParserException",
                                        e.message ?: "Unknown parser exception")
                                    callback(error)
                                }
                            }
                            else -> {
                                Log.e(MXProfileProcessor.TAG, "failed with response: ${data.result.statusString}")
                                val error = MXBase.ErrorInfo(
                                    MXProfileProcessor.TAG,
                                    data.result.statusString,
                                    data.result.extendedStatusMessage)
                                callback(error)
                            }
                        }

                        profileManager.removeDataListener(this)
                    } else {
                        // other profile name, ignore
                        profileManager.removeDataListener(this)
                    }
                } ?: run {
                    // no data, ignore
                    profileManager.removeDataListener(this)
                }
            }
        })
    }

    private fun processProfile(
        context: Context,
        fileName: MXBase.ProfileXML,
        profileName: MXBase.ProfileName,
        params: Map<String, String>? = null,
        callback: (MXBase.ErrorInfo?) -> Unit
    ) {
         backgroundScope.launch {
            params?.also {
                val command = AssetsReader.readFileToStringWithParams(context, fileName.string, it).trimIndent()
                addResultListener(profileName, callback)
                profileManager.processProfileAsync(
                    profileName.string,
                    ProfileManager.PROFILE_FLAG.SET,
                    arrayOf(command)
                )
            } ?: run {
                // process the profile defined by AndroidStudio plugin in app/assets/EMDKConfig.xml
                profileManager.processProfileAsync(
                    profileName.string,
                    ProfileManager.PROFILE_FLAG.SET,
                    null as Array<String>?
                )
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
