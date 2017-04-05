/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.DataContainer;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewDataDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_data_catroid";
	Spinner spinnerToUpdate;
	DialogType dialogType = DialogType.SHOW_LIST_CHECKBOX;
	private int spinnerPositionIfCancel;

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
		if (spinnerToUpdate != null) {
			spinnerToUpdate.setSelection(spinnerPositionIfCancel);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_formula_editor_data_name, null);

		final Dialog dialogNewData = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_data_dialog_title)
				.setNegativeButton(R.string.cancel, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).setPositiveButton(R.string.ok, null).create();

		dialogNewData.setCanceledOnTouchOutside(true);
		dialogNewData.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialogNewData.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewData);

				Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						if (handleOkButton(dialogView)) {
							dialogNewData.dismiss();
						}
					}
				});
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

	private boolean handleOkButton(View dialogView) {
		EditText nameEditText = (EditText) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_edit_text);
		RadioButton local = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_local_variable_radio_button);
		RadioButton global = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_data_name_global_variable_radio_button);
		CheckBox isListCheckbox = (CheckBox) dialogView.findViewById(R.id.dialog_formula_editor_data_is_list_checkbox);

		String name = nameEditText.getText().toString().trim();

		if (name.equalsIgnoreCase("")) {
			switch (dialogType) {
				case SHOW_LIST_CHECKBOX:
					if (isListCheckbox.isChecked()) {
						Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.no_listname_entered);
					} else {
						Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.no_variablename_entered);
					}
					break;
				case USER_LIST:
					Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.no_listname_entered);
					break;
				case USER_VARIABLE:
					Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.no_variablename_entered);
					break;
			}
			nameEditText.getText().clear();
			return false;
		}

		switch (dialogType) {
			case SHOW_LIST_CHECKBOX:
				if (isListCheckbox.isChecked()) {
					if (isListNameValid(name)) {
						addUserList(name, local, global);
						return true;
					} else {
						Utils.showErrorDialog(getActivity(), R.string.formula_editor_existing_variable, R.string.user_listname_already_exists);
						return false;
					}
				} else {
					if (isVariableNameValid(name)) {
						addUserVariable(name, local, global);
						return true;
					} else {
						Utils.showErrorDialog(getActivity(), R.string.formula_editor_existing_variable, R.string.user_variablename_already_exists);
						return false;
					}
				}
			case USER_LIST:
				if (isListNameValid(name)) {
					addUserList(name, local, global);
					return true;
				} else {
					Utils.showErrorDialog(getActivity(), R.string.formula_editor_existing_variable, R.string.user_listname_already_exists);
					return false;
				}
			case USER_VARIABLE:
				if (isVariableNameValid(name)) {
					addUserVariable(name, local, global);
					return true;
				} else {
					Utils.showErrorDialog(getActivity(), R.string.formula_editor_existing_variable, R.string
							.user_variablename_already_exists);
					return false;
				}
			default:
				return false;
		}
	}

	private void addUserList(String name, RadioButton local, RadioButton global) {
		UserList newUserList = null;
		if (global.isChecked()) {
			if (!isListNameValid(name)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_data_item);
			} else {
				newUserList = ProjectManager.getInstance().getCurrentScene().getDataContainer()
						.addProjectUserList(name);
			}
		} else if (local.isChecked()) {
			newUserList = ProjectManager.getInstance().getCurrentScene().getDataContainer().addSpriteUserList(name);
		}
		userListDialogListenerListFinishNewUserListDialog(newUserList);
	}

	private void addUserVariable(String name, RadioButton local, RadioButton global) {
		UserVariable newUserVariable = null;
		if (global.isChecked()) {
			if (!isVariableNameValid(name)) {
				ToastUtil.showError(getActivity(), R.string.formula_editor_existing_variable);
			} else {
				newUserVariable = ProjectManager.getInstance().getCurrentScene().getDataContainer()
						.addProjectUserVariable(name);
			}
		} else if (local.isChecked()) {
			newUserVariable = ProjectManager.getInstance().getCurrentScene().getDataContainer().addSpriteUserVariable(name);
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

		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(
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
				if (name.length() == 0) {
					positiveButton.setEnabled(false);
				} else {
					positiveButton.setEnabled(true);
				}
			}
		});
	}

	private boolean isListNameValid(String name) {
		DataContainer currentData = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		RadioButton global = (RadioButton) getDialog()
				.findViewById(R.id.dialog_formula_editor_data_name_global_variable_radio_button);

		if (global.isChecked()) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			return !currentData.existListInAnySprite(name, sprites) && !currentData.existProjectListWithName(name);
		} else {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			return !currentData.existProjectListWithName(name) && !currentData.existSpriteListByName(name, currentSprite);
		}
	}

	private boolean isVariableNameValid(String name) {
		DataContainer currentData = ProjectManager.getInstance().getCurrentScene().getDataContainer();
		RadioButton global = (RadioButton) getDialog()
				.findViewById(R.id.dialog_formula_editor_data_name_global_variable_radio_button);

		if (global.isChecked()) {
			List<Sprite> sprites = ProjectManager.getInstance().getCurrentScene().getSpriteList();
			return !currentData.existVariableInAnySprite(name, sprites) && !currentData.existProjectVariableWithName(name);
		} else {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			return !currentData.existProjectVariableWithName(name) && !currentData.existSpriteVariableByName(name, currentSprite);
		}
	}

	public void setUserVariableIfCancel(int spinnerPositionIfCancel) {
		this.spinnerPositionIfCancel = spinnerPositionIfCancel;
	}
}
