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

package org.catrobat.catroid.content.bricks;

import android.widget.Spinner;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.adapter.UserListAdapterWrapper;
import org.catrobat.catroid.ui.dialogs.NewDataDialog;

import java.io.Serializable;

public abstract class UserListBrick extends FormulaBrick implements NewDataDialog.NewUserListDialogListener {

	protected UserList userList;

	@XStreamOmitField
	protected BackPackedData backPackedData;

	private void updateUserListIfDeleted(UserListAdapterWrapper userListAdapterWrapper) {
		if (userList != null && (userListAdapterWrapper.getPositionOfItem(userList) == 0)) {
			userList = null;
		}
	}

	protected void setSpinnerSelection(Spinner userListSpinner, UserList newUserList) {
		UserListAdapterWrapper userListAdapterWrapper = (UserListAdapterWrapper) userListSpinner.getAdapter();

		updateUserListIfDeleted(userListAdapterWrapper);

		if (userList != null) {
			userListSpinner.setSelection(userListAdapterWrapper.getPositionOfItem(userList), true);
		} else if (newUserList != null) {
			userListSpinner.setSelection(userListAdapterWrapper.getPositionOfItem(newUserList), true);
			userList = newUserList;
		} else {
			userListSpinner.setSelection(userListAdapterWrapper.getCount() - 1, true);
			userList = userListAdapterWrapper.getItem(userListAdapterWrapper.getCount() - 1);
		}
	}

	@Override
	public void onFinishNewUserListDialog(Spinner spinnerToUpdate, UserList newUserList) {
		UserListAdapterWrapper userListAdapterWrapper = ((UserListAdapterWrapper) spinnerToUpdate.getAdapter());
		userListAdapterWrapper.notifyDataSetChanged();
		setSpinnerSelection(spinnerToUpdate, newUserList);

		for (Brick brick : ProjectManager.getInstance().getCurrentSprite().getAllBricks()) {
			if (brick instanceof UserListBrick && ((UserListBrick) brick).getUserList() == null) {
				if (brick instanceof AddItemToUserListBrick) {
					Spinner spinner = (Spinner) ((AddItemToUserListBrick) brick).view.findViewById(R.id
							.add_item_to_userlist_spinner);
					setSpinnerSelection(spinner, newUserList);
				} else if (brick instanceof ReplaceItemInUserListBrick) {
					Spinner spinner = (Spinner) ((ReplaceItemInUserListBrick) brick).view.findViewById(R.id
							.replace_item_in_userlist_spinner);
					setSpinnerSelection(spinner, newUserList);
				} else if (brick instanceof InsertItemIntoUserListBrick) {
					Spinner spinner = (Spinner) ((InsertItemIntoUserListBrick) brick).view.findViewById(R.id
							.insert_item_into_userlist_spinner);
					setSpinnerSelection(spinner, newUserList);
				} else if (brick instanceof DeleteItemOfUserListBrick) {
					Spinner spinner = (Spinner) ((DeleteItemOfUserListBrick) brick).view.findViewById(R.id
							.delete_item_of_userlist_spinner);
					setSpinnerSelection(spinner, newUserList);
				}
			}
		}
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	public BackPackedData getBackPackedData() {
		return backPackedData;
	}

	public void setBackPackedData(BackPackedData backPackedData) {
		this.backPackedData = backPackedData;
	}

	public class BackPackedData implements Serializable {
		public UserList userList;
		public Integer userListType;

		public BackPackedData() {
		}

		public BackPackedData(BackPackedData backPackedData) {
			if (backPackedData != null) {
				this.userList = backPackedData.userList;
				this.userListType = backPackedData.userListType;
			}
		}
	}

	protected void updateUserListReference(Scene into, Scene from) {
		UserList list;

		if (from.existProjectList(userList)) {
			list = into.getProjectListWithName(userList.getName());

			if (list == null) {
				list = into.getDataContainer().addProjectUserList(userList.getName());
			}
		} else {
			Sprite sprite = from.getSpriteByUserList(userList);
			if (sprite == null || !from.existSpriteList(userList, sprite)) {
				return;
			}
			list = into.getDataContainer().addSpriteListIfDoesNotExist(userList.getName(),
					into.getSpriteBySpriteName(sprite.getName()));
		}

		if (list != null) {
			userList = list;
		}
	}

	@Override
	public boolean isEqualBrick(Brick brick, Scene mergeResult, Scene current) {
		if (!super.isEqualBrick(brick, mergeResult, current)) {
			return false;
		}

		UserList first = this.getUserList();
		UserList second = ((UserListBrick) brick).getUserList();
		if (!first.getName().equals(second.getName())) {
			return false;
		}

		boolean firstIsProjectVariable = mergeResult.getDataContainer().existProjectList(first);
		boolean secondIsProjectVariable = current.getDataContainer().existProjectList(second);

		if ((firstIsProjectVariable && secondIsProjectVariable)
				|| (!firstIsProjectVariable && !secondIsProjectVariable)) {
			return true;
		}
		return false;
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		Integer type = DataContainer.USER_DATA_EMPTY;
		if (getUserList() != null) {
			type = currentScene.getDataContainer()
					.getTypeOfUserList(getUserList().getName(), ProjectManager
							.getInstance().getCurrentSprite());
		}

		if (backPackedData == null) {
			backPackedData = new BackPackedData();
		}
		backPackedData.userList = userList;
		backPackedData.userListType = type;
	}
}
