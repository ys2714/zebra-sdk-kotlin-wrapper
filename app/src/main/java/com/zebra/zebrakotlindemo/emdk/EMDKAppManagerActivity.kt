package com.zebra.zebrakotlindemo.emdk

import EMDKAppManagerViewModel
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

class EMDKAppManagerActivity: ComponentActivity() {

    val viewModel = EMDKAppManagerViewModel()

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
            Text("Control Device use Zebra MX API",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Disable Zebra Volume Control") {
                viewModel.disableApp(context, "com.zebra.zebravolumecontrol3")
            }
        }
    }
}