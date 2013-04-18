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

import java.io.File;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadProjectDialog extends DialogFragment {

	private class UploadReceiver extends ResultReceiver {

		public UploadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == Constants.UPDATE_UPLOAD_PROGRESS) {
				long progress = resultData.getLong("currentUploadProgress");
				boolean endOfFileReached = resultData.getBoolean("endOfFileReached");
				Integer notificationId = resultData.getInt("notificationId");
				String projectName = resultData.getString("projectName");
				long progressPercent = 0;
				if (endOfFileReached) {
					progressPercent = 100;
				} else {
					progressPercent = UtilFile.getProgressFromBytes(projectName, progress);
				}

				String notificationMessage = "Upload " + progressPercent + "% "
						+ activity.getString(R.string.notification_percent_completed) + ":" + projectName;
				StatusBarNotificationManager.INSTANCE.updateNotification(notificationId, notificationMessage,
						Constants.UPLOAD_NOTIFICATION, endOfFileReached);
			}
		}
	}

	public static final String DIALOG_FRAGMENT_TAG = "dialog_upload_project";

	private EditText projectUploadName;
	private EditText projectDescriptionField;
	private TextView projectRename;
	private TextView sizeOfProject;

	private String currentProjectName;
	private String currentProjectDescription;
	private String newProjectName;
	private Activity activity;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_upload_project, null);

		projectRename = (TextView) dialogView.findViewById(R.id.tv_project_rename);
		projectDescriptionField = (EditText) dialogView.findViewById(R.id.project_description_upload);
		projectUploadName = (EditText) dialogView.findViewById(R.id.project_upload_name);
		sizeOfProject = (TextView) dialogView.findViewById(R.id.dialog_upload_size_of_project);

		Dialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView)
				.setTitle(R.string.upload_project_dialog_title)
				.setPositiveButton(R.string.upload_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleUploadButtonClick();
					}
				}).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handleCancelButtonClick();
					}
				}).create();

		dialog.setCanceledOnTouchOutside(true);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

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
		currentProjectName = ProjectManager.INSTANCE.getCurrentProject().getName();
		currentProjectDescription = ProjectManager.INSTANCE.getCurrentProject().getDescription();
		sizeOfProject.setText(UtilFile.getSizeAsString(new File(Constants.DEFAULT_ROOT + "/" + currentProjectName)));
		projectRename.setVisibility(View.GONE);
		projectUploadName.setText(currentProjectName);
		projectDescriptionField.setText(currentProjectDescription);
		projectUploadName.requestFocus();
		projectUploadName.selectAll();
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
		ProjectManager projectManager = ProjectManager.INSTANCE;

		String uploadName = projectUploadName.getText().toString();
		String projectDescription = projectDescriptionField.getText().toString();

		if (uploadName.length() == 0) {
			Utils.showErrorDialog(getActivity(), getString(R.string.error_no_name_entered));
			return;
		}

		if (uploadName.equals(getString(R.string.default_project_name))) {
			Utils.showErrorDialog(getActivity(), getString(R.string.error_upload_project_with_default_name));
			return;
		}

		Context context = getActivity().getApplicationContext();
		if (Utils.isStandardProject(projectManager.getCurrentProject(), context)) {
			Utils.showErrorDialog(getActivity(), getString(R.string.error_upload_default_project));
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

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		Intent uploadIntent = new Intent(getActivity(), ProjectUploadService.class);
		uploadIntent.putExtra("receiver", new UploadReceiver(new Handler()));
		uploadIntent.putExtra("uploadName", uploadName);
		uploadIntent.putExtra("projectDescription", projectDescription);
		uploadIntent.putExtra("projectPath", projectPath);
		uploadIntent.putExtra("username", username);
		uploadIntent.putExtra("token", token);
		int notificationId = createNotification(uploadName);
		uploadIntent.putExtra("notificationId", notificationId);
		activity = getActivity();
		activity.startService(uploadIntent);
	}

	public int createNotification(String uploadName) {
		StatusBarNotificationManager manager = StatusBarNotificationManager.INSTANCE;
		int notificationId = manager.createNotification(uploadName, getActivity(), Constants.UPLOAD_NOTIFICATION);
		return notificationId;
	}

	private void handleCancelButtonClick() {
		dismiss();
	}
}
