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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException

private const val DELTA = 0.1f

abstract class GlideToActionBase : TemporalAction() {
    var scope: Scope? = null
    var destinationSprite: Sprite? = null

    private var startXValue = 0f
    private var startYValue = 0f
    private var currentXValue = 0f
    private var currentYValue = 0f
    private var duration: Formula? = null
    protected var endX: Formula? = null
    protected var endY: Formula? = null
    private var durationValue = 0f
    private var endXValue = 0f
    private var endYValue = 0f

    private var velocityXValue = 0f
    private var velocityYValue = 0f

    private var restart = false

    override fun begin() {
        setEndPosition()

        val durationInterpretation = setFloatValue(duration)
        val endXInterpretation = setFloatValue(endX)
        val endYInterpretation = setFloatValue(endY)

        if (!restart) {
            if (duration != null) {
                super.setDuration(durationInterpretation)
                durationValue = durationInterpretation
            }
            endXValue = endXInterpretation
            endYValue = endYInterpretation
        }
        restart = false

        startXValue = scope!!.sprite.look.xInUserInterfaceDimensionUnit
        startYValue = scope!!.sprite.look.yInUserInterfaceDimensionUnit
        currentXValue = startXValue
        currentYValue = startYValue
        if (startXValue == endXInterpretation && startYValue == endYInterpretation) {
            super.finish()
        }
        if (velocityXValue == 0f && velocityYValue == 0f && durationValue != 0f) {
            velocityXValue = (endXValue - startXValue) / durationValue
            velocityYValue = (endYValue - startYValue) / durationValue
        }

        scope!!.sprite.glidingVelocityX = velocityXValue
        scope!!.sprite.glidingVelocityY = velocityYValue
        scope!!.sprite.isGliding = true
    }

    override fun update(percent: Float) {
        val deltaX = scope!!.sprite.look.xInUserInterfaceDimensionUnit - currentXValue
        val deltaY = scope!!.sprite.look.yInUserInterfaceDimensionUnit - currentYValue

        val isDeltaXInRange = -DELTA > deltaX || deltaX > DELTA
        val isDeltaYInRange = -DELTA > deltaY || deltaY > DELTA
        if (isDeltaXInRange || isDeltaYInRange) {
            restart = true
            setDuration(getDuration() - time)
            restart()
        } else {
            currentXValue = startXValue + (endXValue - startXValue) * percent
            currentYValue = startYValue + (endYValue - startYValue) * percent
            scope!!.sprite.look.setPositionInUserInterfaceDimensionUnit(
                currentXValue,
                currentYValue
            )
        }
    }

    override fun end() {
        scope!!.sprite.isGliding = false
        scope!!.sprite.glidingVelocityX = 0f
        scope!!.sprite.glidingVelocityY = 0f
    }

    fun getXPosition(): Float = this.endXValue

    fun getYPosition(): Float = this.endYValue

    fun setDuration(duration: Formula?) {
        this.duration = duration
    }

    abstract fun setEndPosition()

    private fun setFloatValue(formula: Formula?): Float {
        var result: Float
        try {
            result =
                if (formula == null) java.lang.Float.valueOf(0f) else formula!!.interpretFloat(scope)
        } catch (interpretationException: InterpretationException) {
            result = 0f
            Log.d(
                javaClass.simpleName,
                "Formula interpretation for this specific Brick failed.",
                interpretationException
            )
        }

        return result
    }
}
