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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.DynamicTextSizeArrayAdapter;

import java.util.List;

public class LegoEv3MotorStopBrick extends BrickBaseType implements OnItemSelectedListener {
	private static final long serialVersionUID = 1L;
	private transient Motor motorEnum;
	private String motor;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D, MOTOR_B_C, ALL_MOTORS
	}

	public LegoEv3MotorStopBrick(Motor motor) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		LegoEv3MotorStopBrick copyBrick = (LegoEv3MotorStopBrick) clone();
		return copyBrick;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_ev3_motor_stop, null);
		Spinner legoSpinner = (Spinner) prototypeView.findViewById(R.id.ev3_stop_motor_spinner);
		legoSpinner.setFocusableInTouchMode(false);
		legoSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_stop_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		legoSpinner.setAdapter(motorAdapter);
		legoSpinner.setSelection(motorEnum.ordinal());
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoEv3MotorStopBrick(motorEnum);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_motor_stop, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_ev3_motor_stop_checkbox);

		DynamicTextSizeArrayAdapter<CharSequence> motorAdapter = new DynamicTextSizeArrayAdapter(context,
				android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.ev3_stop_motor_chooser));
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) view.findViewById(R.id.ev3_stop_motor_spinner);
		motorSpinner.setOnItemSelectedListener(this);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());
		return view;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		motorEnum = Motor.values()[position];
		motor = motorEnum.name();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3MotorStopAction(motorEnum));
		return null;
	}
}
