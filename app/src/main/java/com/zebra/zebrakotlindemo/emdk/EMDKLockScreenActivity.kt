package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class EMDKLockScreenActivity: ComponentActivity() {

    val viewModel = EMDKLockScreenViewModel()

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
            Text("Lock Screen",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Lock Screen") {
                viewModel.disableLockScreen(context)
            }
            RoundButton("Enable Lock Screen") {
                viewModel.enableLockScreen(context)
            }
        }
    }
}