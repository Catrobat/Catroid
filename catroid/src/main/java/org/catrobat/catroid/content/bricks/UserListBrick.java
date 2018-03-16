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

package org.catrobat.catroid.content.bricks;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.adapter.UserListAdapterWrapper;
import org.catrobat.catroid.ui.recyclerview.dialog.NewListDialogFragment;

import static org.catrobat.catroid.formulaeditor.datacontainer.DataContainer.DataType.USER_DATA_EMPTY;

public abstract class UserListBrick extends FormulaBrick implements NewListDialogFragment.NewListInterface {

	protected UserList userList;

	@XStreamOmitField
	protected BackPackedListData backPackedData;

	private void updateUserListIfDeleted(UserListAdapterWrapper userListAdapterWrapper) {
		if (userList != null && (userListAdapterWrapper.getPositionOfItem(userList) == 0)) {
			userList = null;
		}
	}

	protected void setSpinnerSelection(Spinner userListSpinner, UserList newUserList) {
		UserListAdapterWrapper userListAdapterWrapper = (UserListAdapterWrapper) userListSpinner.getAdapter();

		updateUserListIfDeleted(userListAdapterWrapper);

		if (newUserList != null) {
			userListSpinner.setSelection(userListAdapterWrapper.getPositionOfItem(newUserList), true);
			userList = newUserList;
		} else if (userList != null) {
			userListSpinner.setSelection(userListAdapterWrapper.getPositionOfItem(userList), true);
		} else {
			userListSpinner.setSelection(userListAdapterWrapper.getCount() - 1, true);
			userList = userListAdapterWrapper.getItem(userListAdapterWrapper.getCount() - 1);
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

	public BackPackedListData getBackPackedData() {
		return backPackedData;
	}

	public void setBackPackedData(BackPackedListData backPackedData) {
		this.backPackedData = backPackedData;
	}

	void updateUserListReference(Scene into, Scene from) {
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
			list = into.getDataContainer().addSpriteListIfDoesNotExist(
					into.getSpriteBySpriteName(sprite.getName()), userList.getName());
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

		return (firstIsProjectVariable && secondIsProjectVariable)
				|| (!firstIsProjectVariable && !secondIsProjectVariable);
	}

	@Override
	public void storeDataForBackPack(Sprite sprite) {
		Scene currentScene = ProjectManager.getInstance().getCurrentScene();
		DataContainer.DataType type = USER_DATA_EMPTY;
		if (userList != null) {
			type = currentScene.getDataContainer()
					.getTypeOfUserList(getUserList().getName(), ProjectManager
							.getInstance().getCurrentSprite());
		}

		if (backPackedData == null) {
			backPackedData = new BackPackedListData();
		}
		backPackedData.userList = userList;
		backPackedData.userListType = type;
	}

	protected View.OnTouchListener createSpinnerOnTouchListener() {
		return new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN && ((Spinner) view).getAdapter().getCount() == 1) {
					showNewListDialog();
					return true;
				}
				return false;
			}
		};
	}

	AdapterView.OnItemSelectedListener createListSpinnerItemSelectedListener() {
		return new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					showNewListDialog();
				} else {
					userList = ((UserList) parent.getItemAtPosition(position));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				userList = null;
			}
		};
	}

	private void showNewListDialog() {
		NewListDialogFragment dialog = new NewListDialogFragment(this);
		dialog.show(((Activity) view.getContext()).getFragmentManager(), NewListDialogFragment.TAG);
	}
}
