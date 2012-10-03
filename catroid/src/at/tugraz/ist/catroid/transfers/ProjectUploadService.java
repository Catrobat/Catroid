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

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.utils.UtilDeviceInfo;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

//public class ProjectUploadTask extends AsyncTask<Void, Long, Boolean> {
public class ProjectUploadService extends IntentService {

	//private final static String TAG = ProjectUploadTask.class.getSimpleName();

	private static final String UPLOAD_FILE_NAME = "upload" + Constants.CATROID_EXTENTION;

	//private Activity uploadActivity;
	private String projectPath;
	private String projectName;
	private String projectDescription;
	private String serverAnswer;
	private String token;
	public ResultReceiver receiver;
	private Integer notificationId;
	private boolean endOfFileReached;

	public ProjectUploadService() {
		super("ProjectUploadService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		int returnCode = super.onStartCommand(intent, flags, startID);
		this.projectPath = intent.getStringExtra("projectPath");
		this.projectName = intent.getStringExtra("uploadName");
		this.projectDescription = intent.getStringExtra("projectDescription");
		this.token = intent.getStringExtra("token");

		this.notificationId = intent.getIntExtra("notificationId", 0);
		this.endOfFileReached = false;

		return returnCode;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		try {
			File directoryPath = new File(projectPath);
			String[] paths = directoryPath.list();

			if (paths == null) {
				return;
			}

			for (int i = 0; i < paths.length; i++) {
				paths[i] = Utils.buildPath(directoryPath.getAbsolutePath(), paths[i]);
			}

			String zipFileString = Utils.buildPath(Constants.TMP_PATH, UPLOAD_FILE_NAME);
			File zipFile = new File(zipFileString);
			if (!zipFile.exists()) {
				zipFile.getParentFile().mkdirs();
				zipFile.createNewFile();
			}
			if (!UtilZip.writeToZipFile(paths, zipFileString)) {
				zipFile.delete();
				return;
			}

			//String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
			String userEmail = UtilDeviceInfo.getUserEmail(this);
			String language = UtilDeviceInfo.getUserLanguageCode(this);

			ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString, userEmail,
					language, token, receiver, notificationId);

			zipFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WebconnectionException webException) {
			serverAnswer = webException.getMessage();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		/*
		 * if (!result) {
		 * showDialog(serverAnswer);
		 * return;
		 * }
		 */
		showDialog(getString(R.string.success_project_upload));
	}

	private void showDialog(String message) {
		//new Builder(this).setMessage(message).setPositiveButton(getString(R.string.ok), null).show();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
/*
 * public ProjectUploadService(Activity uploadActivity, String projectName, String projectDescription,
 * String projectPath, String token) {
 * super("ProjectUploadService");
 * this.uploadActivity = uploadActivity;
 * this.projectPath = projectPath;
 * this.projectName = projectName;
 * this.projectDescription = projectDescription;
 * this.token = token;
 * this.notificationId = 0;
 * this.endOfFileReached = false;
 * 
 * this.receiver = new ResultReceiver() {
 * 
 * @Override
 * public void handleMessage(Message message) {
 * Bundle progressBundle = message.getData();
 * long progress = progressBundle.getLong("currentUploadProgress");
 * endOfFileReached = progressBundle.getBoolean("endOfFileReached");
 * publishProgress(progress);
 * }
 * };
 * 
 * if (uploadActivity != null) {
 * serverAnswer = uploadActivity.getString(R.string.error_project_upload);
 * }
 * }
 */

/*
 * @Override
 * protected void onPreExecute() {
 * super.onPreExecute();
 * if (uploadActivity == null) {
 * return;
 * }
 * //String title = context.getString(R.string.please_wait);
 * //String message = context.getString(R.string.loading);
 * //progressdialog = ProgressDialog.show(context, title, message);
 * }
 * 
 * @Override
 * protected Boolean doInBackground(Void... arg0) {
 * try {
 * File directoryPath = new File(projectPath);
 * String[] paths = directoryPath.list();
 * 
 * if (paths == null) {
 * return false;
 * }
 * 
 * for (int i = 0; i < paths.length; i++) {
 * paths[i] = Utils.buildPath(directoryPath.getAbsolutePath(), paths[i]);
 * }
 * 
 * String zipFileString = Utils.buildPath(Constants.TMP_PATH, UPLOAD_FILE_NAME);
 * File zipFile = new File(zipFileString);
 * if (!zipFile.exists()) {
 * zipFile.getParentFile().mkdirs();
 * zipFile.createNewFile();
 * }
 * if (!UtilZip.writeToZipFile(paths, zipFileString)) {
 * zipFile.delete();
 * return false;
 * }
 * 
 * //String deviceIMEI = UtilDeviceInfo.getDeviceIMEI(context);
 * String userEmail = UtilDeviceInfo.getUserEmail(uploadActivity);
 * String language = UtilDeviceInfo.getUserLanguageCode(uploadActivity);
 * 
 * createNotification(projectName);
 * ServerCalls.getInstance().uploadProject(projectName, projectDescription, zipFileString, userEmail,
 * language, token, progressHandler);
 * 
 * zipFile.delete();
 * return true;
 * } catch (IOException e) {
 * e.printStackTrace();
 * } catch (WebconnectionException webException) {
 * serverAnswer = webException.getMessage();
 * }
 * 
 * return false;
 * }
 * 
 * @Override
 * protected void onProgressUpdate(Long... progress) {
 * super.onProgressUpdate(progress);
 * long progressPercent = 0;
 * if (endOfFileReached) {
 * progressPercent = 100;
 * } else {
 * progressPercent = ProjectManager.INSTANCE.getProgressFromBytes(projectName, progress[0]);
 * }
 * String notificationMessage = "upload " + progressPercent + "% completed:" + projectName;
 * StatusBarNotificationManager.getInstance().updateNotification(notificationId, notificationMessage);
 * }
 * 
 * @Override
 * protected void onPostExecute(Boolean result) {
 * super.onPostExecute(result);
 * //if (progressdialog != null && progressdialog.isShowing()) {
 * // progressdialog.dismiss();
 * //}
 * 
 * if (!result) {
 * showDialog(serverAnswer);
 * return;
 * }
 * 
 * showDialog(uploadActivity.getString(R.string.success_project_upload));
 * }
 * 
 * private void showDialog(String message) {
 * if (uploadActivity == null) {
 * return;
 * }
 * new Builder(uploadActivity).setMessage(message).setPositiveButton(uploadActivity.getString(R.string.ok), null)
 * .show();
 * }
 */
