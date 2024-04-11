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

import android.graphics.PointF
import com.badlogic.gdx.math.Polygon
import org.catrobat.catroid.content.Look
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.sensing.CollisionDetection
import org.catrobat.catroid.utils.TouchUtil

open class SensorObjectCollidesWithFinger : Sensor {

    override fun getSensorValue(): Double =
        calculateCollidesWithFinger(SensorHandler.currentSprite.look) { p: Array<Polygon>, points: ArrayList<PointF> ->
            CollisionDetection.collidesWithFinger(p, points)
        }

    protected fun calculateCollidesWithFinger(
        look: Look,
        collisionDetector: (
            Array<Polygon>,
            ArrayList<PointF>,
        ) -> Double
    ): Double =
        collisionDetector(look.currentCollisionPolygon, TouchUtil.getCurrentTouchingPoints())

    companion object {
        @Volatile
        private var instance: SensorObjectCollidesWithFinger? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SensorObjectCollidesWithFinger().also { instance = it }
            }
    }
}
