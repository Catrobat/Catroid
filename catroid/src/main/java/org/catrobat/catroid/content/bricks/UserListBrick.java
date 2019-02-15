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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.content.bricks.brickspinner.NewOption;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.UiUtils;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.NewItemTextWatcher;

import java.util.ArrayList;
import java.util.List;

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
	public BrickBaseType clone() throws CloneNotSupportedException {
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
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();

		List<Nameable> items = new ArrayList<>();
		items.add(new NewOption(context.getString(R.string.new_option)));
		items.addAll(dataContainer.getSpriteUserLists(sprite));
		items.addAll(dataContainer.getProjectUserLists());

		spinner = new BrickSpinner<>(getSpinnerId(), view, items);
		spinner.setOnItemSelectedListener(this);
		spinner.setSelection(userList);

		return view;
	}

	@Override
	public void onNewOptionSelected() {
		final AppCompatActivity activity = UiUtils.getActivityFromView(view);
		if (activity == null) {
			return;
		}

		final Scene currentScene = ProjectManager.getInstance().getCurrentlyEditedScene();
		final Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		TextInputDialog.Builder builder = new TextInputDialog.Builder(activity);

		builder.setHint(activity.getString(R.string.data_label))
				.setTextWatcher(new NewItemTextWatcher<>(spinner.getItems()))
				.setPositiveButton(activity.getString(R.string.ok), new TextInputDialog.OnClickListener() {
					@Override
					public void onPositiveButtonClick(DialogInterface dialog, String textInput) {
						UserList userList = new UserList(textInput);

						DataContainer dataContainer = currentScene.getDataContainer();

						RadioButton addToProjectListsRadioButton = ((Dialog) dialog).findViewById(R.id.global);
						boolean addToProjectLists = addToProjectListsRadioButton.isChecked();

						if (addToProjectLists) {
							dataContainer.addUserList(userList);
						} else {
							dataContainer.addUserList(currentSprite, userList);
						}
						spinner.add(userList);
						spinner.setSelection(userList);

						ScriptFragment parentFragment = (ScriptFragment) activity
								.getSupportFragmentManager().findFragmentByTag(ScriptFragment.TAG);
						if (parentFragment != null) {
							parentFragment.notifyDataSetChanged();
						}
					}
				});

		builder.setTitle(R.string.formula_editor_list_dialog_title)
				.setView(R.layout.dialog_new_user_data)
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						spinner.setSelection(userList);
					}
				})
				.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						spinner.setSelection(userList);
					}
				})
				.show();
	}

	@Override
	public void onStringOptionSelected(String string) {
	}

	@Override
	public void onItemSelected(@Nullable UserList item) {
		userList = item;
	}
}
