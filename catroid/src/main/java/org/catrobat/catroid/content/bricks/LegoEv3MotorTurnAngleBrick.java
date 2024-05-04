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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.CatrobatLanguageUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Turn EV3")
public class LegoEv3MotorTurnAngleBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;
	private static final String MOTOR_CATLANG_PARAMETER_NAME = "motor";
	private static final BiMap<Motor, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Motor, String>() {
		{
			put(Motor.MOTOR_A, "A");
			put(Motor.MOTOR_B, "B");
			put(Motor.MOTOR_C, "C");
			put(Motor.MOTOR_D, "D");
			put(Motor.MOTOR_B_C, "B+C");
			put(Motor.MOTOR_ALL, "all");
		}
	});

	private String motor;

	public enum Motor {
		MOTOR_A, MOTOR_B, MOTOR_C, MOTOR_D, MOTOR_B_C, MOTOR_ALL
	}

	public LegoEv3MotorTurnAngleBrick() {
		motor = Motor.MOTOR_A.name();
		addAllowedBrickField(BrickField.LEGO_EV3_DEGREES, R.id.ev3_motor_turn_angle_edit_text, "degrees");
	}

	public LegoEv3MotorTurnAngleBrick(Motor motorEnum, int degrees) {
		this(motorEnum, new Formula(degrees));
	}

	public LegoEv3MotorTurnAngleBrick(Motor motorEnum, Formula degreesFormula) {
		this();
		motor = motorEnum.name();
		setFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES, degreesFormula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_ev3_motor_turn_angle;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_LEGO_EV3);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public View getView(final Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
				.createFromResource(context, R.array.ev3_motor_chooser, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.lego_ev3_motor_turn_angle_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			motor = Motor.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Motor.valueOf(motor).ordinal());
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createLegoEv3MotorTurnAngleAction(sprite, sequence,
				Motor.valueOf(motor), getFormulaWithBrickField(BrickField.LEGO_EV3_DEGREES)));
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(MOTOR_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, CATLANG_SPINNER_VALUES.get(Motor.valueOf(motor)));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>();
		requiredArguments.add(MOTOR_CATLANG_PARAMETER_NAME);
		requiredArguments.addAll(super.getRequiredCatlangArgumentNames());
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String motor = arguments.get(MOTOR_CATLANG_PARAMETER_NAME);
		if (motor != null) {
			Motor selectedMotor = CATLANG_SPINNER_VALUES.inverse().get(motor);
			if (selectedMotor != null) {
				this.motor = selectedMotor.name();
			} else {
				throw new CatrobatLanguageParsingException("Invalid motor argument: " + motor);
			}
		}
	}
}
