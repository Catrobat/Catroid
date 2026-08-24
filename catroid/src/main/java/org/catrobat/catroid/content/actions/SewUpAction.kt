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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.stage.StageActivity
import kotlin.math.cos
import kotlin.math.sin

class SewUpAction : TemporalAction() {
    lateinit var sprite: Sprite

    override fun begin() {
        val look = sprite.look ?: throw IllegalStateException("Sprite look is null")
        val radians = Math.toRadians(look.motionDirectionInUserInterfaceDimensionUnit.toDouble())

        sprite.runningStitch?.pause()

        val embroideryPatternManager = StageActivity.stageListener?.embroideryPatternManager
            ?: throw IllegalStateException("StageActivity.stageListener or embroideryPatternManager is null")

        var x = look.xInUserInterfaceDimensionUnit
        var y = look.yInUserInterfaceDimensionUnit
        val zIndex = look.zIndex
        val threadColor = sprite.embroideryThreadColor

        embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(x, y, zIndex, sprite, threadColor)
        )

        x += (STEPS * sin(radians)).toFloat()
        y += (STEPS * cos(radians)).toFloat()
        embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(x, y, zIndex, sprite, threadColor)
        )

        x -= (STEPS * sin(radians)).toFloat()
        y -= (STEPS * cos(radians)).toFloat()
        embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(x, y, zIndex, sprite, threadColor)
        )

        x -= (STEPS * sin(radians)).toFloat()
        y -= (STEPS * cos(radians)).toFloat()
        embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(x, y, zIndex, sprite, threadColor)
        )

        x += (STEPS * sin(radians)).toFloat()
        y += (STEPS * cos(radians)).toFloat()
        embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(x, y, zIndex, sprite, threadColor)
        )

        sprite.runningStitch?.apply {
            setStartCoordinates(look.xInUserInterfaceDimensionUnit, look.yInUserInterfaceDimensionUnit)
            resume()
        }
    }

    override fun update(delta: Float) {
        // Intentionally empty
    }

    companion object {
        const val STEPS = 3
    }
}
