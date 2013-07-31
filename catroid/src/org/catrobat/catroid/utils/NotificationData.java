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

import org.catrobat.catroid.ui.MainMenuActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class NotificationData {

	private PendingIntent pendingIntent;
	private Context context;
	private String programName;
	private String notificationTitlePrefix;
	private MainMenuActivity activity;
	private Notification notification;
	private NotificationCompat.Builder notificationBuilder;

	public NotificationData(PendingIntent pendingIntent, Context context, String programName,
			String notificationTitlePrefix, MainMenuActivity activity, Notification notification) {
		this.pendingIntent = pendingIntent;
		this.context = context;
		this.programName = programName;
		this.notificationTitlePrefix = notificationTitlePrefix;
		this.activity = activity;
		this.notification = notification;
	}

	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}

	public NotificationData setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
		return this;
	}

	public Context getContext() {
		return context;
	}

	public NotificationData setContext(Context context) {
		this.context = context;
		return this;
	}

	public String getProgramName() {
		return programName;
	}

	public NotificationData setProgramName(String programName) {
		this.programName = programName;
		return this;
	}

	public String getNotificationTitle() {
		return notificationTitlePrefix + programName;
	}

	public NotificationData setNotificationTitlePrefix(String notificationTitlePrefix) {
		this.notificationTitlePrefix = notificationTitlePrefix;
		return this;
	}

	public MainMenuActivity getActivity() {
		return activity;
	}

	public NotificationData setActivity(MainMenuActivity activity) {
		this.activity = activity;
		return this;
	}

	public Notification getNotification() {
		return notification;
	}

	public NotificationData setNotification(Notification notification) {
		this.notification = notification;
		return this;
	}

	public NotificationCompat.Builder getNotificationBuilder() {
		return notificationBuilder;
	}

	public NotificationData setNotificationBuilder(NotificationCompat.Builder notificationBuilder) {
		this.notificationBuilder = notificationBuilder;
		return this;
	}
}
