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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.ProjectUploadService;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.utils.FileMetaDataExtractor;
import org.catrobat.catroid.utils.PathBuilder;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.web.ServerCalls;

import java.util.List;

import static org.catrobat.catroid.common.Constants.SHARE_PROGRAM_URL;

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
				.setPositiveButton(R.string.progress_upload_dialog_show_program, null)
				.setNegativeButton(R.string.cancel, null)
				.setCancelable(false)
				.create();

		return progressBarDialog;
	}

	@Override
	public void onStart() {
		super.onStart();

		final AlertDialog alertDialog = (AlertDialog) getDialog();

		alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText(getString(R.string.done));
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

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
								alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
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

		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startWebViewActivity(SHARE_PROGRAM_URL + ServerCalls.getInstance().getProjectId());
				if (alertDialog.getOwnerActivity() == null) {
					alertDialog.dismiss();
				} else {
					alertDialog.getOwnerActivity().finish();
				}
			}
		});

		alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (alertDialog.getOwnerActivity() == null) {
					alertDialog.dismiss();
				} else {
					alertDialog.getOwnerActivity().finish();
				}
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

		List<String> sceneNameList = projectManager.getCurrentProject().getSceneNames();
		String[] sceneNames = new String[sceneNameList.size()];
		sceneNameList.toArray(sceneNames);

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

		sharedPreferences.edit()
				.putInt(NUMBER_OF_UPLOADED_PROJECTS, numberOfUploadedProjects)
				.commit();
	}
}
