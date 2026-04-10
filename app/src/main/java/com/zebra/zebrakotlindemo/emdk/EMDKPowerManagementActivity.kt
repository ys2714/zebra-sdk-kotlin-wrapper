package com.zebra.zebrakotlindemo.emdk

import android.content.Context
import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.StyledOutlinedTextField

class EMDKPowerManagementActivity: ComponentActivity() {

    val viewModel = EMDKPowerManagementViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @Composable
    fun RootView(context: Context) {
        Column() {
            HeaderView(context)
            BottomList(context)
        }
        if (viewModel.flagShouldShowRebootConfirmDialog.value) {
            AlertDialog(
                {
                    viewModel.flagShouldShowRebootConfirmDialog.value = false
                }, {
                    Button({
                        viewModel.setReboot(context)
                    }) {
                        Text("YES")
                    }
                }, dismissButton = {
                    Button({
                        viewModel.flagShouldShowRebootConfirmDialog.value = false
                    }) {
                        Text("NO")
                    }
                }, title = {
                    Text("Do you want reboot")
                }, text = {
                    Text("upgrade finished. click YES to reboot")
                }
            )
        }
    }

    @Composable
    fun HeaderView(context: Context) {
        Text("Power Management",
            modifier = Modifier
                .padding()
        )
        Text("----OS Update Info----",
            modifier = Modifier
                .padding()
        )
        Text("OS Update Status: " + viewModel.osUpdateStatus.value)
        Text("OS Update Detail: " + viewModel.osUpdateDetail.value)
        Text("OS Update Timestamp: " + viewModel.osUpdateTimestamp.value)
        Row() {
            RoundButton(
                "Refresh Status",
                Color(0xFF00D100),
                Modifier.width(100.dp)
            ) {
                viewModel.startFetchOSUpdateStatus(context)
            }
            RoundButton(
                "Force Sleep",
                Color(0xFF00D100),
                Modifier.width(100.dp)
            ) {
                viewModel.setSleep(context)
            }
            RoundButton(
                "Force Reboot",
                Color(0xFF00D100),
                Modifier.width(100.dp)
            ) {
                viewModel.setReboot(context)
            }
        }
        Text("------------------------Scroll View------------------------",
            modifier = Modifier
                .padding()
        )
    }

    @Composable
    fun BottomList(context: Context) {
        LazyColumn(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text("BSP path for OS upgrade (SuppressReboot=true)")
                StyledOutlinedTextField(
                    "BSP path",
                    viewModel.upgradeBSPPath.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState -> })
                ) { newValue ->
                    viewModel.upgradeBSPPath.value = newValue
                }
                RoundButton("Upgrade OS") {
                    viewModel.upgradeOS(context, viewModel.upgradeBSPPath.value, true)
                }
            }
            item {
                Text("BSP path for OS downgrade")
                StyledOutlinedTextField(
                    "BSP path",
                    viewModel.downgradeBSPPath.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState -> })
                ) { newValue ->
                    viewModel.downgradeBSPPath.value = newValue
                }
                RoundButton("Downgrade OS") {
                    viewModel.downgradeOS(context, viewModel.downgradeBSPPath.value)
                }
            }
            item {
                Text("BSP streaming for OS upgrade")
                StyledOutlinedTextField(
                    "BSP streaming",
                    viewModel.upgradeBSPStream.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState -> })
                ) { newValue ->
                    viewModel.upgradeBSPStream.value = newValue
                }
                RoundButton("Stream Upgrade OS") {
                    viewModel.streamUpgradeOS(
                        context,
                        "",
                        ""
                    )
                }
            }
            item {
                Text("BSP streaming for OS downgrade")
                StyledOutlinedTextField(
                    "BSP streaming",
                    viewModel.downgradeBSPStream.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged({ focusState -> })
                ) { newValue ->
                    viewModel.downgradeBSPStream.value = newValue
                }
                RoundButton("Stream Downgrade OS") {
                    viewModel.streamDowngradeOS(
                        context,
                        "",
                        ""
                    )
                }
            }
            item {
                RoundButton("Cancel Ongoing Update", Color(0xFFD10000)) {
                    viewModel.cancelUpdate(context)
                }
            }
        }
    }
}