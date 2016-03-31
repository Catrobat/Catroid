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
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
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

import java.util.List;

public class LegoEv3MotorTurnAngleBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private transient AdapterView<?> adapterView;
	private String motor;
	private transient Motor motorEnum;
	private transient TextView editSpeed;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D, MOTOR_B_C, MOTOR_ALL
	}

	public LegoEv3MotorTurnAngleBrick(Motor motor, int degrees) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		initializeBrickFields(new Formula(degrees));
	}

	public LegoEv3MotorTurnAngleBrick(Motor motor, Formula degreesFormula) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		initializeBrickFields(degreesFormula);
	}

	protected Object readResolve() {
		if (motor != null) {
			motorEnum = Motor.valueOf(motor);
		}
		return this;
	}

	private void initializeBrickFields(Formula degreesFormula) {
		addAllowedBrickField(BrickField.LEGO_EV3_DEGREES);
		setFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES, degreesFormula);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3 | getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES).getRequiredResources();
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_ev3_motor_turn_angle, null);
		TextView textX = (TextView) prototypeView.findViewById(R.id.ev3_motor_turn_angle_text_view);
		textX.setText(String.valueOf(BrickValues.LEGO_ANGLE));

		Spinner legoSpinner = (Spinner) prototypeView.findViewById(R.id.lego_ev3_motor_turn_angle_spinner);
		legoSpinner.setFocusableInTouchMode(false);
		legoSpinner.setFocusable(false);
		legoSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.ev3_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		legoSpinner.setAdapter(motorAdapter);
		legoSpinner.setSelection(motorEnum.ordinal());
		legoSpinner.setGravity(Gravity.CENTER);
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LegoEv3MotorTurnAngleBrick(motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES).clone());
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		FormulaEditorFragment.showFragment(view, this, BrickField.LEGO_EV3_DEGREES);
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		if (view == null) {
			alphaValue = 255;
		}
		view = View.inflate(context, R.layout.brick_ev3_motor_turn_angle, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_ev3_motor_turn_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textSpeed = (TextView) view.findViewById(R.id.ev3_motor_turn_angle_text_view);
		editSpeed = (TextView) view.findViewById(R.id.ev3_motor_turn_angle_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES).setTextFieldId(R.id.ev3_motor_turn_angle_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES).refreshTextField(view);

		textSpeed.setVisibility(View.GONE);
		editSpeed.setVisibility(View.VISIBLE);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.ev3_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) view.findViewById(R.id.lego_ev3_motor_turn_angle_spinner);

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
			}
		});

		motorSpinner.setSelection(motorEnum.ordinal());
		motorSpinner.setGravity(Gravity.CENTER);

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {

		if (view != null) {

			View layout = view.findViewById(R.id.brick_ev3_motor_turn_layout);
			Drawable background = layout.getBackground();
			background.setAlpha(alphaValue);

			TextView textLegoTurnAngleLabel = (TextView) view.findViewById(R.id.brick_ev3_motor_turn_label);
			TextView textLegoTurnAngleTextView = (TextView) view.findViewById(R.id.brick_ev3_motor_turn_angle);
			TextView textLegoTurnAngleView = (TextView) view.findViewById(R.id.ev3_motor_turn_angle_text_view);
			TextView textLegoTurnAngleDegree = (TextView) view.findViewById(R.id.brick_ev3_motor_turn_degree);
			TextView editLegoSpeed = (TextView) view.findViewById(R.id.ev3_motor_turn_angle_edit_text);

			textLegoTurnAngleLabel.setTextColor(textLegoTurnAngleLabel.getTextColors().withAlpha(alphaValue));
			textLegoTurnAngleTextView.setTextColor(textLegoTurnAngleTextView.getTextColors().withAlpha(alphaValue));
			textLegoTurnAngleView.setTextColor(textLegoTurnAngleView.getTextColors().withAlpha(alphaValue));
			textLegoTurnAngleDegree.setTextColor(textLegoTurnAngleDegree.getTextColors().withAlpha(alphaValue));
			Spinner motorSpinner = (Spinner) view.findViewById(R.id.lego_ev3_motor_turn_angle_spinner);
			ColorStateList color = textLegoTurnAngleDegree.getTextColors().withAlpha(alphaValue);
			motorSpinner.getBackground().setAlpha(alphaValue);
			if (adapterView != null) {
				((TextView) adapterView.getChildAt(0)).setTextColor(color);
			}
			editLegoSpeed.setTextColor(editLegoSpeed.getTextColors().withAlpha(alphaValue));
			editLegoSpeed.getBackground().setAlpha(alphaValue);

			this.alphaValue = alphaValue;
		}

		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoEv3MotorTurnAngle(sprite, motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES)));
		return null;
	}

	@Override
	public void updateReferenceAfterMerge(Project into, Project from) {
	}
}
