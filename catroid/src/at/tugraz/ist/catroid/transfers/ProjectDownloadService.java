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

public class ProjectDownloadService extends IntentService {

	private static final String DOWNLOAD_FILE_NAME = "down" + Constants.CATROID_EXTENTION;

	private String projectName;
	private String zipFileString;
	private String url;
	private boolean result, showOverwriteDialog;
	private static ProjectManager projectManager = ProjectManager.getInstance();
	Notification downloadNotification;
	PendingIntent pendingDownload;
	private Integer notificationId;
	public ResultReceiver receiver;

	// mock object testing
	protected ConnectionWrapper createConnection() {
		return new ConnectionWrapper();
	}

	public ProjectDownloadService() {
		super("ProjectDownloadService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		int returnCode = super.onStartCommand(intent, flags, startID);
		this.projectName = intent.getStringExtra("downloadName");
		this.zipFileString = Utils.buildPath(Constants.TMP_PATH, DOWNLOAD_FILE_NAME);
		this.url = intent.getStringExtra("url");
		this.notificationId = intent.getIntExtra("notificationId", 0);

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
			StatusBarNotificationManager.INSTANCE.downloadProjectName.add(projectName);
			StatusBarNotificationManager.INSTANCE.downloadProjectZipFileString.add(zipFileString);
			try {
				//The context of the calling activity is needed, otherwise an exception occurs
				MainMenuActivity activity = StatusBarNotificationManager.INSTANCE.getActivity(notificationId);
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

		Toast.makeText(this, R.string.success_project_download, Toast.LENGTH_SHORT).show();
		MainMenuActivity activity = StatusBarNotificationManager.INSTANCE.getActivity(notificationId);
		if (projectManager.loadProject(projectName, activity, activity, true)) {
			activity.writeProjectTitleInTextfield();
		}
	}

	private void showDialog(int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}
}
