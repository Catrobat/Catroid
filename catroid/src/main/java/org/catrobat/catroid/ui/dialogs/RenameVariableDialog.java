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

package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.ui.adapter.DataAdapter;
import org.catrobat.catroid.utils.TextSizeUtil;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.List;

public class RenameVariableDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_rename_variable_catroid";
	private UserVariable userVariable;
	private UserList userList;
	private EditText nameEditText;
	private DataAdapter adapter;
	private DialogType type;

	public static enum DialogType {
		USER_LIST, USER_VARIABLE
	}

	public RenameVariableDialog() {
		super();
	}

	public RenameVariableDialog(UserVariable userVariable, DataAdapter adapter, DialogType type) {
		super();
		this.userVariable = userVariable;
		this.adapter = adapter;
		this.type = type;
	}

	public RenameVariableDialog(UserList userList, DataAdapter adapter, DialogType type) {
		super();
		this.userList = userList;
		this.adapter = adapter;
		this.type = type;
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_formula_rename_data_name, null);

		nameEditText = (EditText) dialogView.findViewById(R.id.dialog_formula_rename_variable_name_edit_text);

		switch (type) {
			case USER_LIST:
				nameEditText.setText(userList.getName());
				break;
			case USER_VARIABLE:
				nameEditText.setText(userVariable.getName());
				break;
			default:
				break;
		}

		nameEditText.setSelectAllOnFocus(true);

		final Dialog dialogRenameVariable = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_rename_variable_dialog_title)
				.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton();
					}
				}).create();

		dialogRenameVariable.setCanceledOnTouchOutside(true);
		dialogRenameVariable.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialogRenameVariable.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogRenameVariable);
				TextSizeUtil.enlargeViewGroup((ViewGroup) dialogRenameVariable.getWindow().getDecorView().getRootView());
			}
		});

		return dialogRenameVariable;
	}

	private void handleOkButton() {
		String name = nameEditText.getText().toString();

		switch (type) {
			case USER_LIST:
				renameUserList(name);
				break;
			case USER_VARIABLE:
				renameUserVariable(name);
				break;
			default:
				break;
		}
	}

	private void renameUserList(String newName) {
		if (ProjectManager.getInstance().getCurrentScene().getDataContainer().existProjectList(userList)) {
			if (!isVariableNameValid(newName)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
			} else {
				ProjectManager.getInstance().getCurrentScene().getDataContainer()
						.renameProjectUserList(newName, userList.getName());
			}
		} else if (ProjectManager.getInstance().getCurrentScene().getDataContainer().existSpriteList(
				userList, ProjectManager.getInstance().getCurrentSprite())) {
			ProjectManager.getInstance().getCurrentScene().getDataContainer()
					.renameSpriteUserList(newName, userList.getName());
		}
		updateSpinner();
	}

	private void renameUserVariable(String newName) {
		if (ProjectManager.getInstance().getCurrentScene().getDataContainer().existProjectVariable(userVariable)) {
			if (!isVariableNameValid(newName)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
			} else {
				ProjectManager.getInstance().getCurrentScene().getDataContainer()
						.renameProjectUserVariable(newName, userVariable.getName());
			}
		} else if (ProjectManager.getInstance().getCurrentScene().getDataContainer().existSpriteVariable(
				userVariable, ProjectManager.getInstance().getCurrentSprite())) {
			ProjectManager.getInstance().getCurrentScene().getDataContainer()
					.renameSpriteUserVariable(newName, userVariable.getName());
		}
		updateSpinner();
	}

	private void handleOnShow(final Dialog dialogRenameVariable) {
		final Button positiveButton = ((AlertDialog) dialogRenameVariable).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setEnabled(true);

		nameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String name = editable.toString();
				checkName(name, positiveButton);
			}
		});
	}

	private void checkName(String name, Button positiveButton) {
		if (isVariableNameValid(name)) {
			positiveButton.setEnabled(true);
		} else {
			ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
			positiveButton.setEnabled(false);
		}

		if (name.length() == 0) {
			positiveButton.setEnabled(false);
		}
	}

	private boolean isVariableNameValid(String name) {
		DataContainer currentData = ProjectManager.getInstance().getCurrentScene().getDataContainer();

		if (currentData.existProjectVariable(userVariable)) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			return !currentData.existVariableInAnySprite(name, sprites) && !currentData.existProjectVariableWithName(name);
		} else {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			return !currentData.existProjectVariableWithName(name) && !currentData.existSpriteVariableByName(name, currentSprite);
		}
	}

	private void updateSpinner() {
		adapter.notifyDataSetChanged();
	}
}
