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
package org.catrobat.catroid.io;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.exceptions.CompatibilityProjectException;
import org.catrobat.catroid.exceptions.LoadingProjectException;
import org.catrobat.catroid.exceptions.OutdatedVersionProjectException;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

public class LoadProjectTask extends AsyncTask<Void, Void, Boolean> {

	private static final String TAG = LoadProjectTask.class.getSimpleName();

	private Activity activity;
	private String projectName;
	private boolean showErrorMessage;
	private boolean startProjectActivity;
	private LinearLayout linearLayoutProgressCircle;

	private OnLoadProjectCompleteListener onLoadProjectCompleteListener;

	public LoadProjectTask(Activity activity, String projectName, boolean showErrorMessage, boolean startProjectActivity) {
		this.activity = activity;
		this.projectName = projectName;
		this.showErrorMessage = showErrorMessage;
		this.startProjectActivity = startProjectActivity;
	}

	public void setOnLoadProjectCompleteListener(OnLoadProjectCompleteListener listener) {
		onLoadProjectCompleteListener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (activity == null) {
			return;
		}
		linearLayoutProgressCircle = (LinearLayout) activity.findViewById(R.id.progress_circle);
		linearLayoutProgressCircle.setVisibility(View.VISIBLE);
		linearLayoutProgressCircle.bringToFront();
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null || !currentProject.getName().equals(projectName)) {
			try {
				ProjectManager.getInstance().loadProject(projectName, activity);
			} catch (LoadingProjectException loadingProjectException) {
				Log.e(TAG, "Project cannot load", loadingProjectException);
				return false;
			} catch (OutdatedVersionProjectException outdatedVersionException) {
				Log.e(TAG, "Projectcode version is outdated", outdatedVersionException);
				return false;
			} catch (CompatibilityProjectException compatibilityException) {
				Log.e(TAG, "Project is not compatible", compatibilityException);
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (onLoadProjectCompleteListener != null) {
			if (!success && showErrorMessage) {
				linearLayoutProgressCircle.setVisibility(View.GONE);

				Builder builder = new CustomAlertDialogBuilder(activity);
				builder.setTitle(R.string.error);
				builder.setMessage(R.string.error_load_project);
				builder.setNeutralButton(R.string.close, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onLoadProjectCompleteListener.onLoadProjectFailure();
					}
				});
				Dialog errorDialog = builder.create();
				errorDialog.show();

			} else {
				onLoadProjectCompleteListener.onLoadProjectSuccess(startProjectActivity);
			}
		}
	}

	public interface OnLoadProjectCompleteListener {

		void onLoadProjectSuccess(boolean startProjectActivity);

		void onLoadProjectFailure();

	}

}
