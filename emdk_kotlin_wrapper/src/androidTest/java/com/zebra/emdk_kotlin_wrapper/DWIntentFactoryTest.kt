package com.zebra.emdk_kotlin_wrapper

import android.os.Bundle
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DWIntentFactory
import com.zebra.emdk_kotlin_wrapper.dw.DWProfileProcessor
import com.zebra.emdk_kotlin_wrapper.dw.enableDW
import com.zebra.emdk_kotlin_wrapper.dw.sendDeleteProfileIntent
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



    @Test
    fun checkCallLegacy() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        val profileName = "checkCallLegacy-profile-1"

        // Configure created profile to apply to this app
        val profileConfig1 = DWIntentFactory.createLegacyBundle(context, profileName, "com.my.checkCallLegacy-result")

//        setConfigIntent1.setPackage("com.symbol.datawedge")
//        setConfigIntent1.putExtra("APPLICATION_PACKAGE", context.packageName)
//        setConfigIntent1.putExtra("TOKEN", ZDMTokenStore.getToken(ZDMConst.DelegationScope.SCOPE_DW_CONFIG_API))

        val jsonString = JsonUtils.bundleToJson(profileConfig1)

        val enabled: Deferred<Boolean> = async {
            DWAPI.enableDW(context, true)
        }
        val enableSuccess = enabled.await()

        val deleted: Deferred<Boolean> = async {
            DWAPI.sendDeleteProfileIntent(context, profileName)
        }

        if (enableSuccess && deleted.await()) {
            Log.d("DWIntentFactoryTest", "DW enabled")
            DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.SET_CONFIG, profileConfig1) { result ->
                result.onSuccess {
                    println("JSON: $jsonString")
                    complete.complete(Unit)
                }.onFailure {
//                    if (it.message == DWAPI.ResultCodes.APP_ALREADY_ASSOCIATED.value) {
//                        println("JSON: $jsonString")
//                        complete.complete(Unit)
//                    } else {
                        fail(it.message)
//                    }
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
        val bundle = DWProfileProcessor.bundleForUpdateProfile(context, profileName)

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