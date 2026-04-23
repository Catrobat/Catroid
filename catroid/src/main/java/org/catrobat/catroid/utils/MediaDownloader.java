/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.utils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.MediaDownloadService;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.web.ProgressResponseBody;

import java.lang.ref.WeakReference;

public final class MediaDownloader {
	private WeakReference<WebViewActivity> webViewActivityWeakReference;

	public MediaDownloader(WebViewActivity webViewActivity) {
		webViewActivityWeakReference = new WeakReference<>(webViewActivity);
	}

	public void startDownload(WebViewActivity activity, String url, String mediaName, String filePath) {
		WebViewActivity webViewActivity = webViewActivityWeakReference.get();
		if (mediaName == null || webViewActivity == null) {
			return;
		}

		Intent downloadIntent = new Intent(activity, MediaDownloadService.class);
		downloadIntent.putExtra(MediaDownloadService.RECEIVER_TAG, new DownloadMediaReceiver(new Handler()));
		downloadIntent.putExtra(MediaDownloadService.URL_TAG, url);
		downloadIntent.putExtra(MediaDownloadService.MEDIA_FILE_PATH, filePath);
		webViewActivity.createProgressDialog(mediaName);
		webViewActivity.setResultIntent(webViewActivity.getResultIntent()
				.putExtra(WebViewActivity.MEDIA_FILE_PATH, filePath));
		activity.startService(downloadIntent);
	}

	@SuppressLint("ParcelCreator")
	private class DownloadMediaReceiver extends ResultReceiver {
		DownloadMediaReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			WebViewActivity webViewActivity = webViewActivityWeakReference.get();
			if (webViewActivity == null) {
				return;
			}

			if (resultCode == Constants.UPDATE_DOWNLOAD_PROGRESS) {
				long progress = resultData.getLong(ProgressResponseBody.TAG_PROGRESS);
				boolean endOfFileReached = resultData.getBoolean(ProgressResponseBody.TAG_ENDOFFILE);
				if (endOfFileReached) {
					progress = 100;
				}

				webViewActivity.updateProgressDialog(progress);
			} else if (resultCode == Constants.UPDATE_DOWNLOAD_ERROR) {
				webViewActivity.dismissProgressDialog();
			}
		}
	}
}
