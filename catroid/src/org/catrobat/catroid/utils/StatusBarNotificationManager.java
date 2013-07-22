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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class StatusBarNotificationManager {

	private static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private Integer uploadId;
	private Integer downloadId;
	private Integer copyId;
	private HashMap<Integer, NotificationData> uploadNotificationDataMap;
	private HashMap<Integer, NotificationData> downloadNotificationDataMap;
	private HashMap<Integer, NotificationData> copyNotificationDataMap;
	private Notification copyNotification;
	private Notification uploadNotification;
	private Notification downloadNotification;

	//needed when download service is running in background
	public ArrayList<String> downloadProjectName;
	public ArrayList<String> downloadProjectZipFileString;

	@SuppressLint("UseSparseArrays")
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
		this.downloadProjectName = new ArrayList<String>();
		this.downloadProjectZipFileString = new ArrayList<String>();
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	public MainMenuActivity getActivity(int id) {
		MainMenuActivity activity = downloadNotificationDataMap.get(id).getActivity();
		return activity;
	}

	public Integer createNotification(String name, Context context, int notificationCode) {
		int id = 0;
		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
			id = createUploadNotification(name, context, notificationCode);
			uploadId++;
		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
			id = createDownloadNotification(name, context, notificationCode);
			downloadId++;
		} else if (notificationCode == Constants.COPY_NOTIFICATION) {
			id = createCopyNotification(name, context, notificationCode);
			copyId++;
		}
		return id;
	}

	@SuppressWarnings("deprecation")
	private Integer createUploadNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_upload_title);
		boolean newUploadNotification = uploadNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, MainMenuActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context);
		uploadNotificationDataMap.put(uploadId, data);

		if (newUploadNotification) {
			uploadNotification = new Notification(R.drawable.ic_stat_upload_notification, notificationTitle,
					System.currentTimeMillis());
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

	@SuppressWarnings("deprecation")
	private Integer createCopyNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_title_copy_project);
		boolean newCopyNotification = copyNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, MyProjectsActivity.class);

		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle, null);
		copyNotificationDataMap.put(copyId, data);

		if (newCopyNotification) {
			copyNotification = new Notification(R.drawable.ic_stat_copy_notification, notificationTitle,
					System.currentTimeMillis());
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

	@SuppressWarnings("deprecation")
	private Integer createDownloadNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_download_title);
		boolean newDownloadNotification = downloadNotificationDataMap.isEmpty();

		Intent intent = new Intent(context, MainMenuActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context);
		downloadNotificationDataMap.put(downloadId, data);

		if (newDownloadNotification) {
			downloadNotification = new Notification(R.drawable.ic_stat_download_notification, notificationTitle,
					System.currentTimeMillis());
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

	public void updateNotification(Integer id, String message, int notificationCode, boolean finished) {
		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
			updateUploadNotification(id, message, notificationCode, finished);
		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
			updateDownloadNotification(id, message, notificationCode, finished);
		}
	}

	@SuppressWarnings("deprecation")
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

	@SuppressWarnings("deprecation")
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

	public void displayDialogs(MainMenuActivity activity) {
		for (int i = 0; i < downloadProjectName.size() && i < downloadProjectZipFileString.size(); i++) {
			OverwriteRenameDialog renameDialog = new OverwriteRenameDialog(activity, downloadProjectName.get(i),
					downloadProjectZipFileString.get(i));
			renameDialog.show(activity.getSupportFragmentManager(), OverwriteRenameDialog.DIALOG_FRAGMENT_TAG);
		}
		downloadProjectName.clear();
		downloadProjectZipFileString.clear();
	}

}
