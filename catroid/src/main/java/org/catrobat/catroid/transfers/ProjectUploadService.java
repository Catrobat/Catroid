/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
package org.catrobat.catroid.transfers;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.DeviceSettingsProvider;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;

public class ProjectUploadService extends IntentService {

	private static final String TAG = ProjectUploadService.class.getSimpleName();
	public static final String UPLOAD_FILE_NAME = "upload" + Constants.CATROBAT_EXTENSION;

	private String projectPath;
	private String projectName;
	private String projectDescription;
	private String[] sceneNames;
	private String token;
	private String provider;
	private String serverAnswer;
	private boolean result;
	private Integer notificationId;
	private String username;
	private int statusCode;
	private Bundle uploadBackupBundle;

	public ProjectUploadService() {
		super("ProjectUploadService");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		int returnCode = super.onStartCommand(intent, flags, startID);
		this.projectPath = intent.getStringExtra("projectPath");
		this.projectName = intent.getStringExtra("uploadName");
		this.projectDescription = intent.getStringExtra("projectDescription");
		this.sceneNames = intent.getStringArrayExtra("sceneNames");
		this.token = intent.getStringExtra("token");
		this.username = intent.getStringExtra("username");
		this.provider = intent.getStringExtra("provider");
		this.serverAnswer = "";
		this.result = true;
		this.notificationId = intent.getIntExtra("notificationId", 0);
		this.uploadBackupBundle = new Bundle();

		return returnCode;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		XstreamSerializer.getInstance().saveProject(ProjectManager.getInstance().getCurrentProject());

		try {
			if (projectPath == null) {
				result = false;
				Log.e(TAG, "project path is null");
				return;
			}

			File projectDir = new File(projectPath);

			if (projectDir.listFiles().length == 0) {
				result = false;
				return;
			}

			ProjectAndSceneScreenshotLoader screenshotLoader = new ProjectAndSceneScreenshotLoader(getApplicationContext());
			for (String scene : sceneNames) {
				File screenshotFile = screenshotLoader.getScreenshotFile(projectName, scene, false);
				if (screenshotFile.exists() && screenshotFile.length() > 0) {
					try {
						ImageEditing.scaleImageFile(screenshotFile, 480, 480);
					} catch (FileNotFoundException ex) {
						Log.e(TAG, Log.getStackTraceString(ex));
					}
				}
			}

			File archive = new File(CACHE_DIR, UPLOAD_FILE_NAME);
			new ZipArchiver().zip(archive, projectDir.listFiles());

			Context context = getApplicationContext();

			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
			String userEmail = sharedPreferences.getString(Constants.EMAIL, Constants.NO_EMAIL);

			switch (provider) {
				case Constants.FACEBOOK:
					userEmail = sharedPreferences.getString(Constants.FACEBOOK_EMAIL, Constants.NO_FACEBOOK_EMAIL);
					break;
				case Constants.GOOGLE_PLUS:
					userEmail = sharedPreferences.getString(Constants.GOOGLE_EMAIL, Constants.NO_GOOGLE_EMAIL);
					break;
				case Constants.NO_OAUTH_PROVIDER:
					userEmail = sharedPreferences.getString(Constants.EMAIL, Constants.NO_EMAIL);
					break;
			}

			if (userEmail.equals(Constants.NO_EMAIL)) {
				userEmail = DeviceSettingsProvider.getUserEmail(this);
			}

			String language = Locale.getDefault().getLanguage();

			uploadBackupBundle.putString("projectName", projectName);
			uploadBackupBundle.putString("projectDescription", projectDescription);
			uploadBackupBundle.putString("projectPath", projectPath);
			uploadBackupBundle.putStringArray("sceneNames", sceneNames);
			uploadBackupBundle.putString("userEmail", userEmail);
			uploadBackupBundle.putString("language", language);
			uploadBackupBundle.putString("token", token);
			uploadBackupBundle.putString("username", username);
			uploadBackupBundle.putInt("notificationId", notificationId);

			ServerCalls.getInstance().uploadProject(projectName, projectDescription, archive.getAbsolutePath(), userEmail,
					language, token, username, notificationId, context);

			archive.delete();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			result = false;
		} catch (WebconnectionException webconnectionException) {
			serverAnswer = webconnectionException.getMessage();
			statusCode = webconnectionException.getStatusCode();
			Log.e(TAG, serverAnswer);
			result = false;
		}
	}

	@Override
	public void onDestroy() {
		if (!result) {
			ToastUtil.showError(this, getResources().getText(R.string.error_project_upload).toString() + " " + serverAnswer);
			StatusBarNotificationManager.getInstance().showUploadRejectedNotification(notificationId, statusCode, serverAnswer, uploadBackupBundle);
		} else {
			ToastUtil.showSuccess(this, R.string.notification_upload_finished);
		}

		Utils.invalidateLoginTokenIfUserRestricted(getApplicationContext());

		super.onDestroy();
	}
}
