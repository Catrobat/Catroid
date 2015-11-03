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

import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class NotificationData {

	private NotificationCompat.Builder notificationBuilder;
	private PendingIntent pendingIntent;

	private int notificationIcon;
	private String programName;

	private String notificationTitlePrefixWorking;
	private String notificationTitlePrefixDone;

	private String notificationTextWorking;
	private String notificationTextDone;

	public NotificationData(Context context, PendingIntent pendingIntent, int notificationIcon, String programName,
			int notificationTitlePrefixWorkingStringId, int notificationTitlePrefixDoneStringId,
			int notificationTextWorkingStringId, int notificationTextDoneStringId) {
		this.pendingIntent = pendingIntent;

		this.notificationIcon = notificationIcon;
		this.programName = programName;

		this.notificationTitlePrefixWorking = context.getString(notificationTitlePrefixWorkingStringId);
		this.notificationTitlePrefixDone = context.getString(notificationTitlePrefixDoneStringId);
		this.notificationTextWorking = context.getString(notificationTextWorkingStringId);
		this.notificationTextDone = context.getString(notificationTextDoneStringId);
	}

	public PendingIntent getPendingIntent() {
		return pendingIntent;
	}

	public NotificationData setPendingIntent(PendingIntent pendingIntent) {
		this.pendingIntent = pendingIntent;
		return this;
	}

	public int getNotificationIcon() {
		return notificationIcon;
	}

	public String getProgramName() {
		return programName;
	}

	public NotificationData setProgramName(String programName) {
		this.programName = programName;
		return this;
	}

	public String getNotificationTitleWorking() {
		return notificationTitlePrefixWorking + programName;
	}

	public String getNotificationTitleDone() {
		return notificationTitlePrefixDone + programName;
	}

	public String getNotificationTextWorking() {
		return notificationTextWorking;
	}

	public String getNotificationTextDone() {
		return notificationTextDone;
	}

	public void setNotificationTextDone(String newTextDone) {
		this.notificationTextDone = newTextDone;
	}

	public NotificationCompat.Builder getNotificationBuilder() {
		return notificationBuilder;
	}

	public NotificationData setNotificationBuilder(NotificationCompat.Builder notificationBuilder) {
		this.notificationBuilder = notificationBuilder;
		return this;
	}
}
