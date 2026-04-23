/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.RadioButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher;
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider;

import java.util.ArrayList;
import java.util.List;

public class AddUserListDialog {
	private TextInputDialog.Builder builder;
	private BrickSpinner<UserList> spinner;
	private List<UserList> existingUserLists;

	public AddUserListDialog(TextInputDialog.Builder builder, BrickSpinner<UserList> spinner) {
		this.builder = builder;
		this.spinner = spinner;
		this.existingUserLists = spinner.getItems();
	}

	public AddUserListDialog(TextInputDialog.Builder builder) {
		this.builder = builder;
		this.existingUserLists = new ArrayList<>();
	}

	public void show(String hint, String ok, Callback callback) {
		UniqueNameProvider uniqueNameProvider = builder.createUniqueNameProvider(R.string.default_list_name);
		String name = builder.getContext().getString(R.string.default_list_name);
		builder.setHint(hint)
				.setTextWatcher(new DuplicateInputTextWatcher<>(existingUserLists))
				.setText(uniqueNameProvider.getUniqueName(name, null))
				.setPositiveButton(ok,
						callback::onPositiveButton)
				.setTitle(R.string.formula_editor_list_dialog_title)
				.setView(R.layout.dialog_new_user_data)
				.setNegativeButton(R.string.cancel,
						(dialogInterface, index) -> callback.onNegativeButton());
		if (spinner != null) {
			builder.setOnCancelListener(dialog -> callback.onNegativeButton());
		}
		builder.show();
	}

	public void addUserList(DialogInterface dialog, UserList userList,
			List<UserList> projectUserList, List<UserList> spriteUserList) {
		RadioButton addToProjectListsRadioButton = ((Dialog) dialog).findViewById(R.id.global);
		boolean addToProjectLists = addToProjectListsRadioButton.isChecked();
		if (addToProjectLists) {
			projectUserList.add(userList);
		} else {
			spriteUserList.add(userList);
		}
	}

	public interface Callback {
		void onPositiveButton(DialogInterface dialog, String textInput);
		void onNegativeButton();
	}
}
