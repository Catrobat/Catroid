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
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.text.NumberFormat;
import java.util.List;

public class LegoEv3SingleMotorMoveBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;

	private transient Motor motorEnum;

	private String motor;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D
	}

	public LegoEv3SingleMotorMoveBrick() {
		addAllowedBrickField(BrickField.LEGO_EV3_POWER);
		addAllowedBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS);
	}

	public LegoEv3SingleMotorMoveBrick(Motor motor, int powerValue, float durationValue) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();

		initializeBrickFields(new Formula(powerValue), new Formula(durationValue));
	}

	public LegoEv3SingleMotorMoveBrick(Motor motor, Formula powerFormula, Formula durationFormula) {
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
		prototypeView = View.inflate(context, R.layout.brick_ev3_single_motor_move, null);

		TextView textPower = (TextView) prototypeView.findViewById(R.id.brick_ev3_single_motor_move_power_prototype_text_view);

		textPower.setText(String.valueOf(BrickValues.LEGO_POWER));

		Spinner motorSpinner = (Spinner) prototypeView.findViewById(R.id.brick_ev3_single_motor_move_spinner);
		motorSpinner.setFocusableInTouchMode(false);
		motorSpinner.setFocusable(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context,
				R.array.ev3_single_motor_chooser, android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setSelection(motorEnum.ordinal());

		TextView textDuration = (TextView) prototypeView.findViewById(R.id.brick_ev3_single_motor_move_period_prototype_text_view);
		NumberFormat nf = NumberFormat.getInstance(context.getResources().getConfiguration().locale);
		nf.setMinimumFractionDigits(1);
		textDuration.setText(nf.format(BrickValues.LEGO_DURATION));

		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoEv3SingleMotorMoveBrick(motorEnum, getFormulaWithBrickField(BrickField.LEGO_EV3_POWER).clone(),
				getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS));
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_single_motor_move, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_ev3_single_motor_move_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textPower = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_power_prototype_text_view);
		TextView editPower = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_power_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_POWER)
				.setTextFieldId(R.id.brick_ev3_single_motor_move_power_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_POWER).refreshTextField(view);

		textPower.setVisibility(View.GONE);
		editPower.setVisibility(View.VISIBLE);

		editPower.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_period_prototype_text_view);
		TextView editDuration = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_period_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS)
				.setTextFieldId(R.id.brick_ev3_single_motor_move_period_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS).refreshTextField(view);

		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.ev3_single_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_ev3_single_motor_move_spinner);

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
				adapterView = arg0;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});

		motorSpinner.setSelection(motorEnum.ordinal());

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		switch (view.getId()) {
			case R.id.brick_ev3_single_motor_move_power_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_EV3_POWER);
				break;
			case R.id.brick_ev3_single_motor_move_period_edit_text:
				FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_EV3_PERIOD_IN_SECONDS);
				break;
		}
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_ev3_single_motor_move_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textSingleMotorMoveLabel = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_label);
			TextView textSingleMotorMovePower = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_power_text);
			TextView textSingleMotorMovePercent = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_percent);
			TextView textSingleMotorMovePeriod = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_period_text);
			TextView textSingleMotorMoveSecond = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_seconds);

			textSingleMotorMoveLabel.setTextColor(textSingleMotorMoveLabel.getTextColors().withAlpha(alphaValue));
			textSingleMotorMovePower.setTextColor(textSingleMotorMovePower.getTextColors().withAlpha(alphaValue));
			textSingleMotorMovePercent.setTextColor(textSingleMotorMovePercent.getTextColors().withAlpha(alphaValue));
			textSingleMotorMovePeriod.setTextColor(textSingleMotorMovePeriod.getTextColors().withAlpha(alphaValue));
			textSingleMotorMoveSecond.setTextColor(textSingleMotorMoveSecond.getTextColors().withAlpha(alphaValue));

			TextView editSingleMotorMovePower = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_power_edit_text);
			TextView editSingleMotorMovePeriod = (TextView) view.findViewById(R.id.brick_ev3_single_motor_move_period_edit_text);

			editSingleMotorMovePower.setTextColor(editSingleMotorMovePower.getTextColors().withAlpha(alphaValue));
			editSingleMotorMovePower.getBackground().setAlpha(alphaValue);
			editSingleMotorMovePeriod.setTextColor(editSingleMotorMovePeriod.getTextColors().withAlpha(alphaValue));
			editSingleMotorMovePeriod.getBackground().setAlpha(alphaValue);

			Spinner motorSpinner = (Spinner) view.findViewById(R.id.brick_ev3_single_motor_move_spinner);
			ColorStateList color = textSingleMotorMoveLabel.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoEv3SingleMotorMove(sprite, motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_EV3_POWER),
				getFormulaWithBrickField(BrickField.LEGO_EV3_PERIOD_IN_SECONDS)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Project into, Project from) {
	}
}
