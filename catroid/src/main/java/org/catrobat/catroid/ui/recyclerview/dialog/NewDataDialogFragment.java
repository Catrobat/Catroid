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
package org.catrobat.catroid.ui.recyclerview.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.formulaeditor.UserData;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.recyclerview.dialog.dialoginterface.NewItemInterface;
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DialogInputWatcher;

public class NewDataDialogFragment extends DialogFragment {

	public static final String TAG = NewDataDialogFragment.class.getSimpleName();

	private NewItemInterface<UserData> newDataInterface;

	protected TextInputLayout inputLayout;
	protected RadioGroup radioGroup;
	protected CheckBox makeList;

	public void setNewDataInterface(NewItemInterface<UserData> newDataInterface) {
		this.newDataInterface = newDataInterface;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isRestoringPreviouslyDestroyedActivity = savedInstanceState != null;
		if (isRestoringPreviouslyDestroyedActivity) {
			dismiss();
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		View view = View.inflate(getActivity(), R.layout.dialog_new_user_data, null);

		inputLayout = view.findViewById(R.id.input);
		inputLayout.setHint(getString(R.string.data_label));
		radioGroup = view.findViewById(R.id.radio_group);
		makeList = view.findViewById(R.id.make_list);

		makeList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				getDialog().setTitle(isChecked ? R.string.formula_editor_list_dialog_title
						: R.string.formula_editor_variable_dialog_title);
			}
		});

		final AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.formula_editor_variable_dialog_title)
				.setView(view)
				.setPositiveButton(R.string.ok, null)
				.setNegativeButton(R.string.cancel, null)
				.create();

		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
				buttonPositive.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onPositiveButtonClick()) {
							dismiss();
						}
					}
				});
				buttonPositive.setEnabled(!inputLayout.getEditText().getText().toString().isEmpty());
				DialogInputWatcher inputWatcher = new DialogInputWatcher(inputLayout, buttonPositive, false);
				inputLayout.getEditText().addTextChangedListener(inputWatcher);
			}
		});
		inputLayout.getEditText().setOnFocusChangeListener(new OpenSoftkeyboardRightAway(alertDialog));
		return alertDialog;
	}

	protected boolean addUserList(UserList list) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		boolean createProjectVariable = (radioGroup.getCheckedRadioButtonId() == R.id.global);

		if (createProjectVariable) {
			return dataContainer.addUserList(list);
		} else {
			return dataContainer.addUserList(ProjectManager.getInstance().getCurrentSprite(), list);
		}
	}

	protected boolean addUserVariable(UserVariable var) {
		DataContainer dataContainer = ProjectManager.getInstance().getCurrentlyEditedScene().getDataContainer();
		boolean createProjectVariable = (radioGroup.getCheckedRadioButtonId() == R.id.global);

		if (createProjectVariable) {
			return dataContainer.addUserVariable(var);
		} else {
			return dataContainer.addUserVariable(ProjectManager.getInstance().getCurrentSprite(), var);
		}
	}

	protected boolean onPositiveButtonClick() {
		String name = inputLayout.getEditText().getText().toString().trim();

		if (name.isEmpty()) {
			inputLayout.setError(getString(R.string.name_consists_of_spaces_only));
			return false;
		}

		if (makeList.isChecked()) {
			UserList list = new UserList(name);
			if (addUserList(list)) {
				newDataInterface.addItem(list);
				return true;
			}
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		} else {
			UserVariable var = new UserVariable(name);
			if (addUserVariable(var)) {
				newDataInterface.addItem(var);
				return true;
			}
			inputLayout.setError(getString(R.string.name_already_exists));
			return false;
		}
	}
}
