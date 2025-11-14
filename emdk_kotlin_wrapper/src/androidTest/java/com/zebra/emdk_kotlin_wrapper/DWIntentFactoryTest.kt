package com.zebra.emdk_kotlin_wrapper

import android.os.Bundle
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DWIntentFactory
import com.zebra.emdk_kotlin_wrapper.dw.DWProfileProcessor
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DWIntentFactoryTest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    fun createLegacyBundle(profileName: String, intentAction: String): Bundle {
        val profileConfig1 = Bundle().apply {
            putString("PROFILE_NAME", "$profileName")
            putString("PROFILE_ENABLED", "true")
            putString("CONFIG_MODE", "CREATE_IF_NOT_EXIST")
            putParcelableArray("PLUGIN_CONFIG", arrayOf<Bundle>(
                Bundle().apply {
                    putString("PLUGIN_NAME", "BARCODE")
                    putString("RESET_CONFIG", "true") //  This is the default
                    putBundle("PARAM_LIST", Bundle().apply {
                        putString("scanner_selection", "auto")
                        putString("scanner_input_enabled", "true")
                        putString("decoder_code128", "true")
                        putString("decoder_code39", "true")
                        putString("decoder_ean13", "true")
                        putString("decoder_upca", "true")
                    })
                },
                Bundle().apply {
                    putString("PLUGIN_NAME", "INTENT")
                    putString("RESET_CONFIG", "true")
                    putBundle("PARAM_LIST", Bundle().apply {
                        putString("intent_output_enabled", "true")
                        putString("intent_action", "$intentAction")
                        putString("intent_category", "android.intent.category.DEFAULT")
                        putString("intent_delivery", DWAPI.IntentDeliveryOptions.BROADCAST.string)
                    })
                }
            ))
            putParcelableArray("APP_LIST", arrayOf<Bundle>(
                Bundle().apply {
                    putString("PACKAGE_NAME", context.packageName)
                    putStringArray("ACTIVITY_LIST", arrayOf<String>("*"))
                }
            ))
        }
        return profileConfig1
    }

    @Test
    fun checkCallLegacy() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        // Configure created profile to apply to this app
        val profileConfig1 = createLegacyBundle("checkCallLegacy-profile-1", "com.my.checkCallLegacy-result")

//        setConfigIntent1.setPackage("com.symbol.datawedge")
//        setConfigIntent1.putExtra("APPLICATION_PACKAGE", context.packageName)
//        setConfigIntent1.putExtra("TOKEN", ZDMTokenStore.getToken(ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API))

        val jsonString = JsonUtils.bundleToJson(profileConfig1)

        val enabled: Deferred<Boolean> = async {
            DWIntentFactory.enableDW(context, true)
        }
        if (enabled.await()) {
            Log.d("DWIntentFactoryTest", "DW enabled")
            DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.SET_CONFIG, profileConfig1) { result ->
                result.onSuccess {
                    println("JSON: $jsonString")
                    complete.complete(Unit)
                }.onFailure {
                    if (it.message == DWAPI.ResultCodes.APP_ALREADY_ASSOCIATED.value) {
                        println("JSON: $jsonString")
                        complete.complete(Unit)
                    } else {
                        fail(it.message)
                    }
                }
            }
        } else {
            fail("DW not enabled")
        }
        complete.await()
    }

    @Test
    fun checkCallDWAPI() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        val profileName = "bundle_update_profile_${(0..99999999).random()}"
        val intentAction = "com.my.result.action-1"
        val bundle = DWProfileProcessor.bundleForUpdateProfile(context, profileName)
        val legacy = createLegacyBundle(profileName, intentAction)

        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.SET_CONFIG, bundle) { result ->
            result.onSuccess {
                Log.d("DWIntentFactoryTest", "Success")
                complete.complete(Unit)
            }.onFailure {
                if (it.message == DWAPI.ResultCodes.APP_ALREADY_ASSOCIATED.value) {
                    complete.complete(Unit)
                } else {
                    fail(it.message)
                }
            }
        }
        complete.await()
    }
}