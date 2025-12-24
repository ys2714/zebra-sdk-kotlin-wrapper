package com.zebra.emdk_kotlin_wrapper

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
import com.zebra.emdk_kotlin_wrapper.mx.MXConst
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetsReaderTest {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkReadXML() {
        try {
            val xmlString = AssetsReader.readFileToStringWithParams(
                appContext,
                MXBase.ProfileXML.AccessManagerAllowCallService.string,
                mapOf(
                    "Wrong_KEY" to "",
                    MXConst.ServiceIdentifier to "id",
                    MXConst.CallerPackageName to "com.my.package",
                    MXConst.CallerSignature to "xasdfasdfaf"
                )
            )
            Log.d("xmlString", xmlString)
        } catch (e: Exception) {
            fail("read xml error: ${e.message}")
        }
    }

    @Test
    fun checkReadJSONWithMandatoryParams() {
        try {
            val jsonString = AssetsReader.readFileToStringWithParams(
                appContext,
                "test_plugin_scanner.json",
                mapOf(
                    "CONFIG_MODE" to "true",
                    "PROFILE_NAME" to "test",
                    "PROFILE_ENABLED" to "true",
                    "PACKAGE_NAME" to "com.zebra.test",
                    "test_key_1" to "true",
                    "test_key_2" to "0",
                    "test_key_3" to "0"
                )
            )
            JsonUtils.jsonToBundle(jsonString)
        } catch (e: Exception) {
            fail("read json error: ${e.message}")
        }
    }

    @Test
    fun checkReadJSONOmitMandatoryParams() {
        try {
            val jsonString = AssetsReader.readFileToStringWithParams(
                appContext,
                "test_plugin_scanner.json",
                mapOf(
                    "CONFIG_MODE" to "true",
                    "PROFILE_NAME" to "test",
                    "PROFILE_ENABLED" to "true",
                    // "PACKAGE_NAME" to "com.zebra.test",
                    "test_key_1" to "true",
                    "test_key_2" to "0",
                    "test_key_3" to "0"
                )
            )
            JsonUtils.jsonToBundle(jsonString)
            fail("should throw exception: mandatory param not filled")
        } catch (e: Exception) {
            Log.d("", "work as expected")
        }
    }

    @Test
    fun checkReadJSONOmitOptionalParams() {
        try {
            val jsonString = AssetsReader.readFileToStringWithParams(
                appContext,
                "test_plugin_scanner.json",
                mapOf(
                    "CONFIG_MODE" to "true",
                    "PROFILE_NAME" to "test",
                    "PROFILE_ENABLED" to "true",
                    "PACKAGE_NAME" to "com.zebra.test",
                    "test_key_1" to "true",
                    // "test_key_2" to "0",
                    "test_key_3" to "0"
                )
            )
            JsonUtils.jsonToBundle(jsonString)
            Log.d("", "work as expected")
        } catch (e: Exception) {
            fail("${e.message}")
        }
    }

    @Test
    fun checkReadJSONOmitOptionalParamsOnLastLine() {
        try {
            val jsonString = AssetsReader.readFileToStringWithParams(
                appContext,
                "test_plugin_scanner.json",
                mapOf(
                    "CONFIG_MODE" to "true",
                    "PROFILE_NAME" to "test",
                    "PROFILE_ENABLED" to "true",
                    "PACKAGE_NAME" to "com.zebra.test",
                    "test_key_1" to "true",
                    "test_key_2" to "0",
                    // "test_key_3" to "0"
                )
            )
            JsonUtils.jsonToBundle(jsonString)
            fail("should throw exception: can not skip content after last delimiter")
        } catch (e: Exception) {
            Log.d("", "work as expected")
        }
    }
}