/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.utils;

import java.util.HashMap;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Constants;
import at.tugraz.ist.catroid.ui.MainMenuActivity;
import at.tugraz.ist.catroid.ui.MyProjectsActivity;

public class StatusBarNotificationManager {

	private Integer uploadId;
	private Integer downloadId;
	private Integer copyId;
	private HashMap<Integer, NotificationData> uploadNotificationDataMap;
	private HashMap<Integer, NotificationData> downloadNotificationDataMap;
	private HashMap<Integer, NotificationData> copyNotificationDataMap;
	private Notification copyNotification;
	private Notification uploadNotification;
	private Notification downloadNotification;
	public static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private StatusBarNotificationManager() {
		this.uploadId = 0;
		this.downloadId = 0;
		this.copyId = 0;
		this.uploadNotification = null;
		this.downloadNotification = null;
		this.copyNotification = null;
		this.copyNotificationDataMap = new HashMap<Integer, NotificationData>();
		this.uploadNotificationDataMap = new HashMap<Integer, NotificationData>();
		this.downloadNotificationDataMap = new HashMap<Integer, NotificationData>();
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	public MainMenuActivity getActivity(int id) {
		MainMenuActivity activity = downloadNotificationDataMap.get(id).getActivity();
		return activity;
	}

	public Integer createNotification(String name, Context context, Class<?> notificationClass, int notificationCode) {
		int id = 0;
		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
			id = createUploadNotification(name, context, notificationClass, notificationCode);
			uploadId++;
		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
			id = createDownloadNotification(name, context, notificationClass, notificationCode);
			downloadId++;
		} else if (notificationCode == Constants.COPY_NOTIFICATION) {
			id = createCopyNotification(name, context, notificationClass, notificationCode);
			copyId++;
		}
		return id;
	}

	private Integer createUploadNotification(String name, Context context, Class<?> notificationClass,
			int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = "Uploading project";
		boolean newUploadNotification = uploadNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, notificationClass);
		intent.putExtra("projectName", name);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context, uploadId);
		uploadNotificationDataMap.put(uploadId, data);

		if (newUploadNotification) {
			uploadNotification = new Notification(R.drawable.ic_upload, notificationTitle, System.currentTimeMillis());
			uploadNotification.flags = Notification.FLAG_AUTO_CANCEL;
			uploadNotification.number += 1;
			uploadNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
			notificationManager.notify(notificationCode, uploadNotification);
		} else {
			uploadNotification.number += 1;
			notificationManager.notify(notificationCode, uploadNotification);
		}

		return uploadId;
	}

	private Integer createDownloadNotification(String name, Context context, Class<?> notificationClass,
			int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = "Downloading project";
		boolean newDownloadNotification = downloadNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, notificationClass);
		intent.putExtra("projectName", name);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context, downloadId);
		downloadNotificationDataMap.put(downloadId, data);

		if (newDownloadNotification) {
			downloadNotification = new Notification(R.drawable.ic_upload, notificationTitle, System.currentTimeMillis());
			downloadNotification.flags = Notification.FLAG_AUTO_CANCEL;
			downloadNotification.number += 1;
			downloadNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
			notificationManager.notify(notificationCode, downloadNotification);
		} else {
			downloadNotification.number += 1;
			notificationManager.notify(notificationCode, downloadNotification);
		}

		return downloadId;
	}

	private Integer createCopyNotification(String name, Context context, Class<?> notificationClass,
			int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = "Copying project";
		boolean newCopyNotification = copyNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, MyProjectsActivity.class);

		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle, null, copyId);
		copyNotificationDataMap.put(copyId, data);

		if (newCopyNotification) {
			copyNotification = new Notification(R.drawable.ic_upload, notificationTitle, System.currentTimeMillis());
			copyNotification.flags = Notification.FLAG_AUTO_CANCEL;
			copyNotification.number += 1;
			copyNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
			notificationManager.notify(notificationCode, copyNotification);
		} else {
			copyNotification.number += 1;
			copyNotification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
			notificationManager.notify(notificationCode, copyNotification);
		}

		return copyId;
	}

	public void updateNotification(Integer id, String message, int notificationCode, boolean finished) {
		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
			updateUploadNotification(id, message, notificationCode, finished);
		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
			updateDownloadNotification(id, message, notificationCode, finished);
		}
	}

	private void updateUploadNotification(Integer id, String message, int notificationCode, boolean finished) {
		Context context = uploadNotificationDataMap.get(id).getContext();
		String notificationTitle = uploadNotificationDataMap.get(id).getNotificationTitle();
		PendingIntent pendingIntent = uploadNotificationDataMap.get(id).getPendingIntent();

		if (finished) {
			uploadNotification.number--;
		}
		uploadNotification.setLatestEventInfo(context, notificationTitle, message, pendingIntent);

		NotificationManager uploadNotificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		uploadNotificationManager.notify(notificationCode, uploadNotification);
	}

	private void updateDownloadNotification(Integer id, String message, int notificationCode, boolean finished) {
		Context context = downloadNotificationDataMap.get(id).getContext();
		String notificationTitle = downloadNotificationDataMap.get(id).getNotificationTitle();
		PendingIntent pendingIntent = downloadNotificationDataMap.get(id).getPendingIntent();

		if (finished) {
			downloadNotification.number--;
		}
		downloadNotification.setLatestEventInfo(context, notificationTitle, message, pendingIntent);

		NotificationManager downloadNotificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		downloadNotificationManager.notify(notificationCode, downloadNotification);
	}

}
