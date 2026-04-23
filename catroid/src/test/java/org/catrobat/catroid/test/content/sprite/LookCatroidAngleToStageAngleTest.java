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
public class LookCatroidAngleToStageAngleTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{0.0f, 90.0f, 90.0f},
				{45.0f, 45.0f, 135.0f},
				{90.0f, 0.0f, 180.0f},
				{135.0f, -45.0f, 225.0f},
				{180.0f, -90.0f, -90.0f},
				{225.0f, 225.0f, -45.0f},
				{270.0f, 180.0f, 0.0f},
				{315.0f, 135.0f, 45.0f},
				{360.0f, 90.0f, 90.0f}
		});
	}

	@Parameterized.Parameter
	public @IdRes float inputAngle;

	@Parameterized.Parameter(1)
	public @IdRes float expectedAngle;

	@Parameterized.Parameter(2)
	public @IdRes float expectedNegativeAngle;

	@Test
	public void testCatroidAngleToStageAngle() {
		Look look = new Look(new Sprite("testsprite"));
		assertEquals(expectedAngle, look.convertCatroidAngleToStageAngle(inputAngle));
		assertEquals(expectedAngle, look.convertCatroidAngleToStageAngle(inputAngle + 360.0f));

		float negativeInputAngle = inputAngle * (-1.0f);
		assertEquals(expectedNegativeAngle, look.convertCatroidAngleToStageAngle(negativeInputAngle));
		assertEquals(expectedNegativeAngle, look.convertCatroidAngleToStageAngle(negativeInputAngle - 360.0f));
	}
}
