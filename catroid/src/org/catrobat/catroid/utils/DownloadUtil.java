/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.MediaDownloadService;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.ui.WebViewActivity;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameDialog;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameMediaDialog;
import org.catrobat.catroid.ui.fragment.LookFragment;
import org.catrobat.catroid.ui.fragment.SoundFragment;
import org.catrobat.catroid.web.ProgressResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class DownloadUtil {
	private static final DownloadUtil INSTANCE = new DownloadUtil();
	private static final String TAG = DownloadUtil.class.getSimpleName();
	private static final String FILENAME_TAG = "fname=";

	private Set<String> programDownloadQueue;

	private WebViewActivity webViewActivity = null;

	private DownloadUtil() {
		programDownloadQueue = Collections.synchronizedSet(new HashSet<String>());
	}

	public static DownloadUtil getInstance() {
		return INSTANCE;
	}

	public void prepareDownloadAndStartIfPossible(FragmentActivity activity, String url) {
		String programName = getProjectNameFromUrl(url);
		if (programName == null) {
			return;
		}

		boolean programNameExists = Utils.checkIfProjectExistsOrIsDownloadingIgnoreCase(programName);
		if (programNameExists) {
			Log.v(TAG, "Program name exists - show overwrite dialog");
			OverwriteRenameDialog renameDialog = new OverwriteRenameDialog();

			renameDialog.setContext(activity);
			renameDialog.setProgramName(programName);
			renameDialog.setURL(url);

			renameDialog.show(activity.getSupportFragmentManager(), OverwriteRenameDialog.DIALOG_FRAGMENT_TAG);
		} else {
			startDownload(activity, url, programName);
		}
	}

	public void prepareMediaDownloadAndStartIfPossible(FragmentActivity activity, String url, String mediaType,
			String mediaName, String filePath, String callingActivity) {
		if (mediaName == null) {
			return;
		}

		boolean mediaNameExists;
		if (callingActivity.contains(LookFragment.TAG) || callingActivity.contains(SoundFragment.TAG)) {
			switch (mediaType) {
				case Constants.MEDIA_TYPE_LOOK:
					mediaNameExists = Utils.checkIfLookExists(mediaName);
					break;
				case Constants.MEDIA_TYPE_SOUND:
					mediaNameExists = Utils.checkIfSoundExists(mediaName);
					break;
				default:
					mediaNameExists = false;
			}
		} else {
			mediaNameExists = false;
		}

		webViewActivity = (WebViewActivity) activity;

		if (mediaNameExists) {
			OverwriteRenameMediaDialog renameMediaDialog = new OverwriteRenameMediaDialog();

			renameMediaDialog.setContext(activity);
			renameMediaDialog.setMediaName(mediaName);
			renameMediaDialog.setMediaType(mediaType);
			renameMediaDialog.setURL(url);
			renameMediaDialog.setFilePath(filePath);
			renameMediaDialog.setCallingActivity(callingActivity);
			renameMediaDialog.setWebViewActivity(webViewActivity);

			renameMediaDialog.show(activity.getSupportFragmentManager(), OverwriteRenameMediaDialog.DIALOG_FRAGMENT_TAG);
		} else {
			startMediaDownload(activity, url, mediaName, filePath);
		}
	}

	public void startMediaDownload(Context context, String url, String mediaName, String filePath) {
		Intent downloadIntent = new Intent(context, MediaDownloadService.class);
		downloadIntent.putExtra(MediaDownloadService.RECEIVER_TAG, new DownloadMediaReceiver(new Handler()));
		downloadIntent.putExtra(MediaDownloadService.URL_TAG, url);
		downloadIntent.putExtra(MediaDownloadService.MEDIA_FILE_PATH, filePath);
		webViewActivity.createProgressDialog(mediaName);
		webViewActivity.setResultIntent(webViewActivity.getResultIntent().putExtra(WebViewActivity
				.MEDIA_FILE_PATH, filePath));
		context.startService(downloadIntent);
	}

	public void startDownload(Context context, String url, String programName) {
		programDownloadQueue.add(programName.toLowerCase(Locale.getDefault()));
		Intent downloadIntent = new Intent(context, ProjectDownloadService.class);
		downloadIntent.putExtra(ProjectDownloadService.RECEIVER_TAG, new DownloadProjectReceiver(new Handler()));
		downloadIntent.putExtra(ProjectDownloadService.DOWNLOAD_NAME_TAG, programName);
		downloadIntent.putExtra(ProjectDownloadService.URL_TAG, url);
		StatusBarNotificationManager manager = StatusBarNotificationManager.getInstance();
		int notificationId = manager.createDownloadNotification(context, programName);
		downloadIntent.putExtra(ProjectDownloadService.ID_TAG, notificationId);
		context.startService(downloadIntent);
	}

	public void downloadFinished(String programName) {
		programDownloadQueue.remove(programName.toLowerCase(Locale.getDefault()));
	}

	public boolean isProgramNameInDownloadQueueIgnoreCase(String programName) {
		return programDownloadQueue.contains(programName.toLowerCase(Locale.getDefault()));
	}

	ArrayList<Integer> notificationIdArray = new ArrayList<Integer>();

	private class DownloadProjectReceiver extends ResultReceiver {
		public DownloadProjectReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			Integer notificationId = resultData.getInt(ProgressResponseBody.TAG_NOTIFICATION_ID);
			if (!notificationIdArray.contains(notificationId)) {
				notificationIdArray.add(notificationId);
			}
			if (notificationIdArray.size() - 1 == notificationId) {
				super.onReceiveResult(resultCode, resultData);
				if (resultCode == Constants.UPDATE_DOWNLOAD_PROGRESS) {
					long progress = resultData.getLong(ProgressResponseBody.TAG_PROGRESS);
					boolean endOfFileReached = resultData.getBoolean(ProgressResponseBody.TAG_ENDOFFILE);
					if (endOfFileReached) {
						progress = 100;
					}
					StatusBarNotificationManager.getInstance().showOrUpdateNotification(notificationId,
							Long.valueOf(progress).intValue());
				}
			}
		}
	}

	private class DownloadMediaReceiver extends ResultReceiver {
		public DownloadMediaReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
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

	public String getProjectNameFromUrl(String url) {
		int projectNameIndex = url.lastIndexOf(FILENAME_TAG) + FILENAME_TAG.length();
		String programName = url.substring(projectNameIndex);
		try {
			programName = URLDecoder.decode(programName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Could not decode program name: " + programName, e);
			return null;
		}
		return programName;
	}
}
