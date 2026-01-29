package com.zebra.zebrakotlindemo.datawedge

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class DataWedgeProfileActivity : ComponentActivity() {

    val viewModel = DataWedgeProfileViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        val profileNeedExport = remember { mutableStateOf("") }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("----Profile Name List----",
                modifier = Modifier
                    .padding()
            )
            StyledOutlinedTextField(
                "please input profile name here",
                profileNeedExport.value,
                keyboardType = KeyboardType.Text,
                modifier = Modifier
                    .fillMaxWidth()
            ) { newValue ->
                profileNeedExport.value = newValue
            }
            RoundButton("Export Profile as JSON") {
                viewModel.exportProfile(this@DataWedgeProfileActivity, profileNeedExport.value)
            }
        }
    }
}