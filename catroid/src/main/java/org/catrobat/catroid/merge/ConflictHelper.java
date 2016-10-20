/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import android.app.Activity;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public final class ConflictHelper {

	private ConflictHelper() {
	}

	public static boolean checkMergeConflict(Activity activity, Scene mergeResult) {
		return checkVariableMergeConflict(activity, mergeResult)
				&& checkListMergeConflict(activity, mergeResult);
	}

	private static boolean checkVariableMergeConflict(Activity activity, Scene mergeResult) {
		List<UserVariable> globalValues = mergeResult.getDataContainer().getProjectVariables();

		for (Sprite sprite : mergeResult.getSpriteList()) {
			List<UserVariable> localValues = mergeResult.getDataContainer().getVariableListForSprite(sprite);

			if (localValues.size() == 0) {
				continue;
			}

			String name = checkVariableNames(globalValues, localValues);
			if (name != null) {
				if (activity != null) {
					String msg = String.format(activity.getString(R.string.merge_conflict_variable), name);
					Utils.showErrorDialog(activity, msg, R.string.merge_conflict);
				}
				return false;
			}
		}
		return true;
	}

	private static boolean checkListMergeConflict(Activity activity, Scene mergeResult) {
		List<UserList> globalLists = mergeResult.getDataContainer().getProjectLists();

		for (Sprite sprite : mergeResult.getSpriteList()) {
			List<UserList> localLists = mergeResult.getDataContainer().getUserListListForSprite(sprite);
			if (localLists.size() == 0) {
				continue;
			}
			String name = checkListNames(globalLists, localLists);
			if (name != null) {
				if (activity != null) {
					String msg = String.format(activity.getString(R.string.merge_conflict_list), name);
					Utils.showErrorDialog(activity, msg, R.string.merge_conflict);
				}
				return false;
			}
		}
		return true;
	}

	private static String checkVariableNames(List<UserVariable> globalValues, List<UserVariable> localValues) {
		for (UserVariable global : globalValues) {
			for (UserVariable local : localValues) {
				if (global.getName().equals(local.getName())) {
					return global.getName();
				}
			}
		}
		return null;
	}

	private static String checkListNames(List<UserList> globalLists, List<UserList> localLists) {
		for (UserList global : globalLists) {
			for (UserList local : localLists) {
				if (global.getName().equals(local.getName())) {
					return global.getName();
				}
			}
		}
		return null;
	}
}
