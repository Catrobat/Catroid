/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

package org.catrobat.catroid.test.utiltests;

import android.support.annotation.IdRes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.utils.NumberFormats.stringWithoutTrailingZero;

@RunWith(Parameterized.class)
public class NumberFormatsTest {

	@Parameterized.Parameter
	public @IdRes String name;
	@Parameterized.Parameter(1)
	public @IdRes String stringWithoutTrailingZero;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][]{
				{"0", stringWithoutTrailingZero(String.valueOf(0))},
				{"8", stringWithoutTrailingZero(String.valueOf(8))},
				{"-120", stringWithoutTrailingZero(String.valueOf(-120))},
				{"0", stringWithoutTrailingZero(String.valueOf(0.0))},
				{"0.5", stringWithoutTrailingZero(String.valueOf(0.5))},
				{"0.7", stringWithoutTrailingZero(String.valueOf(0.70))},
				{"0.103", stringWithoutTrailingZero(String.valueOf(0.1030))},
				{"15.05", stringWithoutTrailingZero(String.valueOf(15.050))},
				{"string.1900", stringWithoutTrailingZero("string.1900")},
				{"Pocket", stringWithoutTrailingZero("Pocket")}
		});
	}

	@Test
	public void testStringWithoutTrailingZero() {
		assertEquals(stringWithoutTrailingZero, name);
	}
}

