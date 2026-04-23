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

package org.catrobat.catroid.test.content.sprite;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.content.Sprite;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import androidx.annotation.IdRes;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LookDirectionTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{90f, 0f},
				{60f, 30f},
				{30f, 60f},
				{0f, 90f},
				{-30f, 120f},
				{-60f, 150f},
				{-90f, 180.0f},
				{-120f, 210f},
				{-150f, 240f},
				{180f, -90f},
				{150f, -60f},
				{120f, -30f}
		});
	}

	@Parameterized.Parameter
	public @IdRes float degreesInUserInterfaceDimensionUnit;

	@Parameterized.Parameter(1)
	public @IdRes float degrees;

	@Test
	public void testDirection() {
		Look look = new Look(new Sprite("testsprite"));
		look.setMotionDirectionInUserInterfaceDimensionUnit(degreesInUserInterfaceDimensionUnit);
		assertEquals(degreesInUserInterfaceDimensionUnit, look.getMotionDirectionInUserInterfaceDimensionUnit());
		assertEquals(degrees, look.getRotation());
	}
}
