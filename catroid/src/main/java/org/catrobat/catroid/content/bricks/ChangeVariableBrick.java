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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

public class ChangeVariableBrick extends UserVariableBrickWithFormula {

	private static final long serialVersionUID = 1L;

	public ChangeVariableBrick() {
		addAllowedBrickField(BrickField.VARIABLE_CHANGE, R.id.brick_change_variable_edit_text);
	}

	public ChangeVariableBrick(double value) {
		this(new Formula(value));
	}

	public ChangeVariableBrick(Formula formula, UserVariable userVariable) {
		this(formula);
		this.userVariable = userVariable;
	}

	public ChangeVariableBrick(Formula formula) {
		this();
		setFormulaWithBrickField(BrickField.VARIABLE_CHANGE, formula);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_change_variable_by;
	}

	@Override
	protected int getSpinnerId() {
		return R.id.change_variable_spinner;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory()
				.createChangeVariableAction(sprite, sequence,
						getFormulaWithBrickField(BrickField.VARIABLE_CHANGE), userVariable));
	}
}
