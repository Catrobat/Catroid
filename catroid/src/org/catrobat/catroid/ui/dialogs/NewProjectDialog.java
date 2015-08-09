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
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.Utils;

public class NewProjectDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_project";
	public static final String SHARED_PREFERENCES_EMPTY_PROJECT = "shared_preferences_empty_project";

	private static final String TAG = NewProjectDialog.class.getSimpleName();

	private EditText newProjectEditText;
	private Dialog newProjectDialog;
	private CheckBox emptyProjectCheckBox;
	private OrientationDialog orientationDialog;
	private SharedPreferences sharedPreferences;

	private boolean openedFromProjectList = false;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_new_project, null);

		newProjectEditText = (EditText) dialogView.findViewById(R.id.project_name_edittext);

		newProjectEditText.setText("");

		newProjectDialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.new_project_dialog_title)
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		newProjectDialog.setCanceledOnTouchOutside(true);
		newProjectDialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		newProjectDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		newProjectDialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				if (getActivity() == null) {
					Log.e(TAG, "onShow() Activity was null!");
					return;
				}

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(newProjectEditText, InputMethodManager.SHOW_IMPLICIT);

				((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				newProjectEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						if (newProjectEditText.length() == 0) {
							((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
						} else {
							((AlertDialog) newProjectDialog).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
						}
					}
				});

				Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
				positiveButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View view) {
						handleOkButtonClick();
					}
				});
			}
		});

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		boolean shouldBeEmpty = sharedPreferences.getBoolean(SHARED_PREFERENCES_EMPTY_PROJECT, false);

		emptyProjectCheckBox = (CheckBox) dialogView.findViewById(R.id.project_empty_checkbox);
		emptyProjectCheckBox.setChecked(shouldBeEmpty);

		return newProjectDialog;
	}

	protected void handleOkButtonClick() {
		String projectName = newProjectEditText.getText().toString().trim();
		boolean shouldBeEmpty = emptyProjectCheckBox.isChecked();
		if (getActivity() == null) {
			Log.e(TAG, "handleOkButtonClick() Activity was null!");
			return;
		}

		if (projectName.isEmpty()) {
			Utils.showErrorDialog(getActivity(), R.string.no_name, R.string.error_no_program_name_entered);
			return;
		}

		if (Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(projectName)) {
			Utils.showErrorDialog(getActivity(), R.string.name_exists, R.string.error_project_exists);
			return;
		}

		orientationDialog = new OrientationDialog();
		orientationDialog.show(getActivity().getSupportFragmentManager(), OrientationDialog.DIALOG_FRAGMENT_TAG);
		orientationDialog.setOpenedFromProjectList(openedFromProjectList);
		orientationDialog.setProjectName(projectName);
		orientationDialog.setShouldBeEmpty(shouldBeEmpty);

		sharedPreferences.edit().putBoolean(SHARED_PREFERENCES_EMPTY_PROJECT, shouldBeEmpty).commit();
		Utils.saveToPreferences(getActivity(), Constants.PREF_PROJECTNAME_KEY, projectName);

		dismiss();
	}

	public void setOpenedFromProjectList(boolean openedFromProjectList) {
		this.openedFromProjectList = openedFromProjectList;
	}
}
