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
    private var x1: Formula? = null
    private var y1: Formula? = null
    private var x2: Formula? = null
    private var y2: Formula? = null

    private var p1: Point = Point()
    private var p2: Point = Point()

    override fun begin() {
        super.begin()
        if (scope == null) {
            return
        }
        try {
            p1.x = if (x1 == null) 0 else x1!!.interpretInteger(scope)
            p1.y = if (y1 == null) 0 else y1!!.interpretInteger(scope)
            p2.x = if (x2 == null) 0 else x2!!.interpretInteger(scope)
            p2.y = if (y2 == null) 0 else y2!!.interpretInteger(scope)
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
            // calculate slope from current sprite look position to x2, y2
            val ps = Point()
            ps.x = scope!!.sprite.look.xInUserInterfaceDimensionUnit.toInt()
            ps.y = scope!!.sprite.look.yInUserInterfaceDimensionUnit.toInt()

            val pa = Point()
            pa.x = (4 * p1.x - ps.x - p2.x) / 2
            pa.y = (4 * p1.y - ps.y - p2.y) / 2

            val steps = 100
            for (i in 0..steps) {
                val t = i.toDouble() / steps
                val x = (1 - t).pow(2) * ps.x + 2 * (1 - t) * t * pa.x + t.pow(2) * p2.x
                val y = (1 - t).pow(2) * ps.y + 2 * (1 - t) * t * pa.y + t.pow(2) * p2.y
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

    fun setTargetCoordinates(x1: Formula?, y1: Formula?, x2: Formula?, y2: Formula?) {
        this.x1 = x1
        this.y1 = y1
        this.x2 = x2
        this.y2 = y2
    }
}