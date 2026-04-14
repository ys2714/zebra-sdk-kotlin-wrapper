package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class EMDKBatteryManagerActivity: ComponentActivity() {

    val viewModel = EMDKBatteryManagerViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        val threshold = remember { viewModel.criticalLowThreshold }

        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text("Battery Manager",
                modifier = Modifier
                    .padding()
            )
            StyledOutlinedTextField(
                "critical low threshold",
                threshold.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged({ focusState ->

                    })
            ) { newValue ->
                try {
                    threshold.value = newValue
                } catch (e: Exception) {
                    Log.d("", e.message ?: "")
                }
            }
            RoundButton("Set Critical Low Threshold") {
                viewModel.handleSetCriticalLowThreshold(context)
            }
        }
    }
}