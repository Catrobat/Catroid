/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserList;

import java.util.ArrayList;
import java.util.List;

public class NewUserListDialog extends SherlockDialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_userlist_catroid";
	Spinner spinnerToUpdate;

	public NewUserListDialog() {
		super();
	}

	public NewUserListDialog(Spinner spinnerToUpdate) {
		super();
		this.spinnerToUpdate = spinnerToUpdate;
	}

	public interface NewUserListDialogListener {
		void onFinishNewUserListDialog(Spinner spinnerToUpdate, UserList newUserList);
	}

	private List<NewUserListDialogListener> newUserListDialogListenerList = new ArrayList<NewUserListDialog.NewUserListDialogListener>();

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		userListDialogListenerListFinishNewUserListDialog(null);
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		final View dialogView = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_formula_editor_userlist_name, null);

		final Dialog dialogNewUserList = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.formula_editor_userlist_dialog_title)
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

		dialogNewUserList.setCanceledOnTouchOutside(true);
		dialogNewUserList.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialogNewUserList.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				handleOnShow(dialogNewUserList);
			}
		});

		return dialogNewUserList;
	}

	public void addUserListDialogListener(NewUserListDialogListener newUserListDialogListener) {
		newUserListDialogListenerList.add(newUserListDialogListener);
	}

	private void userListDialogListenerListFinishNewUserListDialog(UserList newUserList) {
		for (NewUserListDialogListener newUserListDialogListener : newUserListDialogListenerList) {
			newUserListDialogListener.onFinishNewUserListDialog(spinnerToUpdate, newUserList);
		}
	}

	private void handleOkButton(View dialogView) {
		EditText userListNameEditText = (EditText) dialogView
				.findViewById(R.id.dialog_formula_editor_userlist_name_edit_text);
		RadioButton localList = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_userlist_name_local_variable_radio_button);
		RadioButton globalList = (RadioButton) dialogView
				.findViewById(R.id.dialog_formula_editor_userlist_name_global_variable_radio_button);

		String listName = userListNameEditText.getText().toString();
		UserList newUserList = null;
		if (globalList.isChecked()) {
			if (ProjectManager.getInstance().getCurrentProject().getUserLists()
					.getUserList(listName, ProjectManager.getInstance().getCurrentSprite()) != null) {

				Toast.makeText(getActivity(), R.string.formula_editor_existing_userlist, Toast.LENGTH_LONG).show();

			} else {
				newUserList = ProjectManager.getInstance().getCurrentProject().getUserLists()
						.addProjectUserList(listName);
			}
		} else if (localList.isChecked()) {
			newUserList = ProjectManager.getInstance().getCurrentProject().getUserLists().addSpriteUserList(listName);
		}
		userListDialogListenerListFinishNewUserListDialog(newUserList);
	}

	private void handleOnShow(final Dialog dialogNewUserList) {
		final Button positiveButton = ((AlertDialog) dialogNewUserList).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setEnabled(false);

		EditText dialogEditText = (EditText) dialogNewUserList
				.findViewById(R.id.dialog_formula_editor_userlist_name_edit_text);

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

				String userListName = editable.toString();
				if (ProjectManager.getInstance().getCurrentProject().getUserLists()
						.getUserList(userListName, ProjectManager.getInstance().getCurrentSprite()) != null) {

					Toast.makeText(getActivity(), R.string.formula_editor_existing_userlist, Toast.LENGTH_SHORT).show();

					positiveButton.setEnabled(false);
				} else {
					positiveButton.setEnabled(true);
				}

				if (editable.length() == 0) {
					positiveButton.setEnabled(false);
				}
			}
		});
	}

}
