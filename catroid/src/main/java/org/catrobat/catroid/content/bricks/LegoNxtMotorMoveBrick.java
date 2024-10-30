/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;

import kotlin.Unit;

public class LegoNxtMotorMoveBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String motor;

	public enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_B_C
	}

	public LegoNxtMotorMoveBrick() {
		motor = Motor.MOTOR_A.name();
		addAllowedBrickField(BrickField.LEGO_NXT_SPEED, R.id.motor_action_speed_edit_text);
	}

	public LegoNxtMotorMoveBrick(Motor motorEnum, int speedValue) {
		this(motorEnum, new Formula(speedValue));
	}

	public LegoNxtMotorMoveBrick(Motor motorEnum, Formula formula) {
		this();
		motor = motorEnum.name();
		setFormulaWithBrickField(BrickField.LEGO_NXT_SPEED, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_nxt_motor_action;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(context, R.array.nxt_motor_chooser, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.lego_motor_action_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			motor = Motor.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Motor.valueOf(motor).ordinal());
		return view;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_LEGO_NXT);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoNxtMotorMoveAction(sprite, sequence,
				Motor.valueOf(motor), getFormulaWithBrickField(BrickField.LEGO_NXT_SPEED)));
	}
}
