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
package org.catrobat.catroid.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class NewDataDialog extends SherlockDialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_data_catroid";
	Spinner spinnerToUpdate;
	DialogType dialogType = DialogType.SHOW_LIST_CHECKBOX;

	public static enum DialogType {
		SHOW_LIST_CHECKBOX, USER_LIST, USER_VARIABLE
	}

	public NewDataDialog(DialogType dialogType) {
		super();
		this.dialogType = dialogType;
	}

	public NewDataDialog(Spinner spinnerToUpdate, DialogType dialogType) {
		super();
		this.spinnerToUpdate = spinnerToUpdate;
		this.dialogType = dialogType;
	}

	public interface NewUserListDialogListener {
		void onFinishNewUserListDialog(Spinner spinnerToUpdate, UserList newUserList);
	}

	private List<NewUserListDialogListener> newUserListDialogListenerList = new ArrayList<NewDataDialog.NewUserListDialogListener>();

	public interface NewVariableDialogListener {
		void onFinishNewVariableDialog(Spinner spinnerToUpdate, UserVariable newUserVariable);
	}

	private List<NewVariableDialogListener> newVariableDialogListenerList = new ArrayList<NewVariableDialogListener>();

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		userListDialogListenerListFinishNewUserListDialog(null);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_formula_editor_data_name, null);

		final Dialog dialogNewData = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_data_dialog_title)
				.setNegativeButton(R.string.cancel_button, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleOkButton(dialogView);
					}
				}).create();

		dialogNewData.setCanceledOnTouchOutside(true);
		dialogNewData.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialogNewData.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewData);
			}
		});

		return dialogNewData;
	}

	public void addUserListDialogListener(NewUserListDialogListener newUserListDialogListener) {
		newUserListDialogListenerList.add(newUserListDialogListener);
	}

	private void userListDialogListenerListFinishNewUserListDialog(UserList newUserList) {
		for (NewUserListDialogListener newUserListDialogListener : newUserListDialogListenerList) {
			newUserListDialogListener.onFinishNewUserListDialog(spinnerToUpdate, newUserList);
		}
	}

	public void addVariableDialogListener(NewVariableDialogListener newVariableDialogListener) {
		newVariableDialogListenerList.add(newVariableDialogListener);
	}

	private void variableDialogListenerListFinishNewVariableDialog(UserVariable newUserVariable) {
		for (NewVariableDialogListener newVariableDialogListener : newVariableDialogListenerList) {
			newVariableDialogListener.onFinishNewVariableDialog(spinnerToUpdate, newUserVariable);
		}
	}

	private void handleOkButton(View dialogView) {
		EditText nameEditText = (EditText) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_edit_text);
		RadioButton local = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_local_variable_radio_button);
		RadioButton global = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_global_variable_radio_button);
		CheckBox isListCheckbox = (CheckBox) dialogView.findViewById(R.id.dialog_formula_editor_data_is_list_checkbox);

		String name = nameEditText.getText().toString();
		switch (dialogType) {
			case SHOW_LIST_CHECKBOX:

				if (isListCheckbox.isChecked()) {
					addUserList(name, local, global);
				} else {
					addUserVariable(name, local, global);
				}
				break;
			case USER_LIST:
				addUserList(name, local, global);
				break;
			case USER_VARIABLE:
				addUserVariable(name, local, global);
				break;
		}
	}

	private void addUserList(String name, RadioButton local, RadioButton global) {
		UserList newUserList = null;
		if (global.isChecked()) {
			if (!isListNameValid(name)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_data_item);
			} else {
				newUserList = ProjectManager.getInstance().getCurrentProject().getDataContainer()
						.addProjectUserList(name);
			}
		} else if (local.isChecked()) {
			newUserList = ProjectManager.getInstance().getCurrentProject().getDataContainer().addSpriteUserList(name);
		}
		userListDialogListenerListFinishNewUserListDialog(newUserList);
	}

	private void addUserVariable(String name, RadioButton local, RadioButton global) {
		UserVariable newUserVariable = null;
		if (global.isChecked()) {
			if (!isVariableNameValid(name)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
			} else {
				newUserVariable = ProjectManager.getInstance().getCurrentProject().getDataContainer()
						.addProjectUserVariable(name);
			}
		} else if (local.isChecked()) {
			newUserVariable = ProjectManager.getInstance().getCurrentProject().getDataContainer().addSpriteUserVariable(name);
		}
		variableDialogListenerListFinishNewVariableDialog(newUserVariable);
	}

	private void handleOnShow(final Dialog dialogNewUserList) {
		final Button positiveButton = ((AlertDialog) dialogNewUserList).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setEnabled(false);

		final CheckBox isListCheckbox = (CheckBox) dialogNewUserList.findViewById(R.id.dialog_formula_editor_data_is_list_checkbox);

		switch (dialogType) {
			case SHOW_LIST_CHECKBOX:
				isListCheckbox.setVisibility(View.VISIBLE);
				break;
			case USER_VARIABLE:
				isListCheckbox.setVisibility(View.GONE);
				dialogNewUserList.setTitle(R.string.formula_editor_variable_dialog_title);
				break;
			case USER_LIST:
				isListCheckbox.setVisibility(View.GONE);
				dialogNewUserList.setTitle(R.string.formula_editor_list_dialog_title);
				break;
		}

		final EditText dialogEditText = (EditText) dialogNewUserList
				.findViewById(R.id.dialog_formula_editor_data_name_edit_text);

		InputMethodManager inputMethodManager = (InputMethodManager) getSherlockActivity().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(dialogEditText, InputMethodManager.SHOW_IMPLICIT);

		dialogEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				String name = editable.toString();
				checkName(name, positiveButton, isListCheckbox);
			}
		});

		isListCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				checkName(dialogEditText.getText().toString(), positiveButton, isListCheckbox);
			}
		});
	}

	private void checkName(String name, Button positiveButton, CheckBox isListCheckbox) {
		switch (dialogType) {
			case SHOW_LIST_CHECKBOX:

				if (isListCheckbox.isChecked()) {
					if (isListNameValid(name)) {
						positiveButton.setEnabled(true);
					} else {
						ToastUtil.showError(getActivity(), R.string.formula_editor_existing_data_item);
						positiveButton.setEnabled(false);
					}
				} else {
					if (isVariableNameValid(name)) {
						positiveButton.setEnabled(true);
					} else {
						ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
						positiveButton.setEnabled(false);
					}
				}
				break;
			case USER_LIST:
				if (isListNameValid(name)) {
					positiveButton.setEnabled(true);
				} else {
					ToastUtil.showError(getActivity(), R.string.formula_editor_existing_data_item);
					positiveButton.setEnabled(false);
				}
				break;
			case USER_VARIABLE:
				if (isVariableNameValid(name)) {
					positiveButton.setEnabled(true);
				} else {
					ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
					positiveButton.setEnabled(false);
				}
				break;
		}

		if (name.length() == 0) {
			positiveButton.setEnabled(false);
		}
	}

	private boolean isListNameValid(String name) {
		if (ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.getUserList(name, ProjectManager.getInstance().getCurrentSprite()) != null) {
			return false;
		}
		return true;
	}

	private boolean isVariableNameValid(String name) {
		if (ProjectManager.getInstance().getCurrentProject().getDataContainer()
				.getUserVariable(name, ProjectManager.getInstance().getCurrentSprite()) != null) {
			return false;
		}
		return true;
	}
}
