package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWConst
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PowerManagerTest {

    @Test
    fun checkPowerManagerReset() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            // Define the values to replace the placeholders in the XML
            val resetActionValue = "1" // Example value for ResetAction
            val zipFileValue = "/sdcard/path/to/update.zip" // Example file path

            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXConst.PowerManagerResetXML, // Assumes this constant exists for the reset XML
                mapOf(
                    Pair(MXConst.ResetAction, resetActionValue),
                    Pair(MXConst.ZipFile, zipFileValue)
                )
            )

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
