/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.SystemClock
import androidx.core.app.ActivityCompat

class GpsStatusHandler(private val sensorHandler: SensorHandler) {
    private var gnssCallback: GNSSCallback? = null
    private var lastLocationGpsMillis: Long = 0
    private var lastLocationGps: Location? = null

    fun registerGNSSCallback(locationManager: LocationManager, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            gnssCallback = GNSSCallback()
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationManager.registerGnssStatusCallback(context.mainExecutor, gnssCallback!!)
        } else
            gnssCallback = GNSSCallback()
        locationManager.registerGnssStatusCallback(gnssCallback!!)
    }

    fun deregisterGNSSCallback(locationManager: LocationManager) {
        if (gnssCallback != null) {
            locationManager.unregisterGnssStatusCallback(gnssCallback!!)
        }
    }

    fun setLastLocationAndGpsMillis(lastLocationGps: Location?, lastLocationGpsMillis: Long) {
        this.lastLocationGps = lastLocationGps
        this.lastLocationGpsMillis = lastLocationGpsMillis
    }

    private inner class GNSSCallback : GnssStatus.Callback() {
        override fun onStopped() {
            sensorHandler.setIsGpsConnected(false)
        }

        override fun onFirstFix(ttffMillis: Int) {
            sensorHandler.setIsGpsConnected(true)
        }

        override fun onSatelliteStatusChanged(status: GnssStatus) {
            if (lastLocationGps != null) {
                sensorHandler.setIsGpsConnected(
                    SystemClock.elapsedRealtime() - lastLocationGpsMillis < 3000
                )
            }
        }
    }
}
