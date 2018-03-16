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

package org.catrobat.catroid.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.ScratchConverterActivity;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchJobAdapter;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.scratchconverter.BaseInfoViewListener;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScratchConverterSlidingUpPanelFragment extends Fragment
		implements BaseInfoViewListener, JobViewListener, Client.DownloadCallback,
		ProjectLoaderTask.ProjectLoaderListener {
	private static final String TAG = ScratchConverterSlidingUpPanelFragment.class.getSimpleName();

	private Map<Long, Job> downloadJobsMap = Collections.synchronizedMap(new LinkedHashMap<Long, Job>());
	private Map<Long, String> downloadedProgramsMap = Collections.synchronizedMap(new LinkedHashMap<Long, String>());

	private ScratchJobAdapter runningJobsAdapter;
	private ScratchJobAdapter finishedFailedJobsAdapter;

	public ScratchJobAdapter getFinishedFailedJobsAdapter() {
		return finishedFailedJobsAdapter;
	}

	;
	private List<Job> runningJobs;
	private List<Job> finishedFailedJobs;

	public Map<Long, String> getDownloadedProgramsMap() {
		return this.downloadedProgramsMap;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		runningJobs = new ArrayList<>();
		finishedFailedJobs = new ArrayList<>();

		final View rootView = inflater.inflate(R.layout.fragment_scratch_converter_sliding_up_panel, container, false);

		rootView.findViewById(R.id.scratch_convert_status_text).setVisibility(View.VISIBLE);
		rootView.findViewById(R.id.scratch_convert_progress_layout).setVisibility(View.GONE);
		rootView.findViewById(R.id.scratch_conversion_list).setVisibility(View.GONE);
		rootView.findViewById(R.id.scratch_converted_programs_list).setVisibility(View.GONE);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initAdapters();
	}

	public void scrollUpPanelScrollView() {
		((ScrollView) getActivity().findViewById(R.id.scratch_conversion_scroll_view)).fullScroll(ScrollView.FOCUS_UP);
	}

	private void initAdapters() {
		Preconditions.checkState(getActivity() != null);
		runningJobsAdapter = new ScratchJobAdapter(runningJobs);
		((RecyclerView) getActivity().findViewById(R.id.scratch_conversion_list_view)).setAdapter(runningJobsAdapter);
		getActivity().findViewById(R.id.scratch_conversion_list).setVisibility(View.GONE);
		finishedFailedJobsAdapter = new ScratchJobAdapter(finishedFailedJobs);
		((RecyclerView) getActivity().findViewById(R.id.scratch_converted_programs_list_view))
				.setAdapter(finishedFailedJobsAdapter);
		getActivity().findViewById(R.id.scratch_converted_programs_list).setVisibility(View.GONE);
	}

	public void rotateImageButton(float degrees) {
		ImageView upDownArrowImageView = (ImageView) getActivity().findViewById(R.id.scratch_up_down_image_button);
		upDownArrowImageView.setAlpha(Math.max(1.0f - (float) Math.sin(degrees / 360.0f * 2.0f * Math.PI), 0.3f));
		upDownArrowImageView.setRotation(degrees);
	}

	public boolean hasVisibleJobs() {
		return runningJobs.size() > 0 || finishedFailedJobs.size() > 0;
	}

	private void setIconImageView(final WebImage webImage) {
		final Activity activity = getActivity();
		if (activity != null && activity.getResources() != null && webImage != null && webImage.getUrl() != null) {
			final int height = activity.getResources().getDimensionPixelSize(R.dimen.scratch_project_tiny_thumbnail_height);
			final String originalImageURL = webImage.getUrl().toString();

			// load image but only thumnail!
			// in order to download only thumbnail version of the original image
			// we have to reduce the image size in the URL
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(getActivity()).load(thumbnailImageURL).into((ImageView) (getActivity().findViewById(R.id
					.scratch_convert_icon)));
		} else {
			((ImageView) getActivity().findViewById(R.id.scratch_convert_icon)).setImageBitmap(null);
		}
	}

	private void updateAdapterSingleJob(final Job job) {
		if (job.isInProgress()) {
			if (finishedFailedJobs.contains(job)) {
				finishedFailedJobs.remove(job);
				finishedFailedJobsAdapter.notifyDataSetChanged();
				if (finishedFailedJobs.size() == 0) {
					getActivity().findViewById(R.id.scratch_converted_programs_list).setVisibility(View.GONE);
				}
			}

			if (!runningJobs.contains(job)) {
				runningJobs.add(0, job);
				runningJobsAdapter.notifyDataSetChanged();
				getActivity().findViewById(R.id.scratch_conversion_list).setVisibility(View.VISIBLE);
			} else {
				runningJobsAdapter.notifyDataSetChanged();
			}
			return;
		}

		if (runningJobs.contains(job)) {
			runningJobs.remove(job);
			runningJobsAdapter.notifyDataSetChanged();
			if (runningJobs.size() == 0) {
				getActivity().findViewById(R.id.scratch_conversion_list).setVisibility(View.GONE);
			}
		}

		if (!finishedFailedJobs.contains(job)) {
			finishedFailedJobs.add(0, job);
			finishedFailedJobsAdapter.notifyDataSetChanged();
			getActivity().findViewById(R.id.scratch_converted_programs_list).setVisibility(View.VISIBLE);
		} else {
			finishedFailedJobsAdapter.notifyDataSetChanged();
		}
	}

	private void updateConvertPanel(Job job, int statusTextID, boolean showProgress, int progress) {
		updateAdapterSingleJob(job);
		HashSet allRunningJobs = new HashSet<>(runningJobs);
		allRunningJobs.addAll(downloadJobsMap.values());
		if (allRunningJobs.size() > 1) {
			showPanelBarSummary();
			return;
		}

		((TextView) getActivity().findViewById(R.id.scratch_convert_headline)).setText(job.getTitle());

		if (showProgress) {
			((ProgressBar) getActivity().findViewById(R.id.scratch_convert_progress_bar)).setProgress(progress);
			((TextView) getActivity().findViewById(R.id.scratch_convert_status_progress_text)).setText(
					String.format(Locale.getDefault(), "%1$d%%", progress));

			getActivity().findViewById(R.id.scratch_convert_status_text).setVisibility(View.GONE);
			getActivity().findViewById(R.id.scratch_convert_progress_layout).setVisibility(View.VISIBLE);
		} else {
			((TextView) getActivity().findViewById(R.id.scratch_convert_status_text)).setText(statusTextID);
			getActivity().findViewById(R.id.scratch_convert_status_text).setVisibility(View.VISIBLE);
			getActivity().findViewById(R.id.scratch_convert_progress_layout).setVisibility(View.GONE);
		}

		runningJobsAdapter.notifyDataSetChanged();
		setIconImageView(job.getImage());
	}

	private void showPanelBarSummary() {
		int numberFinishedJobs = 0;
		WebImage webImage = null;

		if (!runningJobs.isEmpty()) {
			for (Job job : runningJobs) {
				if (webImage == null && job.getImage() != null && job.getImage().getUrl() != null) {
					webImage = job.getImage();
					break;
				}
			}
		} else if (!downloadJobsMap.isEmpty()) {
			for (Map.Entry<Long, Job> entry : downloadJobsMap.entrySet()) {
				Job job = entry.getValue();
				if (webImage == null && job.getImage() != null && job.getImage().getUrl() != null) {
					webImage = job.getImage();
					break;
				}
			}
		}

		for (Job job : finishedFailedJobs) {
			if (webImage == null && job.getImage() != null && job.getImage().getUrl() != null) {
				webImage = job.getImage();
			}
			if (job.getState() == Job.State.FINISHED && job.getDownloadState() != Job.DownloadState.DOWNLOADING) {
				numberFinishedJobs++;
			}
		}

		HashSet allRunningJobs = new HashSet<>(runningJobs);
		allRunningJobs.addAll(downloadJobsMap.values());

		int totalRunningJobs = allRunningJobs.size();
		int totalFinishedJobs = numberFinishedJobs;
		((TextView) getActivity().findViewById(R.id.scratch_convert_headline)).setText(getResources().getQuantityString(R.plurals.status_in_progress_x_jobs,
				totalRunningJobs, totalRunningJobs));
		((TextView) getActivity().findViewById(R.id.scratch_convert_status_text)).setText(getResources().getQuantityString(R.plurals.status_completed_x_jobs,
				totalFinishedJobs, totalFinishedJobs));
		getActivity().findViewById(R.id.scratch_convert_status_text).setVisibility(View.VISIBLE);
		getActivity().findViewById(R.id.scratch_convert_progress_layout).setVisibility(View.GONE);
		setIconImageView(webImage);
	}

	private void downloadInProgress(int progress, String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == Constants.INVALID_SCRATCH_PROGRAM_ID) {
			return;
		}

		final Job job = downloadJobsMap.get(jobID);
		if (job == null) {
			Log.e(TAG, "No job with ID " + jobID + " found in downloadJobsMap!");
			return;
		}
		job.setDownloadState(Job.DownloadState.DOWNLOADING);
		job.setDownloadProgress((short) progress);
		updateConvertPanel(job, R.string.status_downloading, true, progress);
	}

	//------------------------------------------------------------------------------------------------------------------
	// BaseInfoViewListener callbacks
	//------------------------------------------------------------------------------------------------------------------
	@Override
	public void onJobsInfo(final Job[] jobs) {
		if (jobs == null || jobs.length == 0) {
			((ScratchConverterActivity) getActivity()).hideSlideUpPanelBar();
			return;
		}

		runningJobs.clear();
		finishedFailedJobs.clear();

		for (Job job : jobs) {
			if (job.isInProgress()) {
				runningJobs.add(job);
			} else if (job.getState() != Job.State.UNSCHEDULED) {
				finishedFailedJobs.add(job);
			}
		}

		if (runningJobs.size() > 0) {
			getActivity().findViewById(R.id.scratch_conversion_list).setVisibility(View.VISIBLE);
			runningJobsAdapter.notifyDataSetChanged();
		} else {
			getActivity().findViewById(R.id.scratch_conversion_list).setVisibility(View.GONE);
		}

		if (finishedFailedJobs.size() > 0) {
			getActivity().findViewById(R.id.scratch_converted_programs_list).setVisibility(View.VISIBLE);
			finishedFailedJobsAdapter.notifyDataSetChanged();
		} else {
			getActivity().findViewById(R.id.scratch_converted_programs_list).setVisibility(View.GONE);
		}

		if (hasVisibleJobs()) {
			((ScratchConverterActivity) getActivity()).showSlideUpPanelBar(0);
		}

		showPanelBarSummary();
		scrollUpPanelScrollView();
	}

	@Override
	public void onError(final String errorMessage) {
		if (!Looper.getMainLooper().equals(Looper.myLooper())) {
			throw new AssertionError("You should not change the UI from any thread except UI thread!");
		}

		Log.e(TAG, "An error occurred: " + errorMessage);
		ToastUtil.showError(getActivity(), errorMessage);
		showPanelBarSummary();
	}

	//------------------------------------------------------------------------------------------------------------------
	// JobViewListener callbacks
	//------------------------------------------------------------------------------------------------------------------
	@Override
	public void onJobScheduled(final Job job) {
		((ScratchConverterActivity) getActivity()).showSlideUpPanelBar(0);
		updateConvertPanel(job, R.string.status_scheduled, false, 0);
	}

	@Override
	public void onJobReady(final Job job) {
		job.setProgress((short) 0);
		updateConvertPanel(job, R.string.status_waiting_for_worker, false, 0);
	}

	@Override
	public void onJobStarted(final Job job) {
		job.setProgress((short) 0);
		updateConvertPanel(job, R.string.status_started, false, 0);
	}

	@Override
	public void onJobProgress(final Job job, final short progress) {
		updateConvertPanel(job, R.string.status_started, true, progress);
	}

	@Override
	public void onJobOutput(final Job job, @NonNull final String[] lines) {
		// reserved for later use (i.e. next ScratchConverter release)!
	}

	@Override
	public void onJobFinished(final Job job) {
		downloadJobsMap.put(job.getJobID(), job);
		updateConvertPanel(job, R.string.status_conversion_finished, false, 0);
	}

	@Override
	public void onJobFailed(final Job job) {
		updateConvertPanel(job, R.string.status_conversion_failed, false, 0);
	}

	@Override
	public void onUserCanceledJob(Job job) {
		updateConvertPanel(job, R.string.status_conversion_canceled, false, 0);
	}

	@Override
	public void onDownloadStarted(String url) {
		downloadInProgress(0, url);
	}

	@Override
	public void onDownloadProgress(short progress, String url) {
		downloadInProgress(progress, url);
	}

	@Override
	public void onDownloadFinished(final String catrobatProgramName, final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == Constants.INVALID_SCRATCH_PROGRAM_ID) {
			Log.w(TAG, "Received download-finished call for program: '" + catrobatProgramName + "' with invalid url: " + url);
			return;
		}

		final Job job = downloadJobsMap.remove(jobID);
		downloadedProgramsMap.put(jobID, catrobatProgramName);
		if (job == null) {
			Log.e(TAG, "No job with ID " + jobID + " found in downloadJobsMap!");
			return;
		}

		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
			r.play();
		} catch (Exception ex) {
			Log.e(TAG, ex.getMessage());
		}

		job.setDownloadState(Job.DownloadState.DOWNLOADED);
		updateConvertPanel(job, R.string.status_download_finished, false, 0);
	}

	@Override
	public void onUserCanceledDownload(final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == Constants.INVALID_SCRATCH_PROGRAM_ID) {
			Log.w(TAG, "Received download-canceled call for program with invalid url: " + url);
			return;
		}

		final Job job = downloadJobsMap.remove(jobID);
		if (job == null) {
			Log.e(TAG, "No job with ID " + jobID + " found in downloadJobsMap!");
			return;
		}

		job.setDownloadState(Job.DownloadState.CANCELED);
		updateConvertPanel(job, R.string.status_download_canceled, false, 0);
	}

	public void onItemClick(Job job) {
		if (!Looper.getMainLooper().equals(Looper.myLooper())) {
			throw new AssertionError("You should not change the UI from any thread except UI thread!");
		}
		if (job == null) {
			Log.e(TAG, "Job not found in runningJobsAdapter!");
			return;
		}

		if (job.getState() == Job.State.FAILED) {
			ToastUtil.showError(getActivity(), R.string.error_cannot_open_failed_scratch_program);
			return;
		}

		String catrobatProgramName = downloadedProgramsMap.get(job.getJobID());
		catrobatProgramName = catrobatProgramName == null ? job.getTitle() : catrobatProgramName;

		if (job.getDownloadState() == Job.DownloadState.DOWNLOADING) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.warning);
			builder.setMessage(R.string.error_cannot_open_currently_downloading_scratch_program);
			builder.setNeutralButton(R.string.close, null);
			Dialog errorDialog = builder.create();
			errorDialog.show();
			return;
		}

		if (job.getDownloadState() == Job.DownloadState.NOT_READY || job.getDownloadState() == Job.DownloadState.CANCELED) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.warning);
			builder.setMessage(R.string.error_cannot_open_not_yet_downloaded_scratch_program);
			builder.setNeutralButton(R.string.close, null);
			Dialog errorDialog = builder.create();
			errorDialog.show();
			return;
		}

		if (!StorageHandler.getInstance().projectExists(catrobatProgramName)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.warning);
			builder.setMessage(R.string.error_cannot_open_not_existing_scratch_program);
			builder.setNeutralButton(R.string.close, null);
			Dialog errorDialog = builder.create();
			errorDialog.show();
			return;
		}

		ProjectLoaderTask loaderTask = new ProjectLoaderTask(getActivity(), this);
		loaderTask.execute(catrobatProgramName);
	}

	@Override
	public void onLoadFinished(boolean success, String message) {
		if (success) {
			getActivity().startActivity(new Intent(getActivity(), ProjectActivity.class));
		} else {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_existing_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
		}
	}
}
