package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.prepare(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Composable
    fun RootView(context: Context) {
        if (viewModel.emdkPrepared.value) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("OEM Info",
                    modifier = Modifier
                        .padding()
                )
                Text("Serial Number: " + viewModel.serial.value)
                Text("IMEI Number: " + viewModel.imei.value)
                Text("Scanner Status: " + viewModel.scannerStatus.value)
                RoundButton("EMDK") {
                    startActivity(Intent(context, EMDKActivity::class.java))
                }
                RoundButton("DataWedge") {
                    startActivity(Intent(context, DataWedgeActivity::class.java))
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
            }
        }
    }
}