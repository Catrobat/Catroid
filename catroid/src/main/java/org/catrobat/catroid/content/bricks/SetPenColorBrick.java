/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.ColorSeekbar;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetPenColorBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	public SetPenColorBrick() {
		addAllowedBrickField(BrickField.PEN_COLOR_RED, R.id.brick_set_pen_color_action_red_edit_text);
		addAllowedBrickField(BrickField.PEN_COLOR_GREEN, R.id.brick_set_pen_color_action_green_edit_text);
		addAllowedBrickField(BrickField.PEN_COLOR_BLUE, R.id.brick_set_pen_color_action_blue_edit_text);
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
	public View getCustomView(Context context) {
		return new ColorSeekbar(this, BrickField.PEN_COLOR_RED,
				BrickField.PEN_COLOR_GREEN, BrickField.PEN_COLOR_BLUE).getView(context);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_pen_color;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (areAllBrickFieldsNumbers()) {
			FormulaEditorFragment.showCustomFragment(view.getContext(), this, getClickedBrickField(view));
		} else {
			super.showFormulaEditorToEditFormula(view);
		}
	}

	private boolean areAllBrickFieldsNumbers() {
		return (getFormulaWithBrickField(BrickField.PEN_COLOR_RED).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER)
				&& (getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER)
				&& (getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE).getRoot().getElementType()
				== FormulaElement.ElementType.NUMBER);
	}

	private BrickField getClickedBrickField(View view) {
		switch (view.getId()) {
			case R.id.brick_set_pen_color_action_green_edit_text:
				return BrickField.PEN_COLOR_GREEN;
			case R.id.brick_set_pen_color_action_blue_edit_text:
				return BrickField.PEN_COLOR_BLUE;
			case R.id.brick_set_pen_color_action_red_edit_text:
			default:
				return BrickField.PEN_COLOR_RED;
		}
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetPenColorAction(sprite,
				getFormulaWithBrickField(BrickField.PEN_COLOR_RED),
				getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN),
				getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE)));
		return null;
	}

	public void correctBrickFieldsFromPhiro() {
		replaceFormulaBrickField(BrickField.PHIRO_LIGHT_RED, BrickField.PEN_COLOR_RED);
		replaceFormulaBrickField(BrickField.PHIRO_LIGHT_GREEN, BrickField.PEN_COLOR_GREEN);
		replaceFormulaBrickField(BrickField.PHIRO_LIGHT_BLUE, BrickField.PEN_COLOR_BLUE);
	}
}
