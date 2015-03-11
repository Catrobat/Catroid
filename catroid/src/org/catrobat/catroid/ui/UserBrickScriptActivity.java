/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.ui;

import android.os.Bundle;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

public class UserBrickScriptActivity extends ScriptActivity {

	private static UserBrick cachedUserBrick;
	private UserBrick userBrick;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setUserBrickIfJustCreated();
	}

	@Override
	public void onResume() {
		super.onResume();
		setUserBrickIfJustCreated();
	}

	public void setUserBrickIfJustCreated() {
		if (UserBrickScriptActivity.cachedUserBrick != null) {
			userBrick = UserBrickScriptActivity.cachedUserBrick;
			UserBrickScriptActivity.cachedUserBrick = null;
		}
		if (userBrick != null) {
			ProjectManager.getInstance().setCurrentUserBrick(userBrick);
		}
	}

	public static void setUserBrick(Brick userBrick) {
		cachedUserBrick = (UserBrick) userBrick;
	}

	@Override
	public void setupBrickAdapter(BrickAdapter adapter) {
		adapter.setUserBrick(userBrick);
		adapter.updateProjectBrickList();
	}
}
