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
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerWithNewOption;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.recyclerview.dialog.NewListDialogFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class UserListBrick extends FormulaBrick implements NewListDialogFragment.NewListInterface,
		SpinnerWithNewOption.SpinnerSelectionListener<UserList> {

	protected UserList userList;

	private transient SpinnerWithNewOption<UserList> spinner;

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	@Override
	public BrickBaseType clone() throws CloneNotSupportedException {
		UserListBrick clone = (UserListBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	protected abstract int getSpinnerId();

	private List<UserList> getUserLists() {
		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();

		List<UserList> lists = new ArrayList<>();
		lists.addAll(dataContainer.getSpriteUserLists(sprite));
		lists.addAll(dataContainer.getProjectUserLists());
		return lists;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		spinner = new SpinnerWithNewOption<>(getSpinnerId(), view, getUserLists(), this);
		spinner.setSelection(userList);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		super.getPrototypeView(context);
		return getView(context);
	}

	@Override
	public boolean onNewOptionClicked() {
		new NewListDialogFragment(this) {

			@Override
			public void onCancel(DialogInterface dialog) {
				super.onCancel(dialog);
				spinner.setSelection(userList);
			}
		}.show(((Activity) view.getContext()).getFragmentManager(), NewListDialogFragment.TAG);
		return false;
	}

	@Override
	public void onNewList(UserList item) {
		userList = item;
		spinner.add(item);
		//TODO: This should work some other way: i.e. it should not rely on the Brick being able to access its adapter.
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemSelected(UserList item) {
		userList = item;
	}
}
