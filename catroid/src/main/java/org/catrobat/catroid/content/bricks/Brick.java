/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.io.Serializable;
import java.util.List;

public interface Brick extends Serializable, Cloneable {

	enum BrickField {
		COLOR, COLOR_CHANGE, BRIGHTNESS, BRIGHTNESS_CHANGE, X_POSITION, Y_POSITION, X_POSITION_CHANGE, Y_POSITION_CHANGE,
		TRANSPARENCY, TRANSPARENCY_CHANGE, SIZE, SIZE_CHANGE, VOLUME, VOLUME_CHANGE, X_DESTINATION, Y_DESTINATION, STEPS,
		DURATION_IN_SECONDS, DEGREES, TURN_RIGHT_DEGREES, TURN_LEFT_DEGREES, TIME_TO_WAIT_IN_SECONDS, VARIABLE,
		VARIABLE_CHANGE, PEN_SIZE, IF_CONDITION, TIMES_TO_REPEAT, VIBRATE_DURATION_IN_SECONDS, USER_BRICK, NOTE, SPEAK,
		SHOWTEXT, HIDETEXT, STRING, REPEAT_UNTIL_CONDITION, ASK_QUESTION,

		LEGO_NXT_SPEED, LEGO_NXT_DEGREES, LEGO_NXT_FREQUENCY, LEGO_NXT_DURATION_IN_SECONDS,

		DRONE_TIME_TO_FLY_IN_SECONDS, LIST_ADD_ITEM, LIST_DELETE_ITEM, INSERT_ITEM_INTO_USERLIST_VALUE,
		INSERT_ITEM_INTO_USERLIST_INDEX, REPLACE_ITEM_IN_USERLIST_VALUE, REPLACE_ITEM_IN_USERLIST_INDEX, DRONE_POWER_IN_PERCENT,

		DRONE_ALTITUDE_LIMIT, DRONE_VERTICAL_SPEED_MAX, DRONE_ROTATION_MAX, DRONE_TILT_ANGLE,

		PHIRO_SPEED, PHIRO_DURATION_IN_SECONDS, PHIRO_LIGHT_RED, PHIRO_LIGHT_GREEN, PHIRO_LIGHT_BLUE,
		IF_PHIRO_SENSOR_CONDITION,

		PHYSICS_BOUNCE_FACTOR, PHYSICS_FRICTION, PHYSICS_GRAVITY_X, PHYSICS_GRAVITY_Y, PHYSICS_MASS,
		PHYSICS_VELOCITY_X, PHYSICS_VELOCITY_Y, PHYSICS_TURN_LEFT_SPEED, PHYSICS_TURN_RIGHT_SPEED,

		ARDUINO_ANALOG_PIN_VALUE, ARDUINO_ANALOG_PIN_NUMBER, ARDUINO_DIGITAL_PIN_VALUE, ARDUINO_DIGITAL_PIN_NUMBER,

		RASPI_DIGITAL_PIN_VALUE, RASPI_DIGITAL_PIN_NUMBER, RASPI_PWM_PERCENTAGE, RASPI_PWM_FREQUENCY
	}

	//use bitwise | for using multiple resources in a brick
	int NO_RESOURCES = 0x0;
	//int SOUND_MANAGER = 0x1;
	int TEXT_TO_SPEECH = 0x2;
	int BLUETOOTH_LEGO_NXT = 0x4;
	int PHYSICS = 0x8;
	int FACE_DETECTION = 0x10;
	int ARDRONE_SUPPORT = 0x20;
	int BLUETOOTH_SENSORS_ARDUINO = 0x40;
	int SOCKET_RASPI = 0x80;
	int CAMERA_FLASH = 0x100;
	int VIBRATOR = 0x200;
	int BLUETOOTH_PHIRO = 0x400;
	int CAMERA_BACK = 0x800;
	int CAMERA_FRONT = 0x1000;
	int SENSOR_ACCELERATION = 0x2000;
	int SENSOR_INCLINATION = 0x4000;
	int SENSOR_COMPASS = 0x8000;
	int NFC_ADAPTER = 0x10000;
	int VIDEO = 0x20000;
	int SENSOR_GPS = 0x40000;

	//	public static final int BLUETOOTH_ARDUINO = 0x20000;

	List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	//needed for the Sprite#clone()-Method
	Brick copyBrickForSprite(Sprite sprite);

	View getView(Context context, int brickId, BaseAdapter adapter);

	View getPrototypeView(Context context);

	Brick clone() throws CloneNotSupportedException;

	int getRequiredResources();

	int getAlphaValue();

	void setBrickAdapter(BrickAdapter adapter);

	CheckBox getCheckBox();

	boolean isChecked();

	boolean isCommentedOut();

	void setCommentedOut(boolean commentedOut);

	void setCheckboxView(int id);

	void setCheckboxView(int id, View view);

	void setAnimationState(boolean animationState);

	void setAlpha(int alphaFull);

	void enableAllViews(View view, boolean enable);

	boolean isEqualBrick(Brick brick, Scene mergeResult, Scene current);

	void storeDataForBackPack(Sprite sprite);
}
