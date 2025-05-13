/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.PlotArcBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import kotlin.math.cos
import kotlin.math.sin

class PlotArcAction : TemporalAction() {
    private var scope: Scope? = null
    private var direction: PlotArcBrick.Directions? = null
    private var radius: Formula? = null
    private var degrees: Formula? = null

    private var degreesValue: Double = 0.0
    private var radiusValue: Double = 0.0
    private var centerX: Double = 0.0
    private var centerY: Double = 0.0
    private var angle: Double = Math.toRadians(90.0)

    override fun begin() {
        super.begin()
        if (scope == null) {
            return
        }
        try {
            degreesValue = if (degrees == null) 0.0 else
                degrees!!.interpretDouble(scope) * if (direction == PlotArcBrick.Directions.LEFT)
                    -1 else 1

            radiusValue = if (radius == null) 0.0 else
                radius!!.interpretDouble(scope)

            val sprite = scope!!.sprite
            val x = sprite.look.xInUserInterfaceDimensionUnit
            val y = sprite.look.yInUserInterfaceDimensionUnit
            angle = Math.toRadians(sprite.look.rotation.toDouble())
            centerX = x + radiusValue * cos(angle)
            centerY = y + radiusValue * sin(angle)

        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) {
        try {
            for (i in 0 until 101){
                val radians = Math.toRadians(degreesValue * i/100)
                val x1 = centerX - radiusValue * cos(radians + angle)
                val y1 = centerY - radiusValue * sin(radians + angle)
                scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(x1.toFloat(), y1.toFloat())
            }
            scope!!.sprite.look.rotation = Math.toDegrees(angle - degreesValue).toFloat()
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    fun setScope(scope: Scope?) {
        this.scope = scope
    }

    fun setDirection(direction: PlotArcBrick.Directions?) {
        this.direction = direction
    }

    fun setRadius(radius: Formula?){
        this.radius = radius
    }

    fun setDegrees(degrees: Formula?){
        this.degrees = degrees
    }
}