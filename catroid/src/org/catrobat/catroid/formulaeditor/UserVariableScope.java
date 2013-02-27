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

import org.catrobat.catroid.ProjectManager;

public class UserVariableScope implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private ScopeType scopeType;

	public static enum ScopeType {
		SPRITE, PROJECT
	}

	public UserVariableScope(String name, ScopeType scopeType) {
		this.scopeType = scopeType;
		this.name = name;
	}

	public boolean checkScope(String spriteName) {

		switch (scopeType) {
			case SPRITE:
				if (spriteName.equals(name)) {
					return true;
				}
				break;
			case PROJECT:
				if (ProjectManager.getInstance().getCurrentProject().getName().equals(name)) {
					return true;
				}
				break;
		}

		return false;

	}

}
