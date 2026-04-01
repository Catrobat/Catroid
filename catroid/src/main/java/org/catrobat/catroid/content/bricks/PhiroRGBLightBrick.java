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
import android.graphics.Color;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.strategy.ShowColorPickerFormulaEditorStrategy;
import org.catrobat.catroid.content.strategy.ShowFormulaEditorStrategy;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.UiUtils;

import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;

public class PhiroRGBLightBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	private String eye;

	public enum Eye {
		LEFT, RIGHT, BOTH
	}

	public PhiroRGBLightBrick() {
		addAllowedBrickField(BrickField.PHIRO_LIGHT_RED, R.id.brick_phiro_rgb_led_action_red_edit_text);
		addAllowedBrickField(BrickField.PHIRO_LIGHT_GREEN, R.id.brick_phiro_rgb_led_action_green_edit_text);
		addAllowedBrickField(BrickField.PHIRO_LIGHT_BLUE, R.id.brick_phiro_rgb_led_action_blue_edit_text);
		eye = Eye.BOTH.name();

		showFormulaEditorStrategy = new ShowColorPickerFormulaEditorStrategy();
	}

	public PhiroRGBLightBrick(Eye eyeEnum, int red, int green, int blue) {
		this(eyeEnum, new Formula(red), new Formula(green), new Formula(blue));
	}

	public PhiroRGBLightBrick(Eye eyeEnum, Formula redFormula, Formula greenFormula, Formula blueFormula) {
		this();
		eye = eyeEnum.name();
		setFormulaWithBrickField(BrickField.PHIRO_LIGHT_RED, redFormula);
		setFormulaWithBrickField(BrickField.PHIRO_LIGHT_GREEN, greenFormula);
		setFormulaWithBrickField(BrickField.PHIRO_LIGHT_BLUE, blueFormula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_phiro_rgb_light;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_phiro_select_light_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_phiro_rgb_light_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			eye = Eye.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Eye.valueOf(eye).ordinal());
		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (areAllBrickFieldsNumbers()) {
			ShowFormulaEditorStrategy.Callback callback = new SetPhiroRGBLightBrickCallback(view);
			showFormulaEditorStrategy.showFormulaEditorToEditFormula(view, callback);
		} else {
			super.showFormulaEditorToEditFormula(view);
		}
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.PHIRO_LIGHT_RED;
	}

	private boolean areAllBrickFieldsNumbers() {
		return isBrickFieldANumber(BrickField.PHIRO_LIGHT_RED)
				&& isBrickFieldANumber(BrickField.PHIRO_LIGHT_GREEN)
				&& isBrickFieldANumber(BrickField.PHIRO_LIGHT_BLUE);
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(BLUETOOTH_PHIRO);
		super.addRequiredResources(requiredResourcesSet);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createPhiroRgbLedEyeActionAction(sprite, sequence,
				Eye.valueOf(eye),
				getFormulaWithBrickField(BrickField.PHIRO_LIGHT_RED),
				getFormulaWithBrickField(BrickField.PHIRO_LIGHT_GREEN),
				getFormulaWithBrickField(BrickField.PHIRO_LIGHT_BLUE)));
	}

	private final class SetPhiroRGBLightBrickCallback implements ShowFormulaEditorStrategy.Callback {
		private final View view;

		private SetPhiroRGBLightBrickCallback(View view) {
			this.view = view;
		}

		@Override
		public void showFormulaEditor(View view) {
			PhiroRGBLightBrick.super.showFormulaEditorToEditFormula(view);
		}

		@Override
		public void setValue(int value) {
			setFormulaWithBrickField(BrickField.PHIRO_LIGHT_RED, new Formula(Color.red(value)));
			setFormulaWithBrickField(BrickField.PHIRO_LIGHT_GREEN, new Formula(Color.green(value)));
			setFormulaWithBrickField(BrickField.PHIRO_LIGHT_BLUE, new Formula(Color.blue(value)));

			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			notifyDataSetChanged(activity);
		}

		@Override
		public int getValue() {
			int red = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_RED);
			int green = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_GREEN);
			int blue = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_BLUE);
			return Color.rgb(red, green, blue);
		}

		private int getColorValueFromBrickField(BrickField brickField) {
			Formula formula = getFormulaWithBrickField(brickField);
			try {
				int value = formula.interpretInteger(null);
				return Math.max(0, Math.min(255, value));
			} catch (InterpretationException e) {
				return 0;
			}
		}
	}
}
