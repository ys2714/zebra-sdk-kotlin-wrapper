package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.emdk.EMDKHelper
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.mx.MXProfileProcessor
import com.zebra.emdk_kotlin_wrapper.mx.callPowerManagerFeature
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MXPowerManagerTest {

    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkCallPowerManagerFeature() {
        EMDKHelper.shared.prepare(appContext) { success ->
            if (!success) {
                fail("EMDK prepare failed")
            }
            MXProfileProcessor.callPowerManagerFeature(
                appContext,
                MXBase.PowerManagerOptions.SLEEP_MODE,
                null,object : MXBase.ProcessProfileCallback {
                    override fun onSuccess(profileName: String) {

                    }

                    override fun onError(errorInfo: MXBase.ErrorInfo) {
                        fail(errorInfo.errorDescription)
                    }
                }
            )
        }
    }

    @Test
    fun checkPowerManagerReset() {
        try {
            // Define the values to replace the placeholders in the XML
            val resetActionValue = "1" // Example value for ResetAction
            val zipFileValue = "/sdcard/path/to/update.zip" // Example file path

            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXBase.ProfileXML.PowerManagerReset.toString(), // Assumes this constant exists for the reset XML
                mapOf(
                    Pair(MXConst.ResetAction, resetActionValue),
                    Pair(MXConst.ZipFile, zipFileValue)
                )
            )
            if (xmlString.contains("=[")) {
                fail("profile XML params replacement error")
            }

            // Verify that the placeholders were successfully replaced
            if (xmlString.contains(resetActionValue) &&
                xmlString.contains(zipFileValue)
            ) {
                // work as expected
            } else {
                fail("PowerManagerReset XML params replacement error")
            }
        } catch (e: Exception) {
            // If any exception occurs, fail the test and report the error
            fail(e.message)
        }
    }
}
