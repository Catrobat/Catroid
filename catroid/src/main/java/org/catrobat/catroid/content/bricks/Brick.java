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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.support.annotation.IntDef;
import android.view.View;
import android.widget.CheckBox;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.List;

public interface Brick extends Serializable, Cloneable {

	enum BrickField {
		COLOR, COLOR_CHANGE, BRIGHTNESS, BRIGHTNESS_CHANGE, X_POSITION, Y_POSITION, X_POSITION_CHANGE, Y_POSITION_CHANGE,
		TRANSPARENCY, TRANSPARENCY_CHANGE, SIZE, SIZE_CHANGE, VOLUME, VOLUME_CHANGE, X_DESTINATION, Y_DESTINATION, STEPS,
		DURATION_IN_SECONDS, DEGREES, TURN_RIGHT_DEGREES, TURN_LEFT_DEGREES, TIME_TO_WAIT_IN_SECONDS, VARIABLE,
		VARIABLE_CHANGE, WEB_REQUEST,

		PEN_SIZE, PEN_COLOR_RED, PEN_COLOR_GREEN, PEN_COLOR_BLUE,

		IF_CONDITION, TIMES_TO_REPEAT, VIBRATE_DURATION_IN_SECONDS, USER_BRICK, NOTE, SPEAK,
		SHOWTEXT, STRING, ROTATION_STYLE, REPEAT_UNTIL_CONDITION, ASK_QUESTION, NFC_NDEF_MESSAGE, ASK_SPEECH_QUESTION,
		LOOK_INDEX,

		LEGO_NXT_SPEED, LEGO_NXT_DEGREES, LEGO_NXT_FREQUENCY, LEGO_NXT_DURATION_IN_SECONDS,

		LEGO_EV3_FREQUENCY, LEGO_EV3_DURATION_IN_SECONDS, LEGO_EV3_VOLUME,
		LEGO_EV3_SPEED, LEGO_EV3_POWER, LEGO_EV3_PERIOD_IN_SECONDS, LEGO_EV3_DEGREES,

		DRONE_TIME_TO_FLY_IN_SECONDS, LIST_ADD_ITEM, LIST_DELETE_ITEM, INSERT_ITEM_INTO_USERLIST_VALUE,
		INSERT_ITEM_INTO_USERLIST_INDEX, REPLACE_ITEM_IN_USERLIST_VALUE, REPLACE_ITEM_IN_USERLIST_INDEX, DRONE_POWER_IN_PERCENT,

		DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED_MAX, DRONE_ROTATION_MAX, DRONE_TILT_ANGLE,

		JUMPING_SUMO_SPEED, JUMPING_SUMO_TIME_TO_DRIVE_IN_SECONDS, JUMPING_SUMO_VOLUME, JUMPING_SUMO_ROTATE,

		PHIRO_SPEED, PHIRO_DURATION_IN_SECONDS, PHIRO_LIGHT_RED, PHIRO_LIGHT_GREEN, PHIRO_LIGHT_BLUE,

		PHYSICS_BOUNCE_FACTOR, PHYSICS_FRICTION, PHYSICS_GRAVITY_X, PHYSICS_GRAVITY_Y, PHYSICS_MASS,
		PHYSICS_VELOCITY_X, PHYSICS_VELOCITY_Y, PHYSICS_TURN_LEFT_SPEED, PHYSICS_TURN_RIGHT_SPEED,

		ARDUINO_ANALOG_PIN_VALUE, ARDUINO_ANALOG_PIN_NUMBER, ARDUINO_DIGITAL_PIN_VALUE, ARDUINO_DIGITAL_PIN_NUMBER,

		RASPI_DIGITAL_PIN_VALUE, RASPI_DIGITAL_PIN_NUMBER, RASPI_PWM_PERCENTAGE, RASPI_PWM_FREQUENCY,

		EMBROIDERY_LENGTH,

		ASSERT_EQUALS_EXPECTED, ASSERT_EQUALS_ACTUAL;

		public static final BrickField[] EXPECTS_STRING_VALUE = {VARIABLE, NOTE, SPEAK, STRING, ASK_QUESTION,
				NFC_NDEF_MESSAGE, ASK_SPEECH_QUESTION, LIST_ADD_ITEM, INSERT_ITEM_INTO_USERLIST_VALUE,
				REPLACE_ITEM_IN_USERLIST_VALUE};

		public static boolean isExpectingStringValue(BrickField field) {
			for (BrickField bf : EXPECTS_STRING_VALUE) {
				if (bf.equals(field)) {
					return true;
				}
			}
			return false;
		}
	}

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({TEXT_TO_SPEECH, BLUETOOTH_LEGO_NXT, PHYSICS, FACE_DETECTION, ARDRONE_SUPPORT,
			BLUETOOTH_SENSORS_ARDUINO, SOCKET_RASPI, CAMERA_FLASH, VIBRATOR, BLUETOOTH_PHIRO, CAMERA_BACK, CAMERA_FRONT,
			SENSOR_ACCELERATION, SENSOR_INCLINATION, SENSOR_COMPASS, NFC_ADAPTER, VIDEO, SENSOR_GPS, COLLISION,
			BLUETOOTH_LEGO_EV3, NETWORK_CONNECTION, CAST_REQUIRED, JUMPING_SUMO, MICROPHONE})
	@interface Resources {}
	int TEXT_TO_SPEECH = 1;
	int BLUETOOTH_LEGO_NXT = 2;
	int PHYSICS = 3;
	int FACE_DETECTION = 4;
	int ARDRONE_SUPPORT = 5;
	int BLUETOOTH_SENSORS_ARDUINO = 6;
	int SOCKET_RASPI = 7;
	int CAMERA_FLASH = 8;
	int VIBRATOR = 9;
	int BLUETOOTH_PHIRO = 10;
	int CAMERA_BACK = 11;
	int CAMERA_FRONT = 12;
	int SENSOR_ACCELERATION = 13;
	int SENSOR_INCLINATION = 14;
	int SENSOR_COMPASS = 15;
	int NFC_ADAPTER = 16;
	int VIDEO = 17;
	int SENSOR_GPS = 18;
	int COLLISION = 19;
	int BLUETOOTH_LEGO_EV3 = 20;
	int NETWORK_CONNECTION = 21;
	int CAST_REQUIRED = 22;
	int JUMPING_SUMO = 23;
	int MICROPHONE = 24;

	class ResourcesSet extends HashSet<Integer> {
		@Override
		public boolean add(@Resources Integer integer) {
			return super.add(integer);
		}
	}

	void addRequiredResources(ResourcesSet requiredResourcesSet);

	void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence);

	View getView(Context context);

	View getPrototypeView(Context context);

	CheckBox getCheckBox();

	void disableSpinners();

	Brick clone() throws CloneNotSupportedException;

	void addToFlatList(List<Brick> bricks);

	boolean consistsOfMultipleParts();

	List<Brick> getAllParts();

	Script getScript();

	int getPositionInScript();

	Brick getParent();

	void setParent(Brick parent);

	List<Brick> getDragAndDropTargetList();

	int getPositionInDragAndDropTargetList();

	boolean removeChild(Brick brick);

	boolean isCommentedOut();

	void setCommentedOut(boolean commentedOut);

	boolean hasHelpPage();
}
