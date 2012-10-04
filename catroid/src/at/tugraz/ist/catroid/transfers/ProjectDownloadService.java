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

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.ResultReceiver;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.dialogs.OverwriteRenameDialog;
import at.tugraz.ist.catroid.utils.StatusBarNotificationManager;
import at.tugraz.ist.catroid.utils.UtilZip;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.web.ConnectionWrapper;
import at.tugraz.ist.catroid.web.ServerCalls;
import at.tugraz.ist.catroid.web.WebconnectionException;

//public class ProjectDownloadService extends AsyncTask<Void, Long, Boolean> implements OnClickListener {
public class ProjectDownloadService extends IntentService implements OnClickListener {

	private static final String DOWNLOAD_FILE_NAME = "down" + Constants.CATROID_EXTENTION;

	//private MainMenuActivity activity;
	private String projectName;
	private String zipFileString;
	private String url;
	private boolean result, showOverwriteDialog;
	private static ProjectManager projectManager = ProjectManager.getInstance();
	//public Handler progressHandler;
	Notification downloadNotification;
	PendingIntent pendingDownload;
	private Integer notificationId;
	private boolean endOfFileReached;
	private boolean unknown;
	public ResultReceiver receiver;
	private String serverAnswer;

	//MainMenuActivity mainMenuActivity;

	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public ProjectDownloadService() {
		super("ProjectDownloadService");
		//TODO: catch class cast exception
		//mainMenuActivity = (MainMenuActivity) this.getBaseContext();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		int returnCode = super.onStartCommand(intent, flags, startID);
		this.projectName = intent.getStringExtra("downloadName");
		this.zipFileString = Utils.buildPath(Constants.TMP_PATH, DOWNLOAD_FILE_NAME);
		this.url = intent.getStringExtra("url");
		this.notificationId = intent.getIntExtra("notificationId", 0);
		this.endOfFileReached = false;
		this.unknown = false;
		this.serverAnswer = null;

		return returnCode;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
		showOverwriteDialog = false;
		try {
			ServerCalls.getInstance().downloadProject(url, zipFileString, receiver, notificationId, projectName);

			if (StorageHandler.getInstance().projectExistsIgnoreCase(projectName)) {
				showOverwriteDialog = true;
				result = true;
			}

			if (!showOverwriteDialog) {
				result = UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
			}

		} catch (WebconnectionException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (result && showOverwriteDialog) {

			//The context of the calling activity is needed, otherwise an exception occurs
			StatusBarNotificationManager.INSTANCE.downloadProjectName.add(projectName);
			StatusBarNotificationManager.INSTANCE.downloadProjectZipFileString.add(zipFileString);

			try {
				MainMenuActivity activity = StatusBarNotificationManager.getInstance().getActivity(notificationId);
				OverwriteRenameDialog renameDialog = new OverwriteRenameDialog(activity, projectName, zipFileString,
						activity, activity);
				renameDialog.show();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}

			return;
		}

		if (!result) {
			showDialog(R.string.error_project_download);
			return;
		}

		/*
		 * if (activity == null) {
		 * return;
		 * }
		 */

		Toast.makeText(this, R.string.success_project_download, Toast.LENGTH_SHORT).show();
		MainMenuActivity activity = StatusBarNotificationManager.getInstance().getActivity(notificationId);
		if (projectManager.loadProject(projectName, activity, activity, true)) {
			activity.writeProjectTitleInTextfield();
		}
	}

	private void showDialog(int messageId) {
		/*
		 * if (activity == null) {
		 * return;
		 * }
		 */
		//TODO: refactor to use strings.xml
		//new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (!result) {
			//MainMenuActivity mainMenuActivity = (MainMenuActivity) this.getBaseContext();
			//mainMenuActivity.finish();
		}
	}
}

/*
 * public ProjectDownloadService(MainMenuActivity mainMenuActivity, String url, String projectName) {
 * this.activity = mainMenuActivity;
 * this.projectName = projectName;
 * this.zipFileString = Utils.buildPath(Constants.TMP_PATH, DOWNLOAD_FILE_NAME);
 * this.url = url;
 * this.notificationId = 0;
 * this.endOfFileReached = false;
 * this.unknown = false;
 * this.progressHandler = new Handler() {
 * 
 * @Override
 * public void handleMessage(Message message) {
 * Bundle progressBundle = message.getData();
 * long progress = progressBundle.getLong("currentDownloadProgress");
 * endOfFileReached = progressBundle.getBoolean("endOfFileReached");
 * unknown = progressBundle.getBoolean("unknown");
 * publishProgress(progress);
 * }
 * };
 * }
 * 
 * @Override
 * protected void onPreExecute() {
 * super.onPreExecute();
 * if (activity == null) {
 * return;
 * }
 * //String title = activity.getString(R.string.please_wait);
 * //String message = activity.getString(R.string.loading);
 * //progressDialog = ProgressDialog.show(activity, title, message);
 * }
 * 
 * @Override
 * protected Boolean doInBackground(Void... arg0) {
 * showOverwriteDialog = false;
 * try {
 * //createNotification(projectName, projectDescription, projectPath, token);
 * createNotification(projectName);
 * ServerCalls.getInstance().downloadProject(url, zipFileString, progressHandler);
 * 
 * if (StorageHandler.getInstance().projectExistsIgnoreCase(projectName)) {
 * showOverwriteDialog = true;
 * result = true;
 * }
 * 
 * if (!showOverwriteDialog) {
 * result = UtilZip.unZipFile(zipFileString, Utils.buildProjectPath(projectName));
 * }
 * 
 * return result;
 * } catch (WebconnectionException e) {
 * e.printStackTrace();
 * }
 * return false;
 * }
 * 
 * @Override
 * protected void onProgressUpdate(Long... progress) {
 * super.onProgressUpdate(progress);
 * long progressPercent = progress[0];
 * String notificationMessage = "";
 * if (unknown) {
 * notificationMessage = "download progress unknown:" + projectName;
 * } else {
 * notificationMessage = "download " + progressPercent + "% completed:" + projectName;
 * }
 * StatusBarNotificationManager.getInstance().updateNotification(notificationId, notificationMessage);
 * }
 * 
 * @Override
 * protected void onPostExecute(Boolean result) {
 * super.onPostExecute(result);
 * 
 * //if (progressDialog != null && progressDialog.isShowing()) {
 * // progressDialog.dismiss();
 * //}
 * 
 * if (result && showOverwriteDialog) {
 * OverwriteRenameDialog renameDialog = new OverwriteRenameDialog(activity, projectName, zipFileString);
 * renameDialog.show();
 * return;
 * }
 * 
 * if (!result) {
 * showDialog(R.string.error_project_download);
 * return;
 * }
 * 
 * if (activity == null) {
 * return;
 * }
 * Toast.makeText(activity, R.string.success_project_download, Toast.LENGTH_SHORT).show();
 * if (projectManager.loadProject(projectName, activity, activity, true)) {
 * activity.writeProjectTitleInTextfield();
 * }
 * }
 * 
 * private void showDialog(int messageId) {
 * if (activity == null) {
 * return;
 * }
 * //TODO: refactor to use strings.xml
 * new Builder(activity).setMessage(messageId).setPositiveButton("OK", null).show();
 * }
 * 
 * @Override
 * public void onClick(DialogInterface dialog, int which) {
 * if (!result) {
 * activity.finish();
 * }
 * }
 * 
 * public void createNotification(String uploadName) {
 * notificationId = StatusBarNotificationManager.getInstance().createNotification(uploadName, activity,
 * ProjectDownloadService.class);
 * }
 * 
 * }
 */