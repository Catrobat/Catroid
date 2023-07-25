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

import org.catrobat.catroid.formulaeditor.SensorHandler

open class SensorAccelerationX : Sensor {
    override fun getSensorValue(): Double {
        val x: Double = SensorHandler.linearAcceleration.accelerationX
        val y: Double = SensorHandler.linearAcceleration.accelerationY
        return getAcceleration(x, y, SensorHandler.rotateOrientation())
    }

    protected fun getAcceleration(x: Double, y: Double, rotate: Int) : Double {
        return SensorHandler.getXAccordingToRotation(x, y, rotate)
    }

    companion object {
        @Volatile
        private var instance: SensorAccelerationX? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorAccelerationX().also { instance = it }
            }
    }
}
