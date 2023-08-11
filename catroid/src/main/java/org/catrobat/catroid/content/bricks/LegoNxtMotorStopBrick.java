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
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Stop NXT")
public class LegoNxtMotorStopBrick extends BrickBaseType implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

	private String motor;

	public enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_B_C, ALL_MOTORS
	}

	public LegoNxtMotorStopBrick() {
		motor = Motor.MOTOR_A.name();
	}

	public LegoNxtMotorStopBrick(Motor motorEnum) {
		motor = motorEnum.name();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_nxt_motor_stop;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(context, R.array.nxt_stop_motor_chooser, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.stop_motor_spinner);
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
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoNxtMotorStopAction(Motor.valueOf(motor)));
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (itemIndex >= 0 && itemIndex < Motor.values().length) {
			motor = Motor.values()[itemIndex].name();
		}
	}

	private String getCatrobatLanguageMotor() {
		switch (LegoNxtMotorStopBrick.Motor.valueOf(motor)) {
			case MOTOR_A:
				return "A";
			case MOTOR_B:
				return "B";
			case MOTOR_C:
				return "C";
			case MOTOR_B_C:
				return "B+C";
			default:
				throw new IllegalStateException("Motor not implemented");
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageParamerCall(indentionLevel, "motor", getCatrobatLanguageMotor());
	}
}
