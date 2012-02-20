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

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class ProjectUploadTask extends AsyncTask<Void, Void, Boolean> {
	//private final static String TAG = ProjectUploadTask.class.getSimpleName();

	private Context context;
	private String projectPath;
	private ProgressDialog progressdialog;
	private String projectName;
	private String projectDescription;
	private String serverAnswer;
	private String token;
	private static final String UPLOAD_FILE_NAME = "upload" + Consts.CATROID_EXTENTION;

	public ProjectUploadTask(Context context, String projectName, String projectDescription, String projectPath,
			String token) {
		this.context = context;
		this.projectPath = projectPath;
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		this.token = token;

		if (context != null) {
			serverAnswer = context.getString(R.string.error_project_upload);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context == null) {
			return;
		}
		String title = context.getString(R.string.please_wait);
		String message = context.getString(R.string.loading);
		progressdialog = ProgressDialog.show(context, title, message);
	}

	@Override
	protected Boolean doInBackground(Void... arg0) {
		try {
			File directoryPath = new File(projectPath);
			String[] paths = directoryPath.list();

			if (paths == null) {
				return false;
			}

			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(directoryPath.getAbsolutePath(), paths[i]);
			}

			String zipFileString = Utils.buildPath(Consts.TMP_PATH, UPLOAD_FILE_NAME);
			File zipFile = new File(zipFileString);
			if (!zipFile.exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
				return false;
			}

			//String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
			String userEmail = UtilDeviceInfo.getUserEmail(context);
			String language = UtilDeviceInfo.getUserLanguageCode(context);

			ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString, userEmail,
					language, token);
			zipFile.delete();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WebconnectionException webException) {
			serverAnswer = webException.getMessage();
		}

		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (progressdialog != null && progressdialog.isShowing()) {
			progressdialog.dismiss();
		}

		if (!result) {
			showDialog(serverAnswer);
			return;
		}

		showDialog(context.getString(R.string.success_project_upload));
	}

	private void showDialog(String message) {
		if (context == null) {
			return;
		}
		//TODO: Refactor to use stings.xml
		new Builder(context).setMessage(message).setPositiveButton("OK", null).show();
	}

}
