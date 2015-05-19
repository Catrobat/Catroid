/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.common;

public final class BrickValues {

	public static final int X_POSITION = 100;
	public static final int Y_POSITION = 200;
	public static final int CHANGE_X_BY = 10;
	public static final int CHANGE_Y_BY = 10;
	public static final Float MOVE_STEPS = 10f;
	public static final Float TURN_DEGREES = 15f;
	public static final Float POINT_IN_DIRECTION = 90f;
	public static final int GLIDE_SECONDS = 1000;
	public static final int GO_BACK = 1;
	public static final int DURATION = 1;

	//constants Looks
	public static final Float SET_SIZE_TO = 60f;
	public static final Float CHANGE_SIZE_BY = 10f;
	public static final Float SET_TRANSPARENCY = 50f;
	public static final Float CHANGE_GHOST_EFFECT = 25f;
	public static final Float SET_BRIGHTNESS_TO = 50f;
	public static final Float CHANGE_BRITHNESS_BY = 25f;
	public static final int VIBRATE_MILLISECONDS = 1000;

	//constants Sounds
	public static final Float SET_VOLUME_TO = 60f;
	public static final Float CHANGE_VOLUME_BY = -10f;
	public static final String SPEAK = "Hello!";

	//Constants Control
	public static final int WAIT = 1000;
	public static final int REPEAT = 10;
	public static final int IF_CONDITION = 1;
	public static final String NOTE = "add comment here…";

	//Constants Lego
	public static final String LEGO_MOTOR = "A";
	public static final int LEGO_ANGLE = 180;
	public static final int LEGO_SPEED = 100;
	public static final float LEGO_DURATION = 1.0f;
	public static final int LEGO_FREQUENCY = 2;

	//Constants Drone
	public static final int DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000;
	public static final float DRONE_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT = 0.2f;

	//Constants Variables
	public static final double SET_VARIABLE = 1d;
	public static final double CHANGE_VARIABLE = 1d;

	//Constants Lists
	public static final double ADD_ITEM_TO_USERLIST = 1d;
	public static final int DELETE_ITEM_OF_USERLIST = 1;
	public static final int INSERT_ITEM_INTO_USERLIST_INDEX = 1;
	public static final double INSERT_ITEM_INTO_USERLIST_VALUE = 1d;
	public static final int REPLACE_ITEM_IN_USERLIST_INDEX = 1;
	public static final double REPLACE_ITEM_IN_USERLIST_VALUE = 1d;

	//Constants Phiro
	public static final int PHIRO_SPEED = 100;
	public static final int PHIRO_DURATION = 1;
	public static final int PHIRO_VALUE_RED = 0;
	public static final int PHIRO_VALUE_GREEN = 255;
	public static final int PHIRO_VALUE_BLUE = 255;
	public static final String PHIRO_IF_SENSOR_DEFAULT_VALUE = "Front Left Sensor";

	// Suppress default constructor for noninstantiability
	private BrickValues() {
		throw new AssertionError();
	}
}
