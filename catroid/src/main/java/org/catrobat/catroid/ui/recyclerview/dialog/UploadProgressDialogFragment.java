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

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.web.ServerCalls;

import java.util.List;

public class UploadProgressDialogFragment extends DialogFragment {

	public static final String TAG = UploadProgressDialogFragment.class.getSimpleName();
	public static final String NUMBER_OF_UPLOADED_PROJECTS = "number_of_uploaded_projects";

	private String openAuthProvider = Constants.NO_OAUTH_PROVIDER;
	private ProgressBar progressBar;
	private ImageView successImage;
	private int progressPercent;

	AlertDialog progressBarDialog;
	private Handler handler = new Handler();

	@Override
	public Dialog onCreateDialog(Bundle bundle) {
		if (bundle != null) {
			openAuthProvider = bundle.getString(Constants.CURRENT_OAUTH_PROVIDER);
		}

		View view = View.inflate(getActivity(), R.layout.dialog_upload_project_progress, null);
		progressBar = view.findViewById(R.id.dialog_upload_progress_progressbar);
		successImage = view.findViewById(R.id.dialog_upload_progress_success_image);

		progressBarDialog = new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.upload_project_dialog_title))
				.setView(view)
				.setCancelable(false)
				.create();

		progressBarDialog.setCanceledOnTouchOutside(false);
		progressBarDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
				(DialogInterface.OnClickListener) null);
		progressBarDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.progress_upload_dialog_show_program),
				(DialogInterface.OnClickListener) null);

		return progressBarDialog;
	}

	@Override
	public void onStart() {
		super.onStart();

		final AlertDialog dialog = (AlertDialog) getDialog();
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(false);
		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(getString(R.string.done));
		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

		uploadProject(getArguments().getString(Constants.PROJECT_UPLOAD_NAME),
				getArguments().getString(Constants.PROJECT_UPLOAD_DESCRIPTION));
		//QUICKFIX: upload response currently not working
		new Thread(new Runnable() {

			public void run() {
				while (progressPercent != 100) {
					progressPercent = StatusBarNotificationManager.getInstance().getProgressPercent();
					handler.post(new Runnable() {

						public void run() {
							if (progressPercent == 100) {
								dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
								progressBar.setVisibility(View.GONE);
								successImage.setImageResource(R.drawable.ic_upload_success);
								successImage.setVisibility(View.VISIBLE);
							}
						}
					});

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						Log.d(TAG, Log.getStackTraceString(e));
					}
				}
			}
		}).start();

		dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startWebViewActivity(Constants.SHARE_PROGRAM_URL + ServerCalls.getInstance().getProjectId());
				dismiss();
			}
		});

		dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	@SuppressLint("ParcelCreator")
	private class UploadReceiver extends ResultReceiver {

		UploadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			if (resultCode == Constants.UPDATE_UPLOAD_PROGRESS) {
				long progress = resultData.getLong("currentUploadProgress");
				boolean endOfFileReached = resultData.getBoolean("endOfFileReached");
				int notificationId = resultData.getInt("notificationId");
				String projectName = resultData.getString("projectName");

				if (endOfFileReached) {
					progressPercent = 100;
				} else {
					progressPercent = (int) FileMetaDataExtractor.getProgressFromBytes(projectName, progress);
				}

				StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, progressPercent);
			}
		}
	}

	public void startWebViewActivity(String url) {
		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		intent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url);
		startActivity(intent);
	}

	private void uploadProject(String uploadName, String projectDescription) {
		ProjectManager projectManager = ProjectManager.getInstance();
		String projectPath = PathBuilder.buildProjectPath(projectManager.getCurrentProject().getName());

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String token = sharedPreferences.getString(Constants.TOKEN, Constants.NO_TOKEN);
		String username = sharedPreferences.getString(Constants.USERNAME, Constants.NO_USERNAME);
		Intent intent = new Intent(getActivity(), ProjectUploadService.class);
		String[] sceneNames = getSceneNamesAsArray(projectManager.getCurrentProject().getSceneList());

		// TODO check this extras - e.g. project description isn't used by web
		intent.putExtra("receiver", new UploadReceiver(new Handler()));
		intent.putExtra("uploadName", uploadName);
		intent.putExtra("projectDescription", projectDescription);
		intent.putExtra("projectPath", projectPath);
		intent.putExtra("username", username);
		intent.putExtra("token", token);
		intent.putExtra("provider", openAuthProvider);
		intent.putExtra("sceneNames", sceneNames);

		int notificationId = StatusBarNotificationManager.getInstance()
				.createUploadNotification(getActivity(), uploadName);

		intent.putExtra("notificationId", notificationId);
		getActivity().startService(intent);

		int numberOfUploadedProjects = sharedPreferences.getInt(NUMBER_OF_UPLOADED_PROJECTS, 0);
		numberOfUploadedProjects++;

		if (numberOfUploadedProjects == 2) {
			new RatePocketCodeDialogFragment().show(getFragmentManager(), RatePocketCodeDialogFragment.TAG);
		}

		sharedPreferences.edit().putInt(NUMBER_OF_UPLOADED_PROJECTS, numberOfUploadedProjects).commit();
	}

	private String[] getSceneNamesAsArray(List<Scene> sceneList) {
		String[] sceneNames = new String[sceneList.size()];
		for (int i = 0; i < sceneNames.length; i++) {
			sceneNames[i] = sceneList.get(i).getName();
		}
		return sceneNames;
	}
}
