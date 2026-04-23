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
package org.catrobat.catroid.test.physics;

import junit.framework.Assert;

import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.TestConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PhysicsWorldConverterAngleTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{0.0f, 0.0f},
				{(float) Math.toRadians(123.456f), 123.456f},
				{(float) Math.toRadians(-123.456f), -123.456f},
				{(float) Math.toRadians(1024.0f), 1024.0f},
				{(float) (Math.PI / 2.0), 90.0f},
				{(float) Math.PI, 180.0f}
		});
	}

	@Parameterized.Parameter
	public float rad;

	@Parameterized.Parameter(1)
	public float deg;

	@Test
	public void testBox2dToNormalAngleConversion() {
		Assert.assertEquals(deg, PhysicsWorldConverter.convertBox2dToNormalAngle(rad), TestConstants.DELTA);
	}

	@Test
	public void testNormalToBox2dAngleConversion() {
		Assert.assertEquals(rad, PhysicsWorldConverter.convertNormalToBox2dAngle(deg), TestConstants.DELTA);
	}
}
