package com.zebra.zebrakotlindemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.content.MediaType.Companion.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zebra.zebrakotlindemo.ui.components.RoundButton
import com.zebra.zebrakotlindemo.ui.components.ShowPngImage

class LandingActivity: ComponentActivity() {

    val viewModel = LandingViewModel()

    enum class RequestPermissionResult {
        ALREADY_HAS,
        NEED_EXPLAIN,
        NEED_REQUEST,
        DENIED,
        GRANTED,
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                handlePermissionResult(RequestPermissionResult.GRANTED)
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                handlePermissionResult(RequestPermissionResult.DENIED)
            }
        }

    private fun askNotificationPermission() {
        // This is only necessary for API level 33 and above.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Your app has the permission.
                handlePermissionResult(RequestPermissionResult.ALREADY_HAS)
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // Display an educational UI to the user explaining why your app
                // needs this permission for a specific feature to behave as
                // expected. In this UI, include a "cancel" or "no thanks" button
                // that allows the user to continue using your app without
                // granting the permission.
                handlePermissionResult(RequestPermissionResult.NEED_EXPLAIN)
            } else {
                // Directly ask for the permission.
                handlePermissionResult(RequestPermissionResult.NEED_REQUEST)
            }
        }
    }

    private fun handlePermissionResult(result: RequestPermissionResult) {
        val ctx = this.applicationContext
        when (result) {
            RequestPermissionResult.ALREADY_HAS -> {
                viewModel.startServiceIfNeeded(ctx)
            }
            RequestPermissionResult.NEED_EXPLAIN -> {
                Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show()
            }
            RequestPermissionResult.NEED_REQUEST -> {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            RequestPermissionResult.DENIED -> {
                Toast.makeText(ctx, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            RequestPermissionResult.GRANTED -> {
                viewModel.startServiceIfNeeded(ctx)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.handleOnCreate(this)
        setContent {
            RootView(this)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleOnResume(this)
    }

    @Composable
    fun RootView(context: Context) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            if (!viewModel.hasPostNotificationPermission.value) {
                Text("Welcome to use Zebra Kotlin Demo App")
                Text("we need permission to show notification")
                Text("please allow")
                RoundButton("Setup Notification Permission") {
                    askNotificationPermission()
                }
                ShowPngImage(R.drawable.allow_notification)
            } else {
                if (!viewModel.servicePrepared.value) {
                    Text("we need service running")
                    Text("please click start service")
                    Text("UI will automatically update when service is ready")
                    Text("push notification will show on status bar a few seconds later")
                    RoundButton("Start Service") {
                        viewModel.startServiceIfNeeded(context)
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
    }
}