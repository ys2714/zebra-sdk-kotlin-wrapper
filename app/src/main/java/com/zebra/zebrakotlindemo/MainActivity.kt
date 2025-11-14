package com.zebra.zebrakotlindemo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {

    private val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.prepare(this)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @Composable
    fun RootView(context: Context) {
        Column {
            RoundButton("EMDK") {
                startActivity(Intent(context, EMDKActivity::class.java))
            }
            RoundButton("DataWedge") {
                startActivity(Intent(context, DataWedgeActivity::class.java))
            }
        }
    }
}