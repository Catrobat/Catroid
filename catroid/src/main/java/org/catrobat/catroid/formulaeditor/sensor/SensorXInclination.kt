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
import kotlin.math.acos

open class SensorXInclination : Sensor {
    override fun getSensorValue(): Double = calculateXInclination()

    protected var rotateOrientation: () -> Int = { -> SensorHandler.rotateOrientation() }
    protected var getRotationMatrixFromVector: (FloatArray, FloatArray) -> Unit =
        { matrix: FloatArray, vector: FloatArray ->
            SensorManager.getRotationMatrixFromVector(
                matrix,
                vector
            )
        }
    protected var getOrientation: (FloatArray, FloatArray) -> FloatArray =
        { r: FloatArray, values: FloatArray -> SensorManager.getOrientation(r, values) }
    protected var remapCoordinateSystem: (FloatArray, Int, Int, FloatArray) -> Boolean =
        { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            SensorManager.remapCoordinateSystem(
                inR,
                X,
                Y,
                outR
            )
        }

    protected fun calculateXInclination(): Double {
        return if (SensorHandler.useRotationVectorFallback) {
            fallBack()
        } else {
            normalOperation()
        }
    }

    private fun getRawInclination(): Float {
        val rawInclinationX: Float
        var rotate: Int
        if (rotateOrientation().also { rotate = it } != 0) {
            rawInclinationX =
                radianToDegreeConst * acos((SensorHandler.accelerationXYZ[1] * rotate).toDouble())
                    .toFloat()
        } else {
            rawInclinationX = radianToDegreeConst * acos(
                SensorHandler.accelerationXYZ[0].toDouble()
            ).toFloat()
        }
        return rawInclinationX
    }

    private fun fallBack(): Double {
        val rawInclinationX: Float = getRawInclination()
        var correctedInclinationX = 0f
        if (rawInclinationX in ninety.toFloat()..oneHundredEighty.toFloat()) {
            correctedInclinationX = if (SensorHandler.signAccelerationZ > 0) {
                -(rawInclinationX - ninety)
            } else {
                -(oneHundredEighty + (ninety - rawInclinationX))
            }
        } else if (rawInclinationX >= 0 && rawInclinationX < ninety) {
            correctedInclinationX = if (SensorHandler.signAccelerationZ > 0) {
                ninety - rawInclinationX
            } else {
                ninety + rawInclinationX
            }
        }
        if (rotateOrientation() != 0) {
            correctedInclinationX = -correctedInclinationX
        }
        return correctedInclinationX.toDouble()
    }

    private fun normalOperation(): Double {
        var rotate: Int
        val rotationMatrixOut = FloatArray(rotationMatrixSize)
        orientations = FloatArray(orientationSize)
        getRotationMatrixFromVector(
            SensorHandler.rotationMatrix,
            SensorHandler.rotationVector
        )
        if (rotateOrientation().also { rotate = it } == 1) {
            remapCoordinateSystem(
                SensorHandler.rotationMatrix,
                SensorManager.AXIS_Y,
                SensorManager.AXIS_MINUS_X,
                rotationMatrixOut
            )
            getOrientation(rotationMatrixOut, orientations)
        } else if (rotate == -1) {
            remapCoordinateSystem(
                SensorHandler.rotationMatrix,
                SensorManager.AXIS_MINUS_Y,
                SensorManager.AXIS_X,
                rotationMatrixOut
            )
            getOrientation(rotationMatrixOut, orientations)
        } else {
            getOrientation(SensorHandler.rotationMatrix, orientations)
        }
        val sensorValue: Double = orientations[2].toDouble()
        return sensorValue * radianToDegreeConst * -1f
    }

    companion object {
        @Volatile
        private var instance: SensorXInclination? = null
        const val radianToDegreeConst = 180f / Math.PI.toFloat()
        const val orientationSize = 3
        const val rotationMatrixSize = 16
        const val ninety = 90
        const val oneHundredEighty = 180
        var orientations = FloatArray(orientationSize)

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorXInclination().also { instance = it }
            }
    }
}
