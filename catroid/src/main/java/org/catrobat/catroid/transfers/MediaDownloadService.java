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
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.catrobat.catroid.utils.StatusBarNotificationManager;
import org.catrobat.catroid.web.ProgressResponseBody;
import org.catrobat.catroid.web.ServerCalls;

import static org.catrobat.catroid.common.Constants.UPDATE_DOWNLOAD_ERROR;
import static org.catrobat.catroid.common.Constants.UPDATE_DOWNLOAD_SUCCESS;

public class MediaDownloadService extends IntentService {

	public static final String TAG = MediaDownloadService.class.getSimpleName();

	public static final String URL_TAG = "url";
	public static final String MEDIA_FILE_PATH = "path";
	public static final String PROGRAM_NAME = "programname";
	public static final String RECEIVER = "receiver";

	public MediaDownloadService() {
		super(MediaDownloadService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String url = intent.getStringExtra(URL_TAG);
		String fileString = intent.getStringExtra(MEDIA_FILE_PATH);
		String programName = intent.getStringExtra(PROGRAM_NAME);
		final ResultReceiver receiver = intent.getParcelableExtra(RECEIVER);
		StatusBarNotificationManager manager = StatusBarNotificationManager.getInstance();
		final int notificationId = manager.createDownloadNotification(getApplicationContext(), programName);
		startForeground(notificationId,
				StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId, 0));
		ServerCalls.getInstance().downloadMedia(url, fileString, receiver, notificationId,
				new ServerCalls.DownloadCallback() {
					@Override
					public void onSuccess() {
						Bundle bundle = new Bundle();
						bundle.putLong(ProgressResponseBody.TAG_PROGRESS, 100);
						receiver.send(UPDATE_DOWNLOAD_SUCCESS, bundle);
					}

					@Override
					public void onError(int statusCode, String errorMessage) {
						Log.e(TAG, "DownloadMedia failed with StatusCode " + Integer.toString(statusCode) + errorMessage);
						StatusBarNotificationManager.getInstance().cancelNotification(notificationId);
						receiver.send(UPDATE_DOWNLOAD_ERROR, null);
					}
				});
	}
}
