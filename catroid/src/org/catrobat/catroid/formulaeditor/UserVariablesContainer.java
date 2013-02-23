/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.formulaeditor.UserVariableScope.ScopeType;


public class UserVariablesContainer implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<UserVariable> userVariables;

	public UserVariablesContainer() {
		userVariables = new LinkedList<UserVariable>();
	}

	public UserVariable getUserVariable(String userVariableName, String spriteName) {
		return UserVariable.getUserVariable(userVariableName, userVariables, spriteName);
	}

	public List<UserVariable> getUserVariables(String scopeName) {
		return UserVariable.getUserVariables(userVariables, scopeName);
	}

	public void addSpriteUserVariable(String userVariableName, Double userVariableValue) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		UserVariableScope userVariableScope = new UserVariableScope(spriteName, ScopeType.SPRITE);
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue, userVariableScope);
		userVariables.add(userVariableToAdd);
	}

	public void addProjectUserVariable(String userVariableName, Double userVariableValue) {
		String projectName = ProjectManager.getInstance().getCurrentProject().getName();
		UserVariableScope userVariableScope = new UserVariableScope(projectName, ScopeType.PROJECT);
		UserVariable userVariableToAdd = new UserVariable(userVariableName, userVariableValue, userVariableScope);
		userVariables.add(userVariableToAdd);
	}

	public void deleteUserVariableByName(String userVariableName) {
		String spriteName = ProjectManager.getInstance().getCurrentSprite().getName();
		UserVariable.deleteUserVariableByName(userVariableName, userVariables, spriteName);
	}
}
