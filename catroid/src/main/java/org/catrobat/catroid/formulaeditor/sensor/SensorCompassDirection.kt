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

open class SensorCompassDirection : Sensor {
    override fun getSensorValue(): Double = calculateCompassDirection(
        rotateOrientation = { -> SensorHandler.rotateOrientation() },
        getOrientation = { r: FloatArray, v: FloatArray -> SensorManager.getOrientation(r, v) },
        remapCoordinateSystem = { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
            SensorManager.remapCoordinateSystem(
                inR,
                X,
                Y,
                outR
            )
        },
        getRotationMatrixFromVector = { r: FloatArray, vector: FloatArray ->
            SensorManager.getRotationMatrixFromVector(
                r,
                vector
            )
        },
    )

    var orientations = FloatArray(orientationsSize)

    protected fun calculateCompassDirection(
        rotateOrientation: () -> Int,
        getOrientation: (r: FloatArray, v: FloatArray) -> FloatArray,
        remapCoordinateSystem: (inR: FloatArray, X: Int, Y: Int, outR: FloatArray) -> Boolean,
        getRotationMatrixFromVector: (r: FloatArray, vector: FloatArray) -> Unit,
    ): Double {
        val rotationMatrixOut = FloatArray(rotationMatrixSize)
        var rotate: Int
        if (!SensorHandler.useRotationVectorFallback) {
            getRotationMatrixFromVector(
                SensorHandler.rotationMatrix,
                SensorHandler.rotationVector
            )
        }
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
        val sensorValue: Double = orientations[0].toDouble()
        return sensorValue * radianToDegreeConst * -1.0
    }

    companion object {
        @Volatile
        private var instance: SensorCompassDirection? = null
        const val orientationsSize = 3
        const val rotationMatrixSize = 16
        const val radianToDegreeConst = 180f / Math.PI.toFloat()

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorCompassDirection().also { instance = it }
            }
    }
}
