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

package org.catrobat.catroid.formulaeditor.sensor.testutils

import org.catrobat.catroid.content.Look
import org.catrobat.catroid.content.Sprite

class LookTestable : Look {
    constructor(sprite: Sprite) : super(sprite)

    var angularVelocity: Float = 0f
    var objectBrightness: Float = 0f
    var color: Float = 0f
    var distance: Float = 0f
    var lookDirection: Float = 0f
    var motionDirection: Float = 0f
    var sizeInUserInterface: Float = 0f
    var transparency: Float = 0f
    var xInUserInterface: Float = 0f
    var xVelocity: Float = 0f
    var yInUserInterface: Float = 0f
    var yVelocity: Float = 0f
    var zIndexForTesting = 0

    override fun getAngularVelocityInUserInterfaceDimensionUnit(): Float = angularVelocity

    override fun getBrightnessInUserInterfaceDimensionUnit(): Float = objectBrightness

    override fun getColorInUserInterfaceDimensionUnit(): Float = color

    override fun getDistanceToTouchPositionInUserInterfaceDimensions(): Float = distance

    override fun getLookDirectionInUserInterfaceDimensionUnit(): Float = lookDirection

    override fun getMotionDirectionInUserInterfaceDimensionUnit(): Float = motionDirection

    override fun getSizeInUserInterfaceDimensionUnit(): Float = sizeInUserInterface

    override fun getTransparencyInUserInterfaceDimensionUnit(): Float = transparency

    override fun getXInUserInterfaceDimensionUnit(): Float = xInUserInterface

    override fun getXVelocityInUserInterfaceDimensionUnit(): Float = xVelocity

    override fun getYInUserInterfaceDimensionUnit(): Float = yInUserInterface

    override fun getYVelocityInUserInterfaceDimensionUnit(): Float = yVelocity

    override fun getZIndex(): Int = zIndexForTesting
}
