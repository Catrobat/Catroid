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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.physics.PhysicsLook

class PointToAction : TemporalAction() {
    private var sprite: Sprite? = null
    private var pointedSprite: Sprite? = null
    override fun update(percent: Float) {
        if (pointedSprite == null
            || !ProjectManager.getInstance().currentlyPlayingScene.spriteList.contains(pointedSprite)
        ) {
            return
        }
        val spriteXPosition = sprite!!.look.xInUserInterfaceDimensionUnit
        val spriteYPosition = sprite!!.look.yInUserInterfaceDimensionUnit
        val pointedSpriteXPosition = pointedSprite!!.look.xInUserInterfaceDimensionUnit
        val pointedSpriteYPosition = pointedSprite!!.look.yInUserInterfaceDimensionUnit
        val rotationDegrees: Double
        rotationDegrees =
            if (spriteXPosition == pointedSpriteXPosition && spriteYPosition == pointedSpriteYPosition) {
                90.0
            } else if (spriteXPosition == pointedSpriteXPosition) {
                if (spriteYPosition < pointedSpriteYPosition) {
                    0.0
                } else {
                    180.0
                }
            } else if (spriteYPosition == pointedSpriteYPosition) {
                if (spriteXPosition < pointedSpriteXPosition) {
                    90.0
                } else {
                    -90.0
                }
            } else {
                90f - Math.toDegrees(
                    Math.atan2(
                        (pointedSpriteYPosition - spriteYPosition).toDouble(), (
                            pointedSpriteXPosition - spriteXPosition).toDouble()
                    )
                )
            }
        if (sprite!!.look is PhysicsLook) {
            (sprite!!.look as PhysicsLook).setFlippedByDirection(rotationDegrees.toFloat())
        }
        sprite!!.look.directionInUserInterfaceDimensionUnit = rotationDegrees.toFloat()
    }

    fun setSprite(sprite: Sprite?) {
        this.sprite = sprite
    }

    fun setPointedSprite(pointedSprite: Sprite?) {
        this.pointedSprite = pointedSprite
    }
}