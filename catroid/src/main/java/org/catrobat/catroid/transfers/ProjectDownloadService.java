/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.content.Intent;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.io.ZipArchiver;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.File;
import java.io.IOException;

import static org.catrobat.catroid.common.Constants.CACHE_DIR;
import static org.catrobat.catroid.common.Constants.CODE_XML_FILE_NAME;
import static org.catrobat.catroid.common.Constants.TMP_DIR_NAME;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;

public class ProjectDownloadService extends IntentService {

	public static final String TAG = ProjectDownloadService.class.getSimpleName();

	public static final String RECEIVER_TAG = "receiver";
	public static final String DOWNLOAD_NAME_TAG = "downloadName";
	public static final String URL_TAG = "url";
	public static final String ID_TAG = "notificationId";
	public static final String RENAME_AFTER_DOWNLOAD = "renameAfterDownload";

	private static final String DOWNLOAD_FILE_NAME = "down" + Constants.CATROBAT_EXTENSION;

	public ResultReceiver receiver;
	private Handler handler;

	public ProjectDownloadService() {
		super(ProjectDownloadService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String projectName = intent.getStringExtra(DOWNLOAD_NAME_TAG);
		String zipFileString = new File(new File(CACHE_DIR, TMP_DIR_NAME), DOWNLOAD_FILE_NAME).getAbsolutePath();
		String url = intent.getStringExtra(URL_TAG);
		int notificationId = intent.getIntExtra(ID_TAG, -1);

		receiver = intent.getParcelableExtra(RECEIVER_TAG);

		try {
			// TODO begin
			File destinationFile = new File(zipFileString);
			if (!(destinationFile.getParentFile().mkdirs() || destinationFile.getParentFile().isDirectory())) {
				throw new IOException("Directory not created");
			}
			// TODO end

			new ServerCalls(CatrobatWebClient.INSTANCE.getClient()).downloadProject(url, destinationFile, projectName, receiver, notificationId);

			boolean renameProject = intent.getBooleanExtra(RENAME_AFTER_DOWNLOAD, false);

			File projectDir = new File(DEFAULT_ROOT_DIRECTORY, projectName);
			new ZipArchiver().unzip(new File(zipFileString), projectDir);

			if (renameProject) {
				XstreamSerializer
						.renameProject(new File(projectDir, CODE_XML_FILE_NAME), projectName);
			}
		} catch (IOException | WebconnectionException e) {
			showToast(R.string.error_project_download, true);
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			DownloadUtil.getInstance().downloadFinished(projectName, url);
		}

		showToast(R.string.notification_download_finished, false);
	}

	private void showToast(final int messageId, boolean error) {
		if (error) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					ToastUtil.showError(getBaseContext(), messageId);
				}
			});
		} else {
			handler.post(new Runnable() {

				@Override
				public void run() {
					ToastUtil.showSuccess(getBaseContext(), messageId);
				}
			});
		}
	}
}
