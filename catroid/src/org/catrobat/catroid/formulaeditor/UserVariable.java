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

public class UserVariable implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Double value;
	private UserVariableScope scope;

	public UserVariable(String name, Double value, UserVariableScope scope) {
		this.name = name;
		this.value = value;
		this.scope = scope;
	}

	public String getXMLDecodedName() {
		return name; //TODO decode Name
	}

	public void setNameAndEncode(String name) {
		this.name = name; //TODO encode Name
	}

	public static UserVariable getUserVariable(String userVariableName, List<UserVariable> userVariables,
			String spriteName) {
		for (UserVariable userVariable : userVariables) {
			if (userVariable.name.equals(userVariableName) && userVariable.scope.checkScope(spriteName)) {
				return userVariable;
			}
		}
		return null;
	}

	public static List<UserVariable> getUserVariables(List<UserVariable> userVariables, String scopeName) {
		List<UserVariable> returnList = new LinkedList<UserVariable>();
		for (UserVariable userVariable : userVariables) {
			if (userVariable.scope.checkScope(scopeName)) {
				returnList.add(userVariable);
			}
		}
		return returnList;
	}

	public static void deleteUserVariableByName(String userVariableName, List<UserVariable> userVariables,
			String spriteName) {
		UserVariable userVariableToDelete = null;
		boolean deleteItem = false;
		for (UserVariable userVariable : userVariables) {
			if (userVariable.name.equals(userVariableName) && userVariable.scope.checkScope(spriteName)) {
				userVariableToDelete = userVariable;
				deleteItem = true;
				break;
			}
		}
		if (deleteItem) {
			userVariables.remove(userVariableToDelete);
		}

	}

	@Override
	public String toString() {
		return name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
