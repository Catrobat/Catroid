/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.text.NumberFormat;
import java.util.List;

public class LegoEv3MotorMoveBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;

	private transient Motor motorEnum;

	private String motor;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D, MOTOR_B_C
	}

	public LegoEv3MotorMoveBrick() {
		addAllowedBrickField(BrickField.LEGO_EV3_POWER);
		addAllowedBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS);
	}

	public LegoEv3MotorMoveBrick(Motor motor, int powerValue, float durationValue) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		initializeBrickFields(new Formula(powerValue), new Formula(durationValue));
	}

	public LegoEv3MotorMoveBrick(Motor motor, Formula powerFormula, Formula durationFormula) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		initializeBrickFields(powerFormula, durationFormula);
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	private void initializeBrickFields(Formula powerFormula, Formula durationFormula) {
		addAllowedBrickField(BrickField.LEGO_EV3_POWER);
		addAllowedBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS);
		setFormulaWithBrickField(BrickField.LEGO_EV3_POWER, powerFormula);
		setFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS, durationFormula);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3 | getFormulaWithBrickField(BrickField.LEGO_EV3_POWER).getRequiredResources()
				| getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_ev3_motor_move, null);

		TextView textPower = (TextView) prototypeView.findViewById(R.id.ev3_motor_move_power_edit_text);

		textPower.setText(String.valueOf(BrickValues.LEGO_POWER));

		Spinner motorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_ev3_motor_move_spinner);
		motorSpinner.setFocusableInTouchMode(false);
		motorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());

		TextView textDuration = (TextView) prototypeView.findViewById(R.id.ev3_motor_move_period_edit_text);
		NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
		nf.setMinimumFractionDigits(1);
		textDuration.setText(nf.format(BrickValues.LEGO_DURATION));

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoEv3MotorMoveBrick(motorEnum, getFormulaWithBrickField(BrickField.LEGO_EV3_POWER).clone(),
				getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS).clone());
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_motor_move, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_ev3_motor_move_checkbox);

		TextView editPower = (TextView) view.findViewById(R.id.ev3_motor_move_power_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_POWER)
				.setTextFieldId(R.id.ev3_motor_move_power_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_POWER).refreshTextField(view);

		editPower.setOnClickListener(this);

		TextView editDuration = (TextView) view.findViewById(R.id.ev3_motor_move_period_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS)
				.setTextFieldId(R.id.ev3_motor_move_period_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS).refreshTextField(view);

		editDuration.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.ev3_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_ev3_motor_move_spinner);

		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			motorSpinner.setClickable(true);
			motorSpinner.setEnabled(true);
		} else {
			motorSpinner.setClickable(false);
			motorSpinner.setEnabled(false);
		}

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

		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.ev3_motor_move_power_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_EV3_POWER);
				break;
			case R.id.ev3_motor_move_period_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_EV3_PERIOD_IN_SECONDS);
				break;
		}
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3SingleMotorMoveAction(sprite, motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_EV3_POWER),
				getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Scene into, Scene from) {
	}
}
