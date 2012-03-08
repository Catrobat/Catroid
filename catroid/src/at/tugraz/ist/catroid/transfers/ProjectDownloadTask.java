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
package at.tugraz.ist.catroid.transfers;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class ProjectDownloadTask extends AsyncTask<Void, Void, Boolean> implements OnClickListener {
	private MainMenuActivity activity;
	private String projectName;
	private String zipFileString;
	private String url;
	private ProgressDialog progressDialog;
	private boolean result;
	private static final String DOWNLOAD_FILE_NAME = "down" + Consts.CATROID_EXTENTION;
	private static ProjectManager projectManager = ProjectManager.getInstance();

	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public ProjectDownloadTask(MainMenuActivity mainMenuActivity, String url, String projectName) {
		this.activity = mainMenuActivity;
		this.projectName = projectName;
		this.zipFileString = Utils.buildPath(Consts.TMP_PATH, DOWNLOAD_FILE_NAME);
		this.url = url;
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
		try {
			ServerCalls.getInstance().downloadProject(url, zipFileString);

			result = UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
			return result;
		} catch (WebconnectionException e) {
			e.printStackTrace();
		}
		return false;

	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);

		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (!result) {
			//Toast.makeText(mActivity, R.string.error_project_download, Toast.LENGTH_SHORT).show();
			showDialog(R.string.error_project_download);
			return;
		}

		if (activity == null) {
			return;
		}
		Toast.makeText(activity, R.string.success_project_download, Toast.LENGTH_SHORT).show();
		projectManager.loadProject(projectName, activity, true);
		activity.writeProjectTitleInTextfield();

	}

	private void showDialog(int messageId) {
		if (activity == null) {
			return;
		}
		//TODO: refactor to use strings.xml
		new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
	}

	public void onClick(DialogInterface dialog, int which) {
		if (!result) {
			activity.finish();
		}
	}

}
