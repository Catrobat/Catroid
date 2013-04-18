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

import java.io.File;
import java.io.IOException;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.utils.UtilDeviceInfo;
import org.catrobat.catroid.utils.UtilZip;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

public class ProjectUploadService extends IntentService {

	private final static String TAG = ProjectUploadService.class.getSimpleName();
	private static final String UPLOAD_FILE_NAME = "upload" + Constants.CATROID_EXTENTION;

	private String projectPath;
	private String projectName;
	private String projectDescription;
	private String token;
	private String serverAnswer;
	private boolean result;
	public ResultReceiver receiver;
	private Integer notificationId;
	private String username;

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
		this.username = intent.getStringExtra("username");
		this.serverAnswer = "";
		this.result = true;
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
					language, token, username, receiver, notificationId);

			zipFile.delete();
		} catch (IOException e) {
			e.printStackTrace();

		} catch (WebconnectionException webException) {
			serverAnswer = webException.getMessage();
			Log.e(TAG, serverAnswer);
			result = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!result) {
			showDialog(getString(R.string.error_project_upload));
			return;
		}
		showDialog(getString(R.string.success_project_upload));
	}

	private void showDialog(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

}
