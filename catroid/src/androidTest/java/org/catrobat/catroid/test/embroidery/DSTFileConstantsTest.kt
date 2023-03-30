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
package org.catrobat.catroid.test.embroidery

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.catrobat.catroid.embroidery.DSTFileConstants
import org.junit.Assert
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class DSTFileConstantsTest {
    private val conversion = intArrayOf(-81, 81, -27, 27, -9, 9, -3, 3, -1, 1)
    private fun getByteForValue(value: Int): Int {
        var value = value
        val mask = 0x200
        var byteValue = 0x0
        for (i in conversion.indices) {
            if (i % 2 == 0 && value <= (conversion[i] - 1) / 2
                || i % 2 == 1 && value >= (conversion[i] + 1) / 2
            ) {
                byteValue = byteValue or (mask ushr i)
                value -= conversion[i]
            }
        }
        return byteValue
    }

    @Test
    fun testConversionTable() {
        val valueArray = IntArray(DSTFileConstants.CONVERSION_TABLE.size)
        for (element in DSTFileConstants.CONVERSION_TABLE.indices) {
            if (element <= 121) {
                valueArray[element] = getByteForValue(element)
            } else {
                valueArray[element] = getByteForValue((element - 121) * -1)
            }
        }
        Assert.assertArrayEquals(DSTFileConstants.CONVERSION_TABLE, valueArray)
    }

    @Test
    fun testGetMaxDistanceBetweenPoints() {
        Assert.assertEquals(
            0f,
            DSTFileConstants.getMaxDistanceBetweenPoints(0f, 0f, 0f, 0f),
            Float.MIN_VALUE
        )
        Assert.assertEquals(
            20f,
            DSTFileConstants.getMaxDistanceBetweenPoints(-5f, 0f, 5f, 0f),
            Float.MIN_VALUE
        )
        Assert.assertEquals(
            100f,
            DSTFileConstants.getMaxDistanceBetweenPoints(-5f, -10f, 5f, 40f),
            Float.MIN_VALUE
        )
        Assert.assertEquals(
            20f,
            DSTFileConstants.getMaxDistanceBetweenPoints(-5f, -5f, 5f, 5f),
            Float.MIN_VALUE
        )
    }

    @Test
    fun testToEmbroideryUnit() {
        Assert.assertEquals(0, DSTFileConstants.toEmbroideryUnit(0f).toLong())
        Assert.assertEquals(10, DSTFileConstants.toEmbroideryUnit(5f).toLong())
        Assert.assertEquals(-10, DSTFileConstants.toEmbroideryUnit(-5f).toLong())
    }
}
