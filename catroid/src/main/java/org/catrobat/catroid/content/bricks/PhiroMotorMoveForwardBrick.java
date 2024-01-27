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
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.SingleSeekBar;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Move Phiro")
public class PhiroMotorMoveForwardBrick extends FormulaBrick implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

	private String motor;

	public enum Motor {
		MOTOR_LEFT, MOTOR_RIGHT, MOTOR_BOTH
	}

	public PhiroMotorMoveForwardBrick() {
		motor = Motor.MOTOR_LEFT.name();
		addAllowedBrickField(BrickField.PHIRO_SPEED, R.id.brick_phiro_motor_forward_action_speed_edit_text, "speed percentage");
	}

	public PhiroMotorMoveForwardBrick(Motor motorEnum, int speed) {
		this(motorEnum, new Formula(speed));
	}

	public PhiroMotorMoveForwardBrick(Motor motorEnum, Formula formula) {
		this();
		motor = motorEnum.name();
		setFormulaWithBrickField(BrickField.PHIRO_SPEED, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_motor_forward;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_select_motor_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_phiro_motor_forward_action_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			motor = Motor.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Motor.valueOf(motor).ordinal());
		return view;
	}

	@Override
	public View getCustomView(Context context) {
		return new SingleSeekBar(this, BrickField.PHIRO_SPEED, R.string.phiro_motor_speed).getView(context);
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (isSpeedOnlyANumber()) {
			FormulaEditorFragment.showCustomFragment(view.getContext(), this, BrickField.PHIRO_SPEED);
		} else {
			super.showFormulaEditorToEditFormula(view);
		}
	}

	private boolean isSpeedOnlyANumber() {
		return getFormulaWithBrickField(BrickField.PHIRO_SPEED)
				.getRoot().getElementType() == FormulaElement.ElementType.NUMBER;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_PHIRO);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPhiroMotorMoveForwardActionAction(sprite, sequence,
				Motor.valueOf(motor), getFormulaWithBrickField(BrickField.PHIRO_SPEED)));
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		Motor[] motors = Motor.values();
		if (itemIndex >= 0 && itemIndex < motors.length) {
			motor = motors[itemIndex].name();
		}
	}

	private String getCatrobatLanguageMotor() {
		switch (Motor.valueOf(motor)) {
			case MOTOR_LEFT:
				return "left";
			case MOTOR_RIGHT:
				return "right";
			case MOTOR_BOTH:
				return "both";
			default:
				throw new IllegalStateException("Motor not implemented");
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		String indention = CatrobatLanguageUtils.getIndention(indentionLevel);

		StringBuilder catrobatLanguage = new StringBuilder(60);
		catrobatLanguage.append(indention);

		if (commentedOut) {
			catrobatLanguage.append("// ");
		}

		catrobatLanguage.append(getCatrobatLanguageCommand())
				.append(" (motor: (")
				.append(getCatrobatLanguageMotor())
				.append("), direction: (forward), ");
		appendCatrobatLanguageArguments(catrobatLanguage);
		catrobatLanguage.append(");\n");
		return catrobatLanguage.toString();
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add("motor");
		requiredArguments.add("direction");
		return requiredArguments;
	}
}
