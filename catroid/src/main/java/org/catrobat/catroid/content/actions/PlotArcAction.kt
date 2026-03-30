/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class PlotArcAction : TemporalAction() {
    private var scope: Scope? = null
    private var direction: PlotArcBrick.Directions = PlotArcBrick.Directions.LEFT
    lateinit var radius: Formula
    lateinit var degrees: Formula

    private var degreesValue: Double = 0.0
    private var radiusValue: Double = 0.0
    private var centerX: Double = 0.0
    private var centerY: Double = 0.0
    private var radiusAngle: Double = 0.0

    override fun begin() {
        super.begin()
        if (scope == null) {
            return
        }
        try {
            val interpretedDegrees = degrees.interpretDouble(scope)
            val effectiveDirection = if (interpretedDegrees < 0) {
                if (direction == PlotArcBrick.Directions.LEFT) PlotArcBrick.Directions.RIGHT else PlotArcBrick.Directions.LEFT
            } else {
                direction
            }
            degreesValue = abs(interpretedDegrees) * if (effectiveDirection == PlotArcBrick.Directions.LEFT) 1 else -1
            radiusValue = radius.interpretDouble(scope)
            val sprite = scope!!.sprite
            val x = sprite.look.xInUserInterfaceDimensionUnit
            val y = sprite.look.yInUserInterfaceDimensionUnit
            val motionDirection = Math.toRadians(sprite.look.motionDirectionInUserInterfaceDimensionUnit.toDouble())
            val normalX = if (effectiveDirection == PlotArcBrick.Directions.LEFT) -cos(motionDirection) else cos(motionDirection)
            val normalY = if (effectiveDirection == PlotArcBrick.Directions.LEFT) sin(motionDirection) else -sin(motionDirection)
            centerX = x + radiusValue * normalX
            centerY = y + radiusValue * normalY
            radiusAngle = atan2(y - centerY, x - centerX)
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) {
        if (scope == null) {
            return
        }
        try {
            var previousX = scope!!.sprite.look.xInUserInterfaceDimensionUnit.toDouble()
            var previousY = scope!!.sprite.look.yInUserInterfaceDimensionUnit.toDouble()
            for (i in 0 until 101) {
                val radians = Math.toRadians(degreesValue * i / 100)
                val x1 = centerX + radiusValue * cos(radiusAngle + radians)
                val y1 = centerY + radiusValue * sin(radiusAngle + radians)
                if (x1 != previousX || y1 != previousY) {
                    val motionDirection = Math.toDegrees(atan2(x1 - previousX, y1 - previousY))
                    scope!!.sprite.look.setMotionDirectionInUserInterfaceDimensionUnit(motionDirection.toFloat())
                }
                scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(
                    x1.toFloat(),
                    y1.toFloat()
                )
                previousX = x1
                previousY = y1
            }
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

    fun setDirection(direction: PlotArcBrick.Directions) {
        this.direction = direction
    }
}
