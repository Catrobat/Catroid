/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;

import junit.framework.Assert;

import org.catrobat.catroid.physics.PhysicsWorld;
import org.catrobat.catroid.physics.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.TestUtils;

public class PhysicsWorldConverterTest extends AndroidTestCase {

	private float ratio = PhysicsWorld.RATIO;

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
	}

	public void testAngleConversion() {
		float angle = 0.0f;
		Assert.assertEquals("convertBox2dToNormalAngle(0) should be zero", angle,
				PhysicsWorldConverter.convertBox2dToNormalAngle(angle));
		Assert.assertEquals("convertNormalToBox2dAngle(0) should be zero", angle,
				PhysicsWorldConverter.convertNormalToBox2dAngle(angle), TestUtils.DELTA);

		Assert.assertEquals("PI/2 should be convertNormalToBox2dAngle(90°)", (float) (Math.PI / 2.0),
				PhysicsWorldConverter.convertNormalToBox2dAngle(90.0f), TestUtils.DELTA);
		Assert.assertEquals("PI should be convertNormalToBox2dAngle(180°)", (float) Math.PI,
				PhysicsWorldConverter.convertNormalToBox2dAngle(180.0f), TestUtils.DELTA);
		Assert.assertEquals("90° should be convertBox2dToNormalAngle(PI/2)", 90.0f,
				PhysicsWorldConverter.convertBox2dToNormalAngle((float) (Math.PI / 2.0)), TestUtils.DELTA);
		Assert.assertEquals("180° should be convertBox2dToNormalAngle(PI)", 180.0f,
				PhysicsWorldConverter.convertBox2dToNormalAngle((float) Math.PI), TestUtils.DELTA);

		float[] angles = { 123.456f, -123.456f, 1024.0f };
		for (float currentAngle : angles) {
			Assert.assertEquals((float) Math.toDegrees(currentAngle),
					PhysicsWorldConverter.convertBox2dToNormalAngle(currentAngle));
			Assert.assertEquals((float) Math.toRadians(currentAngle),
					PhysicsWorldConverter.convertNormalToBox2dAngle(currentAngle));
		}
	}

	public void testLengthConversion() {
		float length = 0.0f;
		Assert.assertEquals(length, PhysicsWorldConverter.convertBox2dToNormalCoordinate(length));
		Assert.assertEquals(length, PhysicsWorldConverter.convertNormalToBox2dCoordinate(length));

		float[] lengths = { 123.456f, -654.321f };
		for (float currentLength : lengths) {
			Assert.assertEquals(currentLength * ratio,
					PhysicsWorldConverter.convertBox2dToNormalCoordinate(currentLength));
			Assert.assertEquals(currentLength / ratio,
					PhysicsWorldConverter.convertNormalToBox2dCoordinate(currentLength));
		}
	}

	public void testVectorConversation() {
		Vector2 vector = new Vector2();
		Assert.assertEquals(vector, PhysicsWorldConverter.convertBox2dToNormalVector(vector));
		Assert.assertEquals(vector, PhysicsWorldConverter.convertCatroidToBox2dVector(vector));

		Vector2[] vectors = { new Vector2(123.456f, 123.456f), new Vector2(654.321f, -123.456f),
				new Vector2(-654.321f, 0.0f), new Vector2(-123.456f, -654.321f) };

		Vector2 expected;
		for (Vector2 currentVector : vectors) {
			expected = new Vector2(currentVector.x * ratio, currentVector.y * ratio);
			Assert.assertEquals(expected, PhysicsWorldConverter.convertBox2dToNormalVector(currentVector));

			expected = new Vector2(currentVector.x / ratio, currentVector.y / ratio);
			Assert.assertEquals(expected, PhysicsWorldConverter.convertCatroidToBox2dVector(currentVector));
		}
	}
}
