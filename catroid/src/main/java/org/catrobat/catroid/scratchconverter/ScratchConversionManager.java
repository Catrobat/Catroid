/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScratchConversionManager implements ConversionManager {

	private static final String TAG = ScratchConversionManager.class.getSimpleName();

	private Activity currentActivity;
	private final Client client;
	private final boolean verbose;
	private Set<Client.DownloadFinishedCallback> delegateCallbackSet;
	private Map<Long, Set<JobViewListener>> jobConsoleViewListeners;
	private Set<JobViewListener> globalJobViewListeners;
	private Set<BaseInfoViewListener> baseInfoViewListeners;
	private boolean shutdown;

	@SuppressLint("UseSparseArrays")
	public ScratchConversionManager(final Activity rootActivity, final Client client, final boolean verbose) {
		this.currentActivity = rootActivity;
		this.client = client;
		this.verbose = verbose;
		this.delegateCallbackSet = new HashSet<>();
		client.setConvertCallback(this);
		this.jobConsoleViewListeners = Collections.synchronizedMap(new HashMap<Long, Set<JobViewListener>>());
		this.globalJobViewListeners = Collections.synchronizedSet(new HashSet<JobViewListener>());
		this.baseInfoViewListeners = Collections.synchronizedSet(new HashSet<BaseInfoViewListener>());
		this.shutdown = false;
	}

	public void setCurrentActivity(final Activity activity) {
		currentActivity = activity;
	}

	public void addDownloadFinishedCallback(final Client.DownloadFinishedCallback callback) {
		delegateCallbackSet.add(callback);
	}

	@Override
	public boolean isJobInProgress(long jobID) {
		return client.isJobInProgress(jobID);
	}

	@Override
	public int getNumberOfJobsInProgress() {
		return client.getNumberOfJobsInProgress();
	}

	public void removeDownloadFinishedCallback(final Client.DownloadFinishedCallback callback) {
		delegateCallbackSet.remove(callback);
	}

	public void connectAndAuthenticate() {
		client.connectAndAuthenticate(this);
	}

	public void shutdown() {
		shutdown = true;
		if (!client.isClosed()) {
			client.close();
		}
	}

	public void convertProgram(final long jobID, final String title, final WebImage image, final boolean force) {
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
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ToastUtil.showSuccess(currentActivity, R.string.connection_established);
			}
		});
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
					ToastUtil.showError(currentActivity, R.string.connection_closed);
				} else {
					ToastUtil.showSuccess(currentActivity, R.string.connection_closed);
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
	public void addGlobalJobConsoleViewListener(JobViewListener jobViewListener) {
		globalJobViewListeners.add(jobViewListener);
	}

	@Override
	public void addJobConsoleViewListener(long jobID, JobViewListener jobViewListener) {
		Set<JobViewListener> listeners = jobConsoleViewListeners.get(jobID);
		if (listeners == null) {
			listeners = new HashSet<>();
		}
		listeners.add(jobViewListener);
		jobConsoleViewListeners.put(jobID, listeners);
	}

	@Override
	public boolean removeJobConsoleViewListener(long jobID, JobViewListener jobViewListener) {
		Set<JobViewListener> listeners = jobConsoleViewListeners.get(jobID);
		return listeners != null && listeners.remove(jobViewListener);
	}

	@NonNull
	private JobViewListener[] getJobConsoleViewListeners(long jobID) {
		final Set<JobViewListener> mergedListenersList = new HashSet<>();
		final Set<JobViewListener> listenersList = jobConsoleViewListeners.get(jobID);
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
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
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
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
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
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
					viewListener.onJobStarted(job);
				}
			}
		});
	}

	@Override
	public void onConversionFinished(final Job job, final Client.DownloadFinishedCallback downloadFinishedCallback,
			final String downloadURL, final Date cachedUTCDate) {
		Log.i(TAG, "Conversion finished!");
		final ScratchConversionManager conversionManager = this;
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
					viewListener.onJobFinished(job);
				}

				final Client.DownloadFinishedCallback[] callbacks;
				callbacks = new Client.DownloadFinishedCallback[] { downloadFinishedCallback, conversionManager };

				if (cachedUTCDate != null) {
					final ScratchReconvertDialog reconvertDialog = new ScratchReconvertDialog();
					reconvertDialog.setContext(currentActivity);
					reconvertDialog.setCachedDate(cachedUTCDate);
					reconvertDialog.setReconvertDialogCallback(new ScratchReconvertDialog.ReconvertDialogCallback() {
						@Override
						public void onDownloadExistingProgram() {
							downloadProgram(downloadURL, callbacks);
						}

						@Override
						public void onReconvertProgram() {
							client.convertProgram(job.getJobID(), job.getTitle(), job.getImage(), verbose, true);
						}

						@Override
						public void onUserCanceledConversion() {
							client.onUserCanceledConversion(job.getJobID());
							for (final JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
								viewListener.onUserCanceledJob(job);
							}
						}
					});
					reconvertDialog.show(currentActivity.getFragmentManager(), ScratchReconvertDialog.DIALOG_FRAGMENT_TAG);
					return;
				}
				downloadProgram(downloadURL, callbacks);
			}
		});
	}

	@Override
	public void onConversionFailure(@Nullable final Job job, final ClientException ex) {
		Log.e(TAG, "Conversion failed: " + ex.getMessage());
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (job != null) {
					for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
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
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
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
				for (JobViewListener viewListener : getJobConsoleViewListeners(job.getJobID())) {
					viewListener.onJobProgress(job, progress);
				}
			}
		});
	}

	private void downloadProgram(final String downloadURL, final Client.DownloadFinishedCallback[] callbacks) {
		// Note: this callback-method is not called on UI-thread
		final String baseUrl = Constants.SCRATCH_CONVERTER_BASE_URL;
		final String fullDownloadUrl = baseUrl.substring(0, baseUrl.length() - 1) + downloadURL;
		Log.d(TAG, "Start download: " + fullDownloadUrl);
		DownloadUtil.getInstance().prepareDownloadAndStartIfPossible(currentActivity, fullDownloadUrl, callbacks);
	}

	// -----------------------------------------------------------------------------------------------------------------
	// DownloadFinishedCallback
	// -----------------------------------------------------------------------------------------------------------------
	@Override
	public void onDownloadStarted(final String url) {
		// Note: this callback-method may not be called on UI-thread
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (final Client.DownloadFinishedCallback callback : delegateCallbackSet) {
					callback.onDownloadStarted(url);
				}
			}
		});
	}

	@Override
	public void onDownloadFinished(final String catrobatProgramName, final String url) {
		// Note: this callback-method is not called on UI-thread
		currentActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (final Client.DownloadFinishedCallback callback : delegateCallbackSet) {
					callback.onDownloadFinished(catrobatProgramName, url);
				}
			}
		});
	}

	@Override
	public void onUserCanceledDownload(String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		client.cancelDownload(jobID);

		for (final Client.DownloadFinishedCallback callback : delegateCallbackSet) {
			callback.onUserCanceledDownload(url);
		}
	}
}
