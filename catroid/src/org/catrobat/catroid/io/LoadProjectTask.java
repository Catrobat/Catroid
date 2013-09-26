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
package org.catrobat.catroid.io;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.utils.Utils;

public class LoadProjectTask extends AsyncTask<Void, Void, Boolean> {
	private Activity activity;
	private String projectName;
	private ProgressDialog progressDialog;
	private boolean showErrorMessage;
	private boolean startProjectActivity;

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
		String title = activity.getString(R.string.please_wait);
		String message = activity.getString(R.string.loading);
		progressDialog = ProgressDialog.show(activity, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		if (currentProject == null) {
			return ProjectManager.getInstance().loadProject(projectName, activity, false);
		} else if (!currentProject.getName().equals(projectName)) {
			return ProjectManager.getInstance().loadProject(projectName, activity, false);
		}
		return true;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		super.onPostExecute(success);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		if (onLoadProjectCompleteListener != null) {
			if (!success && showErrorMessage) {
				Utils.showErrorDialog(activity, R.string.error_load_project);
			} else {
				onLoadProjectCompleteListener.onLoadProjectSuccess(startProjectActivity);
			}
		}
	}

	public interface OnLoadProjectCompleteListener {

		public void onLoadProjectSuccess(boolean startProjectActivity);

	}
}
