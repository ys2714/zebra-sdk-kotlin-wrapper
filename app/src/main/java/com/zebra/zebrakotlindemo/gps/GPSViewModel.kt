package com.zebra.zebrakotlindemo.gps

import android.location.GnssStatus
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.collection.MutableObjectList
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GPSViewModel: ViewModel() {

    data class SatelliteInfo(
        val type: Int,
        val cn0: Float
    ) {
        companion object {
            @RequiresApi(Build.VERSION_CODES.R)
            fun create(status: GnssStatus, index: Int) : SatelliteInfo {
                return SatelliteInfo(
                    status.getConstellationType(index),
                    status.getBasebandCn0DbHz(index)
                )
            }
        }

        fun typeString() : String {
            when (type) {
                GnssStatus.CONSTELLATION_GPS -> {
                    // GPS is being used
                    return "GPS Satellite"
                }
                GnssStatus.CONSTELLATION_BEIDOU -> {
                    // BeiDou is being used
                    return "BeiDou Satellite"
                }
                GnssStatus.CONSTELLATION_GALILEO -> {
                    // Galileo is being used
                    return "Galileo Satellite"
                }
                GnssStatus.CONSTELLATION_GLONASS -> {
                    // GLONASS is being used
                    return "GLONASS Satellite"
                }
                GnssStatus.CONSTELLATION_QZSS -> {
                    // QZSS is being used
                    return "QZSS Satellite"
                }
                GnssStatus.CONSTELLATION_IRNSS -> {
                    // NAVIC (IRNSS) is being used
                    return "NAVIC Satellite"
                }
                else -> {
                    // Other constellations
                    return "Other Satellite"
                }
            }
        }
    }

    val gpsPermissionGranted: MutableState<Boolean> = mutableStateOf(false)

    // Returns the estimated horizontal accuracy radius in meters of this location at the 68th percentile confidence level.
    // This means that there is a 68% chance that the true location of the device is within a distance of this uncertainty of the reported location.
    // var horizontalAccuracy: Float = 0.0F
    val horizontalAccuracyState: MutableFloatState = mutableFloatStateOf(0.0f)
    val verticalAccuracyState: MutableFloatState = mutableFloatStateOf(0.0f)
    val bearingAccuracyState: MutableFloatState = mutableFloatStateOf(0.0f)

    val satelliteList: MutableObjectList<SatelliteInfo> = MutableObjectList()

    val satellitesGPS: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_GPS }
        }

    val satellitesSBAS: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_SBAS }
        }

    val satellitesGLONASS: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_GLONASS }
        }

    val satellitesQZSS: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_QZSS }
        }

    val satellitesBEIDOU: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_BEIDOU }
        }

    val satellitesGALILEO: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_GALILEO }
        }

    val satellitesIRNSS: List<SatelliteInfo>
        get() {
            return satelliteList.asList().filter { it.type == GnssStatus.CONSTELLATION_IRNSS }
        }

    fun handleSatelliteStatusChanged(status: GnssStatus) {
        satelliteList.clear()
        for (i in 0 until status.satelliteCount) {
            val info = SatelliteInfo.create(status, i)
            satelliteList.add(info)
        }
    }

    fun handleLocationChanged(location: Location) {
        horizontalAccuracyState.floatValue = location.accuracy
        verticalAccuracyState.floatValue = location.verticalAccuracyMeters
        bearingAccuracyState.floatValue = location.bearingAccuracyDegrees
    }
}