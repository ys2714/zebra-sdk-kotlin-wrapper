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

class EMDKSystemClockActivity: ComponentActivity() {

    val viewModel = EMDKSystemClockViewModel()

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
            Text("System Clock",
                modifier = Modifier
                    .padding()
            )
            RoundButton("Set Clock to android release date 2008") {
                viewModel.setClockToAndroidReleaseDate(context)
            }
            RoundButton("Set Clock back to Google NTP time") {
                viewModel.setClockToGoogleNTPTime(context)
            }
        }
    }
}