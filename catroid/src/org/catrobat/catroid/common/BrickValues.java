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
package org.catrobat.catroid.common;

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.physic.PhysicsObject;
import org.catrobat.catroid.physic.PhysicsObject.Type;
import org.catrobat.catroid.physic.PhysicsWorld;

public class BrickValues {

	//constants Motions
	public static final int X_POSITION = 100;
	public static final int Y_POSITION = 200;
	public static final int CHANGE_X_BY = 10;
	public static final int CHANGE_Y_BY = 10;
	public static final Float MOVE_STEPS = 10f;
	public static final Float TURN_DEGREES = 15f;
	public static final Float POINT_IN_DIRECTION = 90f;
	public static final int GLIDE_SECONDS = 1000;
	public static final int GO_BACK = 1;

	//constants Physics   // TODO[physic]
	public static final PhysicsObject.Type PHYSIC_TYPE = Type.DYNAMIC;
	public static final Float PHYSIC_MASS = PhysicsObject.DEFAULT_MASS;
	public static final Float PHYSIC_BOUNCE_FACTOR = PhysicsObject.DEFAULT_BOUNCE_FACTOR;
	public static final Float PHYSIC_FRICTION = PhysicsObject.DEFAULT_FRICTION;
	public static final Vector2 PHYSIC_GRAVITY = PhysicsWorld.DEFAULT_GRAVITY;
	public static final Vector2 PHYSIC_VELOCITY = new Vector2();
	public static final Float PHYSIC_TURN_DEGREES = TURN_DEGREES;

	//constants Looks
	public static final Float SET_SIZE_TO = 60f;
	public static final Float CHANGE_SIZE_BY = 10f;
	public static final Float SET_GHOST_EFFECT = 50f;
	public static final Float CHANGE_GHOST_EFFECT = 25f;
	public static final Float SET_BRIGHTNESS_TO = 50f;
	public static final Float CHANGE_BRITHNESS_BY = 25f;

	//constants Sounds
	public static final Float SET_VOLUME_TO = 60f;
	public static final Float CHANGE_VOLUME_BY = -10f;

	//Constants Control
	public static final int WAIT = 1000;
	public static final int REPEAT = 10;
	public static final int IF_CONDITION = 1;

	//Constants Lego
	public static final String LEGO_MOTOR = "A";
	public static final int LEGO_ANGLE = 180;
	public static final int LEGO_SPEED = 100;
	public static final int LEGO_DURATION = 1;
	public static final int LEGO_FREQUENCY = 2;

	//Constants Drone
	public static final int DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000;
	public static final float DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT = 0.2f;

	// Suppress default constructor for noninstantiability
	private BrickValues() {
		throw new AssertionError();
	}
}
