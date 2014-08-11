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
package org.catrobat.catroid.physics;

import com.badlogic.gdx.math.Vector2;

public final class PhysicsWorldConverter {

	private PhysicsWorldConverter(){} // Make sure that utility classes (classes that contain only static methods or fields in their API) do not have a public constructor.

	public static float convertBox2dToNormalAngle(float box2dAngle) {
		return (float) Math.toDegrees(box2dAngle);
	}

	public static float convertNormalToBox2dAngle(float catroidAngle) {
		return (float) Math.toRadians(catroidAngle);
	}

	public static float convertBox2dToNormalCoordinate(float box2dCoordinate) {
		return box2dCoordinate * PhysicsWorld.RATIO;
	}

	public static float convertNormalToBox2dCoordinate(float catroidCoordinate) {
		return catroidCoordinate / PhysicsWorld.RATIO;
	}

	public static Vector2 convertBox2dToNormalVector(Vector2 box2DVector) {
		return new Vector2(convertBox2dToNormalCoordinate(box2DVector.x), convertBox2dToNormalCoordinate(box2DVector.y));
	}

	public static Vector2 convertCatroidToBox2dVector(Vector2 catroidVector) {
		return new Vector2(convertNormalToBox2dCoordinate(catroidVector.x),
				convertNormalToBox2dCoordinate(catroidVector.y));
	}
}
