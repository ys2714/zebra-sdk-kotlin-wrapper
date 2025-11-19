package com.zebra.emdk_kotlin_wrapper

import android.R
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MXAccessManagerTest {

    @Test
    fun checkAccessManagerAllowCallServiceXML() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXBase.ProfileXML.AccessManagerAllowCallService.string,
                mapOf(
                    Pair(MXConst.ServiceIdentifier, "value1"),
                    Pair(MXConst.CallerPackageName, "value2"),
                    Pair(MXConst.CallerSignature, "value3")
                )
            )
            if (xmlString.contains("=[")) {
                fail("profile XML params replacement error")
            }

            if (xmlString.contains("value1") &&
                xmlString.contains("value2") &&
                xmlString.contains("value3")) {
                // work as expected
            } else {
                fail("profile XML params replacement error")
            }
        } catch (e: Exception) {
            fail(e.message)
        }
    }

    @Test
    fun checkAccessManagerAllowPermissionXML() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        try {
            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXBase.ProfileXML.AccessManagerAllowPermission.string,
                mapOf(
                    Pair(MXConst.PermissionAccessAction, "value1"),
                    Pair(MXConst.PermissionAccessPackageName, "value2"),
                    Pair(MXConst.ApplicationClassName, "value3"),
                    Pair(MXConst.PermissionAccessPermissionName, "value4"),
                    Pair(MXConst.PermissionAccessSignature, "value5")
                )
            )
            if (xmlString.contains("value1") &&
                xmlString.contains("value2") &&
                xmlString.contains("value3") &&
                xmlString.contains("value4") &&
                xmlString.contains("value5")
                ) {
                // work as expected
            } else {
                fail("profile XML params replacement error")
            }
        } catch (e: Exception) {
            fail(e.message)
        }
    }
}
