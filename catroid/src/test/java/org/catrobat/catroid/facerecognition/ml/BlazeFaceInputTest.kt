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
package org.catrobat.catroid.facerecognition.ml

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * BlazeFace takes a [1, 128, 128, 3] float input: RGB per pixel, row by row,
 * values -1..1. It is written into one direct buffer instead of 16,384 small
 * arrays that LiteRT copied one by one for every analysed camera frame.
 */
class BlazeFaceInputTest {

    @Test
    fun pixelsBecomeRgbFromMinusOneToOneRowByRow() {
        val pixels = IntArray(BlazeFace.INPUT_SIZE_WIDTH * BlazeFace.INPUT_SIZE_HEIGHT)
        pixels[0] = 0xFF00FF80.toInt()
        pixels[1] = 0xFFFFFFFF.toInt()
        pixels[BlazeFace.INPUT_SIZE_WIDTH] = 0xFF000000.toInt()
        val input = ByteBuffer.allocateDirect(pixels.size * CHANNELS * Float.SIZE_BYTES).order(ByteOrder.nativeOrder())

        BlazeFace.writeInput(pixels, input)

        val values = input.asFloatBuffer()
        assertEquals(pixels.size * CHANNELS, values.remaining())
        assertEquals(-1f, values[0], DELTA)
        assertEquals(1f, values[1], DELTA)
        assertEquals(0x80 / HALF - 1, values[2], DELTA)
        for (channel in 0 until CHANNELS) {
            assertEquals(1f, values[CHANNELS + channel], DELTA)
            assertEquals(-1f, values[BlazeFace.INPUT_SIZE_WIDTH * CHANNELS + channel], DELTA)
        }
    }

    private companion object {
        const val CHANNELS = 3
        const val HALF = 127.5f
        const val DELTA = 1e-6f
    }
}
