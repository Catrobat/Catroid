/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

import kotlin.math.abs

private const val EQUALITY_EPSILON = 0.0000001
class Resolution @JvmOverloads constructor(
    val width: Int,
    val height: Int,
    val offsetX: Int = 0,
    val offsetY: Int = 0
) {
    init {
        if (width == 0) throw IllegalArgumentException("Screen width may not be 0;")
        if (height == 0) throw IllegalArgumentException("Screen height may not be 0;")
    }

    fun isInPortraitMode(): Boolean = width < height
    fun isInLandscapeMode(): Boolean = !isInPortraitMode()
    fun aspectRatio(): Float = width.toFloat() / height.toFloat()

    fun flipToFit(target: Resolution): Resolution = when {
        doesOrientationMatch(target) -> Resolution(width, height, offsetX, offsetY)
        else -> Resolution(height, width, offsetY, offsetX)
    }

    fun flipToPortrait(): Resolution = flipToFit(Resolution(1, 2))
    fun flipToLandscape(): Resolution = flipToFit(Resolution(2, 1))

    private fun doesOrientationMatch(target: Resolution): Boolean {
        return target.height > target.width && isInPortraitMode() ||
            target.height < target.width && isInLandscapeMode()
    }

    fun resizeToFit(target: Resolution): Resolution {
        val ratioHeight = target.height.toFloat() / height
        val ratioWidth = target.width.toFloat() / width

        return when {
            aspectRatio() < target.aspectRatio() -> {
                val scaleFactor = ratioHeight / ratioWidth
                val newWidth = (target.width * scaleFactor).toInt()
                val horizontalOffset = ((target.width - newWidth) / 2.0).toInt()
                Resolution(newWidth, target.height, horizontalOffset, 0)
            }
            aspectRatio() > target.aspectRatio() -> {
                val scaleFactor = ratioWidth / ratioHeight
                val newHeight = (target.height * scaleFactor).toInt()
                val verticalOffset = ((target.height - newHeight) / 2.0).toInt()
                Resolution(target.width, newHeight, 0, verticalOffset)
            }
            else -> Resolution(target.width, target.height, 0, 0)
        }
    }

    fun sameRatioOrMeasurements(target: Resolution): Boolean =
        width == target.width && height == target.height ||
            abs(aspectRatio() - target.aspectRatio()) < EQUALITY_EPSILON
}
