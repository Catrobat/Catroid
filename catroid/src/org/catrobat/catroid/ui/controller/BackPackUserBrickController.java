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
package org.catrobat.catroid.ui.controller;

import android.app.Activity;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.BackPackActivity;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class BackPackUserBrickController {

	public static final String TAG = BackPackUserBrickController.class.getSimpleName();

	private static final BackPackUserBrickController INSTANCE = new BackPackUserBrickController();

	private BackPackUserBrickController() {
	}

	public static BackPackUserBrickController getInstance() {
		return INSTANCE;
	}

	public List<UserBrick> backpack(String groupName, List<Brick> checkedBricks) {
		Iterator<Brick> iterator = checkedBricks.iterator();
		List<UserBrick> userBricksToAdd = new ArrayList<>();
		while (iterator.hasNext()) {
			UserBrick currentBrick = (UserBrick) iterator.next();

			UserBrick userBrickToAdd = (UserBrick) currentBrick.clone();
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			userBrickToAdd.storeDataForBackPack(currentSprite);

			userBricksToAdd.add(userBrickToAdd);
		}
		if (!userBricksToAdd.isEmpty()) {
			BackPackListManager.getInstance().addUserBrickToBackPack(groupName, userBricksToAdd);
		}
		return userBricksToAdd;
	}

	public void unpack(String selectedUserBrickGroup, boolean deleteUnpackedItems, Activity activity) {
		List<UserBrick> userBricksInGroup = BackPackListManager.getInstance().getBackPackedUserBricks().get(selectedUserBrickGroup);

		if (userBricksInGroup == null) {
			return;
		}

		for (UserBrick backPackedUserBrick : userBricksInGroup) {

			UserBrick newUserBrick = backPackedUserBrick.copyBrickForSprite(ProjectManager.getInstance().getCurrentSprite());
			BackPackScriptController.getInstance().handleUserBrickUnpacking(newUserBrick, deleteUnpackedItems);

			if (deleteUnpackedItems) {
				BackPackListManager.getInstance().removeItemFromUserBrickBackPack(selectedUserBrickGroup);
			}

			String textForUnPacking = activity.getResources().getQuantityString(R.plurals.unpacking_items_plural, 1);
			ToastUtil.showSuccess(activity, selectedUserBrickGroup + " " + textForUnPacking);

			((BackPackActivity) activity).returnToScriptActivity(ScriptActivity.USERBRICKS_PROTOTYPE_VIEW);
		}
	}
}
