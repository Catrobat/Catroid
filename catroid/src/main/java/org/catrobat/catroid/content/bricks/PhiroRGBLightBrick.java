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
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageAttributes;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.ui.UiUtils;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Set Phiro")
public class PhiroRGBLightBrick extends FormulaBrick implements UpdateableSpinnerBrick, CatrobatLanguageAttributes {

	private static final long serialVersionUID = 1L;

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	private String eye;

	public enum Eye {
		LEFT, RIGHT, BOTH
	}

	public PhiroRGBLightBrick() {
		addAllowedBrickField(BrickField.PHIRO_LIGHT_RED, R.id.brick_phiro_rgb_led_action_red_edit_text, "red");
		addAllowedBrickField(BrickField.PHIRO_LIGHT_GREEN, R.id.brick_phiro_rgb_led_action_green_edit_text, "green");
		addAllowedBrickField(BrickField.PHIRO_LIGHT_BLUE, R.id.brick_phiro_rgb_led_action_blue_edit_text, "blue");
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

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		if (itemIndex >= 0 && itemIndex < Eye.values().length) {
			eye = Eye.values()[itemIndex].name();
		}
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

	private String getColorValueFromBrickField(BrickField brickField) {
		Formula formula = getFormulaWithBrickField(brickField);
		try {
			int value = formula.interpretInteger(null);
			int minimum = Math.max(0, Math.min(255, value));
			return String.format("%02X", minimum);
		} catch (InterpretationException e) {
			return "00";
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageParameterizedCall(indentionLevel, false).toString();
	}

	@Override
	public void appendCatrobatLanguageArguments(StringBuilder brickBuilder) {
		String red = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_RED);
		String green = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_GREEN);
		String blue = getColorValueFromBrickField(BrickField.PHIRO_LIGHT_BLUE);
		String hexColor = CatrobatLanguageUtils.formatHexColorString(red + green + blue);

		brickBuilder.append("light: (")
				.append(this.getCatrobatLanguageSpinnerValue(Eye.valueOf(eye).ordinal()))
				.append("), color: (")
				.append(hexColor)
				.append(')');
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case 0:
				return "left";
			case 1:
				return "right";
			case 2:
				return "both";
			default:
				throw new IndexOutOfBoundsException("Invalid spinnerIndex in " + getClass().getSimpleName());
		}
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>();
		requiredArguments.add("light");
		requiredArguments.add("color");
		return requiredArguments;
	}
}
