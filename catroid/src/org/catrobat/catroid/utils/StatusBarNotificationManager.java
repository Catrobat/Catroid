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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.SparseArray;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.dialogs.OverwriteRenameDialog;

import java.util.ArrayList;
import java.util.List;

public class StatusBarNotificationManager {

	private static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private int uploadId;
	private int downloadId;
	private int copyId;

	private SparseArray<NotificationData> uploadNotificationDataArray = new SparseArray<NotificationData>();
	private SparseArray<NotificationData> downloadNotificationDataArray = new SparseArray<NotificationData>();
	private SparseArray<NotificationData> copyNotificationDataArray = new SparseArray<NotificationData>();

	private Notification copyNotification;
	private Notification uploadNotification;
	private Notification downloadNotification;

	//needed when download service is running in background
	public List<String> downloadProjectName = new ArrayList<String>();
	public List<String> downloadProjectZipFileString = new ArrayList<String>();

	private StatusBarNotificationManager() {
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	public MainMenuActivity getActivity(int id) {
		MainMenuActivity activity = downloadNotificationDataArray.get(id).getActivity();
		return activity;
	}

	public int createNotification(String name, Context context, int notificationCode) {
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
	private int createUploadNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_upload_title);
		boolean newUploadNotification = isArrayEmpty(uploadNotificationDataArray);

		Intent intent = new Intent(context, MainMenuActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context);
		uploadNotificationDataArray.put(uploadId, data);

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
	private int createCopyNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_title_copy_project);
		boolean newCopyNotification = isArrayEmpty(copyNotificationDataArray);

		Intent intent = new Intent(context, MyProjectsActivity.class);

		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle, null);
		copyNotificationDataArray.put(copyId, data);

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
	private int createDownloadNotification(String name, Context context, int notificationCode) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = context.getString(R.string.notification_download_title);
		boolean newDownloadNotification = isArrayEmpty(downloadNotificationDataArray);

		Intent intent = new Intent(context, MainMenuActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		NotificationData data = new NotificationData(pendingIntent, context, name, notificationTitle,
				(MainMenuActivity) context);
		downloadNotificationDataArray.put(downloadId, data);

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

	public void updateNotification(int id, String message, int notificationCode, boolean finished) {
		if (notificationCode == Constants.UPLOAD_NOTIFICATION) {
			updateUploadNotification(id, message, notificationCode, finished);
		} else if (notificationCode == Constants.DOWNLOAD_NOTIFICATION) {
			updateDownloadNotification(id, message, notificationCode, finished);
		}
	}

	@SuppressWarnings("deprecation")
	private void updateUploadNotification(int id, String message, int notificationCode, boolean finished) {
		Context context = uploadNotificationDataArray.get(id).getContext();
		String notificationTitle = uploadNotificationDataArray.get(id).getNotificationTitle();
		PendingIntent pendingIntent = uploadNotificationDataArray.get(id).getPendingIntent();

		if (finished) {
			uploadNotification.number--;
		}
		uploadNotification.setLatestEventInfo(context, notificationTitle, message, pendingIntent);

		NotificationManager uploadNotificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		uploadNotificationManager.notify(notificationCode, uploadNotification);
	}

	@SuppressWarnings("deprecation")
	private void updateDownloadNotification(int id, String message, int notificationCode, boolean finished) {
		Context context = downloadNotificationDataArray.get(id).getContext();
		String notificationTitle = downloadNotificationDataArray.get(id).getNotificationTitle();
		PendingIntent pendingIntent = downloadNotificationDataArray.get(id).getPendingIntent();

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

	private boolean isArrayEmpty(SparseArray<NotificationData> array) {
		return array.size() == 0;
	}

}
