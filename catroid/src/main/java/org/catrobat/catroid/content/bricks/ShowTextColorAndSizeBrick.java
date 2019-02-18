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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class ShowTextColorAndSizeBrick extends UserVariableBrick {
	private static final long serialVersionUID = 1L;

	public ShowTextColorAndSizeBrick() {
		addAllowedBrickField(BrickField.X_POSITION, R.id.brick_show_variable_color_and_size_edit_text_x);
		addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_show_variable_color_and_size_edit_text_y);
		addAllowedBrickField(BrickField.SIZE, R.id.brick_show_variable_color_and_size_edit_relative_size);
		addAllowedBrickField(BrickField.COLOR, R.id.brick_show_variable_color_and_size_edit_color);
	}

	public ShowTextColorAndSizeBrick(int xPosition, int yPosition, double size, String color) {
		this(new Formula(xPosition), new Formula(yPosition), new Formula(size), new Formula(color));
	}

	public ShowTextColorAndSizeBrick(Formula xPosition, Formula yPosition, Formula size, Formula color) {
		this();
		setFormulaWithBrickField(BrickField.X_POSITION, xPosition);
		setFormulaWithBrickField(BrickField.Y_POSITION, yPosition);
		setFormulaWithBrickField(BrickField.SIZE, size);
		setFormulaWithBrickField(BrickField.COLOR, color);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_show_variable_text_color_and_size;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.show_variable_color_and_size_spinner;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		if (userVariable == null || userVariable.getName() == null) {
			userVariable = new UserVariable("NoVariableSet", Constants.NO_VARIABLE_SELECTED);
			userVariable.setDummy(true);
		}

		sequence.addAction(sprite.getActionFactory().createShowVariableColorAndSizeAction(sprite,
				getFormulaWithBrickField(BrickField.X_POSITION),
				getFormulaWithBrickField(BrickField.Y_POSITION),
				getFormulaWithBrickField(BrickField.SIZE),
				getFormulaWithBrickField(BrickField.COLOR), userVariable));
		return null;
	}
}
