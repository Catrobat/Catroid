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

package org.catrobat.catroid.scratchconverter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.images.WebImage;
import com.google.common.base.Preconditions;
import com.google.firebase.crash.FirebaseCrash;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.ScratchReconvertDialog;
import org.catrobat.catroid.ui.scratchconverter.BaseInfoViewListener;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.DownloadUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ScratchConversionManager implements ConversionManager {

	private static final String TAG = ScratchConversionManager.class.getSimpleName();

	private Activity currentActivity;
	private final Client client;
	private final boolean verbose;
	private Map<String, Client.DownloadCallback> downloadCallbacks;
	private Set<Client.DownloadCallback> globalDownloadCallbacks;
	private Map<Long, Set<JobViewListener>> jobViewListeners;
	private Set<JobViewListener> globalJobViewListeners;
	private Set<BaseInfoViewListener> baseInfoViewListeners;
	private boolean shutdown;

	@SuppressLint("UseSparseArrays")
	public ScratchConversionManager(final Activity rootActivity, final Client client, final boolean verbose) {
		this.currentActivity = rootActivity;
		this.client = client;
		this.verbose = verbose;
		this.downloadCallbacks = new HashMap<>();
		this.globalDownloadCallbacks = Collections.synchronizedSet(new HashSet<Client.DownloadCallback>());
		client.setConvertCallback(this);
		this.jobViewListeners = Collections.synchronizedMap(new HashMap<Long, Set<JobViewListener>>());
		this.globalJobViewListeners = Collections.synchronizedSet(new HashSet<JobViewListener>());
		this.baseInfoViewListeners = Collections.synchronizedSet(new HashSet<BaseInfoViewListener>());
		this.shutdown = false;
		DownloadUtil.getInstance().setDownloadCallback(this);
	}

	@Override
	public void setCurrentActivity(final Activity activity) {
		currentActivity = activity;
	}

	@Override
	public void addGlobalDownloadCallback(final Client.DownloadCallback callback) {
		globalDownloadCallbacks.add(callback);
	}

	@Override
	public boolean removeGlobalDownloadCallback(final Client.DownloadCallback callback) {
		return globalDownloadCallbacks.remove(callback);
	}

	@Override
	public boolean isJobInProgress(long jobID) {
		return client.isJobInProgress(jobID);
	}

	@Override
	public boolean isJobDownloading(long jobID) {
		return readDownloadStateFromDisk(jobID) == Job.DownloadState.DOWNLOADING;
	}

	@Override
	public int getNumberOfJobsInProgress() {
		return client.getNumberOfJobsInProgress();
	}

	@Override
	public void connectAndAuthenticate() {
		client.connectAndAuthenticate(this);
	}

	@Override
	public void shutdown() {
		shutdown = true;
		DownloadUtil.getInstance().setDownloadCallback(null);
		if (!client.isClosed()) {
			client.close();
		}
	}

	@Override
	public void convertProgram(final long jobID, final String title, final WebImage image, final boolean force) {
		updateDownloadStateOnDisk(jobID, Job.DownloadState.NOT_READY);
		client.convertProgram(jobID, title, image, verbose, force);
	}

	private void closeAllActivities() {
		if (!shutdown) {
			Intent intent = new Intent(currentActivity.getApplicationContext(), MainMenuActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			currentActivity.startActivity(intent);
		}
	}

	// -----------------------------------------------------------------------------------------------------------------
	// ConnectAuthCallback
	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void onSuccess(long clientID) {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(currentActivity.getApplicationContext());
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(Constants.SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME, clientID);
		editor.commit();
		Log.i(TAG, "Connection established (clientID: " + clientID + ")");
		Preconditions.checkState(client.isAuthenticated());
		client.retrieveInfo();
	}

	@Override
	public void onConnectionClosed(ClientException ex) {
		Log.d(TAG, "Connection closed!");
		final String exceptionMessage = ex.getMessage();
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (exceptionMessage != null) {
					Log.e(TAG, exceptionMessage);
				}

				if (!shutdown) {
					ToastUtil.showError(currentActivity, R.string.connection_lost_or_closed_by_server);
				}

				closeAllActivities();
			}
		});
	}

	@Override
	public void onConnectionFailure(final ClientException ex) {
		Log.e(TAG, ex.getMessage());
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastUtil.showError(currentActivity, R.string.connection_failed);
				closeAllActivities();
			}
		});
	}

	@Override
	public void onAuthenticationFailure(final ClientException ex) {
		Log.e(TAG, ex.getMessage());
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastUtil.showError(currentActivity, R.string.authentication_failed);
				closeAllActivities();
			}
		});
	}

	// -----------------------------------------------------------------------------------------------------------------
	// ConversionManager interface
	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void addBaseInfoViewListener(BaseInfoViewListener baseInfoViewListener) {
		baseInfoViewListeners.add(baseInfoViewListener);
	}

	@Override
	public boolean removeBaseInfoViewListener(BaseInfoViewListener baseInfoViewListener) {
		return baseInfoViewListeners.remove(baseInfoViewListener);
	}

	@Override
	public void addGlobalJobViewListener(JobViewListener jobViewListener) {
		globalJobViewListeners.add(jobViewListener);
	}

	@Override
	public boolean removeGlobalJobViewListener(JobViewListener jobViewListener) {
		return globalJobViewListeners.remove(jobViewListener);
	}

	@Override
	public void addJobViewListener(long jobID, JobViewListener jobViewListener) {
		Set<JobViewListener> listeners = jobViewListeners.get(jobID);
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		listeners.add(jobViewListener);
		jobViewListeners.put(jobID, listeners);
	}

	@Override
	public boolean removeJobViewListener(long jobID, JobViewListener jobViewListener) {
		Set<JobViewListener> listeners = jobViewListeners.get(jobID);
		return listeners != null && listeners.remove(jobViewListener);
	}

	@NonNull
	private JobViewListener[] getJobViewListeners(long jobID) {
		final Set<JobViewListener> mergedListenersList = new HashSet<>();
		final Set<JobViewListener> listenersList = jobViewListeners.get(jobID);
		if (listenersList != null) {
			mergedListenersList.addAll(listenersList);
		}
		mergedListenersList.addAll(globalJobViewListeners);
		return mergedListenersList.toArray(new JobViewListener[mergedListenersList.size()]);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// ConvertCallback
	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void onInfo(final float supportedCatrobatLanguageVersion, final Job[] jobs) {
		for (Job job : jobs) {
			job.setDownloadState(readDownloadStateFromDisk(job.getJobID()));
		}

		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "Supported Catrobat Language version: " + supportedCatrobatLanguageVersion);

				for (BaseInfoViewListener viewListener : baseInfoViewListeners) {
					viewListener.onJobsInfo(jobs);
				}

				if (Constants.CURRENT_CATROBAT_LANGUAGE_VERSION < supportedCatrobatLanguageVersion) {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(currentActivity);
					builder.setTitle(R.string.warning);
					builder.setMessage(R.string.error_scratch_converter_outdated_pocketcode_version);
					builder.setNeutralButton(R.string.close, null);
					Dialog errorDialog = builder.create();
					errorDialog.show();
				}
			}
		});
	}

	@Override
	public void onJobScheduled(final Job job) {
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobScheduled(job);
				}
			}
		});
	}

	@Override
	public void onConversionReady(final Job job) {
		Log.i(TAG, "Conversion ready!");
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobReady(job);
				}
			}
		});
	}

	@Override
	public void onConversionStart(final Job job) {
		// Note: this callback-method is not called on UI-thread
		Log.i(TAG, "Conversion started!");
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastUtil.showSuccess(currentActivity, currentActivity.getString(R.string.scratch_conversion_started));
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobStarted(job);
				}
			}
		});
	}

	@Override
	public void onConversionFinished(final Job job, final Client.DownloadCallback downloadCallback,
			final String downloadURL, final Date cachedUTCDate) {
		Log.i(TAG, "Conversion finished!");
		updateDownloadStateOnDisk(job.getJobID(), Job.DownloadState.READY);
		conversionFinished(job, downloadCallback, downloadURL, cachedUTCDate);
	}

	@Override
	public void onConversionAlreadyFinished(Job job, Client.DownloadCallback downloadCallback, String downloadURL) {
		if (readDownloadStateFromDisk(job.getJobID()) == Job.DownloadState.NOT_READY) {
			updateDownloadStateOnDisk(job.getJobID(), Job.DownloadState.READY);
		}
		conversionFinished(job, downloadCallback, downloadURL, null);
	}

	private void conversionFinished(final Job job, final Client.DownloadCallback downloadCallback,
			final String downloadURL, final Date cachedUTCDate) {
		final String baseUrl = Constants.SCRATCH_CONVERTER_BASE_URL;
		final String fullDownloadURL = baseUrl.substring(0, baseUrl.length() - 1) + downloadURL;
		Job.DownloadState localDownloadState = readDownloadStateFromDisk(job.getJobID());

		if (localDownloadState != Job.DownloadState.READY && localDownloadState != Job.DownloadState.DOWNLOADING) {
			return;
		}

		final Job.DownloadState finalLocalDownloadState = localDownloadState;

		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobFinished(job);
				}

				downloadCallbacks.put(downloadURL, downloadCallback);

				if (finalLocalDownloadState == Job.DownloadState.DOWNLOADING) {
					Log.i(TAG, "Download of converted project is already RUNNNING!!");
					onDownloadStarted(fullDownloadURL);
					return;
				}

				Log.i(TAG, "Downloading missed converted project...");
				if (cachedUTCDate != null) {
					final ScratchReconvertDialog reconvertDialog = new ScratchReconvertDialog();
					reconvertDialog.setContext(currentActivity);
					reconvertDialog.setCachedDate(cachedUTCDate);
					reconvertDialog.setReconvertDialogCallback(new ScratchReconvertDialog.ReconvertDialogCallback() {
						@Override
						public void onDownloadExistingProgram() {
							downloadProgram(fullDownloadURL);
						}

						@Override
						public void onReconvertProgram() {
							convertProgram(job.getJobID(), job.getTitle(), job.getImage(), true);
						}

						@Override
						public void onUserCanceledConversion() {
							client.onUserCanceledConversion(job.getJobID());
							for (final JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
								viewListener.onUserCanceledJob(job);
							}
						}
					});
					reconvertDialog.show(currentActivity.getFragmentManager(), ScratchReconvertDialog.DIALOG_FRAGMENT_TAG);
					return;
				}

				downloadProgram(fullDownloadURL);
			}
		});
	}

	private void downloadProgram(final String fullDownloadURL) {
		Log.d(TAG, "Start download: " + fullDownloadURL);
		DownloadUtil.getInstance().prepareDownloadAndStartIfPossible(currentActivity, fullDownloadURL);
	}

	@Override
	public void onConversionFailure(@Nullable final Job job, final ClientException ex) {
		Log.e(TAG, "Conversion failed: " + ex.getMessage());
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (job != null) {
					for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
						viewListener.onJobFailed(job);
					}
					final Resources resources = currentActivity.getResources();
					ToastUtil.showError(currentActivity, resources.getString(R.string.error_specific_scratch_program_conversion_failed_x, job.getTitle()));
				} else {
					ToastUtil.showError(currentActivity, R.string.error_scratch_program_conversion_failed);
					closeAllActivities();
				}
			}
		});
	}

	@Override
	public void onError(final String errorMessage) {
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (BaseInfoViewListener viewListener : baseInfoViewListeners) {
					viewListener.onError(errorMessage);
				}
			}
		});
	}

	@Override
	public void onJobOutput(final Job job, final String[] lines) {
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobOutput(job, lines);
				}
			}
		});
	}

	@Override
	public void onJobProgress(final Job job, final short progress) {
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobViewListeners(job.getJobID())) {
					viewListener.onJobProgress(job, progress);
				}
			}
		});
	}

	private void updateDownloadStateOnDisk(final long jobID, final Job.DownloadState downloadState) {
		Log.d(TAG, "Update download-state of program on disk");
		try {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(currentActivity
					.getApplicationContext());
			SharedPreferences.Editor editor = sharedPref.edit();

			String data = sharedPref.getString(Constants.SCRATCH_CONVERTER_DOWNLOAD_STATE_SHARED_PREFERENCE_NAME, null);
			HashMap<String, String> downloadStates = new HashMap<>();
			if (data != null) {
				JSONObject jsonObject = new JSONObject(data);
				Iterator<String> keysItr = jsonObject.keys();
				while (keysItr.hasNext()) {
					String key = keysItr.next();
					String value = jsonObject.getString(key);
					downloadStates.put(key, value);
				}
			}

			downloadStates.put(Long.toString(jobID), Integer.toString(downloadState.getDownloadStateID()));
			Log.d(TAG, downloadStates.toString());
			editor.putString(Constants.SCRATCH_CONVERTER_DOWNLOAD_STATE_SHARED_PREFERENCE_NAME,
					new JSONObject(downloadStates).toString());
			editor.commit();
		} catch (JSONException e) {
			FirebaseCrash.report(e);
			Log.e(TAG, e.getMessage());
		}
	}

	private Job.DownloadState readDownloadStateFromDisk(final long jobID) {
		Log.d(TAG, "Read download-state of program from disk");

		try {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(currentActivity
					.getApplicationContext());

			String data = sharedPref.getString(Constants.SCRATCH_CONVERTER_DOWNLOAD_STATE_SHARED_PREFERENCE_NAME, null);
			HashMap<String, String> downloadStates = new HashMap<>();
			if (data != null) {
				JSONObject jsonObject = new JSONObject(data);
				Iterator<String> keysItr = jsonObject.keys();
				while (keysItr.hasNext()) {
					String key = keysItr.next();
					String value = jsonObject.getString(key);
					downloadStates.put(key, value);
				}
			}

			String result = downloadStates.get(Long.toString(jobID));
			if (result == null) {
				return Job.DownloadState.NOT_READY;
			}
			return Job.DownloadState.valueOf(Integer.parseInt(result));
		} catch (JSONException e) {
			FirebaseCrash.report(e);
			Log.e(TAG, e.getMessage());
		}
		return Job.DownloadState.NOT_READY;
	}

	// -----------------------------------------------------------------------------------------------------------------
	// DownloadCallback
	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void onDownloadStarted(final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		updateDownloadStateOnDisk(jobID, Job.DownloadState.DOWNLOADING);

		// Note: this callback-method may not be called on UI-thread
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Client.DownloadCallback callback = downloadCallbacks.get(url);
				if (callback != null) {
					callback.onDownloadStarted(url);
				}

				for (final Client.DownloadCallback cb : globalDownloadCallbacks) {
					cb.onDownloadStarted(url);
				}
			}
		});
	}

	@Override
	public void onDownloadProgress(final short progress, final String url) {
		// Note: this callback-method is not called on UI-thread
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Client.DownloadCallback callback = downloadCallbacks.get(url);
				if (callback != null) {
					callback.onDownloadProgress(progress, url);
				}

				for (final Client.DownloadCallback cb : globalDownloadCallbacks) {
					cb.onDownloadProgress(progress, url);
				}
			}
		});
	}

	@Override
	public void onDownloadFinished(final String catrobatProgramName, final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		updateDownloadStateOnDisk(jobID, Job.DownloadState.DOWNLOADED);

		// Note: this callback-method is not called on UI-thread
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Client.DownloadCallback callback = downloadCallbacks.get(url);
				if (callback != null) {
					callback.onDownloadFinished(catrobatProgramName, url);
				}

				for (final Client.DownloadCallback cb : globalDownloadCallbacks) {
					cb.onDownloadFinished(catrobatProgramName, url);
				}
			}
		});
	}

	@Override
	public void onUserCanceledDownload(String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		updateDownloadStateOnDisk(jobID, Job.DownloadState.CANCELED);

		final Client.DownloadCallback callback = downloadCallbacks.get(url);
		if (callback != null) {
			callback.onUserCanceledDownload(url);
		}

		for (final Client.DownloadCallback cb : globalDownloadCallbacks) {
			cb.onUserCanceledDownload(url);
		}
	}
}
