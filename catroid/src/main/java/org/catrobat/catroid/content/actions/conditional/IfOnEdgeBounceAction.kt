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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.EventWrapper
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.eventids.BounceOffEventId

class IfOnEdgeBounceAction : TemporalAction() {
    private var sprite: Sprite? = null
    override fun update(percent: Float) {
        val width = sprite!!.look.widthInUserInterfaceDimensionUnit
        val height = sprite!!.look.heightInUserInterfaceDimensionUnit
        var xPosition = sprite!!.look.xInUserInterfaceDimensionUnit
        var yPosition = sprite!!.look.yInUserInterfaceDimensionUnit
        val halfVirtualScreenWidth =
            ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenWidth / 2
        val halfVirtualScreenHeight =
            ProjectManager.getInstance().currentProject.xmlHeader.virtualScreenHeight / 2
        var newDirection = sprite!!.look.directionInUserInterfaceDimensionUnit
        if (xPosition < -halfVirtualScreenWidth + width / 2) {
            if (isLookingLeft(newDirection)) {
                newDirection = -newDirection
                fireBounceEvent()
            }
            xPosition = -halfVirtualScreenWidth + width / 2
        } else if (xPosition > halfVirtualScreenWidth - width / 2) {
            if (isLookingRight(newDirection)) {
                newDirection = -newDirection
                fireBounceEvent()
            }
            xPosition = halfVirtualScreenWidth - width / 2
        }
        if (yPosition < -halfVirtualScreenHeight + height / 2) {
            if (isLookingDown(newDirection)) {
                newDirection = 180f - newDirection
                fireBounceEvent()
            }
            yPosition = -halfVirtualScreenHeight + height / 2
        } else if (yPosition > halfVirtualScreenHeight - height / 2) {
            if (isLookingUp(newDirection)) {
                newDirection = 180f - newDirection
                fireBounceEvent()
            }
            yPosition = halfVirtualScreenHeight - height / 2
        }
        sprite!!.look.directionInUserInterfaceDimensionUnit = newDirection
        sprite!!.look.setPositionInUserInterfaceDimensionUnit(xPosition, yPosition)
    }

    private fun fireBounceEvent() {
        sprite!!.look.fire(EventWrapper(BounceOffEventId(sprite, null), false))
    }

    private fun isLookingUp(direction: Float): Boolean {
        return direction > -90f && direction < 90f
    }

    private fun isLookingDown(direction: Float): Boolean {
        return direction > 90f || direction < -90f
    }

    private fun isLookingLeft(direction: Float): Boolean {
        return direction > -180f && direction < 0f
    }

    private fun isLookingRight(direction: Float): Boolean {
        return direction > 0f && direction < 180f
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }
}