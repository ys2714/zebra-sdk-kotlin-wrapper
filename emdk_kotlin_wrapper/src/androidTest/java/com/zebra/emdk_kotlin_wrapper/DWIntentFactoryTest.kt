package com.zebra.emdk_kotlin_wrapper

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DWIntentFactory
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import com.zebra.emdk_kotlin_wrapper.dw.barcodeInputIntentOutputBundle
import com.zebra.emdk_kotlin_wrapper.dw.simpleBarcodePluginBundle
import com.zebra.emdk_kotlin_wrapper.dw.simpleCreateProfileBundle
import com.zebra.emdk_kotlin_wrapper.dw.simpleIntentPluginBundle
import com.zebra.emdk_kotlin_wrapper.dw.simpleKeystrokePluginBundle
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DWIntentFactoryTest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkSimpleBarcodeUsecase() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        val profileName = "checkCallLegacy-profile-1"

        DWIntentFactory.callDWAPI(
            context,
            DWAPI.ActionExtraKeys.SET_CONFIG,
            DWIntentFactory.simpleCreateProfileBundle(context, profileName)) { createResult ->
            createResult.onSuccess {
                DWIntentFactory.callDWAPI(
                    context,
                    DWAPI.ActionExtraKeys.SET_CONFIG,
                    DWIntentFactory.simpleBarcodePluginBundle(context, profileName)) {}
                DWIntentFactory.callDWAPI(
                    context,
                    DWAPI.ActionExtraKeys.SET_CONFIG,
                    DWIntentFactory.simpleKeystrokePluginBundle(context, profileName)) {}
                DWIntentFactory.callDWAPI(
                    context,
                    DWAPI.ActionExtraKeys.SET_CONFIG,
                    DWIntentFactory.simpleIntentPluginBundle(
                        context,
                        profileName,
                        DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value)) { finalResult ->
                    finalResult.onSuccess {
                        Log.d("DWIntentFactoryTest", "Success")
                        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, profileName) {}
                        complete.complete(Unit)
                    }.onFailure {
                        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, profileName) {}
                        fail(it.message)
                    }
                }
            }.onFailure {
                DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, profileName) {}
                fail(it.message)
            }
        }
        complete.await()
    }

    @Test
    fun checkSetMultiPluginsByArray() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        val profileName = "checkSetMultiPluginsByArray-profile-1"

        val bundle = DWIntentFactory.barcodeInputIntentOutputBundle(
            context,
            profileName,
            DWAPI.ResultActionNames.SCAN_RESULT_ACTION.value)

        DWIntentFactory.callDWAPI(
            context,
            DWAPI.ActionExtraKeys.SET_CONFIG,
            bundle
        ) { result ->
            result.onSuccess {
                complete.complete(Unit)
            }.onFailure {
                DataWedgeHelper.deleteProfile(context, profileName) { }
                fail(it.message)
            }
        }

        complete.await()
        DataWedgeHelper.deleteProfile(context, profileName) { }
    }
}