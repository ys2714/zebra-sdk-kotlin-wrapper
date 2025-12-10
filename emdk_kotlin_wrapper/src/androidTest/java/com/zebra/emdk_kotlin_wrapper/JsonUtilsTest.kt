package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.utils.AssetsReader
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import com.zebra.emdk_kotlin_wrapper.utils.compressStringByTrimAll
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JsonUtilsTest {

    val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun checkJsonToBundle() {
        val jsonString = AssetsReader.readFileToStringWithParams(
            context,
            "set_config.json",
            mapOf(
                "PROFILE_NAME" to "test-profile",
                "PACKAGE_NAME" to "com.zebra.demo",
                "intent_action" to "com.zebra.test-action"
            )
        ).compressStringByTrimAll()
        val bundle = JsonUtils.jsonToBundle(jsonString)
        val resultJson = JsonUtils.bundleToJson(bundle).compressStringByTrimAll()
        assert(jsonString == resultJson, { "json to bundle error" })
    }



}