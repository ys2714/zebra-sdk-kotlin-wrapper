package com.zebra.zebrakotlindemo.datawedge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton

class DataWedgeActivity: ComponentActivity() {

    val viewModel = DataWedgeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally) {
            RoundButton("DataWedge Basic") {
                startActivity(Intent(context, DataWedgeBasicActivity::class.java))
            }
            RoundButton("DataWedge Advanced") {
                startActivity(Intent(context, DataWedgeAdvancedActivity::class.java))
            }
            RoundButton("DataWedge Profile") {
                startActivity(Intent(context, DataWedgeProfileActivity::class.java))
            }
            RoundButton("DataWedge Trigger Control Basic", color = Color.DarkGray) {
                startActivity(Intent(context, DataWedgeTriggerBasicActivity::class.java))
            }
            RoundButton("DataWedge Trigger Control Advanced", color = Color.DarkGray) {
                startActivity(Intent(context, DataWedgeTriggerActivity::class.java))
            }
        }
    }
}