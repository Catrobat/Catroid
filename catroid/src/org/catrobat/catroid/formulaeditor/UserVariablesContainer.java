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
package org.catrobat.catroid.formulaeditor;

import android.annotation.SuppressLint;
import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElements;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UserVariablesContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int INVALID_ID = -1;

	@XStreamAlias("programVariableList")
	private List<UserVariable> projectVariables;
	@XStreamAlias("objectVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables;

	@XStreamAlias("userBrickVariableList")
	@SuppressLint("UseSparseArrays")
	private Map<Integer, List<UserVariable>> userBrickVariables = new HashMap<Integer, List<UserVariable>>();

	public UserVariablesContainer() {
		projectVariables = new ArrayList<UserVariable>();
		spriteVariables = new HashMap<Sprite, List<UserVariable>>();
	}

	public UserVariableAdapter createUserVariableAdapter(Context context, int userBrickId, Sprite sprite, boolean inUserBrick)
	{
		List<UserVariable> userBrickVariables;
		if (userBrickId == INVALID_ID || !inUserBrick) {
			userBrickVariables = new LinkedList<UserVariable>();
		} else {
			userBrickVariables = getOrCreateVariableListForUserBrick(userBrickId);
		}
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(sprite);
		return new UserVariableAdapter(context, userBrickVariables, spriteVariables, projectVariables);
	}

	public UserVariable getUserVariable(String userVariableName, Sprite sprite) {
		UserVariable userVariable;
		userVariable = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (userVariable == null) {
			userVariable = findUserVariable(userVariableName, projectVariables);
		}
		if (userVariable == null && ProjectManager.getInstance().getCurrentUserBrick() != null) {
			int id = ProjectManager.getInstance().getCurrentUserBrick().getUserBrickId();
			userVariable = findUserVariable(userVariableName, getOrCreateVariableListForUserBrick(id));
		}
		return userVariable;
	}

	public List<UserVariable> getProjectVariables() {

		return projectVariables;
	}

	public UserVariable addUserBrickUserVariableToUserBrick(int userBrickId, String userVariableName, Object userVariableValue) {
		List<UserVariable> varList = getOrCreateVariableListForUserBrick(userBrickId);
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addSpriteUserVariable(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserVariableToSprite(currentSprite, userVariableName);
	}

	public UserVariable addSpriteUserVariableToSprite(Sprite sprite, String userVariableName) {
		List<UserVariable> varList = getOrCreateVariableListForSprite(sprite);
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addProjectUserVariable(String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		projectVariables.add(userVariableToAdd);
		return userVariableToAdd;
	}

	/**
	 * This function deletes the user variable with userVariableName in the current context.
	 *
	 * The current context consists of all global variables, the sprite variables for the current sprite,
	 * and the user brick variables for the current user brick.
	 */
	public void deleteUserVariableByName(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserBrick currentUserBrick = ProjectManager.getInstance().getCurrentUserBrick();
		int userBrickId = INVALID_ID;
		if (currentUserBrick != null) {
			userBrickId = currentUserBrick.getDefinitionBrick().getUserBrickId();
		}
		List<UserVariable> context = getUserVariableContext(userVariableName, userBrickId, currentSprite);
		if (context != null) {
			UserVariable variableToDelete = findUserVariable(userVariableName, context);
			if (variableToDelete != null) {
				context.remove(variableToDelete);
				if (currentUserBrick != null) {
					UserScriptDefinitionBrickElements currentElements = currentUserBrick.getUserScriptDefinitionBrickElements();
					for (int id = 0; id < currentElements.getUserScriptDefinitionBrickElementList().size(); id++) {
						if (currentElements.getUserScriptDefinitionBrickElementList().get(id).name.equals(userVariableName) && currentElements.getUserScriptDefinitionBrickElementList().get(id).isVariable) {
							int alpha = currentUserBrick.getAlphaValue();
//							Context context
							currentUserBrick.getDefinitionBrick().removeVariablesInFormulas(currentElements.getUserScriptDefinitionBrickElementList().get(id).name, currentUserBrick.getDefinitionBrick().getViewWithAlpha(alpha).getContext());
							currentElements.getUserScriptDefinitionBrickElementList().remove(id);
							currentElements.incrementVersion();
						}
					}
				}
			}
		}
	}

	public void deleteUserVariableFromUserBrick(int userBrickId, String userVariableName) {
		List<UserVariable> context = userBrickVariables.get(userBrickId);
		UserVariable variableToDelete = findUserVariable(userVariableName, context);
		if (variableToDelete != null) {
			context.remove(variableToDelete);
		}
	}

	public List<UserVariable> getOrCreateVariableListForUserBrick(int userBrickId) {
		List<UserVariable> variables = userBrickVariables.get(userBrickId);

		if (variables == null) {
			variables = new ArrayList<UserVariable>();
			userBrickVariables.put(userBrickId, variables);
		}

		return variables;
	}

	public void cleanVariableListForUserBrick(int userBrickId) {
		List<UserVariable> variables = userBrickVariables.get(userBrickId);
		if (variables != null) {
			variables.clear();
		}
		userBrickVariables.remove(userBrickId);
	}

	public List<UserVariable> getOrCreateVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);

		if (variables == null) {
			variables = new ArrayList<UserVariable>();
			spriteVariables.put(sprite, variables);
		}
		return variables;
	}

	public void cleanVariableListForSprite(Sprite sprite) {
		List<UserVariable> variables = spriteVariables.get(sprite);
		if (variables != null) {
			variables.clear();
		}
		spriteVariables.remove(sprite);
	}

	public UserVariable getUserVariable(String name, int userBrickId, Sprite currentSprite) {
		List<UserVariable> contextList = getUserVariableContext(name, userBrickId, currentSprite);
		return findUserVariable(name, contextList);
	}

	/**
	 * This function finds the user variable with userVariableName in the current context.
	 *
	 * The current context consists of all global variables, the sprite variables for the current sprite,
	 * and the user brick variables for the current user brick.
	 */
	public List<UserVariable> getUserVariableContext(String name, int userBrickId, Sprite currentSprite) {

		UserVariable variableToReturn;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		variableToReturn = findUserVariable(name, spriteVariables);
		if (variableToReturn != null) {
			return spriteVariables;
		}

		if (userBrickId != INVALID_ID) {
			List<UserVariable> userBrickVariables = getOrCreateVariableListForUserBrick(userBrickId);
			variableToReturn = findUserVariable(name, userBrickVariables);
			if (variableToReturn != null) {
				return userBrickVariables;
			}
		}

		variableToReturn = findUserVariable(name, projectVariables);
		if (variableToReturn != null) {
			return projectVariables;
		}
		return null;
	}

	public UserVariable findUserVariable(String name, List<UserVariable> variables) {
		if (variables == null) {
			return null;
		}
		for (UserVariable variable : variables) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	public void resetAllUserVariables() {

		resetUserVariables(projectVariables);

		Iterator<Sprite> spriteIterator = spriteVariables.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			resetUserVariables(spriteVariables.get(currentSprite));
		}
		for (int key : userBrickVariables.keySet()) {
			resetUserVariables(userBrickVariables.get(key));
		}
	}

	private void resetUserVariables(List<UserVariable> userVariableList) {
		for (UserVariable userVariable : userVariableList) {
			userVariable.setValue(0.0);
		}
	}
}
