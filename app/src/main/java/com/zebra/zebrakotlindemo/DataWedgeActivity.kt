package com.zebra.zebrakotlindemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zebra.emdk_kotlin_wrapper.dw.DWAPI
import com.zebra.emdk_kotlin_wrapper.dw.DataWedgeHelper

class DataWedgeActivity : ComponentActivity() {

    val viewModel = DataWedgeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setupDataWedgeIfNeeded(this)
        setContent {
            RootView()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setupDataWedgeIfNeeded(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Composable
    fun RootView() {
        val newText = remember { viewModel.text }
        Column {
            StyledOutlinedTextField(newText.value) { newValue ->
               newText.value = newValue
            }
            RoundButton("Push Scan Button or Tap this") {
                DataWedgeHelper.softScanTrigger(
                    this@DataWedgeActivity,
                    DWAPI.SoftScanTriggerOptions.START_SCANNING)
            }
        }
    }
}