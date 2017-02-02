/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserObject;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.ArrayList;
import java.util.List;

public final class ConflictHelper {

	private ConflictHelper() {
	}

	public static void renameConflicts(Scene scene, String conflictEnding) {
		renameVariablesWithConflict(scene, conflictEnding);
		renameListsWithConflict(scene, conflictEnding);
	}

	public static void renameVariablesWithConflict(Scene scene, String conflictEnding) {
		List<UserVariable> globalValues = scene.getDataContainer().getProjectVariables();

		for (Sprite sprite : scene.getSpriteList()) {
			List<UserVariable> localValues = scene.getDataContainer().getVariableListForSprite(sprite);

			List<String> conflicts = checkObjectNames(globalValues, localValues);
			if (conflicts.size() > 0) {
				for (String name : conflicts) {
					String newName = getUniqueGlobalObjectName(name.concat("_").concat(conflictEnding), globalValues);
					scene.getDataContainer().renameProjectUserVariable(newName, name);
				}
			}
		}
	}

	private static void renameListsWithConflict(Scene scene, String conflictEnding) {
		List<UserList> globalLists = scene.getDataContainer().getProjectLists();

		for (Sprite sprite : scene.getSpriteList()) {
			List<UserList> localLists = scene.getDataContainer().getUserListListForSprite(sprite);

			List<String> conflicts = checkObjectNames(globalLists, localLists);
			if (conflicts.size() > 0) {
				for (String name : conflicts) {
					String newName = getUniqueGlobalObjectName(name.concat("_").concat(conflictEnding), globalLists);
					scene.getDataContainer().renameProjectUserList(newName, name);
				}
			}
		}
	}

	private static List<String> checkObjectNames(List<? extends UserObject> globalObjects, List<? extends UserObject> localObjects) {
		List conflicts = new ArrayList();
		for (UserObject global : globalObjects) {
			for (UserObject local : localObjects) {
				if (global.getName().equals(local.getName())) {
					conflicts.add(global.getName());
				}
			}
		}
		return conflicts;
	}

	private static String getUniqueGlobalObjectName(String name, List<? extends UserObject> globalObjects) {
		return getUniqueGlobalObjectName(name, globalObjects, 0);
	}

	private static String getUniqueGlobalObjectName(String name, List<? extends UserObject> globalObjects, int number) {
		String newName = name;
		if (number != 0) {
			newName = name.concat(String.valueOf(number));
		}

		for (UserObject object : globalObjects) {
			if (object.getName().equals(newName)) {
				return getUniqueGlobalObjectName(name, globalObjects, number++);
			}
		}
		return newName;
	}
}
