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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.GetTagsTask;
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilFile;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UploadProjectTagsDialog extends DialogFragment implements GetTagsTask.AsyncResponse {

	private String openAuthProvider = Constants.NO_OAUTH_PROVIDER;

	@SuppressLint("ParcelCreator")
	private class UploadReceiver extends ResultReceiver {

		public UploadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == Constants.UPDATE_UPLOAD_PROGRESS) {
				long progress = resultData.getLong("currentUploadProgress");
				boolean endOfFileReached = resultData.getBoolean("endOfFileReached");
				int notificationId = resultData.getInt("notificationId");
				String projectName = resultData.getString("projectName");
				int progressPercent;
				if (endOfFileReached) {
					progressPercent = 100;
				} else {
					progressPercent = (int) UtilFile.getProgressFromBytes(projectName, progress);
				}

				StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, progressPercent);
			}
		}
	}

	public static final String DIALOG_TAGGING_FRAGMENT_TAG = "dialog_upload_project_tags";
	public static final String NUMBER_OF_UPLOADED_PROJECTS = "number_of_uploaded_projects";
	public static final int MAX_NUMBER_OF_TAGS_CHECKED = 3;
	public List<String> tags;
	private String currentProjectName;
	private String currentProjectDescription;

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		if (bundle != null) {
			openAuthProvider = bundle.getString(Constants.CURRENT_OAUTH_PROVIDER);
		}

		final List<String> checkedTags = new ArrayList<>();
		final String[] tagChoices = tags.toArray(new String[tags.size()]);
		Dialog tagDialog = new AlertDialog.Builder(getActivity())
				.setTitle(R.string.upload_tag_dialog_title)
				.setMultiChoiceItems(tagChoices, null, new DialogInterface.OnMultiChoiceClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
						if (isChecked) {
							if (checkedTags.size() >= MAX_NUMBER_OF_TAGS_CHECKED) {
								((AlertDialog) dialog).getListView().setItemChecked(indexSelected, false);
							} else {
								checkedTags.add(tagChoices[indexSelected]);
							}
						} else {
							checkedTags.remove(tagChoices[indexSelected]);
						}
					}
				}).setPositiveButton(getText(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						ProjectManager.getInstance().getCurrentProject().setTags(checkedTags);
						uploadProject(currentProjectName, currentProjectDescription);
					}
				}).setNegativeButton(getText(R.string.cancel_button), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						handleCancelButtonClick();
					}
				}).create();

		tagDialog.setCanceledOnTouchOutside(false);
		tagDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tagDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		return tagDialog;
	}

	public void setProjectName(String projectName) {
		this.currentProjectName = projectName;
	}
	public void setProjectDescription(String projectDescription) {
		this.currentProjectDescription = projectDescription;
	}

	private void uploadProject(String uploadName, String projectDescription) {

		dismiss();
		ProjectManager projectManager = ProjectManager.getInstance();
		String projectPath = Constants.DEFAULT_ROOT + "/" + projectManager.getCurrentProject().getName();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		Intent uploadIntent = new Intent(getActivity(), ProjectUploadService.class);

		// TODO check this extras - e.g. project description isn't used by web
		uploadIntent.putExtra("receiver", new UploadReceiver(new Handler()));
		uploadIntent.putExtra("uploadName", uploadName);
		uploadIntent.putExtra("projectDescription", projectDescription);
		uploadIntent.putExtra("projectPath", projectPath);
		uploadIntent.putExtra("username", username);
		uploadIntent.putExtra("token", token);
		uploadIntent.putExtra("provider", openAuthProvider);

		int notificationId = StatusBarNotificationManager.getInstance().createUploadNotification(getActivity(),
				uploadName);
		uploadIntent.putExtra("notificationId", notificationId);
		getActivity().startService(uploadIntent);
		int numberOfUploadedProjects = sharedPreferences.getInt(NUMBER_OF_UPLOADED_PROJECTS, 0);
		numberOfUploadedProjects = numberOfUploadedProjects + 1;

		if (numberOfUploadedProjects == 2) {
			RatingDialog dialog = new RatingDialog();
			dialog.show(getFragmentManager(), RatingDialog.TAG);
		}
		sharedPreferences.edit().putInt(NUMBER_OF_UPLOADED_PROJECTS, numberOfUploadedProjects).commit();
	}

	@Override
	public void onTagsReceived(List<String> tags) {
		this.tags = tags;
	}

	private void handleCancelButtonClick() {
		Utils.invalidateLoginTokenIfUserRestricted(getActivity());
		dismiss();
	}
}
