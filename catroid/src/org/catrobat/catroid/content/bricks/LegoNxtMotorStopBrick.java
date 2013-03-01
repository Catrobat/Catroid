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
package org.catrobat.catroid.content.bricks;

import org.catrobat.catroid.R;
import org.catrobat.catroid.LegoNXT.LegoNXT;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class LegoNxtMotorStopBrick implements Brick, OnItemSelectedListener {
	private static final long serialVersionUID = 1L;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_A_C, ALL_MOTORS
	}

	public LegoNxtMotorStopBrick() {

	}

	private Sprite sprite;
	private transient Motor motorEnum;
	private String motor;
	private transient CheckBox checkbox;
	private transient View view;
	private transient boolean checked;

	private static final int NO_DELAY = 0;

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	public LegoNxtMotorStopBrick(Sprite sprite, Motor motor) {
		this.sprite = sprite;
		this.motorEnum = motor;
		this.motor = motorEnum.name();
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT;
	}

	@Override
	public void execute() {
		if (motorEnum.equals(Motor.ALL_MOTORS)) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_A.ordinal(), 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_B.ordinal(), 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_C.ordinal(), 0, 0);
		} else if (motorEnum.equals(Motor.MOTOR_A_C)) {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_A.ordinal(), 0, 0);
			LegoNXT.sendBTCMotorMessage(NO_DELAY, Motor.MOTOR_C.ordinal(), 0, 0);
		} else {
			LegoNXT.sendBTCMotorMessage(NO_DELAY, motorEnum.ordinal(), 0, 0);
		}

	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_nxt_motor_stop, null);
	}

	@Override
	public Brick clone() {
		return new LegoNxtMotorStopBrick(getSprite(), motorEnum);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_nxt_motor_stop, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_nxt_motor_stop_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
			ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
					R.array.nxt_stop_motor_chooser, android.R.layout.simple_spinner_item);
			motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			Spinner motorSpinner = (Spinner) view.findViewById(R.id.stop_motor_spinner);
			motorSpinner.setOnItemSelectedListener(this);
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
			motorSpinner.setAdapter(motorAdapter);
			motorSpinner.setSelection(motorEnum.ordinal());
		}
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
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_nxt_motor_stop_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}
}
