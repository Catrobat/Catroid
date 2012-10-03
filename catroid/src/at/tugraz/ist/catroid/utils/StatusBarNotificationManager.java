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
import at.tugraz.ist.catroid.transfers.ProjectDownloadService;
import at.tugraz.ist.catroid.transfers.ProjectUploadService;

public class StatusBarNotificationManager {

	private Integer id;
	private HashMap<Integer, NotificationData> notificationDataMap;
	public static final StatusBarNotificationManager INSTANCE = new StatusBarNotificationManager();

	private StatusBarNotificationManager() {
		this.id = 0;
		notificationDataMap = new HashMap<Integer, NotificationData>();
	}

	public static StatusBarNotificationManager getInstance() {
		return INSTANCE;
	}

	public Integer createNotification(String name, Context context, Class<?> notificationClass) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		String notificationTitle = "";
		if (notificationClass == ProjectUploadService.class) {
			notificationTitle = "Uploading project";
		} else if (notificationClass == ProjectDownloadService.class) {
			notificationTitle = "Downloading project";
		}
		Notification notification = new Notification(R.drawable.ic_upload, notificationTitle,
				System.currentTimeMillis());
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Intent intent = new Intent(context, notificationClass);
		intent.putExtra("projectName", name);

		intent.setAction(Intent.ACTION_MAIN);
		intent = intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		notification.setLatestEventInfo(context, notificationTitle, name, pendingIntent);
		notification.number += 1;
		notificationManager.notify(id, notification);

		NotificationData data = new NotificationData(notification, pendingIntent, context, name, notificationTitle);
		notificationDataMap.put(id, data);
		return id++;
	}

	public void updateNotification(Integer id, String message) {
		Notification notification = notificationDataMap.get(id).getNotification();
		Context context = notificationDataMap.get(id).getContext();
		String notificationTitle = notificationDataMap.get(id).getNotificationTitle();
		PendingIntent pendingIntent = notificationDataMap.get(id).getPendingIntent();

		notification.setLatestEventInfo(context, notificationTitle, message, pendingIntent);
		notification.number += 1; // just 4 testing

		NotificationManager uploadNotificationManager = (NotificationManager) context
				.getSystemService(Activity.NOTIFICATION_SERVICE);
		uploadNotificationManager.notify(id, notification);
	}

}
