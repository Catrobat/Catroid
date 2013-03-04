/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;

import android.content.Context;

public class UserVariablesContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<UserVariable> projectVariables;
	private Map<String, List<UserVariable>> spriteVariables;

	public UserVariablesContainer() {
		projectVariables = new ArrayList<UserVariable>();
		spriteVariables = new HashMap<String, List<UserVariable>>();
	}

	public UserVariableAdapter createUserVariableAdapter(Context context, String spriteName) {
		return new UserVariableAdapter(context, getOrCreateVariableListForSprite(spriteName), projectVariables);
	}

	public UserVariable getUserVariable(String userVariableName, String spriteName) {
		UserVariable var;
		var = findUserVariable(userVariableName, getOrCreateVariableListForSprite(spriteName));
		if (var == null) {
			var = findUserVariable(userVariableName, projectVariables);
		}
		return var;
	}

	public void addSpriteUserVariable(String userVariableName, Double userVariableValue) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue,
				UserVariable.ScopeType.SPRITE);
		List<UserVariable> varList = getOrCreateVariableListForSprite(spriteName);
		varList.add(userVariableToAdd);
	}

	public void addProjectUserVariable(String userVariableName, Double userVariableValue) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue,
				UserVariable.ScopeType.PROJECT);
		projectVariables.add(userVariableToAdd);
	}

	public void deleteUserVariableByName(String userVariableName) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		UserVariable variableToDelete;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(spriteName);
		variableToDelete = findUserVariable(userVariableName, spriteVariables);
		if (variableToDelete != null) {
			spriteVariables.remove(variableToDelete);
		}

		variableToDelete = findUserVariable(userVariableName, projectVariables);
		if (variableToDelete != null) {
			projectVariables.remove(variableToDelete);
		}
	}

	private List<UserVariable> getOrCreateVariableListForSprite(String sprite) {
		List<UserVariable> vars = spriteVariables.get(sprite);
		if (vars == null) {
			vars = new ArrayList<UserVariable>();
			spriteVariables.put(sprite, vars);
		}
		return vars;
	}

	private UserVariable findUserVariable(String name, List<UserVariable> variables) {
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

}
