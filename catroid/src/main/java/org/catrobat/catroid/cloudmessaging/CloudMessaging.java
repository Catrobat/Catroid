/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

package org.catrobat.catroid.cloudmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.utils.StatusBarNotificationManager;

public class CloudMessaging {

	private final Context context;
	private final NotificationBuilderProvider notificationBuilderProvider;
	private final NotificationManager notificationManager;
	private static boolean isCloudNotificationsEnabled;
	private static CloudMessagingInterface messagingService = new FirebaseCloudMessaging();

	public CloudMessaging(Context context, NotificationBuilderProvider notificationBuilderProvider,
			NotificationManager notificationManager) {
		this.context = context;
		this.notificationBuilderProvider = notificationBuilderProvider;
		this.notificationManager = notificationManager;
		isCloudNotificationsEnabled = BuildConfig.FEATURE_CLOUD_MESSAGING_ENABLED;
	}

	public static boolean initialize(Context context) {
		boolean isEnabled = isCloudNotificationsEnabled(context);
		if (isEnabled) {
			messagingService.subscribe();
		} else {
			messagingService.unsubscribe();
		}
		return isEnabled;
	}

	@VisibleForTesting
	public static void setCloudMessagingInterface(CloudMessagingInterface cloudMessagingInterface) {
		messagingService = cloudMessagingInterface;
	}

	@VisibleForTesting
	public static void setIsCloudNotificationsEnabled(boolean isEnabled) {
		isCloudNotificationsEnabled = isEnabled;
	}

	private static boolean isCloudNotificationsEnabled(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity.SETTINGS_CLOUD_NOTIFICATIONS, false) && isCloudNotificationsEnabled;
	}

	public void showNotification(CloudMessage cloudMessage) {
		if (cloudMessage.isValidData()) {
			Notification notification = buildNotification(cloudMessage);
			notificationManager.notify(StatusBarNotificationManager.getInstance().getUniqueNotificationId(), notification);
		}
	}

	private Notification buildNotification(CloudMessage cloudMessage) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cloudMessage.getWebPageUrl()));
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		return notificationBuilderProvider.get()
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(cloudMessage.getTitle())
				.setContentText(cloudMessage.getMessage())
				.setAutoCancel(true)
				.setSound(defaultSoundUri)
				.setContentIntent(pendingIntent)
				.build();
	}
}
