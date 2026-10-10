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
package org.catrobat.catroid.FaceRecognizer.ml

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.FloatBuffer

/**
 * The MobileFaceNet input: RGB planes (NCHW), values 0..1, nothing else.
 * Measured on the fixture faces: BGR order or the old FaceNet lighting
 * normalisation both separate people clearly worse.
 */
class MobileFaceNetInputTest {

    @Test
    fun theModelHasTheExpectedShape() {
        assertEquals(112, MobileFaceNet.INPUT_SIZE)
        assertEquals(128, MobileFaceNet.EMBEDDING_SIZE)
    }

    @Test
    fun pixelsBecomeRedThenGreenThenBluePlanesFromZeroToOne() {
        val pixels = intArrayOf(RED, GREEN, BLUE, MIXED)
        val input = FloatBuffer.allocate(3 * pixels.size)

        MobileFaceNet.fillInput(pixels, input)

        assertArrayEquals(
            floatArrayOf(
                1f, 0f, 0f, 0x33 / 255f,
                0f, 1f, 0f, 0x66 / 255f,
                0f, 0f, 1f, 0x99 / 255f
            ),
            input.array(),
            TOLERANCE
        )
    }

    @Test
    fun alphaIsIgnoredAndValuesAreNotNormalisedFurther() {
        val opaque = FloatBuffer.allocate(3)
        val transparent = FloatBuffer.allocate(3)

        MobileFaceNet.fillInput(intArrayOf(MIXED), opaque)
        MobileFaceNet.fillInput(intArrayOf(MIXED and 0x00FFFFFF), transparent)

        assertArrayEquals(opaque.array(), transparent.array(), TOLERANCE)
        assertArrayEquals(floatArrayOf(0x33 / 255f, 0x66 / 255f, 0x99 / 255f), opaque.array(), TOLERANCE)
    }

    private companion object {
        const val RED = 0xFFFF0000.toInt()
        const val GREEN = 0xFF00FF00.toInt()
        const val BLUE = 0xFF0000FF.toInt()
        const val MIXED = 0xFF336699.toInt()
        const val TOLERANCE = 1e-6f
    }
}
