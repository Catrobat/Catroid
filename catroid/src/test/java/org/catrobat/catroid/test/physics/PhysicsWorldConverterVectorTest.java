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

import com.badlogic.gdx.math.Vector2;

import junit.framework.Assert;

import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public class PhysicsWorldConverterVectorTest {

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{new Vector2()},
				{new Vector2(123.456f, 123.456f)},
				{new Vector2(-654.321f, 0.0f)},
				{new Vector2(-123.456f, -654.321f)},
				{new Vector2(654.321f, -123.456f)}
		});
	}

	@Parameterized.Parameter
	public Vector2 vector;

	@Test
	public void testVectorNormalToBox2dConversation() {
		Vector2 expectedVector = new Vector2(vector.x / PhysicsWorld.RATIO, vector.y / PhysicsWorld.RATIO);
		Assert.assertEquals(expectedVector, PhysicsWorldConverter.convertCatroidToBox2dVector(vector));
	}

	@Test
	public void testVectorBox2dToNormalConversation() {
		Vector2 expectedVector = new Vector2(vector.x * PhysicsWorld.RATIO, vector.y * PhysicsWorld.RATIO);
		Assert.assertEquals(expectedVector, PhysicsWorldConverter.convertBox2dToNormalVector(vector));
	}
}
