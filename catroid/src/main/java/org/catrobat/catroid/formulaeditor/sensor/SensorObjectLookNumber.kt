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

import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.formulaeditor.SensorHandler

open class SensorObjectLookNumber : Sensor {

    override fun getSensorValue(): Double = getLookNumber(
    SensorHandler.currentSprite.lookList,
    SensorHandler.currentSprite.look.lookData
    )

    protected fun getLookNumber(list: List<LookData?>, data: LookData?): Double {
        val lookData = data ?: if (list.isNotEmpty()) {
            list[0]
        } else {
            null
        }
        return tryGetLookNumber(lookData, list)
    }

    private fun tryGetLookNumber(lookData: LookData?, lookDataList: List<LookData?>) =
        lookData?.let { lookDataList.indexOf(it) + 1.0 } ?: 1.0

    companion object {
        @Volatile
        private var instance: SensorObjectLookNumber? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorObjectLookNumber().also { instance = it }
            }
    }
}
