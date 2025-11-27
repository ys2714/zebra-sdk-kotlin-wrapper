package com.zebra.ppiddemo

import android.content.Context
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
import androidx.compose.ui.unit.dp

/**
 * please switch the activity between MainActivityJava and MainActivityKotlin in AndroidManifest.xml
 * opening MainActivityJava by default
 * */
class MainActivityKotlin : ComponentActivity() {

    private val viewModel = MainViewModelKotlin()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.prepareEMDK(this)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        if (viewModel.emdkPrepared.value) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                ,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text("----OEM Info----",
                    modifier = Modifier
                        .padding()
                )
                Text("PPID: " + viewModel.ppid.value)
                Text("Serial Number: " + viewModel.serial.value)
                RoundButton("GET PPID") {
                    viewModel.fetchPPID(context) {}
                }
                RoundButton("GET Serial Number") {
                    viewModel.fetchSerialNumber(context) {}
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