/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.formula.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*

object SensorListener {

    enum class SensorType {
        X_ACCELERATION, Y_ACCELERATION, Z_ACCELERATION, X_INCLINATION, Y_INCLINATION,
        DATE_YEAR, DATE_MONTH, DATE_DAY,
        HOUR, MINUTE, SECOND
    }


    val accelerationListener = AccelerationListener()
    val rotationListener  = RotationListener()

    fun registerListeners(sensorManager: SensorManager) {

        if (!registerListener(sensorManager, rotationListener, Sensor.TYPE_ROTATION_VECTOR)) {
            if (!registerListener(sensorManager, rotationListener, Sensor.TYPE_LINEAR_ACCELERATION)) {
                registerListener(sensorManager, rotationListener, Sensor.TYPE_ACCELEROMETER)
            }
        }

        if (!registerListener(sensorManager, accelerationListener, Sensor.TYPE_LINEAR_ACCELERATION)) {
            registerListener(sensorManager, accelerationListener, Sensor.TYPE_ACCELEROMETER)
        }
    }

    private fun registerListener(sensorManager: SensorManager, listener: SensorEventListener, type: Int): Boolean {
        return sensorManager.registerListener(listener, sensorManager.getDefaultSensor(type), SensorManager
                .SENSOR_DELAY_GAME)
    }

    fun unregisterListeners(sensorManager: SensorManager) {
        sensorManager.unregisterListener(accelerationListener)
        sensorManager.unregisterListener(rotationListener)
    }

    fun getSensorValue(type: SensorType) : Double {

        when (type) {
            SensorType.X_ACCELERATION -> {
                return accelerationListener.accelerationVector[1].toDouble()
            }

            SensorType.Y_ACCELERATION -> {
                return accelerationListener.accelerationVector[1].toDouble()
            }

            SensorType.Z_ACCELERATION -> {
                return accelerationListener.accelerationVector[2].toDouble()
            }

            SensorType.X_INCLINATION -> {
                return rotationListener.rotationVector[2].toDouble()
            }

            SensorType.Y_INCLINATION -> {
                return rotationListener.rotationVector[1].toDouble()
            }

            SensorType.DATE_YEAR -> return Calendar.getInstance().get(Calendar.YEAR).toDouble()
            SensorType.DATE_MONTH -> return Calendar.getInstance().get(Calendar.MONTH).toDouble()
            SensorType.DATE_DAY -> return Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toDouble()
            SensorType.HOUR -> return Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toDouble()
            SensorType.MINUTE -> return Calendar.getInstance().get(Calendar.MINUTE).toDouble()
            SensorType.SECOND -> return Calendar.getInstance().get(Calendar.SECOND).toDouble()
        }
    }

    class AccelerationListener : SensorEventListener {

        val gravity = FloatArray(3)
        val accelerationVector = FloatArray(3)

        override fun onSensorChanged(event: SensorEvent?) {
            when (event?.sensor?.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val alpha = 0.8f

                    gravity[0] = alpha * gravity[0] + (1 - alpha) * (event.values?.get(0) ?: 0f)
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * (event.values?.get(1) ?: 0f)
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * (event.values?.get(2) ?: 0f)

                    accelerationVector[0] = -1f * (event.values?.get(0) ?: 0f - gravity[0])
                    accelerationVector[1] = -1f * (event.values?.get(1) ?: 0f - gravity[1])
                    accelerationVector[2] = -1f * (event.values?.get(2) ?: 0f - gravity[2])
                }

                Sensor.TYPE_LINEAR_ACCELERATION -> {
                    accelerationVector[0] = event.values?.get(0) ?: 0f
                    accelerationVector[1] = event.values?.get(1) ?: 0f
                    accelerationVector[2] = event.values?.get(2) ?: 0f
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    class RotationListener : SensorEventListener {

        val rotationVector = FloatArray(3)

        override fun onSensorChanged(event: SensorEvent?) {
            when (event?.sensor?.type) {
                Sensor.TYPE_ROTATION_VECTOR -> {
                    rotationVector[0] = event.values?.get(0) ?: 0f
                    rotationVector[1] = event.values?.get(1) ?: 0f
                    rotationVector[2] = event.values?.get(2) ?: 0f

                    val inR = FloatArray(16)

                    SensorManager.getRotationMatrixFromVector(inR, rotationVector)
                    SensorManager.getOrientation(inR, rotationVector)
                }

                Sensor.TYPE_LINEAR_ACCELERATION -> {
                }

                Sensor.TYPE_ACCELEROMETER -> {
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }
}
