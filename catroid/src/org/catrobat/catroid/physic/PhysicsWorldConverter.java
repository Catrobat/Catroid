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
package org.catrobat.catroid.physic;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.Look;

public final class PhysicsWorldConverter {

	public static float toCatroidAngle(float box2dAngle) {
		float direction = (float) (Math.toDegrees(box2dAngle) + Look.getDegreeUserInterfaceOffset()) % 360;
		if (direction < 0) {
			direction += 360f;
		}
		direction = 180f - direction;

		return direction;
	}

	public static float toBox2dAngle(float catroidAngle) {
		return (float) Math.toRadians((-catroidAngle + Look.getDegreeUserInterfaceOffset()) % 360);
	}

	public static float toCatroidCoordinate(float box2dCoordinate) {
		return box2dCoordinate * PhysicsWorld.RATIO;
	}

	public static float toBox2dCoordinate(float catroidCoordinate) {
		return catroidCoordinate / PhysicsWorld.RATIO;
	}

	public static Vector2 toCatroidVector(Vector2 box2DVector) {
		return new Vector2(toCatroidCoordinate(box2DVector.x), toCatroidCoordinate(box2DVector.y));
	}

	public static Vector2 toBox2dVector(Vector2 catroidVector) {
		return new Vector2(toBox2dCoordinate(catroidVector.x), toBox2dCoordinate(catroidVector.y));
	}
}
