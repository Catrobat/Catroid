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
package org.catrobat.catroid.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.transfers.ProjectDownloadService;
import org.catrobat.catroid.web.ProgressBufferedOutputStream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadUtil {
	private static DownloadUtil instance;
	private Set<String> programDownloadQueue;

	private static final String TAG = DownloadUtil.class.getSimpleName();
	private static final String PROJECTNAME_TAG = "fname=";

	private DownloadUtil() {
		programDownloadQueue = Collections.synchronizedSet(new HashSet<String>());

	}

	public static DownloadUtil getInstance() {
		if (instance == null) {
			instance = new DownloadUtil();
		}
		return instance;
	}

	public void prepareDownloadAndStartIfPossible(Context context, String url) {
		int projectNameIndex = url.lastIndexOf(PROJECTNAME_TAG) + PROJECTNAME_TAG.length();
		String programName = url.substring(projectNameIndex);
		try {
			programName = URLDecoder.decode(programName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Could not decode program name: " + programName, e);
			return;
		}

		boolean newProgramName = programDownloadQueue.add(programName)
				&& !StorageHandler.getInstance().projectExistsIgnoreCase(programName);
		if (!newProgramName) {
			Log.v(TAG, "Program name exists - show overwrite dialog");
			// FIXME show overwritedialog here
		} else {
			startDownload(context, url, programName);
		}
	}

	public void startDownload(Context context, String url, String programName) {
		Intent downloadIntent = new Intent(context, ProjectDownloadService.class);
		downloadIntent.putExtra(ProjectDownloadService.RECEIVER_TAG, new DownloadReceiver(new Handler()));
		downloadIntent.putExtra(ProjectDownloadService.DOWNLOAD_NAME_TAG, programName);
		downloadIntent.putExtra(ProjectDownloadService.URL_TAG, url);
		StatusBarNotificationManager manager = StatusBarNotificationManager.getInstance();
		int notificationId = manager.createDownloadNotification(context, programName);
		downloadIntent.putExtra(ProjectDownloadService.ID_TAG, notificationId);
		context.startService(downloadIntent);
	}

	public void downloadFinished(String programName) {
		programDownloadQueue.remove(programName);
	}

	private class DownloadReceiver extends ResultReceiver {
		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if (resultCode == Constants.UPDATE_DOWNLOAD_PROGRESS) {
				long progress = resultData.getLong(ProgressBufferedOutputStream.TAG_PROGRESS);
				boolean endOfFileReached = resultData.getBoolean(ProgressBufferedOutputStream.TAG_ENDOFFILE);
				Integer notificationId = resultData.getInt(ProgressBufferedOutputStream.TAG_NOTIFICATION_ID);
				if (endOfFileReached) {
					progress = 100;
				}

				StatusBarNotificationManager.getInstance().updateNotification(notificationId,
						Long.valueOf(progress).intValue(), endOfFileReached);
			}
		}
	}

}
