/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

import java.util.List;

public class ForItemInUserListAction extends com.badlogic.gdx.scenes.scene2d.actions.RepeatAction {

	private UserList userList;
	private UserVariable currentItemVariable;
	private boolean isCurrentLoopInitialized = false;
	private static final float LOOP_DELAY = 0.02f;
	private float currentTime = 0f;
	private int index = 0;

	@Override
	public boolean delegate(float delta) {
		if (!isCurrentLoopInitialized) {
			currentTime = 0f;
			isCurrentLoopInitialized = true;
		}

		List<Object> list = userList.getValue();
		if (index >= list.size()) {
			return true;
		}

		setCurrentItemVariable(list.get(index));
		currentTime += delta;

		if (action != null && action.act(delta) && currentTime >= LOOP_DELAY) {
			index++;

			isCurrentLoopInitialized = false;
			action.restart();
		}
		return false;
	}

	@Override
	public void restart() {
		isCurrentLoopInitialized = false;
		index = 0;
		super.restart();
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	public void setCurrentItemVariable(UserVariable variable) {
		this.currentItemVariable = variable;
	}

	private void setCurrentItemVariable(Object listItem) {
		this.currentItemVariable.setValue(listItem);
	}
}
