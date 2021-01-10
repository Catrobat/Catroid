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

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.utils.AddUserListDialog;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class UserListBrick extends FormulaBrick implements BrickSpinner.OnItemSelectedListener<UserList> {

	protected UserList userList;

	private transient BrickSpinner<UserList> spinner;

	public UserList getUserList() {
		return userList;
	}

	public void setUserList(UserList userList) {
		this.userList = userList;
	}

	@Override
	public Brick clone() throws CloneNotSupportedException {
		UserListBrick clone = (UserListBrick) super.clone();
		clone.spinner = null;
		return clone;
	}

	@IdRes
	protected abstract int getSpinnerId();

	@Override
	public View getView(Context context) {
		super.getView(context);

		Sprite sprite = ProjectManager.getInstance().getCurrentSprite();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(sprite.getUserLists());
		items.addAll(ProjectManager.getInstance().getCurrentProject().getUserLists());

		spinner = new BrickSpinner<>(getSpinnerId(), view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(userList);

		return view;
	}

	@Override
	public void onNewOptionSelected(Integer spinnerId) {
		final AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		final List<UserList> projectUserList =
				ProjectManager.getInstance().getCurrentProject().getUserLists();
		final List<UserList> spriteUserList =
				ProjectManager.getInstance().getCurrentSprite().getUserLists();

		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);

		AddUserListDialog userListDialog = new AddUserListDialog(builder, spinner);
		userListDialog.show(activity.getString(R.string.data_label), activity.getString(R.string.ok), new AddUserListDialog.Callback() {
			@Override
			public void onPositiveButton(DialogInterface dialog, String textInput) {
				UserList userList = new UserList(textInput);

				userListDialog.addUserList(dialog, userList, projectUserList, spriteUserList);
				spinner.add(userList);
				spinner.setSelection(userList);

				ScriptFragment parentFragment = (ScriptFragment) activity
						.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
				if (parentFragment != null) {
					parentFragment.notifyDataSetChanged();
				}
			}

			@Override
			public void onNegativeButton() {
				spinner.setSelection(userList);
			}
		});
	}

	@Override
	public void onEditOptionSelected(Integer spinnerId) {
	}

	@Override
	public void onStringOptionSelected(Integer spinnerId, String string) {
	}

	@Override
	public void onItemSelected(Integer spinnerId, @Nullable UserList item) {
		userList = item;
	}
}
