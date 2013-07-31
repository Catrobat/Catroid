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
package org.catrobat.catroid.transfers;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameDialog;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ConnectionWrapper;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.ResultReceiver;
import android.widget.Toast;

public class ProjectDownloadService extends IntentService {

	private static final String DOWNLOAD_FILE_NAME = "down" + Constants.CATROBAT_EXTENTION;

	private String projectName;
	private String zipFileString;
	private String url;
	private boolean result, showOverwriteDialog;
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

		if (result && showOverwriteDialog) {
			//project name and zip file string are temporariliy saved in the StatusBarNotificationManager to create it later on in the right context  
			StatusBarNotificationManager.getInstance().downloadProjectName.add(projectName);
			StatusBarNotificationManager.getInstance().downloadProjectZipFileString.add(zipFileString);
			try {
				//The context of the calling activity is needed, otherwise an exception occurs
				MainMenuActivity activity = StatusBarNotificationManager.getInstance().getActivity(notificationId);
				OverwriteRenameDialog renameDialog = new OverwriteRenameDialog(activity, projectName, zipFileString);
				renameDialog.show(activity.getSupportFragmentManager(), OverwriteRenameDialog.DIALOG_FRAGMENT_TAG);
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
		super.onDestroy();
	}

	private void showDialog(int messageId) {
		Toast.makeText(this, messageId, Toast.LENGTH_SHORT).show();
	}
}
