/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import kotlin.math.atan2
import kotlin.math.pow

class GoThroughAction : TemporalAction() {
    private var scope: Scope? = null
    lateinit var x1: Formula
    lateinit var y1: Formula
    lateinit var x2: Formula
    lateinit var y2: Formula

    private var startX = 0.0
    private var startY = 0.0
    private var throughX = 0.0
    private var throughY = 0.0
    private var endX = 0.0
    private var endY = 0.0
    private var anchorX = 0.0
    private var anchorY = 0.0

    override fun begin() {
        super.begin()
        val activeScope = scope ?: return
        try {
            val sprite = activeScope.sprite
            startX = sprite.look.xInUserInterfaceDimensionUnit.toDouble()
            startY = sprite.look.yInUserInterfaceDimensionUnit.toDouble()
            throughX = x1.interpretDouble(activeScope)
            throughY = y1.interpretDouble(activeScope)
            endX = x2.interpretDouble(activeScope)
            endY = y2.interpretDouble(activeScope)
            anchorX = 2 * throughX - (startX + endX) / 2
            anchorY = 2 * throughY - (startY + endY) / 2
        } catch (interpretationException: InterpretationException) {
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
    }

    override fun update(percent: Float) {
        val activeScope = scope ?: return
        try {
            val look = activeScope.sprite.look
            var previousX = startX
            var previousY = startY
            val steps = 100
            for (i in 0..steps) {
                val timeStep = i.toDouble() / steps
                val inverseTimeStep = 1 - timeStep
                val x =
                    inverseTimeStep.pow(2) * startX + 2 * inverseTimeStep * timeStep * anchorX + timeStep.pow(
                        2
                    ) * endX
                val y =
                    inverseTimeStep.pow(2) * startY + 2 * inverseTimeStep * timeStep * anchorY + timeStep.pow(
                        2
                    ) * endY
                if (x != previousX || y != previousY) {
                    val motionDirection = Math.toDegrees(atan2(x - previousX, y - previousY))
                    look.setMotionDirectionInUserInterfaceDimensionUnit(motionDirection.toFloat())
                }
                look.setPositionInUserInterfaceDimensionUnit(
                    x.toFloat(),
                    y.toFloat()
                )
                previousX = x
                previousY = y
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

    fun setTargetCoordinates(x1: Formula, y1: Formula, x2: Formula, y2: Formula) {
        this.x1 = x1
        this.y1 = y1
        this.x2 = x2
        this.y2 = y2
    }
}
