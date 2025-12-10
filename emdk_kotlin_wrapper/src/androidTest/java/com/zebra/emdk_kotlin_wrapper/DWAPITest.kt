package com.zebra.emdk_kotlin_wrapper

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DWIntentFactory
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DWAPITest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkEnableDW() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.enableDW(context) { success ->
            assert(success)
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkDisableDW() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.disableDW(context) { success ->
            assert(success)
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkGetDWStatus() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.checkDWStatus(context) { enabled ->
            Log.d("", "DW enabled: $enabled")
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkCreateProfile() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.createProfile(context, "test") { success ->
            assert(success)
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkDeleteProfile() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.createProfile(context, "test") { success1 ->
            if (!success1) fail("create profile failed")
            DataWedgeHelper.deleteProfile(context, "test") { success2 ->
                assert(success2)
                complete.complete(Unit)
            }
        }
        complete.await()
    }

    @Test
    fun checkGetScannerList() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.getScannerList(context) { list ->
            assert(list.isNotEmpty())
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkPrepareDW() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        DataWedgeHelper.prepare(context) { success ->
            assert(success)
            complete.complete(Unit)
        }
        complete.await()
    }

    @Test
    fun checkBindProfileToApp() = runBlocking {
        val complete = CompletableDeferred<Unit>()
        val profileName = "checkBindProfileToApp-2"
        DataWedgeHelper.prepare(context) { success ->
            assert(success, { "prepare DW failed" })
            DataWedgeHelper.deleteProfile(context, profileName) { deleteSuccess ->
                assert(deleteSuccess, { "delete profile failed" })
                DataWedgeHelper.createProfile(context, profileName) { createSuccess ->
                    assert(createSuccess, { "create profile failed" })
                    DataWedgeHelper.switchProfile(context, profileName) { configSuccess ->
                        assert(configSuccess, { "config profile failed" })
                        DWIntentFactory.callDWAPI(context, DWAPI.ActionExtraKeys.DELETE_PROFILE, profileName) {}
                        complete.complete(Unit)
                    }
                }
            }
        }
        complete.await()
    }
}