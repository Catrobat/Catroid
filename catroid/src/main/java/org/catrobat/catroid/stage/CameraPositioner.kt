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

package org.catrobat.catroid.stage

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import org.catrobat.catroid.content.Sprite
import kotlin.math.abs

class CameraPositioner(
    var camera: OrthographicCamera,
    var virtualHeightHalf: Float,
    var virtualWidthHalf: Float
) {
    var spriteToFocusOn: Sprite? = null
    var horizontalFlex: Float = 0.0f
    var verticalFlex: Float = 0.0f

    fun updateCameraPositionForFocusedSprite() {
        val spritePositionX = spriteToFocusOn?.look?.xInUserInterfaceDimensionUnit ?: return
        val spritePositionY = spriteToFocusOn?.look?.yInUserInterfaceDimensionUnit ?: return
        val limitX: Float = virtualWidthHalf * (horizontalFlex / CALCULATE_PERCENT)
        val limitY: Float = virtualHeightHalf * (verticalFlex / CALCULATE_PERCENT)
        val currentPos: Vector3 = camera.position

        val currentXDifference: Float = spritePositionX - currentPos.x
        val currentYDifference: Float = spritePositionY - currentPos.y

        var newCameraXPos: Float = camera.position.x
        var newCameraYPos: Float = camera.position.y
        if (abs(currentXDifference) > limitX) {
            newCameraXPos =
                spritePositionX - convertLimitOnNegativeDifference(currentXDifference, limitX)
        }
        if (abs(currentYDifference) > limitY) {
            newCameraYPos =
                spritePositionY - convertLimitOnNegativeDifference(currentYDifference, limitY)
        }
        if (newCameraXPos != camera.position.x || newCameraYPos != camera.position.y) {
            camera.position.set(newCameraXPos, newCameraYPos, 0.0f)
            camera.update()
        }
    }

    private fun convertLimitOnNegativeDifference(difference: Float, limit: Float): Float =
        if (difference < 0) limit * -1 else limit

    fun reset() {
        spriteToFocusOn ?: return
        spriteToFocusOn = null
        camera.position[0f, 0f] = 0f
        camera.update()
    }

    companion object {
        private const val CALCULATE_PERCENT = 100
    }
}
