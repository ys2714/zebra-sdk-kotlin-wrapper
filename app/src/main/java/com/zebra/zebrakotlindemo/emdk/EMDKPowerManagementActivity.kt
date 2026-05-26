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
import com.zebra.emdk_kotlin_wrapper.mx.MXBase
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
                        MXBase.AuthInfo.Type.ZebraAuth,
                        "https://downloads.zebra.com/content/servlet/smartrepo/downloadrequest?urlPath=/content/dam/support-dam/en/operating-system/restricted/0006/AT_FULL_UPDATE_14-35-10.00-UG-U76-STD-ATH-04.zip",
                        "&#1273j20CrKQQdsv9WQWZSBsb7nE63RKBvErXprMuCH4orFegefv0PgZy5rUQ1iQGc9sm5s0jr8Y3/cfov26KSXJ8U2RgFsu/EccMPJfJ3ofz9GRWd6V8VXIv16240MBMHKsahTMf4Prrh5SFfs2L7S4qYReW4S3QxwJf/zxfQPgmqy2aUATm/kMCSiyFavmMhIgjKt8KGiVqLt5wkwKiBCU7G8d10ttq9XZ7Kjwhmo0Q7V9XadO3bs+aQKetH7YKLXTdSB2Pjey2U3WIeFqrTFQoNggdSypDrwOW7ObKM0yMvWXnKBcq7NPwsie+OlzdwLNAurBmvWzgzu5XIcA9AKz7KQ==&#1278HEJtau5tfDwWgNHrQIa2oHZe/7gHvbIvVO5Zc1lGaATYQOa+4i4nAZH41kbzmNqRRcyizmfuwIWME/dTWQSZlf0CcWuMKrUR/2HbnpWwmpbDBi1mKdl5sHnvWG5mTwG2PkC3lT0KyqxCmaEDntlwdD8nhTHOnIjFKBvJzv7qLN7dZekNG3vU4oE8jhsKDULyArHK/viAwUsPYQJeLXKHDiJkbfPrdciJgCmPry1J8JlMqClwb1G3Q0UgGeCKmfTXxyw4NYRuHLydtXPiMMC11PGIs1zwCI+QSykvDbwMWxGnS9/g2TS7ms98YQguUlKDfYJmJeNnW1nirG+RPH9OqcivpSEPAtEdRCLsk33eeMPAPoH3XBCzIlRlHuptRy4yFjZ2Y7DlurOg1ePQOVSzkC6DRH9kiZDuZgRLy1nvs7cvzx6RDXhedl2D4gdi7gBZCotd9cCrmY8HzJjJ8P+nugB6OfW+F3SAD27Jne+yo0=",
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