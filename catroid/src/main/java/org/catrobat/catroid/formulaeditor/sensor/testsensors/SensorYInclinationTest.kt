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

package org.catrobat.catroid.formulaeditor.sensor.testsensors

import android.hardware.SensorManager
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.sensor.SensorYInclination

class SensorYInclinationTest : SensorYInclination() {

    override fun getSensorValue(): Double = super.calculateYInclination()

    fun setRotateOrientationFunction(fkt: () -> Int) {
        super.rotateOrientation = fkt
    }

    fun setGetRotationMatrixFromVectorFunction(fkt: (FloatArray, FloatArray) -> Unit) {
        super.getRotationMatrixFromVector = fkt
    }

    fun setGetOrientationFunction(fkt: (FloatArray, FloatArray) -> FloatArray) {
        super.getOrientation = fkt
    }

    fun setRemapCoordinateSystemFunction(fkt: (FloatArray, Int, Int, FloatArray) -> Boolean) {
        super.remapCoordinateSystem = fkt
    }

    fun resetFunctions() {
        super.rotateOrientation = { -> SensorHandler.rotateOrientation() }
        super.getRotationMatrixFromVector =
            { matrix: FloatArray, vector: FloatArray ->
                SensorManager.getRotationMatrixFromVector(
                    matrix,
                    vector
                )
            }
        super.getOrientation =
            { r: FloatArray, values: FloatArray -> SensorManager.getOrientation(r, values) }
        super.remapCoordinateSystem =
            { inR: FloatArray, X: Int, Y: Int, outR: FloatArray ->
                SensorManager.remapCoordinateSystem(
                    inR,
                    X,
                    Y,
                    outR
                )
            }
    }

    companion object {
        @Volatile
        private var instance: SensorYInclinationTest? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorYInclinationTest().also { instance = it }
            }
    }
}
