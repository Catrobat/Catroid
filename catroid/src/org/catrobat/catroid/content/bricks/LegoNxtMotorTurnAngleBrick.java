/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class LegoNxtMotorTurnAngleBrick extends FormulaBrick implements OnClickListener {
	private static final long serialVersionUID = 1L;

	private String motor;
	private transient Motor motorEnum;

	public static enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_A_C
	}

	public LegoNxtMotorTurnAngleBrick() {
		addAllowedBrickField(BrickField.LEGO_NXT_DEGREES);
	}

	public LegoNxtMotorTurnAngleBrick(Motor motor, int degrees) {
		this.motorEnum = motor;
		this.motor = motorEnum.name();
		initializeBrickFields(new Formula(degrees));
	}

	public LegoNxtMotorTurnAngleBrick(Motor motor, Formula degreesFormula) {
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
		addAllowedBrickField(BrickField.LEGO_NXT_DEGREES);
		setFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES, degreesFormula);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_NXT | getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES).getRequiredResources();
	}

	@Override
	public Brick clone() {
		return new LegoNxtMotorTurnAngleBrick(motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES).clone());
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

		view = View.inflate(context, R.layout.brick_nxt_motor_turn_angle, null);

		TextView editSpeed = (TextView) view.findViewById(R.id.motor_turn_angle_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES).setTextFieldId(R.id.motor_turn_angle_edit_text);
		getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES).refreshTextField(view);

		editSpeed.setOnClickListener(this);

		ArrayAdapter<CharSequence> motorAdapter = ArrayAdapter.createFromResource(context, R.array.nxt_motor_chooser,
				android.R.layout.simple_spinner_item);
		motorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner motorSpinner = (Spinner) view.findViewById(R.id.lego_motor_turn_angle_spinner);

		motorSpinner.setFocusableInTouchMode(false);
		motorSpinner.setFocusable(false);

		motorSpinner.setAdapter(motorAdapter);
		motorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

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
	public void onClick(View view) {
		if (!clickAllowed()) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES));
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.legoNxtMotorTurnAngle(sprite, motorEnum,
				getFormulaWithBrickField(BrickField.LEGO_NXT_DEGREES)));
		return null;
	}

}
