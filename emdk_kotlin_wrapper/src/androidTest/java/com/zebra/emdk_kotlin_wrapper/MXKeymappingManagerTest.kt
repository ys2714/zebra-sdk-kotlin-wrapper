package com.zebra.emdk_kotlin_wrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MXKeymappingManagerTest {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkMappingScanKeyToSendIntent() = runBlocking {
        val complete = CompletableDeferred<Unit>()

        EMDKHelper.shared.prepare(appContext) { success ->
            if (!success) {
                fail("prepare EMDK failed")
            }
            MXProfileProcessor.processProfileWithCallback(
                appContext,
                MXBase.ProfileXML.KeymappingManagerSetScanKey,
                MXBase.ProfileName.KeymappingManagerSetScanKey,
                mapOf(
                    MXConst.KeyIdentifier to "SCAN",
                    MXConst.BaseIntentAction to "com.my.action",
                    MXConst.BaseIntentCategory to "com.my.category"
                ), object : MXBase.ProcessProfileCallback {
                    override fun onSuccess(profileName: String) {

                        ContextCompat.registerReceiver(
                            appContext, object : BroadcastReceiver() {
                                override fun onReceive(
                                    context: Context?,
                                    intent: Intent?
                                ) {
                                    if (intent == null) {
                                        fail("intent is null")
                                    } else {
                                        // SUCCESS
                                        complete.complete(Unit)
                                    }
                                    context?.unregisterReceiver(this)
                                }
                            },
                            IntentFilter().apply {
                                addAction("com.my.action")
                                addCategory("com.my.category")
                            },
                            ContextCompat.RECEIVER_EXPORTED
                        )

                    }

                    override fun onError(errorInfo: MXBase.ErrorInfo) {
                        fail(errorInfo.errorDescription)
                    }
                }
            )
        }

        complete.await()
    }
}