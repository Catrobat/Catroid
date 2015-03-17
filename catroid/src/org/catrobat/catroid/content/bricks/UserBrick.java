/**
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.util.Pair;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UserBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("definitionBrick")
	private UserScriptDefinitionBrick definitionBrick;

	// belonging to brick instance
	@XStreamAlias("userBrickParameters")
	private ArrayList<UserBrickParameter> userBrickParameters;

	@XStreamAlias("userBrickPositionToParameter")
	private ArrayList<Pair<Integer, Integer>> userBrickPositionToParameter;

	// belonging to stored brick
	private transient int lastDataVersion = 0;

	@XStreamAlias("userBrickId")
	private int userBrickId;

	public UserBrick(int userBrickId) {
		this.userBrickId = userBrickId;
		this.definitionBrick = new UserScriptDefinitionBrick(this);
		//first = first occurance of this Userbrick in script, second = parameterindex of this occurence of this Userbrick in script
		userBrickPositionToParameter = new ArrayList<Pair<Integer, Integer>>();
		updateUserBrickParameters(null);
	}

	public UserBrick(UserScriptDefinitionBrick definitionBrick, Sprite sprite) {
		this.definitionBrick = definitionBrick;
		this.userBrickId = sprite.getUserBrickList().size() - 1;
		definitionBrick.setUserBrick(this);
		userBrickPositionToParameter = new ArrayList<Pair<Integer, Integer>>();
		updateUserBrickParameters(null);
	}

	@Override
	public int getRequiredResources() {
		return definitionBrick.getRequiredResources();
	}

	@Override
	public UserBrick copyBrickForSprite(Sprite sprite) {
		UserBrick copyBrick = new UserBrick(definitionBrick, sprite);
		return copyBrick;
	}

	public boolean isInstanceOf(UserBrick other) {
		return (other.getUserScriptDefinitionBrickElements() == getUserScriptDefinitionBrickElements());
	}

	public ArrayList<UserBrickParameter> getUserBrickParameters() {
		return userBrickParameters;
	}

	public void updateUserBrickParameters(ArrayList<UserBrickParameter> userBrickParameterList) {
		ArrayList<UserBrickParameter> newParameters = new ArrayList<UserBrickParameter>();

		for (int elementPosition = 0; elementPosition < getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().size(); elementPosition++) {
			UserBrickParameter parameter = new UserBrickParameter();
			parameter.parameterIndex = elementPosition;
			if (getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(elementPosition).isVariable) {
				if (userBrickParameterList == null) {
					parameter.setFormulaWithBrickField(BrickField.USER_BRICK, new Formula(0));
				} else {
					parameter.setFormulaWithBrickField(BrickField.USER_BRICK, userBrickParameterList.get(elementPosition).getFormulaWithBrickField(BrickField.USER_BRICK));
				}
				parameter.variableName = getUserScriptDefinitionBrickElements().getUserScriptDefinitionBrickElementList().get(elementPosition).name;
			}
			newParameters.add(parameter);
		}

		if (userBrickParameters != null) {
			copyFormulasMatchingNames(userBrickParameters, newParameters);
		}

		userBrickParameters = newParameters;
		lastDataVersion = getUserScriptDefinitionBrickElements().getVersion();
	}

	public List<Formula> getFormulas() {
		List<Formula> formulaList = new LinkedList<Formula>();
		for (UserBrickParameter parameter : userBrickParameters) {
			if (parameter.getFormulaWithBrickField(BrickField.USER_BRICK) != null && parameter.variableName != null) {
				formulaList.add(parameter.getFormulaWithBrickField(BrickField.USER_BRICK));
			}
		}
		return formulaList;
	}

	public void copyFormulasMatchingNames(ArrayList<UserBrickParameter> from, ArrayList<UserBrickParameter> to) {
		for (UserBrickParameter fromElement : from) {
			UserScriptDefinitionBrickElements elements = getUserScriptDefinitionBrickElements();
			if (fromElement.parameterIndex < elements.getUserScriptDefinitionBrickElementList().size()) {
				UserScriptDefinitionBrickElement fromData = elements.getUserScriptDefinitionBrickElementList().get(fromElement.parameterIndex);
				if (fromData.isVariable) {
					for (UserBrickParameter toElement : to) {
						if (toElement.parameterIndex < elements.getUserScriptDefinitionBrickElementList().size()) {
							UserScriptDefinitionBrickElement toData = elements.getUserScriptDefinitionBrickElementList().get(toElement.parameterIndex);
							if (fromData.name.equals(toData.name)) {
								toElement.setFormulaWithBrickField(BrickField.USER_BRICK, fromElement.getFormulaWithBrickField(BrickField.USER_BRICK).clone());
								toElement.variableName = toData.name;
							}
						}
					}
				}
			}
		}
	}

	public void appendBrickToScript(Brick brick) {
		definitionBrick.appendBrickToScript(brick);
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {

		ArrayList<SequenceAction> returnActionList = new ArrayList<SequenceAction>();

		SequenceAction userSequence = ExtendedActions.sequence();
		definitionBrick.getScriptSafe().run(sprite, userSequence);
		returnActionList.add(userSequence);
		sequence.addAction(ExtendedActions.userBrick(userSequence));
		ProjectManager.getInstance().setCurrentUserBrick(this);

		return returnActionList;
	}

	public UserScriptDefinitionBrick getDefinitionBrick() {
		return definitionBrick;
	}

	public void setDefinitionBrick(UserScriptDefinitionBrick definitionBrick) {
		this.definitionBrick = definitionBrick;
	}

	public UserScriptDefinitionBrickElements getUserScriptDefinitionBrickElements() {
		return definitionBrick.getUserScriptDefinitionBrickElements();
	}

	public void setUserScriptDefinitionBrickElements(UserScriptDefinitionBrickElements elements) {
		definitionBrick.setUserScriptDefinitionBrickElements(elements);
	}

	public int getUserBrickId() {
		return userBrickId;
	}

	public void setUserBrickId(int userBrickId) {
		this.userBrickId = userBrickId;
	}

	public ArrayList<Pair<Integer, Integer>> getUserBrickPositionToParameter() {
		return userBrickPositionToParameter;
	}

	public void setUserBrickPositionToParameter(Pair<Integer, Integer> userBrickPositionToParameterPair, int index) {
		userBrickPositionToParameter.set(index, userBrickPositionToParameterPair);
	}

	public void addUserBrickPositionToParameter(Pair<Integer, Integer> userBrickPositionToParameterPair) {
		userBrickPositionToParameter.add(userBrickPositionToParameterPair);
	}

	public int getUserBrickIndexInScript(Pair<Integer, Integer> userBrickPositionToParameterPair) {
		return userBrickPositionToParameter.indexOf(userBrickPositionToParameterPair);
	}

	public int getLastDataVersion() {
		return lastDataVersion;
	}
}