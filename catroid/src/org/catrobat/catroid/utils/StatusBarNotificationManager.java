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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameDialog;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class StatusBarNotificationManager {

	private static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private Integer notificationId;
	private HashMap<Integer, NotificationData> notificationDataMap;

	//needed when download service is running in background
	@Deprecated
	public ArrayList<String> downloadProjectName;
	@Deprecated
	public ArrayList<String> downloadProjectZipFileString;

	NotificationManager notificationManager;

	public static final String EXTRA_PROJECT_NAME = "projectName";

	@SuppressLint("UseSparseArrays")
	private StatusBarNotificationManager() {
		notificationId = 0;
		notificationDataMap = new HashMap<Integer, NotificationData>();

		downloadProjectName = new ArrayList<String>();
		downloadProjectZipFileString = new ArrayList<String>();
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	public Context getContext(int id) {
		return notificationDataMap.get(id).getContext();
	}

	private void initNotificationManager(Context context) {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
	}

	public Integer createUploadNotification(String name, Context context) {
		//		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		//		String notificationTitle = context.getString(R.string.notification_upload_title);
		//		boolean newUploadNotification = notificationDataMap.isEmpty();
		//

		//
		//		if (newUploadNotification) {
		//			uploadNotification = new Notification(R.drawable.ic_stat_upload_notification, notificationTitle,
		//					System.currentTimeMillis());
		//			uploadNotification.flags = Notification.FLAG_AUTO_CANCEL;
		//			uploadNotification.number += 1;
		//			uploadNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
		//			notificationManager.notify(notificationCode, uploadNotification);
		//		} else {
		//			uploadNotification.number += 1;
		//			notificationManager.notify(notificationCode, uploadNotification);
		//		}
		//
		//		return uploadId;

		//		initNotificationManager(context);
		//
		//		Intent intent = new Intent(context, MainMenuActivity.class);
		//		intent.setAction(Intent.ACTION_MAIN);
		//		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		//
		//		NotificationData data = new NotificationData(pendingIntent, context, name, "Uploading ",
		//				(MainMenuActivity) context, null);
		//
		//		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
		//		notificationBuilder.setContentTitle(data.getNotificationTitleWorking()).setContentText("Upload in progress")
		//				.setSmallIcon(R.drawable.ic_launcher).setOngoing(true);
		//
		//		data.setNotificationBuilder(notificationBuilder);
		//		notificationDataMap.put(notificationId, data);

		return notificationId++;
	}

	public Integer createCopyNotification(String name, Context context) {
		//		NotificationManager notificationManager = (NotificationManager) context
		//				.getSystemService(Activity.NOTIFICATION_SERVICE);
		//		String notificationTitle = context.getString(R.string.notification_title_copy_project);
		//		boolean newCopyNotification = notificationDataMap.isEmpty();
		//
		//		Intent intent = new Intent(context, MyProjectsActivity.class);
		//
		//		intent.setAction(Intent.ACTION_MAIN);
		//		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		//		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		//		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle, null, null);
		//		notificationDataMap.put(copyId, data);
		//
		//		if (newCopyNotification) {
		//			copyNotification = new Notification(R.drawable.ic_stat_copy_notification, notificationTitle,
		//					System.currentTimeMillis());
		//			copyNotification.flags = Notification.FLAG_AUTO_CANCEL;
		//			copyNotification.number += 1;
		//			copyNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
		//			notificationManager.notify(notificationCode, copyNotification);
		//		} else {
		//			copyNotification.number += 1;
		//			copyNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
		//			notificationManager.notify(notificationCode, copyNotification);
		//		}
		//
		//		return copyId;
		return -1;
	}

	public Integer createDownloadNotification(Context context, String programName) {
		initNotificationManager(context);

		Intent downloadIntent = new Intent(context, MainMenuActivity.class);
		downloadIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(EXTRA_PROJECT_NAME, programName);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, downloadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_launcher, programName,
				"Downloading ", "Start ", "Download in progress", "Download completed");

		return createNotification(context, data);
	}

	public Integer createNotification(Context context, NotificationData data) {
		initNotificationManager(context);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
		notificationBuilder.setContentTitle(data.getNotificationTitleWorking())
				.setContentText(data.getNotificationTextWorking()).setSmallIcon(data.getNotificationIcon())
				.setOngoing(true);

		data.setNotificationBuilder(notificationBuilder);
		notificationDataMap.put(notificationId, data);

		return notificationId++;
	}

	@Deprecated
	public void updateNotification(Integer id, String message, int notificationCode, boolean finished) {
		//		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
		//			updateUploadNotification(id, message, notificationCode, finished);
		//		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
		//			updateDownloadNotification(id, message, notificationCode, finished);
		//		}
	}

	public void updateUploadNotification(Integer id, int progress, boolean finished, String url) {
		//		Context context = notificationDataMap.get(id)
		//				.getContext();
		//		String notificationTitle = notificationDataMap.get(id)
		//				.getNotificationTitle();
		//		PendingIntent pendingIntent = notificationDataMap.get(id)
		//				.getPendingIntent();
		//
		//		if (finished) {
		//			uploadNotification.number--;
		//		}
		//		uploadNotification.setLatestEventInfo(context, notificationTitle, message, pendingIntent);
		//
		//		NotificationManager uploadNotificationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		//		uploadNotificationManager.notify(notificationCode, uploadNotification);

		//		NotificationData notificationData = notificationDataMap.get(id);
		//		NotificationCompat.Builder notificationBuilder = notificationData.getNotificationBuilder();
		//		notificationBuilder.setProgress(100, progress, false);
		//		notificationManager.notify(id, notificationBuilder.build());
		//
		//		if (finished) {
		//			notificationData.setNotificationTitlePrefix("View ");
		//			notificationBuilder.setContentTitle(notificationData.getNotificationTitleWorking())
		//					.setContentText("Upload complete").setProgress(0, 0, false).setAutoCancel(true)
		//					.setContentIntent(notificationData.getPendingIntent()).setOngoing(false);
		//			notificationManager.notify(id, notificationBuilder.build());
		//		}
	}

	public void updateNotification(Integer id, int progressInPercent) {
		NotificationData notificationData = notificationDataMap.get(id);
		NotificationCompat.Builder notificationBuilder = notificationData.getNotificationBuilder();
		notificationBuilder.setProgress(100, progressInPercent, false);
		notificationManager.notify(id, notificationBuilder.build());

		if (progressInPercent == 100) {
			notificationBuilder.setContentTitle(notificationData.getNotificationTitleDone())
					.setContentText(notificationData.getNotificationTextDone()).setProgress(0, 0, false)
					.setAutoCancel(true).setContentIntent(notificationData.getPendingIntent()).setOngoing(false);
			notificationManager.notify(id, notificationBuilder.build());
		}
	}

	// FIXME
	@Deprecated
	public boolean displayDialogs(MainMenuActivity activity) {
		boolean dialogsAreShown = false;
		for (int i = 0; i < downloadProjectName.size() && i < downloadProjectZipFileString.size(); i++) {
			OverwriteRenameDialog renameDialog = new OverwriteRenameDialog(activity, downloadProjectName.get(i),
					downloadProjectZipFileString.get(i));
			renameDialog.show(activity.getSupportFragmentManager(), OverwriteRenameDialog.DIALOG_FRAGMENT_TAG);
			dialogsAreShown = true;
		}
		downloadProjectName.clear();
		downloadProjectZipFileString.clear();

		return dialogsAreShown;
	}

	// FIXME
	@Deprecated
	public void cancelNotification(String projectName) {
		for (Map.Entry<Integer, NotificationData> entry : notificationDataMap.entrySet()) {
			if (entry.getValue().getProgramName().compareTo(projectName) == 0) {
				notificationManager.cancel(entry.getKey());
				notificationDataMap.remove(entry.getKey());

				break;
			}
		}
	}

	// FIXME
	@Deprecated
	public void projectRenamed(Context context, String oldProjectName, String newProjectName) {
		for (Map.Entry<Integer, NotificationData> entry : notificationDataMap.entrySet()) {
			if (entry.getValue().getProgramName().compareTo(oldProjectName) == 0) {
				entry.getValue().setProgramName(newProjectName);

				Intent downloadIntent = new Intent(context, MainMenuActivity.class);
				downloadIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						.putExtra(EXTRA_PROJECT_NAME, newProjectName);

				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, downloadIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);

				entry.getValue().setPendingIntent(pendingIntent);
				NotificationCompat.Builder builder = entry.getValue().getNotificationBuilder();
				builder.setContentTitle(entry.getValue().getNotificationTitleWorking()).setContentIntent(pendingIntent);

				notificationManager.notify(entry.getKey(), builder.build());

				break;
			}
		}
	}
}
