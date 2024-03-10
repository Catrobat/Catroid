/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormula;
import org.catrobat.catroid.ui.FormulaEditorActivity;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;
import org.catrobat.catroid.ui.fragment.SingleSeekBar;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;

import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.REQUEST_CODE_EDIT_FORMULA;

public class PhiroMotorMoveForwardBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private String motor;

	public enum Motor {
		MOTOR_LEFT, MOTOR_RIGHT, MOTOR_BOTH
	}

	public PhiroMotorMoveForwardBrick() {
		motor = Motor.MOTOR_LEFT.name();
		addAllowedBrickField(BrickField.PHIRO_SPEED, R.id.brick_phiro_motor_forward_action_speed_edit_text);
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
			showCustomFragment(view.getContext(), this, BrickField.PHIRO_SPEED);
		} else {
			super.showFormulaEditorToEditFormula(view);
		}
	}

	private void showCustomFragment(Context context, FormulaBrick formulaBrick, Brick.FormulaField formulaField) {
		Intent intent = new Intent(context, FormulaEditorActivity.class);
		intent.putExtra(FormulaEditorFragment.SHOW_CUSTOM_VIEW, true);
		intent.putExtra(FormulaEditorFragment.FORMULA_BRICK_BUNDLE_ARGUMENT, formulaBrick);
		intent.putExtra(FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT, formulaField);
		intent.putExtra(FormulaEditorFragment.BRICK_FIELD_TO_TEXT_VIEW_ID_MAP, new HashMap<>(brickFieldToTextViewIdMap));
		intent.putExtra(FormulaEditorFragment.FORMULA_MAP_BUNDLE_ARGUMENT, formulaMap);

		Brick.FormulaField currentFormulaField = (Brick.FormulaField) intent
				.getSerializableExtra(FormulaEditorFragment.FORMULA_FIELD_BUNDLE_ARGUMENT);

		Formula currentFormula = formulaBrick.getFormulaWithBrickField(currentFormulaField);
		InternFormula internFormula = currentFormula.internFormula;
		intent.putExtra(FormulaEditorFragment.CURRENT_BRICK_INTERN_FORMULA, internFormula);
		intent.putExtra(EXTRA_BRICK_HASH, hashCode());

		AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (!(activity instanceof SpriteActivity)) {
			return;
		}
		activity.startActivityForResult(intent, REQUEST_CODE_EDIT_FORMULA);
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
}
