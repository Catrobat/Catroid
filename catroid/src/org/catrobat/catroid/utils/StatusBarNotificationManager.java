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

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;

import java.util.HashMap;
import java.util.Map;

public class StatusBarNotificationManager {

	private static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private Integer notificationId;
	private Map<Integer, NotificationData> notificationDataMap;

	NotificationManager notificationManager;

	public static final String EXTRA_PROJECT_NAME = "projectName";

	@SuppressLint("UseSparseArrays")
	private StatusBarNotificationManager() {
		notificationId = 0;
		notificationDataMap = new HashMap<Integer, NotificationData>();
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

	public Integer createUploadNotification(Context context, String programName) {
		initNotificationManager(context);

		Intent uploadIntent = new Intent(context, MainMenuActivity.class);
		uploadIntent.setAction(Intent.ACTION_MAIN);
		uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, uploadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_launcher, programName,
				R.string.notification_upload_title_pending, R.string.notification_upload_title_finished,
				R.string.notification_upload_pending, R.string.notification_upload_finished);

		Integer id = createNotification(context, data);
		showOrUpdateNotification(id, 0);
		return id;
	}

	public Integer createCopyNotification(Context context, String programName) {
		initNotificationManager(context);

		Intent copyIntent = new Intent(context, MyProjectsActivity.class);
		copyIntent.setAction(Intent.ACTION_MAIN);
		copyIntent = copyIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, copyIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_launcher, programName,
				R.string.notification_copy_title_pending, R.string.notification_upload_title_finished,
				R.string.notification_copy_pending, R.string.notification_copy_finished);

		Integer id = createNotification(context, data);
		showOrUpdateNotification(id, 0);
		return id;
	}

	public Integer createDownloadNotification(Context context, String programName) {
		initNotificationManager(context);

		Intent downloadIntent = new Intent(context, MainMenuActivity.class);
		downloadIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				.putExtra(EXTRA_PROJECT_NAME, programName);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, downloadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(context, pendingIntent, R.drawable.ic_launcher, programName,
				R.string.notification_download_title_pending, R.string.notification_download_title_finished,
				R.string.notification_download_pending, R.string.notification_download_finished);

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

	public void showOrUpdateNotification(Integer id, int progressInPercent) {
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

	public void cancelNotification(Integer id) {
		notificationDataMap.remove(id);
		notificationManager.cancel(id);
	}
}
