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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.List;

public class LegoEv3MotorMoveBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String motor;
	private transient Motor motorEnum;

	public enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D, MOTOR_B_C
	}

	public LegoEv3MotorMoveBrick(Motor motor, int speedValue) {

		this(motor, new Formula(speedValue));
	}

	public LegoEv3MotorMoveBrick(Motor motor, Formula speed) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		addAllowedBrickField(BrickField.LEGO_EV3_SPEED, R.id.ev3_motor_move_speed_edit_text);
		setFormulaWithBrickField(BrickField.LEGO_EV3_SPEED, speed);
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_ev3_motor_move;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		Spinner motorSpinner = prototypeView.findViewById(R.id.brick_ev3_motor_move_spinner);
		motorSpinner.setFocusableInTouchMode(false);
		motorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());
		return prototypeView;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.ev3_motor_chooser,
				android.R.layout.simple_spinner_item);

		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = view.findViewById(R.id.brick_ev3_motor_move_spinner);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				motorEnum = Motor.values()[position];
				motor = motorEnum.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (motorEnum == null) {
			readResolve();
		}
		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3 | getFormulaWithBrickField(BrickField.LEGO_EV3_SPEED).getRequiredResources();
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3SingleMotorMoveAction(sprite, motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_EV3_SPEED)));
		return null;
	}
}
