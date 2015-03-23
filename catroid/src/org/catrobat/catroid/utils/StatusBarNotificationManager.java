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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.SparseArray;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;

public final class StatusBarNotificationManager {
	public static final String EXTRA_PROJECT_NAME = "projectName";
	public static final int MAXIMUM_PERCENT = 100;

	private static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private int notificationId;
	private SparseArray<NotificationData> notificationDataMap = new SparseArray<NotificationData>();

	private NotificationManager notificationManager;

	private StatusBarNotificationManager() {
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	private void initNotificationManager(Context context) {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		}
	}

	public int createUploadNotification(Context context, String programName) {
		if (context == null || programName == null) {
			return -1;
		}
		initNotificationManager(context);

		Intent uploadIntent = new Intent(context, MainMenuActivity.class);
		uploadIntent.setAction(Intent.ACTION_MAIN);
		uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, uploadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_stat, programName,
				R.string.notification_upload_title_pending, R.string.notification_upload_title_finished,
				R.string.notification_upload_pending, R.string.notification_upload_finished);

		int id = createNotification(context, data);
		showOrUpdateNotification(id, 0);
		return id;
	}

	public int createCopyNotification(Context context, String programName) {
		if (context == null || programName == null) {
			return -1;
		}
		initNotificationManager(context);

		Intent copyIntent = new Intent(context, MainMenuActivity.class);
		copyIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(EXTRA_PROJECT_NAME, programName);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, copyIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_stat, programName,
				R.string.notification_copy_title_pending, R.string.notification_title_open,
				R.string.notification_copy_pending, R.string.notification_copy_finished);

		int id = createNotification(context, data);
		showOrUpdateNotification(id, 0);
		return id;
	}

	public int createDownloadNotification(Context context, String programName) {
		if (context == null || programName == null) {
			return -1;
		}
		initNotificationManager(context);

		Intent downloadIntent = new Intent(context, MainMenuActivity.class);
		downloadIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(EXTRA_PROJECT_NAME, programName);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, downloadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_stat, programName,
				R.string.notification_download_title_pending, R.string.notification_title_open,
				R.string.notification_download_pending, R.string.notification_download_finished);

		return createNotification(context, data);
	}

	private int createNotification(Context context, NotificationData data) {
		initNotificationManager(context);

		PendingIntent doesNothingPendingIntent = PendingIntent.getActivity(context, -1, new Intent(),
				Intent.FLAG_ACTIVITY_NEW_TASK);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
		notificationBuilder.setContentTitle(data.getNotificationTitleWorking())
				.setContentText(data.getNotificationTextWorking()).setSmallIcon(data.getNotificationIcon())
				.setOngoing(true).setContentIntent(doesNothingPendingIntent);

		data.setNotificationBuilder(notificationBuilder);
		notificationDataMap.put(notificationId, data);

		return notificationId++;
	}

	public void showOrUpdateNotification(int id, int progressInPercent) {
		NotificationData notificationData = notificationDataMap.get(id);
		if (notificationData == null) {
			return;
		}

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

	public void abortProgressNotificationWithMessage(int id, String changeDoneText) {

		NotificationData notificationData = notificationDataMap.get(id);
		if (notificationData == null) {
			return;
		}
		notificationData.setNotificationTextDone(changeDoneText);
		notificationDataMap.put(id, notificationData);

		showOrUpdateNotification(id, MAXIMUM_PERCENT);
	}

	public void cancelNotification(int id) {
		notificationDataMap.remove(id);
		notificationManager.cancel(id);
	}
}
