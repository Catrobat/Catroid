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
package org.catrobat.catroid.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.ScratchConversionManager;
import org.catrobat.catroid.scratchconverter.WebSocketClient;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.WebSocketMessageListener;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchJobAdapter;
import org.catrobat.catroid.ui.recyclerview.fragment.scratchconverter.ScratchProgramsFragment;
import org.catrobat.catroid.ui.recyclerview.fragment.scratchconverter.ScratchSearchResultsFragment;
import org.catrobat.catroid.ui.scratchconverter.BaseInfoViewListener;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SCRATCH_CONVERTER_CLIENT_ID_PREFERENCE_KEY;

public class ScratchConverterActivity extends BaseActivity implements
		BaseInfoViewListener,
		JobViewListener,
		Client.ProjectDownloadCallback {

	public static final String TAG = ScratchConverterActivity.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({FRAGMENT_SEARCH, FRAGMENT_PROJECTS})
	@interface FragmentPosition {}
	public static final int FRAGMENT_SEARCH = 0;
	public static final int FRAGMENT_PROJECTS = 1;

	private static Client client;

	public static void setClient(final Client converterClient) {
		client = converterClient;
	}

	private List<Job> runningJobs = new ArrayList<>();
	private List<Job> finishedJobs = new ArrayList<>();

	private ConversionManager conversionManager;
	private ScratchSearchResultsFragment searchResultsFragment;
	private ScratchProgramsFragment scratchProjectsFragment;

	private OnJobListListener jobListListener;

	private View bottomBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scratch_converter);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		String scratchConverter = getString(R.string.main_menu_scratch_converter);
		SpannableString scratchConverterBeta = new SpannableString(scratchConverter
				+ " "
				+ getString(R.string.beta));
		scratchConverterBeta.setSpan(
				new ForegroundColorSpan(getResources().getColor(R.color.beta_label_color)),
				scratchConverter.length(), scratchConverterBeta.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(scratchConverterBeta);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		long clientID = sharedPreferences
				.getLong(SCRATCH_CONVERTER_CLIENT_ID_PREFERENCE_KEY, Client.INVALID_CLIENT_ID);

		client = new WebSocketClient(clientID, new WebSocketMessageListener());
		conversionManager = new ScratchConversionManager(this, client, false);
		conversionManager.addBaseInfoViewListener(this);
		conversionManager.addGlobalJobViewListener(this);
		conversionManager.addGlobalDownloadCallback(this);

		searchResultsFragment = new ScratchSearchResultsFragment();
		searchResultsFragment.setConversionManager(conversionManager);

		scratchProjectsFragment = new ScratchProgramsFragment();
		scratchProjectsFragment.initializeAdapters(new ScratchJobAdapter(runningJobs),
				new ScratchJobAdapter(finishedJobs));

		jobListListener = scratchProjectsFragment;

		bottomBar = findViewById(R.id.bottom_bar);
		bottomBar.setVisibility(View.GONE);

		bottomBar.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchResultsFragment.isVisible()) {
					switchToFragment(FRAGMENT_PROJECTS);
				}
				if (scratchProjectsFragment.isVisible()) {
					switchToFragment(FRAGMENT_SEARCH);
				}
			}
		});

		searchResultsFragment.setConversionManager(conversionManager);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, searchResultsFragment)
				.commit();
	}

	private void switchToFragment(@FragmentPosition int fragmentPosition) {
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

		switch (fragmentPosition) {
			case FRAGMENT_SEARCH:
				if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
					getSupportFragmentManager().popBackStack();
				} else {
					fragmentTransaction.replace(R.id.fragment_container, searchResultsFragment);
				}
				break;
			case FRAGMENT_PROJECTS:
				fragmentTransaction
						.replace(R.id.fragment_container, scratchProjectsFragment)
						.addToBackStack(ScratchProgramsFragment.TAG);
				break;
			default:
				return;
		}

		fragmentTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		conversionManager.setCurrentActivity(this);
		if (!client.isAuthenticated()) {
			conversionManager.connectAndAuthenticate();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		conversionManager.shutdown();
	}

	@Override
	public void onJobsInfo(Job[] jobs) {
		runningJobs.clear();
		finishedJobs.clear();

		for (Job job : jobs) {
			if (job.isInProgress()) {
				runningJobs.add(job);
			} else if (job.getState() != Job.State.UNSCHEDULED) {
				finishedJobs.add(job);
			}
		}

		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	private void updateBottomBar() {
		TextView titleView = bottomBar.findViewById(R.id.title_view);
		TextView detailsView = bottomBar.findViewById(R.id.details_view);

		titleView.setText(getResources().getQuantityString(R.plurals.status_in_progress_x_jobs,
				runningJobs.size(),
				runningJobs.size()));

		detailsView.setText(getResources().getQuantityString(R.plurals.status_completed_x_jobs,
				finishedJobs.size(),
				finishedJobs.size()));

		if (runningJobs.size() > 0 || finishedJobs.size() > 0) {
			bottomBar.setVisibility(View.VISIBLE);
		} else {
			bottomBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onError(String errorMessage) {
	}

	@Override
	public void onJobScheduled(Job job) {
		if (!listContainsJob(runningJobs, job)) {
			runningJobs.add(0, job);
		}
		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	@Override
	public void onJobReady(Job job) {
		jobListListener.onJobListChanged();
	}

	@Override
	public void onJobStarted(Job job) {
		if (!listContainsJob(runningJobs, job)) {
			runningJobs.add(0, job);
		}
		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	@Override
	public void onJobProgress(Job job, short progress) {
	}

	@Override
	public void onJobOutput(Job job, @NonNull String[] lines) {
	}

	@Override
	public void onJobFinished(Job job) {
		removeFromList(runningJobs, job);
		if (!listContainsJob(finishedJobs, job)) {
			finishedJobs.add(0, job);
		}
		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	@Override
	public void onJobFailed(Job job) {
		removeFromList(runningJobs, job);
		if (!listContainsJob(finishedJobs, job)) {
			finishedJobs.add(0, job);
		}
		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	@Override
	public void onUserCanceledJob(Job job) {
		removeFromList(runningJobs, job);
		if (!listContainsJob(finishedJobs, job)) {
			finishedJobs.add(0, job);
		}
		updateBottomBar();
		jobListListener.onJobListChanged();
	}

	@Override
	public void onDownloadStarted(String url) {
		long jobId = Utils.extractScratchJobIDFromURL(url);

		for (Job job : finishedJobs) {
			if (job.getJobID() == jobId) {
				job.setDownloadState(Job.DownloadState.DOWNLOADING);
			}
		}

		jobListListener.onJobListChanged();
	}

	@Override
	public void onDownloadProgress(int progress, String url) {
	}

	@Override
	public void onDownloadFinished(String catrobatProgramName, String url) {
		long jobId = Utils.extractScratchJobIDFromURL(url);

		for (Job job : finishedJobs) {
			if (job.getJobID() == jobId) {
				job.setDownloadState(Job.DownloadState.DOWNLOADED);
			}
		}

		jobListListener.onJobListChanged();
	}

	@Override
	public void onUserCanceledDownload(String url) {
		long jobId = Utils.extractScratchJobIDFromURL(url);

		for (Job job : finishedJobs) {
			if (job.getJobID() == jobId) {
				job.setDownloadState(Job.DownloadState.CANCELED);
			}
		}

		jobListListener.onJobListChanged();
	}

	private boolean listContainsJob(List<Job> list, Job job) {
		for (Job jobInList : list) {
			if (jobInList.getJobID() == job.getJobID()) {
				return true;
			}
		}
		return false;
	}

	private boolean removeFromList(List<Job> list, Job job) {
		for (Job jobInList : list) {
			if (jobInList.getJobID() == job.getJobID()) {
				list.remove(jobInList);
				return true;
			}
		}

		return false;
	}

	public interface OnJobListListener {

		void onJobListChanged();
	}
}
