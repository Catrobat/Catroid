/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.physics;

import android.test.AndroidTestCase;

import com.badlogic.gdx.math.Vector2;

import junit.framework.Assert;

import org.catrobat.catroid.content.Look;
import org.catrobat.catroid.physic.PhysicsWorld;
import org.catrobat.catroid.physic.PhysicsWorldConverter;
import org.catrobat.catroid.test.utils.TestUtils;

public class PhysicWorldConverterTest extends AndroidTestCase {

	private float ratio = PhysicsWorld.RATIO;

	@Override
	public void setUp() {
	}

	@Override
	public void tearDown() {
	}

	public void testAngleConversion() {
		// TODO[Physics] refactor test
		float angle = 0.0f;
		//float angle = PhysicWorldTest.computeScratchCompatibleAngleForDirectSetting(0.0f);
		Assert.assertEquals(angle + Look.getDegreeUserInterfaceOffset(), PhysicsWorldConverter.toCatroidAngle(angle));
		Assert.assertEquals(angle - Math.toRadians(Look.getDegreeUserInterfaceOffset()),
				PhysicsWorldConverter.toBox2dAngle(angle), TestUtils.DELTA);

		Assert.assertEquals((float) (Math.PI / 2.0) - Math.toRadians(Look.getDegreeUserInterfaceOffset()),
				PhysicsWorldConverter.toBox2dAngle(90.0f), TestUtils.DELTA);
		Assert.assertEquals((float) Math.PI - Math.toRadians(Look.getDegreeUserInterfaceOffset()),
				PhysicsWorldConverter.toBox2dAngle(180.0f), TestUtils.DELTA);
		angle = PhysicWorldTest.computeScratchCompatibleAngleForDirectSetting(90.0f);
		Assert.assertEquals(angle + Look.getDegreeUserInterfaceOffset(),
				PhysicsWorldConverter.toCatroidAngle((float) (Math.PI / 2.0)), TestUtils.DELTA);
		angle = PhysicWorldTest.computeScratchCompatibleAngleForDirectSetting(180.0f);
		Assert.assertEquals(angle + Look.getDegreeUserInterfaceOffset(),
				PhysicsWorldConverter.toCatroidAngle((float) Math.PI), TestUtils.DELTA);

		float[] angles = { 123.456f, -123.456f, 1024.0f };
		for (float currentAngle : angles) {
			Assert.assertEquals((float) Math.toDegrees(currentAngle),
					PhysicsWorldConverter.toCatroidAngle(currentAngle));
			Assert.assertEquals((float) Math.toRadians(currentAngle), PhysicsWorldConverter.toBox2dAngle(currentAngle));
		}
	}

	public void testLengthConversion() {
		float length = 0.0f;
		Assert.assertEquals(length, PhysicsWorldConverter.toCatroidCoordinate(length));
		Assert.assertEquals(length, PhysicsWorldConverter.toBox2dCoordinate(length));

		float[] lengths = { 123.456f, -654.321f };
		for (float currentLength : lengths) {
			Assert.assertEquals(currentLength * ratio, PhysicsWorldConverter.toCatroidCoordinate(currentLength));
			Assert.assertEquals(currentLength / ratio, PhysicsWorldConverter.toBox2dCoordinate(currentLength));
		}
	}

	public void testVectorConversation() {
		Vector2 vector = new Vector2();
		Assert.assertEquals(vector, PhysicsWorldConverter.toCatroidVector(vector));
		Assert.assertEquals(vector, PhysicsWorldConverter.toBox2dVector(vector));

		Vector2[] vectors = { new Vector2(123.456f, 123.456f), new Vector2(654.321f, -123.456f),
				new Vector2(-654.321f, 0.0f), new Vector2(-123.456f, -654.321f) };

		Vector2 expected;
		for (Vector2 currentVector : vectors) {
			expected = new Vector2(currentVector.x * ratio, currentVector.y * ratio);
			Assert.assertEquals(expected, PhysicsWorldConverter.toCatroidVector(currentVector));

			expected = new Vector2(currentVector.x / ratio, currentVector.y / ratio);
			Assert.assertEquals(expected, PhysicsWorldConverter.toBox2dVector(currentVector));
		}
	}
}
