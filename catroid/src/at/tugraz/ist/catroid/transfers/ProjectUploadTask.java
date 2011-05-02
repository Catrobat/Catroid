/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.transfers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.AsyncTask;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.WebconnectionException;

public class ProjectUploadTask extends AsyncTask<Void, Void, Boolean> {

	public static boolean useTestUrl = false;

	private Context context;
	private String projectPath;
	private ProgressDialog progressdialog;
	private String projectName;
	private String projectDescription;
	private String resultString;
	private String serverAnswer;

	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public ProjectUploadTask(Context context, String projectName, String projectDescription, String projectPath) {
		this.context = context;
		this.projectPath = projectPath;
		this.projectName = projectName;
		this.projectDescription = projectDescription;
		serverAnswer = context.getString(R.string.error_project_upload);
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
			File dirPath = new File(projectPath);
			String[] paths = dirPath.list();

			if (paths == null) {
				return false;
			}

			for (int i = 0; i < paths.length; i++) {
				paths[i] = dirPath + "/" + paths[i];
			}

			String zipFileString = Consts.TMP_PATH + "/upload.zip";
			File file = new File(zipFileString);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				file.delete();
				return false;
			}

			HashMap<String, String> hm = buildPostValues(file);

			String serverUrl = useTestUrl ? Consts.TEST_FILE_UPLOAD_URL : Consts.FILE_UPLOAD_URL;

			resultString = createConnection()
					.doHttpPostFileUpload(serverUrl, hm, Consts.FILE_UPLOAD_TAG, zipFileString);

			JSONObject jsonObject = null;
			int statusCode = 0;

			try {
				jsonObject = new JSONObject(resultString);
				statusCode = jsonObject.getInt("statusCode");
				serverAnswer = jsonObject.getString("answer");
				int serverProjectId = jsonObject.getInt("projectId");
				ProjectManager.getInstance().setServerProjectId(serverProjectId);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (statusCode == 200) {
				file.delete();
				return true;
			} else if (statusCode >= 500) {
				return false;
			}

			return false;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (WebconnectionException e) {
			e.printStackTrace();
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

		//		showDialog(serverAnswer);
		showDialog(context.getString(R.string.success_project_upload));

	}

	private void showDialog(String message) {
		if (context == null) {
			return;
		}
		new Builder(context)
				.setMessage(message)
				.setPositiveButton("OK", null)
				.show();
	}

	private HashMap<String, String> buildPostValues(File zipFile) throws IOException {
		String md5Checksum = StorageHandler.getInstance().getMD5Checksum(zipFile);

		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Consts.PROJECT_NAME_TAG, projectName);
		hm.put(Consts.PROJECT_DESCRIPTION_TAG, projectDescription);
		hm.put(Consts.PROJECT_CHECKSUM_TAG, md5Checksum);

		String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
		if (deviceIMEI != null) {
			hm.put(Consts.DEVICE_IMEI, deviceIMEI);
		}

		String userEmail = UtilDeviceInfo.getUserEmail(context);
		if (userEmail != null) {
			hm.put(Consts.USER_EMAIL, userEmail);
		}

		String language = UtilDeviceInfo.getUserLanguageCode(context);
		if (language != null) {
			hm.put(Consts.USER_LANGUAGE, language);
		}

		return hm;
	}

	public String getResultString() {
		return resultString;
	}

}
