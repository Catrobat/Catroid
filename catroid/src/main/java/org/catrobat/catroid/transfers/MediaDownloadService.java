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
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ServerCalls;
import org.catrobat.catroid.web.WebconnectionException;

import java.io.IOException;

public class MediaDownloadService extends IntentService {

	public static final String TAG = MediaDownloadService.class.getSimpleName();

	public static final String RECEIVER_TAG = "receiver";
	public static final String URL_TAG = "url";
	public static final String MEDIA_FILE_PATH = "path";

	public ResultReceiver receiver;
	private Handler handler;

	public MediaDownloadService() {
		super(MediaDownloadService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler = new Handler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		boolean result = true;
		String url = intent.getStringExtra(URL_TAG);
		String fileString = intent.getStringExtra(MEDIA_FILE_PATH);
		int errorMessage = R.string.error_unknown_error;

		receiver = intent.getParcelableExtra(RECEIVER_TAG);
		try {
			new ServerCalls(CatrobatWebClient.INSTANCE.getClient()).downloadMedia(url, fileString, receiver);
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
			result = false;
			receiver.send(Constants.UPDATE_DOWNLOAD_ERROR, null);
		} catch (WebconnectionException webconnectionException) {
			Log.e(TAG, Log.getStackTraceString(webconnectionException));
			result = false;
			errorMessage = R.string.error_internet_connection;
			receiver.send(Constants.UPDATE_DOWNLOAD_ERROR, null);
		}

		if (!result) {
			showToast(errorMessage, true);
			return;
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
