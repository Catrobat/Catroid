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
package org.catrobat.catroid.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.embroidery.DSTStitchCommand
import org.catrobat.catroid.stage.StageActivity

class SewUpAction : TemporalAction() {
    private var sprite: Sprite? = null
    override fun begin() {
        val radians = Math.toRadians(sprite!!.look.directionInUserInterfaceDimensionUnit.toDouble())
        sprite!!.runningStitch.pause()
        var x = sprite!!.look.xInUserInterfaceDimensionUnit
        var y = sprite!!.look.yInUserInterfaceDimensionUnit
        StageActivity.stageListener.embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(
                x,
                y,
                sprite!!.look.zIndex,
                sprite,
                sprite!!.embroideryThreadColor
            )
        )
        x += (STEPS * Math.sin(radians)).toFloat()
        y += (STEPS * Math.cos(radians)).toFloat()
        StageActivity.stageListener.embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(
                x,
                y,
                sprite!!.look.zIndex,
                sprite,
                sprite!!.embroideryThreadColor
            )
        )
        x -= (STEPS * Math.sin(radians)).toFloat()
        y -= (STEPS * Math.cos(radians)).toFloat()
        StageActivity.stageListener.embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(
                x,
                y,
                sprite!!.look.zIndex,
                sprite,
                sprite!!.embroideryThreadColor
            )
        )
        x -= (STEPS * Math.sin(radians)).toFloat()
        y -= (STEPS * Math.cos(radians)).toFloat()
        StageActivity.stageListener.embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(
                x,
                y,
                sprite!!.look.zIndex,
                sprite,
                sprite!!.embroideryThreadColor
            )
        )
        x += (STEPS * Math.sin(radians)).toFloat()
        y += (STEPS * Math.cos(radians)).toFloat()
        StageActivity.stageListener.embroideryPatternManager.addStitchCommand(
            DSTStitchCommand(
                x,
                y,
                sprite!!.look.zIndex,
                sprite,
                sprite!!.embroideryThreadColor
            )
        )
        sprite!!.runningStitch.setStartCoordinates(
            sprite!!.look.xInUserInterfaceDimensionUnit,
            sprite!!.look.yInUserInterfaceDimensionUnit
        )
        sprite!!.runningStitch.resume()
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }

    override fun update(delta: Float) {}

    companion object {
        const val STEPS = 3
    }
}