package com.zebra.zebrakotlindemo.beep

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import kotlin.getValue

class BeepActivity : ComponentActivity() {

    val viewModel: BeepViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        LazyColumn() {
            items(
                viewModel.getBeepItems(),
                { item -> item.id }
            ) { item ->
                RoundButton(item.type.name, color = Color.Blue, Modifier.padding(horizontal = 5.dp)) {
                    viewModel.playBeep(item.type)
                }
            }
        }
    }
}