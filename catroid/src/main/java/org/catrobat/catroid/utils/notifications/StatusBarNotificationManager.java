/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.utils.notifications;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.transfers.project.ProjectUploadService;
import org.catrobat.catroid.ui.MainMenuActivity;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import kotlin.jvm.Synchronized;

import static org.catrobat.catroid.common.Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;
import static org.catrobat.catroid.common.Constants.EXTRA_PROJECT_NAME;
import static org.catrobat.catroid.common.Constants.MAX_PERCENT;

public final class StatusBarNotificationManager {
	private static final String TAG = StatusBarNotificationManager.class.getSimpleName();
	private static final String ACTION_UPDATE_POCKET_CODE_VERSION = "update_pocket_code_version";
	private static final String ACTION_RETRY_UPLOAD = "retry_upload";
	private static final String ACTION_CANCEL_UPLOAD = "cancel_upload";
	public static final String CHANNEL_ID = "pocket_code_notification_channel_id";

	private static final int NOTIFICATION_PENDING_INTENT_REQUEST_CODE = 1;
	public static final int UPLOAD_PENDING_INTENT_REQUEST_CODE = 0xFFFF;

	private static int notificationId = 1;
	private NotificationManager notificationManager;

	public StatusBarNotificationManager(Context context) {
		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		createNotificationChannel(context);
	}

	private NotificationData createAndShowUploadNotification(Context context, String programName) {
		if (context == null || programName == null) {
			return null;
		}

		Intent uploadIntent = new Intent(context, MainMenuActivity.class);
		uploadIntent.setAction(Intent.ACTION_MAIN);
		uploadIntent = uploadIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, uploadIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		NotificationData data = new NotificationData(R.drawable.ic_stat, programName,
				context.getString(R.string.notification_upload_title_pending), context.getString(R.string.notification_upload_title_finished),
				context.getString(R.string.notification_upload_pending), context.getString(R.string.notification_upload_finished),
				0, MAX_PERCENT, true, false, getNextNotificationID());

		showOrUpdateNotification(context, data, 0, pendingIntent);
		return data;
	}

	public NotificationData createSaveProjectToExternalMemoryNotification(Context context, String programName) {
		if (context == null || programName == null) {
			return null;
		}

		NotificationData data = new NotificationData(R.drawable.ic_stat, programName,
				context.getString(R.string.notification_save_project_to_external_storage_title_pending),
				context.getString(R.string.notification_save_project_to_external_storage_title_open),
				context.getString(R.string.notification_save_project_to_external_storage_pending),
				context.getString(R.string.notification_save_project_to_external_storage_open, EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY),
				0, MAX_PERCENT, false, false, getNextNotificationID());

		showOrUpdateNotification(context, data, 0, null);
		return data;
	}

	public NotificationData createProjectDownloadNotification(Context context, String programName) {
		return new NotificationData(R.drawable.ic_stat, programName,
				context.getString(R.string.notification_download_title_pending), context.getString(R.string.notification_title_open),
				context.getString(R.string.notification_download_pending), context.getString(R.string.notification_download_finished),
				0, MAX_PERCENT, true, false, getNextNotificationID());
	}

	@Synchronized
	public static int getNextNotificationID() {
		return notificationId++;
	}

	public void showOrUpdateNotification(Context context, NotificationData notificationData, int progressInPercent, PendingIntent contentIntent) {
		int notificationID = notificationData.getNotificationID();
		notificationData.setProgressInPercent(progressInPercent);
		if (progressInPercent < MAX_PERCENT) {
			notificationManager.notify(notificationID, notificationData.toNotification(context, CHANNEL_ID, contentIntent));
		} else {
			notificationData.setProgressInPercent(0);
			notificationData.setMaxProgress(0);
			notificationData.setAutoCancel(true);
			notificationData.setOngoing(false);
			notificationManager.notify(notificationID, notificationData.toNotification(context, CHANNEL_ID, contentIntent));
		}
	}

	public void abortProgressNotificationWithMessage(Context context, NotificationData notificationData, @StringRes int changeDoneText) {
		if (notificationData == null) {
			return;
		}
		notificationData.setTextDone(context.getString(changeDoneText));
		showOrUpdateNotification(context, notificationData, MAX_PERCENT, null);
	}

	public Notification createUploadRejectedNotification(Context context, int statusCode, String serverAnswer, Bundle bundle) {
		Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
				.setContentTitle(context.getResources().getString(R.string.notification_upload_rejected))
				.setContentText(serverAnswer)
				.setTicker(context.getResources().getString(R.string.notification_upload_rejected))
				.setSound(alarmSound)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(serverAnswer))
				.setProgress(0, 0, false)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setOngoing(false);

		switch (statusCode) {
			case Constants.STATUS_CODE_INTERNAL_SERVER_ERROR:
			case Constants.STATUS_CODE_UPLOAD_MISSING_DATA:
			case Constants.STATUS_CODE_UPLOAD_INVALID_CHECKSUM:
			case Constants.STATUS_CODE_UPLOAD_COPY_FAILED:
			case Constants.STATUS_CODE_UPLOAD_UNZIP_FAILED:
			case Constants.STATUS_CODE_UPLOAD_MISSING_XML:
			case Constants.STATUS_CODE_UPLOAD_RENAME_FAILED:
			case Constants.STATUS_CODE_UPLOAD_SAVE_THUMBNAIL_FAILED:
				Intent actionIntentRetryUpload = new Intent(context, NotificationActionService.class)
						.setAction(ACTION_RETRY_UPLOAD);
				actionIntentRetryUpload.putExtra("bundle", bundle);

				PendingIntent actionPendingIntentRetryUpload = PendingIntent.getService(context, NOTIFICATION_PENDING_INTENT_REQUEST_CODE,
						actionIntentRetryUpload, PendingIntent.FLAG_CANCEL_CURRENT);
				builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_popup_sync,
						context.getResources().getString(R.string.notification_upload_retry), actionPendingIntentRetryUpload));

				Intent actionIntentCancelUpload = new Intent(context, NotificationActionService.class)
						.setAction(ACTION_CANCEL_UPLOAD);
				actionIntentCancelUpload.putExtra("bundle", bundle);
				PendingIntent actionPendingIntentCancelUpload = PendingIntent.getService(context, NOTIFICATION_PENDING_INTENT_REQUEST_CODE,
						actionIntentCancelUpload, PendingIntent.FLAG_ONE_SHOT);
				builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel,
						context.getResources().getString(R.string.cancel), actionPendingIntentCancelUpload));

				break;
			case Constants.STATUS_CODE_UPLOAD_MISSING_CHECKSUM:
			case Constants.STATUS_CODE_UPLOAD_OLD_CATROBAT_LANGUAGE:
			case Constants.STATUS_CODE_UPLOAD_OLD_CATROBAT_VERSION:
				Intent actionIntentUpdatePocketCodeVersion = new Intent(context, NotificationActionService.class)
						.setAction(ACTION_UPDATE_POCKET_CODE_VERSION)
						.putExtra("notificationId", NOTIFICATION_PENDING_INTENT_REQUEST_CODE);
				PendingIntent actionPendingIntentUpdatePocketCodeVersion = PendingIntent.getService(context, NOTIFICATION_PENDING_INTENT_REQUEST_CODE,
						actionIntentUpdatePocketCodeVersion, PendingIntent.FLAG_ONE_SHOT);
				builder.addAction(new NotificationCompat.Action(R.drawable.pc_toolbar_icon,
						context.getResources().getString(R.string.notification_open_play_store), actionPendingIntentUpdatePocketCodeVersion));
				break;

			default:
				Intent openIntent = new Intent(context, MainMenuActivity.class);
				openIntent.setAction(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
						.putExtra(EXTRA_PROJECT_NAME, bundle.getString("projectName"));

				PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, openIntent,
						PendingIntent.FLAG_CANCEL_CURRENT);
				builder.setContentIntent(pendingIntent);
				break;
		}

		return builder.build();
	}

	public static class NotificationActionService extends IntentService {
		public NotificationActionService() {
			super(NotificationActionService.class.getSimpleName());
		}

		@Override
		protected void onHandleIntent(Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "Received notification, action is: " + action);

			if (ACTION_UPDATE_POCKET_CODE_VERSION.equals(action)) {
				final String appPackageName = getPackageName();

				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
				closeNotificationBar();
			}
			if (ACTION_RETRY_UPLOAD.equals(action)) {
				Intent reuploadIntent = prepareReuploadIntent(intent);
				String projectName = intent.getBundleExtra("bundle").getString("projectName");

				NotificationData notificationData = new StatusBarNotificationManager(getApplicationContext())
						.createAndShowUploadNotification(getApplicationContext(), projectName);
				int notificationId = notificationData == null ? -1 : notificationData.getNotificationID();
				reuploadIntent.putExtra("notificationId", notificationId);
				getApplicationContext().startService(reuploadIntent);
			}

			if (ACTION_CANCEL_UPLOAD.equals(action)) {
				closeNotificationBar();
			}
		}

		private Intent prepareReuploadIntent(Intent intent) {
			String projectName = intent.getBundleExtra("bundle").getString("projectName");
			String projectDescription = intent.getBundleExtra("bundle").getString("projectDescription");
			String projectPath = intent.getBundleExtra("bundle").getString("projectPath");
			String[] sceneNames = intent.getBundleExtra("bundle").getStringArray("sceneNames");
			String token = intent.getBundleExtra("bundle").getString("token");
			String username = intent.getBundleExtra("bundle").getString("username");
			ResultReceiver receiver = intent.getBundleExtra("bundle").getParcelable("receiver");

			Intent reuploadIntent = new Intent(getApplicationContext(), ProjectUploadService.class);
			reuploadIntent.putExtra("receiver", receiver);
			reuploadIntent.putExtra("uploadName", projectName);
			reuploadIntent.putExtra("projectDescription", projectDescription);
			reuploadIntent.putExtra("projectPath", projectPath);
			reuploadIntent.putExtra("username", username);
			reuploadIntent.putExtra("token", token);
			reuploadIntent.putExtra("sceneNames", sceneNames);
			return reuploadIntent;
		}

		private void closeNotificationBar() {
			Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
			sendBroadcast(it);
		}
	}

	public void createNotificationChannel(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getResources().getString(R.string.app_name);
			String description = context.getResources().getString(R.string.channel_description, name);

			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			if (notificationManager == null || notificationManager.getNotificationChannel(CHANNEL_ID) != null) {
				return;
			}

			int importance = NotificationManager.IMPORTANCE_DEFAULT;

			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			channel.enableVibration(false);
			channel.enableLights(false);
			channel.setSound(null, null);
			notificationManager.createNotificationChannel(channel);
		}
	}
}
