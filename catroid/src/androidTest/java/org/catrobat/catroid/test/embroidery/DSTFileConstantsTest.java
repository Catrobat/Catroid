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

package org.catrobat.catroid.test.embroidery;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.embroidery.DSTFileConstants.CONVERSION_TABLE;
import static org.catrobat.catroid.embroidery.DSTFileConstants.getMaxDistanceBetweenPoints;
import static org.catrobat.catroid.embroidery.DSTFileConstants.toEmbroideryUnit;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DSTFileConstantsTest {
	private final int[] conversion = {-81, 81, -27, 27, -9, 9, -3, 3, -1, 1};

	private int getByteForValue(int value) {
		int mask = 0x200;
		int byteValue = 0x0;
		for (int i = 0; i < conversion.length; i++) {
			if (((i % 2 == 0) && value <= (conversion[i] - 1) / 2)
					|| ((i % 2 == 1) && value >= (conversion[i] + 1) / 2)) {
				byteValue |= (mask >>> i);
				value -= conversion[i];
			}
		}
		return byteValue;
	}

	@Test
	public void testConversionTable() {
		int[] valueArray = new int[CONVERSION_TABLE.length];
		for (int element = 0; element < CONVERSION_TABLE.length; element++) {
			if (element <= 121) {
				valueArray[element] = getByteForValue(element);
			} else {
				valueArray[element] = getByteForValue((element - 121) * (-1));
			}
		}
		assertArrayEquals(CONVERSION_TABLE, valueArray);
	}

	@Test
	public void testGetMaxDistanceBetweenPoints() {
		assertEquals(0, getMaxDistanceBetweenPoints(0, 0, 0, 0), Float.MIN_VALUE);
		assertEquals(20, getMaxDistanceBetweenPoints(-5, 0, 5, 0), Float.MIN_VALUE);
		assertEquals(100, getMaxDistanceBetweenPoints(-5, -10, 5, 40), Float.MIN_VALUE);
		assertEquals(20, getMaxDistanceBetweenPoints(-5, -5, 5, 5), Float.MIN_VALUE);
	}

	@Test
	public void testToEmbroideryUnit() {
		assertEquals(0, toEmbroideryUnit(0));
		assertEquals(10, toEmbroideryUnit(5));
		assertEquals(-10, toEmbroideryUnit(-5));
	}
}
