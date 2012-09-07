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

import java.io.File;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.transfers.ProjectUploadTask;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

public class UploadProjectDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_upload_project";

	private EditText projectUploadName;
	private EditText projectDescriptionField;
	private TextView projectRename;
	private TextView sizeOfProject;
	private Button uploadButton;
	private Button cancelButton;

	private String currentProjectName;
	private String currentProjectDescription;
	private String newProjectName;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.dialog_upload_project, container);

		projectRename = (TextView) rootView.findViewById(R.id.tv_project_rename);
		projectDescriptionField = (EditText) rootView.findViewById(R.id.project_description_upload);
		projectUploadName = (EditText) rootView.findViewById(R.id.project_upload_name);
		cancelButton = (Button) rootView.findViewById(R.id.cancel_button);
		uploadButton = (Button) rootView.findViewById(R.id.upload_button);
		sizeOfProject = (TextView) rootView.findViewById(R.id.dialog_upload_size_of_project);

		initControls();

		getDialog().setTitle(R.string.upload_project_dialog_title);
		getDialog().setCanceledOnTouchOutside(true);
		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		getDialog().setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				initListeners();

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(projectUploadName, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		return rootView;
	}

	private void initControls() {
		currentProjectName = ProjectManager.INSTANCE.getCurrentProject().getName();
		currentProjectDescription = ProjectManager.INSTANCE.getCurrentProject().getDescription();
		sizeOfProject.setText(UtilFile.getSizeAsString(new File(Constants.DEFAULT_ROOT + "/" + currentProjectName)));
		projectRename.setVisibility(View.GONE);
		projectUploadName.setText(currentProjectName);
		projectDescriptionField.setText(currentProjectDescription);
		projectUploadName.requestFocus();
		projectUploadName.selectAll();

		uploadButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleUploadButtonClick();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleCancelButtonClick();
			}
		});
	}

	private void initListeners() {
		projectUploadName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getDialog().getWindow()
							.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		projectDescriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					getDialog().getWindow()
							.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		projectUploadName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();

				if (!projectUploadName.getText().toString().equals(currentProjectName)) {
					projectRename.setVisibility(View.VISIBLE);
					newProjectName = projectUploadName.getText().toString();
				} else {
					projectRename.setVisibility(View.GONE);
				}
				if (s.length() == 0) {
					Toast.makeText(getActivity(), R.string.notification_invalid_text_entered, Toast.LENGTH_SHORT)
							.show();
					uploadButton.setEnabled(false);
				} else {
					uploadButton.setEnabled(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void handleUploadButtonClick() {
		ProjectManager projectManager = ProjectManager.INSTANCE;

		String uploadName = projectUploadName.getText().toString();
		String projectDescription = projectDescriptionField.getText().toString();

		if (uploadName.length() == 0) {
			Utils.displayErrorMessage(getActivity(), getString(R.string.error_no_name_entered));
			return;
		}
		if (!uploadName.equals(currentProjectName)) {
			projectRename.setVisibility(View.VISIBLE);
			boolean renamed = projectManager.renameProjectNameAndDescription(newProjectName, projectDescription,
					getActivity());
			if (!renamed) {
				return;
			}
		} else if (uploadName.equals(currentProjectName) && (!projectDescription.equals(currentProjectDescription))) {
			projectManager.getCurrentProject().setDescription(projectDescription);
		}

		projectManager.getCurrentProject().setDeviceData(getActivity());
		projectManager.saveProject();

		dismiss();
		String projectPath = Constants.DEFAULT_ROOT + "/" + projectManager.getCurrentProject().getName();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String token = prefs.getString(Constants.TOKEN, "0");
		new ProjectUploadTask(getActivity(), uploadName, projectDescription, projectPath, token).execute();
	}

	private void handleCancelButtonClick() {
		dismiss();
	}
}
