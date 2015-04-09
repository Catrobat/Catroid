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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import java.io.Serializable;
import java.util.List;

public interface Brick extends Serializable, Cloneable {

	enum BrickField {
		BRIGHTNESS, BRIGHTNESS_CHANGE, X_POSITION, Y_POSITION, X_POSITION_CHANGE, Y_POSITION_CHANGE, TRANSPARENCY,
		TRANSPARENCY_CHANGE, SIZE, SIZE_CHANGE, VOLUME, VOLUME_CHANGE, X_DESTINATION, Y_DESTINATION, STEPS,
		DURATION_IN_SECONDS, DEGREES, TURN_RIGHT_DEGREES, TURN_LEFT_DEGREES, TIME_TO_WAIT_IN_SECONDS, VARIABLE,
		VARIABLE_CHANGE, IF_CONDITION, TIMES_TO_REPEAT, VIBRATE_DURATION_IN_SECONDS, USER_BRICK, NOTE, SPEAK,

		LEGO_NXT_SPEED, LEGO_NXT_DEGREES, LEGO_NXT_FREQUENCY, LEGO_NXT_DURATION_IN_SECONDS,

		DRONE_TIME_TO_FLY_IN_SECONDS, LIST_ADD_ITEM, LIST_DELETE_ITEM, INSERT_ITEM_INTO_USERLIST_VALUE, INSERT_ITEM_INTO_USERLIST_INDEX, REPLACE_ITEM_IN_USERLIST_VALUE, REPLACE_ITEM_IN_USERLIST_INDEX, DRONE_POWER_IN_PERCENT
	}

	//use bitwise | for using multiple ressources in a brick
	int NO_RESOURCES = 0x0;
	//	public static final int SOUND_MANAGER = 0x1;
	int TEXT_TO_SPEECH = 0x2;
	int BLUETOOTH_LEGO_NXT = 0x4;
	int BLUETOOTH_SENSORS_ARDUINO = 0x40;
	int ARDRONE_SUPPORT = 0x20;
	int CAMERA_LED = 0x100;
	int VIBRATOR = 0x200;

	//	public static final int BLUETOOTH_ARDUINO = 0x8;
	int FACE_DETECTION = 0x10;

	List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence);

	//needed for the Sprite#clone()-Method
	Brick copyBrickForSprite(Sprite sprite);

	View getView(Context context, int brickId, BaseAdapter adapter);

	View getPrototypeView(Context context);

	Brick clone() throws CloneNotSupportedException;

	int getRequiredResources();

	void setCheckboxVisibility(int visibility);

	int getAlphaValue();

	void setBrickAdapter(BrickAdapter adapter);

	CheckBox getCheckBox();

	boolean isChecked();

	void setCheckedBoolean(boolean newValue);

	void setCheckboxView(int id);

	void setCheckboxView(int id, View view);

	View getViewWithAlpha(int alphaValue);

	void setAnimationState(boolean animationState);

	void setAlpha(int alphaFull);
}
