package com.zebra.emdk_kotlin_wrapper

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DWConst
import com.zebra.emdk_kotlin_wrapper.dw.DWProfileProcessor
import com.zebra.emdk_kotlin_wrapper.dw.bundleForScannerPlugin
import com.zebra.emdk_kotlin_wrapper.utils.JsonUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.fail

@RunWith(AndroidJUnit4::class)
class ScannerPluginTest {

    @Test
    fun checkJsonBundleConvertion() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        try {
            val bundle = DWProfileProcessor.bundleForScannerPlugin(
                appContext,
                "test-scanner-profile-2", DWAPI.ScanInputModeOptions.SINGLE
            )

            val debugString = JsonUtils.bundleToJson(bundle)
            val placeholderRegex = "\${"
            val ignoredValueRegex = "xxx"
            val wrongObjectEnd = ",}"
            val wrongArrayEnd = ",]"

            assert(!debugString.contains(placeholderRegex), { "result contains placeholder" })
            assert(!debugString.contains(ignoredValueRegex), { "result contains ignored value" })
            assert(!debugString.contains(wrongObjectEnd), { "result contains wrong object end" })
            assert(!debugString.contains(wrongArrayEnd), { "result contains wrong array end" })

            val CONFIG_MODE = bundle.getString(DWConst.CONFIG_MODE)
            assert(CONFIG_MODE == DWAPI.ConfigModeOptions.UPDATE.value, { "CONFIG_MODE should be UPDATE" })

            val PLUGIN_CONFIG = bundle.getBundle(DWConst.PLUGIN_CONFIG)
            assert(PLUGIN_CONFIG != null, { "PLUGIN_CONFIG should not be null" })

            val RESET_CONFIG = PLUGIN_CONFIG!!.getString(DWConst.RESET_CONFIG)
            assert(RESET_CONFIG == "true", { "RESET_CONFIG should be true" })

            val PARAM_LIST = PLUGIN_CONFIG!!.getBundle(DWConst.PARAM_LIST)
            assert(PARAM_LIST != null, { "PARAM_LIST should not be null" })

            val scanner_input_enabled = PARAM_LIST!!.getString(DWConst.scanner_input_enabled)
            assert(scanner_input_enabled == "true", { "scanner_input_enabled should be true" })

        } catch (e: Exception) {
            fail("Exception thrown: ${e.message}")
        }
    }

    @Test
    fun checkJsonBundleConvertionMultipleTimes() {
        var count = 100
        while (count > 0) {
            checkJsonBundleConvertion()
            count--
        }
    }
}