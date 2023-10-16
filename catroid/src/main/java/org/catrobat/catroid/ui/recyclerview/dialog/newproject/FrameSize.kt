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

package org.catrobat.catroid.ui.recyclerview.dialog.newproject

import kotlin.math.roundToInt

enum class FrameSizeUnit {
    CM, INCH, PIXEL
}

class FrameSize(private val value1: Int, private val value2: Int) {

    fun getHeight(landscape: Boolean, unit: FrameSizeUnit): Int {
        val height = if (landscape) value2 else value1
        return toUnit(height, unit)
    }

    fun getWidth(landscape: Boolean, unit: FrameSizeUnit): Int {
        val width = if (landscape) value1 else value2
        return toUnit(width, unit)
    }

    private fun toUnit(value: Int, unit: FrameSizeUnit): Int = when (unit) {
        FrameSizeUnit.CM -> (value / CM_TO_PIXEL).roundToInt()
        FrameSizeUnit.INCH -> (value / INCH_TO_PIXEL).roundToInt()
        FrameSizeUnit.PIXEL -> value
    }

    companion object {
        const val CM_TO_PIXEL = 37.7952755906
        const val INCH_TO_PIXEL = 96.0

        const val PIXEL_384 = 384 // 4 inch, 10 cm
        const val PIXEL_480 = 480 // 5 inch, 13 cm
        const val PIXEL_576 = 576 // 6 inch, 15 cm
        const val PIXEL_672 = 672 // 7 inch, 18 cm
        const val PIXEL_768 = 768 // 8 inch, 20 cm
        const val PIXEL_864 = 864 // 9 inch, 23 cm
        const val PIXEL_960 = 960 // 10 inch, 25 cm
        const val PIXEL_1152 = 1152 // 12 inch, 30 cm
        const val PIXEL_1344 = 1344 // 14 inch, 36 cm

            val FrameSizes = arrayOf(
                FrameSize(PIXEL_384, PIXEL_384),
                FrameSize(PIXEL_672, PIXEL_480),
                FrameSize(PIXEL_960, PIXEL_576),
                FrameSize(PIXEL_1152, PIXEL_672),
                FrameSize(PIXEL_768, PIXEL_768),
                FrameSize(PIXEL_1152, PIXEL_768),
                FrameSize(PIXEL_1152, PIXEL_864),
                FrameSize(PIXEL_1344, PIXEL_960)
            )
    }
}
