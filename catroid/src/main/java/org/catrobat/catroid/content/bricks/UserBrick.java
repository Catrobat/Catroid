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

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserBrick extends BrickBaseType implements OnClickListener {
	private static final long serialVersionUID = 1L;
	private static final String TAG = UserBrick.class.getSimpleName();

	@XStreamAlias("definitionBrick")
	private UserScriptDefinitionBrick definitionBrick;

	@XStreamAlias("userBrickParameters")
	private List<UserBrickParameter> userBrickParameters = new ArrayList<>();

	public UserBrick() {
		this.definitionBrick = new UserScriptDefinitionBrick();
	}

	public UserBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		definitionBrick.addRequiredResources(requiredResourcesSet);
	}

	public List<UserBrickParameter> getUserBrickParameters() {
		return userBrickParameters;
	}

	public void updateUserBrickParametersAndVariables() {
		updateUserVariableValues();
	}

	private void updateUserVariableValues() {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		List<UserVariable> variables = new ArrayList<>();

		for (UserBrickParameter userBrickParameter : userBrickParameters) {
			UserScriptDefinitionBrickElement element = userBrickParameter.getElement();
			if (element != null) {
				List<Formula> formulas = userBrickParameter.getFormulas();
				Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
				try {
					for (Formula formula : formulas) {
						variables.add(new UserVariable(element.getText(), formula.interpretDouble(sprite)));
					}
				} catch (InterpretationException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}

		if (variables.isEmpty()) {
			return;
		}

		dataContainer.setUserBrickVariables(this, variables);
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		if (currentSprite != null) {
			currentSprite.updateUserVariableReferencesInUserVariableBricks(variables);
		}
	}

	public List<Formula> getFormulas() {
		List<Formula> formulaList = new LinkedList<>();
		for (UserBrickParameter parameter : userBrickParameters) {
			UserScriptDefinitionBrickElement element = parameter.getElement();
			Formula formula = parameter.getFormulaWithBrickField(BrickField.USER_BRICK);
			if (formula != null && element != null && element.isVariable()) {
				formulaList.add(formula);
			}
		}
		return formulaList;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_user;
	}

	@Override
	public void onClick(View eventOrigin) {
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		updateUserVariableValues();
		List<ScriptSequenceAction> returnActionList = new ArrayList<>();

		ActionFactory actionFactory = sprite.getActionFactory();
		ScriptSequenceAction userSequence = (ScriptSequenceAction) ActionFactory.eventSequence(definitionBrick.getScript());
		definitionBrick.getScript().run(sprite, userSequence);
		returnActionList.add(userSequence);
		sequence.addAction(actionFactory.createUserBrickAction(userSequence, this));
		ProjectManager.getInstance().setCurrentUserBrick(this);

		if (sprite.isClone) {
			sprite.addUserBrick(this);
		}

		return returnActionList;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}

	public void setDefinitionBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}
}
