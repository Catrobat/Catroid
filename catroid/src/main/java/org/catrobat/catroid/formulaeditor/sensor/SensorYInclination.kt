/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.sensor

import android.hardware.SensorManager
import org.catrobat.catroid.formulaeditor.SensorHandler
import kotlin.math.abs
import kotlin.math.acos

class SensorYInclination : Sensor {
    override fun getSensorValue(): Double = calculateYInclination()

    var orientations = FloatArray(orientationSize)

    private fun getRawInclination(): Float {
        var rotate: Int
        val rawInclinationY: Float
        if (SensorHandler.rotateOrientation().also { rotate = it } != 0) {
            rawInclinationY =
                radianToDegreeConst * acos((SensorHandler.accelerationXYZ[0] * rotate).toDouble())
                    .toFloat()
        } else {
            rawInclinationY = radianToDegreeConst * acos(
                SensorHandler.accelerationXYZ[1].toDouble()
            ).toFloat()
        }
        return rawInclinationY
    }
    private fun fallback(): Double {
        val rawInclinationY: Float = getRawInclination()
        var correctedInclinationY = 0f
        if (rawInclinationY in ninety.toFloat()..oneHundredEighty.toFloat()) {
            correctedInclinationY = if (SensorHandler.signAccelerationZ > 0) {
                -(rawInclinationY - ninety)
            } else {
                -(oneHundredEighty + (ninety - rawInclinationY))
            }
        } else if (rawInclinationY >= 0 && rawInclinationY < ninety) {
            correctedInclinationY = if (SensorHandler.signAccelerationZ > 0) {
                ninety - rawInclinationY
            } else {
                ninety + rawInclinationY
            }
        }
        return correctedInclinationY.toDouble()
    }

    private fun normalOperation(): Double {
        val rotationMatrixOut = FloatArray(rotationMatrixSize)
        orientations = FloatArray(orientationSize)
        SensorManager.getRotationMatrixFromVector(
            SensorHandler.rotationMatrix,
            SensorHandler.rotationVector
        )
        if (SensorHandler.rotateOrientation() == 1) {
            SensorManager.remapCoordinateSystem(
                SensorHandler.rotationMatrix,
                SensorManager.AXIS_Y,
                SensorManager.AXIS_MINUS_X,
                rotationMatrixOut
            )
            SensorManager.getOrientation(rotationMatrixOut, orientations)
        } else if (SensorHandler.rotateOrientation() == -1) {
            SensorManager.remapCoordinateSystem(
                SensorHandler.rotationMatrix,
                SensorManager.AXIS_MINUS_Y,
                SensorManager.AXIS_X,
                rotationMatrixOut
            )
            SensorManager.getOrientation(rotationMatrixOut, orientations)
        } else {
            SensorManager.getOrientation(SensorHandler.rotationMatrix, orientations)
        }
        val xInclinationUsedToExtendRangeOfRoll =
            orientations[2] * radianToDegreeConst * -1f
        val sensorValue: Double = orientations[1].toDouble()
        return if (abs(xInclinationUsedToExtendRangeOfRoll) <= ninety.toFloat()) {
            sensorValue * radianToDegreeConst * -1f
        } else {
            val uncorrectedYInclination =
                sensorValue.toFloat() * radianToDegreeConst * -1f
            if (uncorrectedYInclination > 0f) {
                oneHundredEighty.toDouble() - uncorrectedYInclination
            } else {
                -oneHundredEighty.toDouble() - uncorrectedYInclination
            }
        }
    }

    private fun calculateYInclination(): Double {
        return if (SensorHandler.useRotationVectorFallback) {
            fallback()
        } else {
            normalOperation()
        }
    }

    companion object {
        @Volatile
        private var instance: SensorYInclination? = null
        const val orientationSize = 3
        const val rotationMatrixSize = 16
        const val radianToDegreeConst = 180f / Math.PI.toFloat()
        const val ninety = 90
        const val oneHundredEighty = 180

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorYInclination().also { instance = it }
            }
    }
}
