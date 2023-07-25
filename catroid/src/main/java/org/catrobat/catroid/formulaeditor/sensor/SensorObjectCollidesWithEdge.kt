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

import com.badlogic.gdx.math.Rectangle
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.common.Conversions
import org.catrobat.catroid.sensing.CollisionDetection
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageListener

class SensorObjectCollidesWithEdge : Sensor {

    override fun getSensorValue(): Double {
        return tryCalculateCollidesWithEdge(SensorHandler.currentSprite.look, StageActivity.stageListener,
                                            SensorHandler.currentProject.screenRectangle)
    }

    private fun tryCalculateCollidesWithEdge(
        look: Look,
        stageListener: StageListener?,
        screen: Rectangle?
    ): Double {
        return if (stageListener?.firstFrameDrawn == true) {
            Conversions.booleanToDouble(
                CollisionDetection.collidesWithEdge(look.currentCollisionPolygon, screen)
            )
        } else {
            Conversions.FALSE
        }
    }

    companion object {
        @Volatile
        private var instance: SensorObjectCollidesWithEdge? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorObjectCollidesWithEdge().also { instance = it }
            }
    }
}
