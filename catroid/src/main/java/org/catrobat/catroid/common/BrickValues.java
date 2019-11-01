/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import com.badlogic.gdx.math.Vector2;

import org.catrobat.catroid.content.PenColor;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.physics.PhysicsObject.Type;
import org.catrobat.catroid.physics.PhysicsWorld;

public final class BrickValues {

	//constants Motions
	public static final int X_POSITION = 100;
	public static final int Y_POSITION = 200;
	public static final int CHANGE_X_BY = 10;
	public static final int CHANGE_Y_BY = 10;
	public static final double MOVE_STEPS = 10;
	public static final double TURN_DEGREES = 15;
	public static final double POINT_IN_DIRECTION = 90;
	public static final int GLIDE_SECONDS = 1000;
	public static final int GO_BACK = 1;
	public static final int DURATION = 1;

	//constants Physics
	public static final PhysicsObject.Type PHYSIC_TYPE = Type.DYNAMIC;
	public static final double PHYSIC_MASS = 1.0;
	public static final double PHYSIC_BOUNCE_FACTOR = 0.8;
	public static final double PHYSIC_FRICTION = 0.2;
	public static final Vector2 PHYSIC_GRAVITY = PhysicsWorld.DEFAULT_GRAVITY;
	public static final Vector2 PHYSIC_VELOCITY = new Vector2();
	public static final double PHYSIC_TURN_DEGREES = TURN_DEGREES;

	//constants Looks
	public static final double SET_SIZE_TO = 60;
	public static final double RELATIVE_SIZE_IN_PERCENT = 120.0f;
	public static final double CHANGE_SIZE_BY = 10;
	public static final double SET_TRANSPARENCY = 50;
	public static final double CHANGE_TRANSPARENCY_EFFECT = 25;
	public static final double SET_BRIGHTNESS_TO = 50;
	public static final double CHANGE_BRITHNESS_BY = 25;
	public static final double SET_COLOR_TO = 0;
	public static final double CHANGE_COLOR_BY = 25;
	public static final double VIBRATE_SECONDS = 1;
	public static final int GO_TO_TOUCH_POSITION = 80;
	public static final int GO_TO_RANDOM_POSITION = 81;
	public static final int GO_TO_OTHER_SPRITE_POSITION = 82;
	public static final int SET_LOOK_BY_INDEX = 1;

	//constants Pen
	public static final double PEN_SIZE = 3.15;
	public static final PenColor PEN_COLOR = new PenColor(0, 0, 1, 1);

	//constants Sounds
	public static final double SET_VOLUME_TO = 60;
	public static final double CHANGE_VOLUME_BY = -10;

	//Constants Control
	public static final int WAIT = 1000;
	public static final int REPEAT = 10;
	public static final String IF_CONDITION = "1 < 2";
	public static final String NOTE = "add comment here…";
	public static final int STOP_THIS_SCRIPT = 0;
	public static final int STOP_ALL_SCRIPTS = 1;
	public static final int STOP_OTHER_SCRIPTS = 2;

	//Constants Lego
	public static final String LEGO_MOTOR = "A";
	public static final int LEGO_ANGLE = 180;
	public static final int LEGO_SPEED = 100;
	public static final double LEGO_DURATION = 1.0;
	public static final int LEGO_FREQUENCY = 2;
	public static final int LEGO_VOLUME = 100;

	//Constants Drone
	public static final int DRONE_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000;
	public static final int DRONE_MOVE_BRICK_DEFAULT_POWER_PERCENT = 20;
	public static final String STRING_VALUE = "default";

	public static final int DRONE_ALTITUDE_MIN = 3;
	public static final int DRONE_ALTITUDE_INDOOR = 5;
	public static final int DRONE_ALTITUDE_OUTDOOR = 10;
	public static final int DRONE_ALTITUDE_MAX = 100;

	public static final int DRONE_VERTICAL_MIN = 200;
	public static final int DRONE_VERTICAL_INDOOR = 700;
	public static final int DRONE_VERTICAL_OUTDOOR = 1000;
	public static final int DRONE_VERTICAL_MAX = 2000;

	public static final int DRONE_ROTATION_MIN = 40;
	public static final int DRONE_ROTATION_INDOOR = 100;
	public static final int DRONE_ROTATION_OUTDOOR = 200;
	public static final int DRONE_ROTATION_MAX = 350;

	public static final int DRONE_TILT_MIN = 5;
	public static final int DRONE_TILT_INDOOR = 12;
	public static final int DRONE_TILT_OUTDOOR = 20;
	public static final int DRONE_TILT_MAX = 30;

	//Constants Jumping Sumo
	public static final int JUMPING_SUMO_MOVE_BRICK_DEFAULT_TIME_MILLISECONDS = 1000;
	public static final int JUMPING_SUMO_MOVE_BRICK_DEFAULT_MOVE_POWER_PERCENT = 80;
	public static final int JUMPING_SUMO_SOUND_BRICK_DEFAULT_VOLUME_PERCENT = 50;
	public static final int JUMPING_SUMO_ROTATE_DEFAULT_DEGREE = 90;

	//Constants Variables
	public static final double SET_VARIABLE = 1d;
	public static final String SHOW_VARIABLE_COLOR = "#FF0000";
	public static final double CHANGE_VARIABLE = 1d;

	//Constants Lists
	public static final double ADD_ITEM_TO_USERLIST = 1;
	public static final int DELETE_ITEM_OF_USERLIST = 1;
	public static final int INSERT_ITEM_INTO_USERLIST_INDEX = 1;
	public static final double INSERT_ITEM_INTO_USERLIST_VALUE = 1;
	public static final int REPLACE_ITEM_IN_USERLIST_INDEX = 1;
	public static final double REPLACE_ITEM_IN_USERLIST_VALUE = 1;

	//Constants Phiro
	public static final int PHIRO_SPEED = 100;
	public static final int PHIRO_DURATION = 1;
	public static final int PHIRO_VALUE_RED = 0;
	public static final int PHIRO_VALUE_GREEN = 255;
	public static final int PHIRO_VALUE_BLUE = 255;
	public static final String PHIRO_IF_SENSOR_DEFAULT_VALUE = "Front Left Sensor";

	//Constants Arduino
	public static final int ARDUINO_PWM_INITIAL_PIN_VALUE = 255;
	public static final int ARDUINO_PWM_INITIAL_PIN_NUMBER = 3;
	public static final int ARDUINO_DIGITAL_INITIAL_PIN_VALUE = 1;
	public static final int ARDUINO_DIGITAL_INITIAL_PIN_NUMBER = 13;

	//Constants Raspi
	public static final int RASPI_DIGITAL_INITIAL_PIN_VALUE = 1;
	public static final int RASPI_DIGITAL_INITIAL_PIN_NUMBER = 3;
	public static final String[] RASPI_EVENTS = {"pressed", "released"};
	public static final double RASPI_PWM_INITIAL_PERCENTAGE = 50.0;
	public static final double RASPI_PWM_INITIAL_FREQUENCY = 100.0;

	//Constants NFC
	public static final short TNF_MIME_MEDIA = 0;
	public static final short TNF_WELL_KNOWN_HTTP = 1;
	public static final short TNF_WELL_KNOWN_HTTPS = 2;
	public static final short TNF_WELL_KNOWN_SMS = 3;
	public static final short TNF_WELL_KNOWN_TEL = 4;
	public static final short TNF_WELL_KNOWN_MAILTO = 5;
	public static final short TNF_EXTERNAL_TYPE = 6;
	public static final short TNF_EMPTY = 7;
	public static final byte NDEF_PREFIX_HTTP = 0x03;
	public static final byte NDEF_PREFIX_HTTPS = 0x04;
	public static final byte NDEF_PREFIX_TEL = 0x05;
	public static final byte NDEF_PREFIX_MAILTO = 0x06;

	//Constants Embroidery
	public static final float STITCH_SIZE = 3.15f;
	public static final int STITCH_LENGTH = 10;

	private BrickValues() {
		throw new AssertionError("No.");
	}
}
