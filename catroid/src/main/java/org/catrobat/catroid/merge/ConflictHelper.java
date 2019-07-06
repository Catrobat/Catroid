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
package org.catrobat.catroid.merge;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;

public class ConflictHelper {
	public ConflictHelper() {
	}

	public boolean getGlobalVariableConflicts(List<UserVariable> project1GlobalVars, List<UserVariable> project2GlobalVars) {
		List<UserVariable> conflicts = new ArrayList<>();
		for (UserVariable project1GlobalVar : project1GlobalVars) {
			for (UserVariable project2GlobalVar : project2GlobalVars) {
				if (project1GlobalVar.getName().equals(project2GlobalVar.getName())) {
					conflicts.add(project1GlobalVar);
				}
			}
		}
		return !conflicts.isEmpty();
	}

	public boolean getGlobalListConflicts(List<UserList> project1GlobalLists, List<UserList> project2GlobalLists) {
		List<UserList> conflicts = new ArrayList<>();
		for (UserList project1GlobalList : project1GlobalLists) {
			for (UserList project2GlobalList : project2GlobalLists) {
				if (project1GlobalList.getName().equals(project2GlobalList.getName())) {
					conflicts.add(project1GlobalList);
				}
			}
		}
		return !conflicts.isEmpty();
	}

	public boolean conflictExist(Project firstProject, Project secondProject) {
		return (getGlobalListConflicts(firstProject.getUserLists(), secondProject.getUserLists())
				|| getGlobalVariableConflicts(firstProject.getUserVariables(), secondProject.getUserVariables()));
	}
}
