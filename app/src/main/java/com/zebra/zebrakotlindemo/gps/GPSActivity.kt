package com.zebra.zebrakotlindemo.gps

import ZebraBaseComponentActivity
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat

class GPSActivity: ZebraBaseComponentActivity() {

    private val viewModel by viewModels<GPSViewModel>()

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        requestPermissionWithName(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            RootView(viewModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun handlePermissionGranted() {
        viewModel.gpsPermissionGranted.value = true
        registerGnssStatusCallback(viewModel)
        requestLocation()
        super.handlePermissionGranted()
    }

    override fun handlePermissionDenied() {
        viewModel.gpsPermissionGranted.value = false
        super.handlePermissionDenied()
    }

    @Composable
    fun RootView(viewModel: GPSViewModel) {
        Column {
            if (!viewModel.gpsPermissionGranted.value) {
                Text(text = "Please allow use GPS permission in system setting page", color = Color.Red)
            }
            Text("GPS count: ${viewModel.satellitesGPS.size}", color = Color.Gray)
            Text("SBAS count: ${viewModel.satellitesSBAS.size}", color = Color.Gray)
            Text("GLONASS count: ${viewModel.satellitesGLONASS.size}", color = Color.Gray)
            Text("QZSS count: ${viewModel.satellitesQZSS.size}", color = Color.Gray)
            Text("BEIDOU count: ${viewModel.satellitesBEIDOU.size}", color = Color.Gray)
            Text("GALILEO count: ${viewModel.satellitesGALILEO.size}", color = Color.Gray)
            Text("IRNSS count: ${viewModel.satellitesIRNSS.size}", color = Color.Gray)

            Text("Horizontal Accuracy: ${viewModel.horizontalAccuracyState.floatValue}", color = Color.Gray)
            Text("Vertical Accuracy: ${viewModel.verticalAccuracyState.floatValue}", color = Color.Gray)
            Text("Bearing Accuracy: ${viewModel.bearingAccuracyState.floatValue}", color = Color.Gray)

            RequestButton()
            BackButton()
        }
    }

    @Composable
    fun RequestButton() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
            , onClick = {
                requestLocation()
            }
            , colors = ButtonColors(
                containerColor = Color(0xFF0073E6),
                contentColor = Color(0xFFFFFFFF),
                disabledContentColor = Color.White,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(text = "Request Location")
        }
    }

    @Composable
    fun BackButton() {
        Button(
            modifier = Modifier
                .fillMaxWidth()
            , onClick = {
                finish()
            }
            , colors = ButtonColors(
                containerColor = Color(0xFF0073E6),
                contentColor = Color(0xFFFFFFFF),
                disabledContentColor = Color.White,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(text = "Back")
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            registerGnssStatusCallback(viewModel)
            requestLocation()
        }
    }

    private fun requestLocation() {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                viewModel.handleLocationChanged(location)
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                3000,
                0.0f,
                locationListener
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun registerGnssStatusCallback(viewModel: GPSViewModel) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.registerGnssStatusCallback(
                this.mainExecutor,
                object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    super.onSatelliteStatusChanged(status)
                    viewModel.handleSatelliteStatusChanged(status)
                }
            })
        }
    }
}