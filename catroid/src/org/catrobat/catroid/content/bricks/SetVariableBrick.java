/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class SetVariableBrick extends FormulaBrick {
	private static final long serialVersionUID = 1L;
	public boolean inUserBrick = false;
	private UserVariable userVariable;

	public SetVariableBrick() {
		addAllowedBrickField(BrickField.VARIABLE);
	}

	public SetVariableBrick(Formula variableFormula, UserVariable userVariable) {
		this.userVariable = userVariable;
		initializeBrickFields(variableFormula);
	}

	public SetVariableBrick(double value) {
		this.userVariable = null;
		initializeBrickFields(new Formula(value));
	}

	private void initializeBrickFields(Formula variableFormula) {
		addAllowedBrickField(BrickField.VARIABLE);
		setFormulaWithBrickField(BrickField.VARIABLE, variableFormula);
	}

	@Override
	public int getRequiredResources() {
		return getFormulaWithBrickField(BrickField.VARIABLE).getRequiredResources();
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setVariable(sprite, getFormulaWithBrickField(BrickField.VARIABLE),
				userVariable));
		return null;
	}

	@Override
	public Brick clone() {
		SetVariableBrick clonedBrick = new SetVariableBrick(getFormulaWithBrickField(BrickField.VARIABLE)
				.clone(), userVariable);
		return clonedBrick;
	}

	@Override
	public Brick copyBrickForSprite(Sprite cloneSprite) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			throw new RuntimeException("The current project must be set before cloning it");
		}

		SetVariableBrick copyBrick = (SetVariableBrick) clone();
		copyBrick.userVariable = currentProject.getUserVariables().getUserVariable(userVariable.getName(), cloneSprite);
		return copyBrick;
	}

	public void setInUserBrick(boolean inUserBrick) {
		this.inUserBrick = inUserBrick;
	}

	public boolean isInUserBrick() {
		return inUserBrick;
	}

	public void setUserVariable(UserVariable userVariable) {
		this.userVariable = userVariable;
	}

	public UserVariable getUserVariable() {
		return userVariable;
	}
}
