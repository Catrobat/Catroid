/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.content.actions.conditional

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

open class GlideToAction : TemporalAction() {
    private var startX = 0f
    private var startY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var endX: Formula? = null
    private var endY: Formula? = null
    private var scope: Scope? = null
    private var duration: Formula? = null
        get() = this.duration

    private var endXValue = 0f
    private var endYValue = 0f
    private var restart = false
    override fun begin() {
        var durationInterpretation: Float?
        var endXInterpretation = 0f
        var endYInterpretation = 0f
        try {
            durationInterpretation =
                if (duration == null) java.lang.Float.valueOf(0f) else duration!!.interpretFloat(
                    scope
                )
        } catch (interpretationException: InterpretationException) {
            durationInterpretation = 0f
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        try {
            endXInterpretation =
                if (endX == null) java.lang.Float.valueOf(0f) else endX!!.interpretFloat(scope)
        } catch (interpretationException: InterpretationException) {
            durationInterpretation = 0f
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        try {
            endYInterpretation =
                if (endY == null) java.lang.Float.valueOf(0f) else endY!!.interpretFloat(scope)
        } catch (interpretationException: InterpretationException) {
            durationInterpretation = 0f
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }
        if (!restart) {
            if (duration != null) {
                super.setDuration(durationInterpretation!!)
            }
            endXValue = endXInterpretation
            endYValue = endYInterpretation
        }
        restart = false
        startX = scope!!.sprite.look.xInUserInterfaceDimensionUnit
        startY = scope!!.sprite.look.yInUserInterfaceDimensionUnit
        currentX = startX
        currentY = startY
        if (startX == endXInterpretation && startY == endYInterpretation) {
            super.finish()
        }
    }

    override fun update(percent: Float) {
        val deltaX = scope!!.sprite.look.xInUserInterfaceDimensionUnit - currentX
        val deltaY = scope!!.sprite.look.yInUserInterfaceDimensionUnit - currentY
        if (-0.1f > deltaX || deltaX > 0.1f || -0.1f > deltaY || deltaY > 0.1f) {
            restart = true
            setDuration(getDuration() - time)
            restart()
        } else {
            currentX = startX + (endXValue - startX) * percent
            currentY = startY + (endYValue - startY) * percent
            scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(currentX, currentY)
        }
    }

    fun setPosition(x: Formula?, y: Formula?) {
        endX = x
        endY = y
    }

    fun setDuration(duration: Formula) {
        this.duration = duration
    }
    fun setScope(scope: Scope){
        this.scope = scope
    }
}