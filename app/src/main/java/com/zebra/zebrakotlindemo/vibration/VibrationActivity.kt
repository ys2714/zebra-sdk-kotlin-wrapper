package com.zebra.zebrakotlindemo.vibration

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import kotlin.getValue

class VibrationActivity : ComponentActivity() {

    val viewModel: VibrationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            RoundButton("DEFAULT", color = Color(0xFFD10000)) {
                viewModel.triggerDefault(context)
            }
            RoundButton("CLICK", color = Color(0xFFD10000)) {
                viewModel.triggerClick(context)
            }
            RoundButton("DOUBLE CLICK", color = Color(0xFFD10000)) {
                viewModel.triggerDoubleClick(context)
            }
            RoundButton("HEAVY CLICK", color = Color(0xFFD10000)) {
                viewModel.triggerHeavyClick(context)
            }
            RoundButton("TICK", color = Color(0xFFD10000)) {
                viewModel.triggerTick(context)
            }
        }
    }
}