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
package org.catrobat.catroid.content.actions.conditional

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

open class GlideToAction : TemporalAction() {
    private companion object {
        const val POSITION_DELTA_TOLERANCE = 0.1f
    }

    private var startXValue = 0f
    private var startYValue = 0f
    private var currentXValue = 0f
    private var currentYValue = 0f
    private var endX: Formula? = null
    private var endY: Formula? = null
    var duration: Formula? = null
    lateinit var scope: Scope

    var durationValue = 0f
    private var endXValue = 0f
    private var endYValue = 0f

    private var velocityXValue = 0f
    private var velocityYValue = 0f

    private var restart = false

    override fun begin() {
        var durationInterpretation = interpretFormula(duration, scope, "durationInterpretation") ?: 0f
        val endXInterpretation = interpretFormula(endX, scope, "endXInterpretation")
            ?: run {
                durationInterpretation = 0f
                0f
            }
        val endYInterpretation = interpretFormula(endY, scope, "endYInterpretation")
            ?: run {
                durationInterpretation = 0f
                0f
            }

        if (!restart) {
            super.setDuration(durationInterpretation)
            durationValue = durationInterpretation
            endXValue = endXInterpretation
            endYValue = endYInterpretation
        }
        restart = false

        startXValue = scope.sprite.look.xInUserInterfaceDimensionUnit
        startYValue = scope.sprite.look.yInUserInterfaceDimensionUnit
        currentXValue = startXValue
        currentYValue = startYValue
        if (startXValue == endXInterpretation && startYValue == endYInterpretation) {
            super.finish()
        }
        if (velocityXValue == 0f && velocityYValue == 0f && durationValue != 0f) {
            velocityXValue = (endXValue - startXValue) / durationValue
            velocityYValue = (endYValue - startYValue) / durationValue
        }

        scope.sprite.glidingVelocityX = velocityXValue
        scope.sprite.glidingVelocityY = velocityYValue
        scope.sprite.isGliding = true
    }

    override fun update(percent: Float) {
        val deltaX = scope.sprite.look.xInUserInterfaceDimensionUnit - currentXValue
        val deltaY = scope.sprite.look.yInUserInterfaceDimensionUnit - currentYValue
        if ((kotlin.math.abs(deltaX) > POSITION_DELTA_TOLERANCE) ||
            (kotlin.math.abs(deltaY) > POSITION_DELTA_TOLERANCE)
        ) {
            restart = true
            val remainingDuration = getDuration() - time
            setDuration(remainingDuration)
            durationValue = remainingDuration
            restart()
        } else {
            currentXValue = startXValue + (endXValue - startXValue) * percent
            currentYValue = startYValue + (endYValue - startYValue) * percent
            scope.sprite.look.setPositionInUserInterfaceDimensionUnit(currentXValue, currentYValue)
        }
    }

    override fun end() {
        scope.sprite.isGliding = false
        scope.sprite.glidingVelocityX = 0f
        scope.sprite.glidingVelocityY = 0f
    }

    fun setPosition(x: Formula?, y: Formula?) {
        endX = x
        endY = y
    }

    private fun interpretFormula(formula: Formula?, scope: Scope, name: String): Float? {
        return try {
            formula?.interpretFloat(scope) ?: 0f
        } catch (interpretationException: InterpretationException) {
            Log.d(javaClass.simpleName, "Formula interpretation failed for [$name].", interpretationException)
            null
        }
    }
}
