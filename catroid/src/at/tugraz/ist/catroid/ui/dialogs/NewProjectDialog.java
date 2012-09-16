/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.dialogs;

import java.io.IOException;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.transfers.RegistrationTask.OnRegistrationCompleteListener;
import at.tugraz.ist.catroid.ui.ProjectActivity;
import at.tugraz.ist.catroid.utils.Utils;

public class NewProjectDialog extends DialogFragment implements OnRegistrationCompleteListener {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_new_project";

	private EditText newProjectEditText;
	private EditText newProjectDescriptionEditText;
	private Button okButton;
	private Button cancelButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_new_project, container);

		newProjectEditText = (EditText) rootView.findViewById(R.id.project_name_edittext);
		newProjectDescriptionEditText = (EditText) rootView.findViewById(R.id.project_description_edittext);
		okButton = (Button) rootView.findViewById(R.id.new_project_ok_button);
		cancelButton = (Button) rootView.findViewById(R.id.new_project_cancel_button);

		newProjectEditText.setText("");
		newProjectDescriptionEditText.setText("");

		okButton.setEnabled(false);

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
					okButton.setEnabled(false);
				} else {
					okButton.setEnabled(true);
				}
			}
		});

		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleOkButtonClick();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleCancelButtonClick();
			}
		});

		getDialog().setTitle(R.string.new_project_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		getDialog().setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(newProjectEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		return rootView;
	}

	@Override
	public void onRegistrationComplete() {
		dismiss();

		UploadProjectDialog uploadProjectDialog = new UploadProjectDialog();
		uploadProjectDialog.show(getFragmentManager(), UploadProjectDialog.DIALOG_FRAGMENT_TAG);
	}

	protected boolean handleOkButtonClick() {
		String projectName = newProjectEditText.getText().toString().trim();
		String projectDescription = newProjectDescriptionEditText.getText().toString().trim();

		if (projectName.length() == 0) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_no_name_entered));
			return false;
		}

		if (StorageHandler.getInstance().projectExistsIgnoreCase(projectName)) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_project_exists));
			return false;
		}

		try {
			ProjectManager.INSTANCE.initializeNewProject(projectName, getActivity());
			ProjectManager.INSTANCE.getCurrentProject().setDescription(projectDescription);
		} catch (IOException e) {
			Utils.displayErrorMessageFragment(getFragmentManager(), getString(R.string.error_new_project));
			dismiss();
		}

		Utils.saveToPreferences(getActivity(), Constants.PREF_PROJECTNAME_KEY, projectName);
		Intent intent = new Intent(getActivity(), ProjectActivity.class);
		getActivity().startActivity(intent);

		return true;
	}

	protected boolean handleCancelButtonClick() {
		dismiss();
		return false;
	}

	protected String getTitle() {
		return getString(R.string.new_project_dialog_title);
	}

	protected String getHint() {
		return getString(R.string.new_project_dialog_hint);
	}
}
