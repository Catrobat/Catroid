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

package org.catrobat.catroid.test.ui.recyclerview.dialog.newproject

import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSize
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSizeUnit
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(Parameterized::class)
class FrameSizeTest(
    private val height: Int,
    private val width: Int,
    private val unit: FrameSizeUnit,
    private val heightExpected: Int,
    private val widthExpected: Int
) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            arrayOf(FrameSize.PIXEL_384, FrameSize.PIXEL_384, FrameSizeUnit.CM, 10, 10),
            arrayOf(FrameSize.PIXEL_672, FrameSize.PIXEL_480, FrameSizeUnit.CM, 18, 13),
            arrayOf(FrameSize.PIXEL_960, FrameSize.PIXEL_576, FrameSizeUnit.CM, 25, 15),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_672, FrameSizeUnit.CM, 30, 18),
            arrayOf(FrameSize.PIXEL_768, FrameSize.PIXEL_768, FrameSizeUnit.CM, 20, 20),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_768, FrameSizeUnit.CM, 30, 20),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_864, FrameSizeUnit.CM, 30, 23),
            arrayOf(FrameSize.PIXEL_1344, FrameSize.PIXEL_960, FrameSizeUnit.CM, 36, 25),

            arrayOf(FrameSize.PIXEL_384, FrameSize.PIXEL_384, FrameSizeUnit.INCH, 4, 4),
            arrayOf(FrameSize.PIXEL_672, FrameSize.PIXEL_480, FrameSizeUnit.INCH, 7, 5),
            arrayOf(FrameSize.PIXEL_960, FrameSize.PIXEL_576, FrameSizeUnit.INCH, 10, 6),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_672, FrameSizeUnit.INCH, 12, 7),
            arrayOf(FrameSize.PIXEL_768, FrameSize.PIXEL_768, FrameSizeUnit.INCH, 8, 8),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_768, FrameSizeUnit.INCH, 12, 8),
            arrayOf(FrameSize.PIXEL_1152, FrameSize.PIXEL_864, FrameSizeUnit.INCH, 12, 9),
            arrayOf(FrameSize.PIXEL_1344, FrameSize.PIXEL_960, FrameSizeUnit.INCH, 14, 10),

            arrayOf(
                FrameSize.PIXEL_384,
                FrameSize.PIXEL_384,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_384,
                FrameSize.PIXEL_384
            ),
            arrayOf(
                FrameSize.PIXEL_672,
                FrameSize.PIXEL_480,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_672,
                FrameSize.PIXEL_480
            ),
            arrayOf(
                FrameSize.PIXEL_960,
                FrameSize.PIXEL_576,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_960,
                FrameSize.PIXEL_576
            ),
            arrayOf(
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_672,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_672
            ),
            arrayOf(
                FrameSize.PIXEL_768,
                FrameSize.PIXEL_768,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_768,
                FrameSize.PIXEL_768
            ),
            arrayOf(
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_768,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_768
            ),
            arrayOf(
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_864,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_1152,
                FrameSize.PIXEL_864
            ),
            arrayOf(
                FrameSize.PIXEL_1344,
                FrameSize.PIXEL_960,
                FrameSizeUnit.PIXEL,
                FrameSize.PIXEL_1344,
                FrameSize.PIXEL_960
            ),
        )
    }

    @Test
    fun testFrameSizeConvertion() {
        val frame = FrameSize(height, width)
        Assert.assertEquals(heightExpected, frame.getHeight(false, unit))
        Assert.assertEquals(widthExpected, frame.getWidth(false, unit))

        Assert.assertEquals(heightExpected, frame.getWidth(true, unit))
        Assert.assertEquals(widthExpected, frame.getHeight(true, unit))
    }
}
