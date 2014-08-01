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

import android.content.Context;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.UserVariableAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UserVariablesContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	@XStreamAlias("programVariableList")
	private List<UserVariable> projectVariables;
	@XStreamAlias("spriteVariableList")
	private Map<Sprite, List<UserVariable>> spriteVariables;

	public UserVariablesContainer() {
		projectVariables = new ArrayList<UserVariable>();
		spriteVariables = new HashMap<Sprite, List<UserVariable>>();
	}

	public UserVariableAdapter createUserVariableAdapter(Context context, Sprite sprite) {
		return new UserVariableAdapter(context, getOrCreateVariableListForSprite(sprite), projectVariables);
	}

	public UserVariable getUserVariable(String userVariableName, Sprite sprite) {
		UserVariable var;
		var = findUserVariable(userVariableName, getOrCreateVariableListForSprite(sprite));
		if (var == null) {
			var = findUserVariable(userVariableName, projectVariables);
		}
		return var;
	}

	public UserVariable addSpriteUserVariable(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		return addSpriteUserVariableToSprite(currentSprite, userVariableName);
	}

	public UserVariable addSpriteUserVariableToSprite(Sprite sprite, String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		List<UserVariable> varList = getOrCreateVariableListForSprite(sprite);
		varList.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public UserVariable addProjectUserVariable(String userVariableName) {
		UserVariable userVariableToAdd = new UserVariable(userVariableName);
		projectVariables.add(userVariableToAdd);
		return userVariableToAdd;
	}

	public void deleteUserVariableByName(String userVariableName) {
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
		UserVariable variableToDelete;
		List<UserVariable> spriteVariables = getOrCreateVariableListForSprite(currentSprite);
		variableToDelete = findUserVariable(userVariableName, spriteVariables);
		if (variableToDelete != null) {
			spriteVariables.remove(variableToDelete);
		}

		variableToDelete = findUserVariable(userVariableName, projectVariables);
		if (variableToDelete != null) {
			projectVariables.remove(variableToDelete);
		}
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
		List<UserVariable> vars = spriteVariables.get(sprite);
		if (vars != null) {
			vars.clear();
		}
		spriteVariables.remove(sprite);
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

	public void resetAllUserVariables() {

		resetUserVariables(projectVariables);

		Iterator<Sprite> spriteIterator = spriteVariables.keySet().iterator();
		while (spriteIterator.hasNext()) {
			Sprite currentSprite = spriteIterator.next();
			resetUserVariables(spriteVariables.get(currentSprite));
		}
	}

	private void resetUserVariables(List<UserVariable> userVariableList) {
		for (UserVariable userVariable : userVariableList) {
			userVariable.setValue(0.0);
		}
	}
}