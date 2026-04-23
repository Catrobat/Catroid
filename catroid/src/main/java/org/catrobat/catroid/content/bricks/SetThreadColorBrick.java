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

public class SetThreadColorBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private final transient ShowFormulaEditorStrategy showFormulaEditorStrategy;

	public SetThreadColorBrick() {
		addAllowedBrickField(BrickField.THREAD_COLOR, R.id.brick_set_thread_color_action_edit_text);

		showFormulaEditorStrategy = new ShowColorPickerFormulaEditorStrategy();
	}

	public SetThreadColorBrick(Formula color) {
		this();
		setFormulaWithBrickField(BrickField.THREAD_COLOR, color);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_thread_color;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		ShowFormulaEditorStrategy.Callback callback = new SetThreadColorBrickCallback(view);
		showFormulaEditorStrategy.showFormulaEditorToEditFormula(view, callback);
	}

	private void superShowFormulaEditor(View view) {
		super.showFormulaEditorToEditFormula(view);
	}

	@Override
	public BrickField getDefaultBrickField() {
		return BrickField.THREAD_COLOR;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetThreadColorAction(
				sprite, sequence, getFormulaWithBrickField(BrickField.THREAD_COLOR)));
	}

	private final class SetThreadColorBrickCallback implements ShowFormulaEditorStrategy.Callback {
		private final View view;

		private SetThreadColorBrickCallback(View view) {
			this.view = view;
		}

		@Override
		public void showFormulaEditor(View view) {
			superShowFormulaEditor(view);
		}

		@Override
		public void setValue(int value) {
			setFormulaWithBrickField(BrickField.THREAD_COLOR, new Formula(convertToHexString(value)));

			AppCompatActivity activity = UiUtils.getActivityFromView(view);
			notifyDataSetChanged(activity);
		}

		@Override
		public int getValue() {
			return getColorValueFromBrickField(BrickField.THREAD_COLOR);
		}

		private int getColorValueFromBrickField(BrickField brickField) {
			Formula formula = getFormulaWithBrickField(brickField);
			try {
				String value = formula.interpretString(null);
				return Integer.decode(value);
			} catch (InterpretationException e) {
				return 0;
			}
		}

		private String convertToHexString(int value) {
			String hexValue = Integer.toHexString(value);
			hexValue = hexValue.substring(2);
			hexValue = "#" + hexValue;
			return hexValue;
		}
	}
}
