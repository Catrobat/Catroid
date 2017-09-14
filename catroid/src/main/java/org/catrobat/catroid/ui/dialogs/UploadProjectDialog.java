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
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.GetTagsTask;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.io.File;

public class UploadProjectDialog extends DialogFragment {

	public static final String DIALOG_FRAGMENT_TAG = "dialog_upload_project";

	private EditText projectUploadName;
	private EditText projectDescriptionField;
	private TextView projectRename;
	private TextView sizeOfProject;

	private String currentProjectName;
	private String currentProjectDescription;
	private String newProjectName;
	private UploadProjectTagsDialog tagDialog;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_upload_project, null);

		tagDialog = new UploadProjectTagsDialog();
		GetTagsTask task = new GetTagsTask(getActivity());
		task.setOnTagsResponseListener(tagDialog);
		task.execute();

		projectRename = (TextView) dialogView.findViewById(R.id.tv_project_rename);
		projectDescriptionField = (EditText) dialogView.findViewById(R.id.project_description_upload);
		projectUploadName = (EditText) dialogView.findViewById(R.id.project_upload_name);
		sizeOfProject = (TextView) dialogView.findViewById(R.id.dialog_upload_size_of_project);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.upload_project_dialog_title)
				.setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleUploadButtonClick();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleCancelButtonClick();
					}
				}).create();

		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		dialog.setOnShowListener(new OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				initListeners();

				InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(projectUploadName, InputMethodManager.SHOW_IMPLICIT);
			}
		});

		initControls();

		return dialog;
	}

	private void initControls() {
		currentProjectName = ProjectManager.getInstance().getCurrentProject().getName();
		currentProjectDescription = ProjectManager.getInstance().getCurrentProject().getDescription();
		sizeOfProject.setText(UtilFile.getSizeAsString(new File(Utils.buildProjectPath(currentProjectName))));
		projectRename.setVisibility(View.GONE);
		projectUploadName.setText(currentProjectName);
		projectDescriptionField.setText(currentProjectDescription);
		projectUploadName.requestFocus();
		projectUploadName.selectAll();
	}

	private void initListeners() {
		projectUploadName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					getDialog().getWindow()
							.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});

		projectDescriptionField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
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
					ToastUtil.showError(getActivity(), R.string.notification_invalid_text_entered);
					((AlertDialog) getDialog()).getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
				} else {
					((AlertDialog) getDialog()).getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
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
		if (checkInputOfUploadDialog(projectUploadName.getText().toString(), projectDescriptionField.getText().toString())) {
			Bundle args = new Bundle();
			args.putString(Constants.PROJECT_UPLOAD_NAME, projectUploadName.getText().toString());
			args.putString(Constants.PROJECT_UPLOAD_DESCRIPTION, projectDescriptionField.getText().toString());
			tagDialog.setArguments(args);
			tagDialog.show(getFragmentManager(), UploadProjectTagsDialog.DIALOG_TAGGING_FRAGMENT_TAG);
		}
	}

	private void handleCancelButtonClick() {
		Utils.invalidateLoginTokenIfUserRestricted(getActivity());
		dismiss();
	}

	private boolean checkInputOfUploadDialog(String uploadName, String projectDescription) {
		ProjectManager projectManager = ProjectManager.getInstance();

		if (uploadName.isEmpty()) {
			Utils.showErrorDialog(getActivity(), R.string.error_no_program_name_entered);
			return false;
		}

		if (uploadName.equals(getString(R.string.default_project_name))) {
			Utils.showErrorDialog(getActivity(), R.string.error_upload_project_with_default_name);
			return false;
		}

		Context context = getActivity().getApplicationContext();
		if (Utils.isStandardProject(projectManager.getCurrentProject(), context)) {
			Utils.showErrorDialog(getActivity(), R.string.error_upload_default_project);
			return false;
		}

		boolean needsRenaming;
		if ((needsRenaming = !uploadName.equals(currentProjectName))
				|| !projectDescription.equals(currentProjectDescription)) {

			String oldDescription = currentProjectDescription;
			projectManager.getCurrentProject().setDescription(projectDescription);
			if (needsRenaming) {
				projectRename.setVisibility(View.VISIBLE);
				boolean renamed = projectManager.renameProject(newProjectName, getActivity());
				if (!renamed) {
					projectManager.getCurrentProject().setDescription(oldDescription);
					return false;
				}
			}
		}

		projectManager.getCurrentProject().setDeviceData(getActivity());
		return true;
	}
}
