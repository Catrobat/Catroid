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

import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.PlotArcBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class PlotThroughAction : TemporalAction() {
    private var scope: Scope? = null
    lateinit var x1: Formula
    lateinit var y1: Formula
    lateinit var x2: Formula
    lateinit var y2: Formula

    private var p1: Point = Point()
    private var p2: Point = Point()

    override fun begin() {
        super.begin()
        if (scope == null) {
            return
        }
        try {
            p1.x = x1.interpretInteger(scope)
            p1.y = y1.interpretInteger(scope)
            p2.x = x2.interpretInteger(scope)
            p2.y = y2.interpretInteger(scope)
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
            // calculate slope from current sprite look position to x2, y2
            val startPoint = Point()
            startPoint.x = scope!!.sprite.look.xInUserInterfaceDimensionUnit.toInt()
            startPoint.y = scope!!.sprite.look.yInUserInterfaceDimensionUnit.toInt()

            val anchorPoint = Point()
            anchorPoint.x = (4 * p1.x - startPoint.x - p2.x) / 2
            anchorPoint.y = (4 * p1.y - startPoint.y - p2.y) / 2

            val steps = 100
            for (i in 0..steps) {
                val timeStep = i.toDouble() / steps
                val x = (1 - timeStep).pow(2) * startPoint.x + 2 * (1 - timeStep) * timeStep * anchorPoint.x + timeStep.pow(2) * p2.x
                val y = (1 - timeStep).pow(2) * startPoint.y + 2 * (1 - timeStep) * timeStep * anchorPoint.y + timeStep.pow(2) * p2.y
                scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(x.toFloat(), y.toFloat())
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