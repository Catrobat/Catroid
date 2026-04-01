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

import android.graphics.Color;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.strategy.ShowColorPickerFormulaEditorStrategy;
import org.catrobat.catroid.content.strategy.ShowFormulaEditorStrategy;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.ui.UiUtils;

import androidx.appcompat.app.AppCompatActivity;

public class SetPenColorBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	public SetPenColorBrick() {
		addAllowedBrickField(BrickField.PEN_COLOR_RED, R.id.brick_set_pen_color_action_red_edit_text);
		addAllowedBrickField(BrickField.PEN_COLOR_GREEN, R.id.brick_set_pen_color_action_green_edit_text);
		addAllowedBrickField(BrickField.PEN_COLOR_BLUE, R.id.brick_set_pen_color_action_blue_edit_text);

		showFormulaEditorStrategy = new ShowColorPickerFormulaEditorStrategy();
	}

	public SetPenColorBrick(int red, int green, int blue) {
		this(new Formula(red), new Formula(green), new Formula(blue));
	}

	public SetPenColorBrick(Formula red, Formula green, Formula blue) {
		this();
		setFormulaWithBrickField(BrickField.PEN_COLOR_RED, red);
		setFormulaWithBrickField(BrickField.PEN_COLOR_GREEN, green);
		setFormulaWithBrickField(BrickField.PEN_COLOR_BLUE, blue);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_pen_color;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (areAllBrickFieldsNumbers()) {
			ShowFormulaEditorStrategy.Callback callback = new SetPenColorBrickCallback(view);
			showFormulaEditorStrategy.showFormulaEditorToEditFormula(view, callback);
		} else {
			superShowFormulaEditor(view);
		}
	}

	private void superShowFormulaEditor(View view) {
		super.showFormulaEditorToEditFormula(view);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.PEN_COLOR_RED;
	}

	private boolean areAllBrickFieldsNumbers() {
		return isBrickFieldANumber(BrickField.PEN_COLOR_RED)
				&& isBrickFieldANumber(BrickField.PEN_COLOR_GREEN)
				&& isBrickFieldANumber(BrickField.PEN_COLOR_BLUE);
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetPenColorAction(sprite, sequence,
				getFormulaWithBrickField(BrickField.PEN_COLOR_RED),
				getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN),
				getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE)));
	}

	private final class SetPenColorBrickCallback implements ShowFormulaEditorStrategy.Callback {
		private final View view;

		private SetPenColorBrickCallback(View view) {
			this.view = view;
		}

		@Override
		public void showFormulaEditor(View view) {
			superShowFormulaEditor(view);
		}

		@Override
		public void setValue(int value) {
			setFormulaWithBrickField(BrickField.PEN_COLOR_RED, new Formula(Color.red(value)));
			setFormulaWithBrickField(BrickField.PEN_COLOR_GREEN, new Formula(Color.green(value)));
			setFormulaWithBrickField(BrickField.PEN_COLOR_BLUE, new Formula(Color.blue(value)));

			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			notifyDataSetChanged(activity);
		}

		@Override
		public int getValue() {
			int red = getColorValueFromBrickField(BrickField.PEN_COLOR_RED);
			int green = getColorValueFromBrickField(BrickField.PEN_COLOR_GREEN);
			int blue = getColorValueFromBrickField(BrickField.PEN_COLOR_BLUE);
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
