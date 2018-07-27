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
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.BrickValues;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.ui.fragment.ColorSeekbar;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class SetPenColorBrick extends FormulaBrick {

	private static final long serialVersionUID = 1L;

	private transient ColorSeekbar colorSeekbar = new ColorSeekbar(this, BrickField.PEN_COLOR_RED,
			BrickField.PEN_COLOR_GREEN, BrickField.PEN_COLOR_BLUE);

	public SetPenColorBrick() {
		addAllowedBrickField(BrickField.PEN_COLOR_RED);
		addAllowedBrickField(BrickField.PEN_COLOR_GREEN);
		addAllowedBrickField(BrickField.PEN_COLOR_BLUE);
	}

	public SetPenColorBrick(int red, int green, int blue) {
		initializeBrickFields(new Formula(red), new Formula(green), new Formula(blue));
	}

	public SetPenColorBrick(Formula red, Formula green, Formula blue) {
		initializeBrickFields(red, green, blue);
	}

	private void initializeBrickFields(Formula red, Formula green, Formula blue) {
		addAllowedBrickField(BrickField.PEN_COLOR_RED);
		addAllowedBrickField(BrickField.PEN_COLOR_GREEN);
		addAllowedBrickField(BrickField.PEN_COLOR_BLUE);
		setFormulaWithBrickField(BrickField.PEN_COLOR_RED, red);
		setFormulaWithBrickField(BrickField.PEN_COLOR_GREEN, green);
		setFormulaWithBrickField(BrickField.PEN_COLOR_BLUE, blue);
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);

		TextView textValueRed = prototypeView.findViewById(R.id.brick_set_pen_color_action_red_edit_text);
		textValueRed.setText(formatNumberForPrototypeView(BrickValues.PEN_COLOR.r * 255));

		TextView textValueGreen = prototypeView.findViewById(R.id.brick_set_pen_color_action_green_edit_text);
		textValueGreen.setText(formatNumberForPrototypeView(BrickValues.PEN_COLOR.g * 255));

		TextView textValueBlue = prototypeView.findViewById(R.id.brick_set_pen_color_action_blue_edit_text);
		textValueBlue.setText(formatNumberForPrototypeView(BrickValues.PEN_COLOR.b * 255));

		return prototypeView;
	}

	@Override
	public BrickBaseType clone() {
		return new SetPenColorBrick(getFormulaWithBrickField(BrickField.PEN_COLOR_RED).clone(),
				getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN).clone(),
				getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE).clone());
	}

	@Override
	public View getCustomView(Context context, int brickId, BaseAdapter baseAdapter) {
		return colorSeekbar.getView(context);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_set_pen_color;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		TextView editRedValue = view.findViewById(R.id.brick_set_pen_color_action_red_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_RED).setTextFieldId(R.id.brick_set_pen_color_action_red_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_RED).refreshTextField(view);

		editRedValue.setOnClickListener(this);

		TextView editGreenValue = view.findViewById(R.id.brick_set_pen_color_action_green_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN).setTextFieldId(R.id.brick_set_pen_color_action_green_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN).refreshTextField(view);

		editGreenValue.setOnClickListener(this);

		TextView editBlueValue = view.findViewById(R.id.brick_set_pen_color_action_blue_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE).setTextFieldId(R.id.brick_set_pen_color_action_blue_edit_text);
		getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE).refreshTextField(view);

		editBlueValue.setOnClickListener(this);

		return view;
	}

	@Override
	public void showFormulaEditorToEditFormula(View view) {
		if (areAllBrickFieldsNumbers()) {
			FormulaEditorFragment.showCustomFragment(view, this, getClickedBrickField(view));
		} else {
			FormulaEditorFragment.showFragment(view, this, getClickedBrickField(view));
		}
	}

	private boolean areAllBrickFieldsNumbers() {
		return (getFormulaWithBrickField(BrickField.PEN_COLOR_RED).getRoot().getElementType() == FormulaElement.ElementType.NUMBER)
				&& (getFormulaWithBrickField(BrickField.PEN_COLOR_GREEN).getRoot().getElementType() == FormulaElement.ElementType.NUMBER)
				&& (getFormulaWithBrickField(BrickField.PEN_COLOR_BLUE).getRoot().getElementType() == FormulaElement.ElementType.NUMBER);
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
