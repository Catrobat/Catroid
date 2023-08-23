/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.languagetranslator;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.actions.TranslateTextFromToAction;
import org.catrobat.catroid.stage.StageActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LanguageTranslator {

	public static final String TAG = "LanguageTranslator";
	public static final String NOTIFICATION_CANCEL_ACTION = "org.catrobat.catroid.languagetranslator.NOTIFICATION_DELETE";
	public static final String NOTIFICATION_CANCEL_EXTRAS_KEY = "org.catrobat.catroid.languagetranslator.NotificationId";
	private final String hyphen = "-";
	private final String undefinedLanguageCode = "und";
	private final String defaultLanguageModel = "en";
	private final String text;
	private String sourceLanguage;
	private String targetLanguage;
	private TranslatorOptions translatorOptions;
	private Translator translationModel;
	private RemoteModelManager modelManager;
	private DownloadManager downloadManager;
	private TranslateTextFromToAction.TranslationResult translationResult;
	private LanguageIdentifier languageIdentifier;
	private List<String> modelsOnDevice;
	private List<String> modelsToDownload;
	private Map<String, Integer> notificationIds;
	private Map<String, String> downloadChannelIds;
	private Map<String, String> cancelChannelIds;
	private Map<String, NotificationCompat.Builder> notificationBuilders;
	private Map<String, Boolean> canceledModels;
	private Integer notificationId = 0;
	private Integer canceledNotificationId = 2;
	private AtomicBoolean identifiedLanguage;
	private AtomicBoolean taskCanceled;
	private AtomicInteger downloadCounter;
	private final StageActivity stageActivity;
	private ConnectivityChangeBroadcastReceiver connectivityChangeBroadcastReceiver;
	private CancelDownloadBroadcastReceiver cancelDownloadBroadcastReceiver;

	public LanguageTranslator(String text, String sourceLanguage, String targetLanguage,
			StageActivity stageActivity) {
		this.text = text;
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.stageActivity = stageActivity;
	}

	public void translate() {
		if (sourceLanguage.contains(hyphen)) {
			removePostfixCode(sourceLanguage, true);
		}
		if (targetLanguage.contains(hyphen)) {
			removePostfixCode(targetLanguage, false);
		}
		if (sourceLanguage.equals(undefinedLanguageCode) || sourceLanguage.isEmpty()) {
			identifyLanguage();
		} else {
			checkModelsOnDevice();
		}
		Log.i(TAG, "Waiting for ML Kit API to complete tasks...");
	}

	private void removePostfixCode(String languageCode, boolean isSourceLanguage) {
		if (!languageCode.contains("-Latn")) {
			String[] tokens = languageCode.split(hyphen);
			String languageCodeWithoutPostFix = tokens[0];
			if (isSourceLanguage) {
				sourceLanguage = languageCodeWithoutPostFix;
			} else {
				targetLanguage = languageCodeWithoutPostFix;
			}
		}
	}

	public void identifyLanguage() {
		identifiedLanguage = new AtomicBoolean(false);

		if (languageIdentifier == null) {
			languageIdentifier = LanguageIdentification.getClient();
			Log.i(TAG, "Initialized language identifier");
		}

		Log.i(TAG, "Beginning language identification...");
		Task<String> identifyLanguageTask = languageIdentifier.identifyLanguage(text);
		identifyLanguageTask.addOnSuccessListener(languageCode -> {
			// task is successful both when a language code was identified and when not, i.e., the
			// language code is "und".
			if (!languageCode.equals(undefinedLanguageCode)) {
				Log.i(TAG, "Identified language is: " + languageCode);
				sourceLanguage = languageCode;
				identifiedLanguage.set(true);
			}
		});
		identifyLanguageTask.addOnFailureListener(e -> {
			// task fails due to some internal API error
			String error = "Language identification failed with exception: " + e.getMessage();
			translationResult.onComplete(error);
			Log.i(TAG, error);
		});
		identifyLanguageTask.addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				if (identifiedLanguage.get()) {
					Log.i(TAG, "Successfully completed language identification.");
					checkModelsOnDevice();
				} else {
					String message = "The source language couldn't be identified.";
					translationResult.onComplete(message);
					Log.i(TAG, message);
				}
			}
		});
	}

	public void checkModelsOnDevice() {
		if (modelManager == null) {
			modelManager = RemoteModelManager.getInstance();
		}
		Log.i(TAG, "Get translation models stored on the device...");
		Task<Set<TranslateRemoteModel>> getModelsTask = modelManager.getDownloadedModels(TranslateRemoteModel.class);
		getModelsTask.addOnSuccessListener(models -> Log.i(TAG, "Get downloaded models succeeded"));
		getModelsTask.addOnFailureListener(e -> Log.i(TAG, "Get downloaded models failed with exception: " + e.getMessage()));
		getModelsTask.addOnCompleteListener(task -> {
			Log.i(TAG, "Get downloaded models was completed");
			if (task.isSuccessful() && !task.getResult().isEmpty()) {
				modelsOnDevice = new ArrayList<>();
				for (TranslateRemoteModel model : task.getResult()) {
					String language = model.getLanguage();
					modelsOnDevice.add(language);
					Log.i(TAG, "Language model " + language + " already on device");
				}
				if (modelsOnDevice.contains(sourceLanguage) && modelsOnDevice.contains(targetLanguage)) {
					Log.i(TAG, "Language models required for translation already on device");
					if (!sourceLanguage.equals(targetLanguage)) {
						translateText();
					} else {
						translationResult.onComplete(text);
					}
				} else {
					modelsToDownload = new ArrayList<>();
					canceledModels = new ArrayMap<>();
					if (!targetLanguage.equals(defaultLanguageModel) && !modelsOnDevice.contains(targetLanguage)) {
						modelsToDownload.add(targetLanguage);
						canceledModels.put(targetLanguage, false);
					}
					if (!sourceLanguage.equals(defaultLanguageModel) && !modelsOnDevice.contains(sourceLanguage)) {
						modelsToDownload.add(sourceLanguage);
						canceledModels.put(sourceLanguage, false);
					}
					if (showPopupDownloadDialog()) {
						Log.i(TAG, "Show popup dialog for language model(s) to download on the device.");
						stageActivity.manageDownloadLanguageModels();
					} else {
						Log.i(TAG, "Download for language model(s) is already in progress.");
						setTranslationResult();
					}
				}
			}
		});
	}

	public boolean showPopupDownloadDialog() {
		Map<String, Integer> activeDownloadNotifications = trackActiveNotifications();
		if (activeDownloadNotifications.containsKey(sourceLanguage)) {
			modelsToDownload.remove(sourceLanguage);
			canceledModels.remove(sourceLanguage);
		}
		if (activeDownloadNotifications.containsKey(targetLanguage)) {
			modelsToDownload.remove(targetLanguage);
			canceledModels.remove(targetLanguage);
		}
		return modelsToDownload.size() > 0;
	}

	public void checkIfDownloadLanguageModels(boolean download) {
		if (download) {
			removePreviousDownloads();
			if (modelsToDownload.size() > 0) {
				notificationId = getMaxNotificationId(trackActiveNotifications());
				downloadLanguageModels();
			}
		}
		setTranslationResult();
	}

	public int getMaxNotificationId(Map<String, Integer> activeNotifications) {
		int maxId = 0;
		for (Map.Entry<String, Integer> notification : activeNotifications.entrySet()) {
			maxId = Math.max(notification.getValue(), maxId);
		}
		return maxId;
	}

	public void setTranslationResult() {
		translationResult.onComplete("Missing language model(s) to translate " + "'" + text + "'");
	}

	public void downloadLanguageModels() {
		taskCanceled = new AtomicBoolean(false);
		downloadCounter = new AtomicInteger(modelsToDownload.size());
		notificationIds = new ArrayMap<>();

		Context context = stageActivity.getApplicationContext();
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		registerCancelDownloadBroadcastReceiver();
		registerConnectivityChangeBroadcastReceiver();

		Log.i(TAG, "Number of language models to download is " + modelsToDownload.size());
		for (String language : modelsToDownload) {
			notificationIds.put(language, ++notificationId);
			String channelId = "Download " + language + " language model";
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
			createDownloadNotification(context, language, notificationId, builder, notificationManager);

			Log.i(TAG, "Downloading " + language + " language model to the device...");
			DownloadConditions conditions = new DownloadConditions.Builder().build();
			TranslateRemoteModel targetModel = new TranslateRemoteModel.Builder(language).build();
			Task<Void> downloadTask = modelManager.download(targetModel, conditions);
			downloadTask.addOnCanceledListener(() -> {
				// API doesn't support canceling an ongoing download; but a download can be
				// indirectly canceled via the DownloadManager
				Log.i(TAG, "Download of language model " + language + " was cancelled.");
				taskCanceled.set(true);
			});
			downloadTask.addOnSuccessListener(v -> Log.i(TAG, "Successfully downloaded " + language + " model."));
			downloadTask.addOnFailureListener(e -> {
				if (canceledModels.get(language)) {
					//Listener is called when the download was cancelled by the DownloadManager
					Log.i(TAG, "User canceled " + language + " language model.");
					createCanceledDownloadNotification(context, language, ++canceledNotificationId, notificationManager);
					deleteNotificationOnCancelActionFromUser(language, notificationManager);
				} else {
					//Listener is called when a language is not supported by the API
					Log.i(TAG, "Download of " + language + " model failed with exception: " + "'" + e.getMessage() + "'");
					updateNotificationOnDownloadError(language, builder, notificationManager);
					deleteNotificationOnDownloadCompleteOrError(language, notificationManager);
				}
			});
			downloadTask.addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					Log.i(TAG, "Completed downloading " + language + " model.");
					updateNotificationOnDownloadComplete(language, builder, notificationManager);
					deleteNotificationOnDownloadCompleteOrError(language, notificationManager);
				}
				if (downloadCounter.get() > 0) {
					downloadCounter.addAndGet(-1);
				}
				if (downloadCounter.get() == 0) {
					unregisterCancelDownloadBroadcastReceiver();
					unregisterConnectivityChangeBroadcastReceiver();
				}
			});
		}
	}

	public void deleteNotificationOnDownloadCompleteOrError(String language,
			NotificationManagerCompat notificationManager) {
		String channelId = downloadChannelIds.get(language);
		notificationManager.deleteNotificationChannel(channelId);
		Log.i(TAG, "Deleted " + "'" + channelId + "'" + " notification channel");
	}

	public void deleteNotificationOnCancelActionFromUser(String language,
			NotificationManagerCompat notificationManager) {
		String channelId = cancelChannelIds.get(language);
		notificationManager.deleteNotificationChannel(channelId);
		Log.i(TAG, "Deleted " + "'" + channelId + "'" + " notification channel");
	}

	public Map<String, Integer> trackActiveNotifications() {
		Map<String, Integer> activeNotifications = new ArrayMap<>();
		for (StatusBarNotification notification : getActiveNotifications()) {
			Bundle extras = notification.getNotification().extras;
			if (extras != null) {
				int id = notification.getId();
				String language = extras.getString("languageCode");
				activeNotifications.put(language, id);
				Log.i(TAG, "Found active notification for "
						+ language + " language model with id " + id);
			}
		}
		Log.i(TAG, "Number of active notification is " + activeNotifications.size());

		return activeNotifications;
	}

	public StatusBarNotification[] getActiveNotifications() {
		Context context = stageActivity.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		return notificationManager.getActiveNotifications();
	}

	public void removePreviousDownloads() {
		Log.i(TAG, "Removing previous downloads from the DownloadManager.");
		queryDownloadNotification(DownloadManager.STATUS_SUCCESSFUL, -1, false);
	}

	public void cancelRunningDownload(int notificationId) {
		Log.i(TAG, "User canceled download of language model with notification id: " + notificationId);
		queryDownloadNotification(DownloadManager.STATUS_RUNNING, notificationId, true);
	}

	private void queryDownloadNotification(int downloadStatus, int notificationId, boolean cancelDownload) {
		Context context = stageActivity.getApplicationContext();
		downloadManager = context.getSystemService(DownloadManager.class);
		DownloadManager.Query query = new DownloadManager.Query();
		query.setFilterByStatus(downloadStatus);
		Cursor cursor = downloadManager.query(query);

		String language = cancelDownload && notificationId > 0 ? getCanceledLanguageModel(notificationId) : "";

		Log.i(TAG, "Cursor: number of entries is " + cursor.getCount());
		if (cursor.moveToFirst()) {
			int position = 0;
			while (position < cursor.getCount()) {
				if (cursor.moveToPosition(position)) {
					int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
					Log.i(TAG, "Download Manager: status is " + status);
					if (status == downloadStatus) {
						String downloadNotificationTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
						Log.i(TAG, "Download Manager: notification title is " + downloadNotificationTitle);

						String downloadId = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
						Log.i(TAG, "Download Manager: notification id is " + downloadId);

						long downloadIdNum = Long.parseLong(downloadId);
						if (!language.isEmpty() && downloadNotificationTitle.contains(language)) {
							if (downloadManager.remove(downloadIdNum) > 0) {
								Log.i(TAG, "Download Manager: canceled download with id " + downloadId);
								break;
							}
						} else if (!cancelDownload && downloadManager.remove(downloadIdNum) > 0) {
							Log.i(TAG, "Download Manager: removed download with id " + downloadId);
						}
					}
				}
				position++;
			}
		}
		cursor.close();
	}

	private String getCanceledLanguageModel(int notificationId) {
		String language = "";
		for (Map.Entry<String, Integer> entry : notificationIds.entrySet()) {
			if (entry.getValue() == notificationId) {
				language = entry.getKey();
				canceledModels.put(language, true);
				Log.i(TAG, "Marked " + language + " as canceled language model.");
				break;
			}
		}
		return language;
	}

	public void translateText() {
		if (translatorOptions == null) {
			translatorOptions = new TranslatorOptions.Builder()
					.setSourceLanguage(sourceLanguage)
					.setTargetLanguage(targetLanguage)
					.build();
			Log.i(TAG, "Created translator options.");
		}

		if (translationModel == null) {
			translationModel = Translation.getClient(translatorOptions);
			Log.i(TAG, "Initialized translation model.");
		}

		Log.i(TAG, "Beginning translation...");
		if (translationModel != null) {
			Task<String> translateTask = translationModel.translate(text);
			translateTask.addOnSuccessListener(s -> Log.i(TAG, "Successfully translated text."));
			translateTask.addOnFailureListener(e -> Log.i(TAG, "Translation failed with exception " + e.getMessage()));
			translateTask.addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					Log.i(TAG, "Completed translation.");
					translationModel.close();
					Log.i(TAG, "Closed translation model.");

					String translation = task.getResult();
					Log.i(TAG, "Translation is: " + translation);
					translationResult.onComplete(translation);
				} else {
					String error = "Unable to complete translation because of "
							+ Objects.requireNonNull(task.getException(), "exception must not be null").getMessage();
					translationModel.close();
					Log.i(TAG, "Closed translation model.");

					translationResult.onComplete(error);
					Log.i(TAG, "Set translation result with error message");
				}
			});
		}
	}

	public void createCanceledDownloadNotification(Context context, String language, int notificationId,
			NotificationManagerCompat notificationManager) {

		Intent intent = new Intent(context, StageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

		String title = "Canceled " + "'" + language + "'" + " language model";
		String contextText = "Download canceled";

		String channelId = "Cancel " + language + " language model";
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

		builder.setSmallIcon(R.drawable.ic_baseline_info_24)
				.setContentTitle(title)
				.setContentText(contextText)
				.setContentIntent(pendingIntent)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setProgress(0, 0, false)
				.setOngoing(false);

		createNotificationChannel(context, language, NotificationType.CANCEL);

		notificationManager.notify(notificationId, builder.build());
		Log.i(TAG, "Created " + language + " language model download canceled notification with id:"
				+ " " + notificationId);
	}

	public void createDownloadNotification(Context context, String language, int notificationId,
			NotificationCompat.Builder builder, NotificationManagerCompat notificationManager) {

		Intent intent = new Intent(context, StageActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

		String title = "Download " + "'" + language + "'" + " language model";
		String contentText = hasConnection(context) ? "Download in progress" : "No internet connection available";

		Bundle languageCode = new Bundle();
		languageCode.putString("languageCode", language);

		builder.setSmallIcon(R.drawable.ic_baseline_file_download_24)
				.setContentTitle(title)
				.setContentText(contentText)
				.setContentIntent(pendingIntent)
				.setDeleteIntent(createOnCancelIntent(context, notificationId))
				.setPriority(NotificationCompat.PRIORITY_DEFAULT)
				.setProgress(0, 0, true)
				.setExtras(languageCode)
				.setOngoing(false);

		createNotificationChannel(context, language, NotificationType.DOWNLOAD);

		if (notificationBuilders == null) {
			notificationBuilders = new ArrayMap<>();
		}
		notificationBuilders.put(language, builder);

		notificationManager.notify(notificationId, builder.build());
		Log.i(TAG, "Created " + language + " language model download notification with id: " + notificationId);
	}

	public PendingIntent createOnCancelIntent(Context context, int notificationId) {
		Intent intent = new Intent();
		intent.setAction(NOTIFICATION_CANCEL_ACTION);
		intent.putExtra(NOTIFICATION_CANCEL_EXTRAS_KEY, notificationId);
		return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_ONE_SHOT);
	}

	public void createNotificationChannel(Context context, String language, NotificationType type) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = context.getResources().getString(R.string.app_name);
			String description = context.getResources().getString(R.string.channel_description, name);

			NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
			if (notificationManager == null || notificationManager.getNotificationChannel("DownloadLanguageModel") != null) {
				return;
			}

			String channelId = "";
			if (type == NotificationType.DOWNLOAD) {
				channelId = "Download ";
			} else if (type == NotificationType.CANCEL) {
				channelId = "Cancel ";
			}
			channelId += language + " language model";

			if (channelId.contains("Download")) {
				if (downloadChannelIds == null) {
					downloadChannelIds = new ArrayMap<>();
				}
				downloadChannelIds.put(language, channelId);
			} else if (channelId.contains("Cancel")) {
				if (cancelChannelIds == null) {
					cancelChannelIds = new ArrayMap<>();
				}
				cancelChannelIds.put(language, channelId);
			}

			NotificationChannel channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
			channel.setDescription(description);
			channel.enableVibration(false);
			channel.enableLights(false);
			channel.setSound(null, null);

			notificationManager.createNotificationChannel(channel);
		}
	}

	public void updateNotificationOnDownloadComplete(String language,
			NotificationCompat.Builder builder, NotificationManagerCompat notificationManager) {

		Integer id = notificationIds.get(language);
		Log.i(TAG, "Updating language model download notification complete with id " + id);

		builder.setContentText("Download complete")
				.setProgress(0, 0, false);
		notificationManager.notify(id, builder.build());
	}

	public void updateNotificationOnDownloadError(String language,
			NotificationCompat.Builder builder, NotificationManagerCompat notificationManager) {

		Integer id = notificationIds.get(language);
		Log.i(TAG, "Updating language model download notification on error with id " + id);

		builder.setContentText("Download error")
				.setProgress(0, 0, false);
		notificationManager.notify(id, builder.build());
	}

	public boolean hasConnection(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager != null
				? connectivityManager.getActiveNetworkInfo() : null;
		return networkInfo != null && networkInfo.isConnected();
	}

	public void registerConnectivityChangeBroadcastReceiver() {
		Log.i(TAG, "Register connectivity change broadcast receiver");
		connectivityChangeBroadcastReceiver = new ConnectivityChangeBroadcastReceiver(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		Context context = stageActivity.getApplicationContext();
		context.registerReceiver(connectivityChangeBroadcastReceiver, intentFilter);
	}

	public void registerCancelDownloadBroadcastReceiver() {
		Log.i(TAG, "Register cancel download broadcast receiver");
		cancelDownloadBroadcastReceiver = new CancelDownloadBroadcastReceiver(this);
		IntentFilter intentFilter = new IntentFilter(NOTIFICATION_CANCEL_ACTION);
		Context context = stageActivity.getApplicationContext();
		context.registerReceiver(cancelDownloadBroadcastReceiver, intentFilter);
	}

	public void unregisterConnectivityChangeBroadcastReceiver() {
		if (connectivityChangeBroadcastReceiver != null) {
			Log.i(TAG, "Unregister connectivity change broadcast receiver");
			Context context = stageActivity.getApplicationContext();
			context.unregisterReceiver(connectivityChangeBroadcastReceiver);
		}
	}

	public void unregisterCancelDownloadBroadcastReceiver() {
		if (cancelDownloadBroadcastReceiver != null) {
			Log.i(TAG, "Unregister cancel download broadcast receiver");
			Context context = stageActivity.getApplicationContext();
			context.unregisterReceiver(cancelDownloadBroadcastReceiver);
		}
	}

	public void updateDownloadNotificationsConnectionAvailable(boolean hasConnection) {
		String status = hasConnection ? "available" : "unavailable";
		Log.i(TAG, "Network " + status + ", updating download notifications");

		Context context = stageActivity.getApplicationContext();
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

		if (notificationIds != null && notificationBuilders != null) {
			for (Map.Entry<String, Integer> entry : notificationIds.entrySet()) {
				NotificationCompat.Builder builder = notificationBuilders.get(entry.getKey());
				if (builder != null) {
					String contentText = hasConnection ? "Download in progress"
							: "No internet connection available";
					builder.setContentText(contentText);
					notificationManager.notify(entry.getValue(), builder.build());
				}
			}
			Log.i(TAG, "Updated download notifications");
		}
	}

	public void registerTranslationListener(TranslateTextFromToAction.TranslationResult translationResult) {
		this.translationResult = translationResult;
	}

	public void setLanguageIdentifier(LanguageIdentifier languageIdentifier) {
		this.languageIdentifier = languageIdentifier;
	}

	public List<String> getModelsOnDevice() {
		return modelsOnDevice;
	}

	public void setModelManager(RemoteModelManager modelManager) {
		this.modelManager = modelManager;
	}

	public void setDownloadManager(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}

	public String getText() {
		return text;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public List<String> getModelsToDownload() {
		return modelsToDownload;
	}

	public Map<String, Boolean> getCanceledModels() {
		return canceledModels;
	}

	public Integer getCanceledNotificationId() {
		return canceledNotificationId;
	}

	public AtomicBoolean getIdentifiedLanguage() {
		return identifiedLanguage;
	}

	public void setTranslationModel(Translator translationModel) {
		this.translationModel = translationModel;
	}

	public void setTranslatorOptions(TranslatorOptions translatorOptions) {
		this.translatorOptions = translatorOptions;
	}

	public void setModelsToDownload(List<String> modelsToDownload) {
		this.modelsToDownload = modelsToDownload;
	}

	public void setCanceledModels(Map<String, Boolean> canceledModels) {
		this.canceledModels = canceledModels;
	}

	public StageActivity getStageActivity() {
		return stageActivity;
	}

	public ConnectivityChangeBroadcastReceiver getConnectivityChangeBroadcastReceiver() {
		return connectivityChangeBroadcastReceiver;
	}

	public AtomicInteger getDownloadCounter() {
		return downloadCounter;
	}

	public AtomicBoolean getTaskCanceled() {
		return taskCanceled;
	}
}
