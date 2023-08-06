/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.utils

import org.catrobat.catroid.visualplacement.model.Size

/**
 * Game Coordinates are centered in the middle of the screen and are scaled to the current device.
 */
data class GameCoordinates(val x: Float, val y: Float) {
    fun toAndroidCoordinates(viewSize: Size) = AndroidCoordinates(
        x + viewSize.width / 2, y + viewSize.height / 2
    )

    fun toUnscaledGameCoordinates(scalingFactor: Size) = UnscaledGameCoordinatesForBrick(
        x / scalingFactor.width, -y / scalingFactor.height
    )
}

/**
 * Actual coordinates of the android system. This is used for click events.
 * The center is on the top left corner of the screen
 */
class AndroidCoordinates(val x: Float, val y: Float) {
    operator fun minus(other: AndroidCoordinates) = AndroidCoordinates(
        this.x - other.x, this.y - other.y
    )

    operator fun plus(other: AndroidCoordinates) = AndroidCoordinates(
        this.x + other.x, this.y + other.y
    )

    fun toGameCoordinates(viewSize: Size) = GameCoordinates(
        x - viewSize.width / 2, y - viewSize.height / 2
    )
}

/**
 *  Unscaled Game Coordinates are the unscaled game coordinates that are saved in the bricks.
 *  The center is also in the center of the screen.
 */
data class UnscaledGameCoordinatesForBrick(val x: Float, val y: Float) {
    fun toGameCoordinates(scalingFactor: Size) = GameCoordinates(
        x * scalingFactor.width, -y * scalingFactor.height
    )
}
