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
package org.catrobat.catroid.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.ScratchConversionManager;
import org.catrobat.catroid.scratchconverter.WebSocketClient;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.scratchconverter.protocol.WebSocketMessageListener;
import org.catrobat.catroid.ui.fragment.ScratchConverterSlidingUpPanelFragment;
import org.catrobat.catroid.ui.fragment.ScratchProgramsListFragment;
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.asynctask.ProjectLoaderTask;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.ServerCalls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScratchConverterActivity extends BaseActivity implements
		SlidingUpPanelLayout.PanelSlideListener,
		RVAdapter.OnItemClickListener<Job> {

	private static final String TAG = ScratchConverterActivity.class.getSimpleName();

	private static Client client = null;
	private static ScratchDataFetcher dataFetcher = ServerCalls.getInstance();

	private SlidingUpPanelLayout slidingLayout;
	private ConversionManager conversionManager;

	private ScratchConverterSlidingUpPanelFragment getSlidingUpFragment() {
		return (ScratchConverterSlidingUpPanelFragment) getFragmentManager().findFragmentById(R.id
				.fragment_scratch_converter_sliding_up_panel);
	}

	private ScratchProgramsListFragment getSearchFragment() {
		return (ScratchProgramsListFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_search_scratch_programs);
	}

	// dependency-injection for testing with mock object
	public static void setDataFetcher(final ScratchDataFetcher fetcher) {
		dataFetcher = fetcher;
	}

	public static void setClient(final Client converterClient) {
		client = converterClient;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scratch_converter);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		String scratchConverter = getString(R.string.main_menu_scratch_converter);
		SpannableString scratchConverterBeta = new SpannableString(scratchConverter + " "
				+ getString(R.string.beta));
		scratchConverterBeta.setSpan(
				new ForegroundColorSpan(
						getResources().getColor(R.color.beta_label_color)),
				scratchConverter.length(), scratchConverterBeta.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getSupportActionBar().setTitle(scratchConverterBeta);

		getSearchFragment().setDataFetcher(dataFetcher);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		long clientID = settings.getLong(Constants.SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME,
				Client.INVALID_CLIENT_ID);

		client = new WebSocketClient(clientID, new WebSocketMessageListener());

		conversionManager = new ScratchConversionManager(this, client, false);
		conversionManager.setCurrentActivity(this);
		conversionManager.addGlobalDownloadCallback(getSlidingUpFragment());
		conversionManager.addBaseInfoViewListener(getSlidingUpFragment());
		conversionManager.addGlobalJobViewListener(getSlidingUpFragment());
		getSearchFragment().setConversionManager(conversionManager);

		slidingLayout = findViewById(R.id.sliding_layout);
		slidingLayout.addPanelSlideListener(this);
		hideSlideUpPanelBar();
	}

	@Override
	protected void onPostCreate(Bundle savedBundle) {
		super.onPostCreate(savedBundle);
		getSlidingUpFragment().getFinishedFailedJobsAdapter().setOnItemClickListener(this);
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
		conversionManager.removeGlobalDownloadCallback(getSlidingUpFragment());
		conversionManager.removeBaseInfoViewListener(getSlidingUpFragment());
		conversionManager.removeGlobalJobViewListener(getSlidingUpFragment());
	}

	public void convertProjects(List<ScratchProgramData> programList) {
		final int numberOfJobsInProgress = conversionManager.getNumberOfJobsInProgress();
		if (numberOfJobsInProgress + programList.size() > Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
			ToastUtil.showError(this, getResources().getQuantityString(
					R.plurals.error_cannot_convert_more_than_x_programs,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT));
			return;
		}

		int counter = 0;

		for (ScratchProgramData programData : programList) {
			if (Utils.isDeprecatedScratchProgram(programData)) {
				final Date releasePublishedDate = Utils.getScratchSecondReleasePublishedDate();
				java.text.DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

				ToastUtil.showError(this, getString(R.string.error_cannot_convert_deprecated_scratch_program_x_x,
						programData.getTitle(), dateFormat.format(releasePublishedDate)));
				continue;
			}

			if (conversionManager.isJobInProgress(programData.getId())) {
				continue;
			}

			conversionManager.convertProgram(programData.getId(), programData.getTitle(), programData.getImage(), false);
			counter++;
		}

		if (counter > 0) {
			ToastUtil.showSuccess(this, getResources().getQuantityString(R.plurals.scratch_conversion_scheduled_x,
					counter, counter));
		}
	}

	public boolean isSlideUpPanelEmpty() {
		return !getSlidingUpFragment().hasVisibleJobs();
	}

	public void showSlideUpPanelBar(final long delayMillis) {
		final int marginTop = getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_top);
		final int marginBottom = getResources().getDimensionPixelSize(
				R.dimen.scratch_project_search_list_view_margin_bottom);

		if (delayMillis > 0) {
			slidingLayout.postDelayed(new Runnable() {
				public void run() {
					slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
					getSearchFragment().setSearchResultsListViewMargin(0, marginTop, 0, marginBottom);
				}
			}, delayMillis);
		} else {
			slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			getSearchFragment().setSearchResultsListViewMargin(0, marginTop, 0, marginBottom);
		}
	}

	public void hideSlideUpPanelBar() {
		int marginTop = getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_top);
		getSearchFragment().setSearchResultsListViewMargin(0, marginTop, 0, 0);
		slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_scratch_projects, menu);
		return true;
	}

	public void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_SPEECH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.INTENT_REQUEST_CODE_SPEECH && resultCode == RESULT_OK) {
			String spokenText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
			getSearchFragment().searchAndUpdateText(spokenText);
		} else if (requestCode == Constants.INTENT_REQUEST_CODE_CONVERT && resultCode == RESULT_OK) {
			if (!data.hasExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA)) {
				super.onActivityResult(requestCode, resultCode, data);
				return;
			}
			final ScratchProgramData projectData = data.getParcelableExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA);
			final List<ScratchProgramData> projectList = new ArrayList<>();
			projectList.add(projectData);
			convertProjects(projectList);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset) {
		getSlidingUpFragment().rotateImageButton(slideOffset * 180.0f);
	}

	@Override
	public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
			SlidingUpPanelLayout.PanelState newState) {
		switch (newState) {
			case EXPANDED:
				getSlidingUpFragment().rotateImageButton(180);
				getSlidingUpFragment().scrollUpPanelScrollView();
				break;
			case COLLAPSED:
				getSlidingUpFragment().rotateImageButton(0);
				getSlidingUpFragment().scrollUpPanelScrollView();
				break;
		}
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
			ToastUtil.showError(this, R.string.error_cannot_open_failed_scratch_program);
			return;
		}

		String catrobatProgramName = getSlidingUpFragment().getDownloadedProgramsMap().get(job.getJobID());
		catrobatProgramName = catrobatProgramName == null ? job.getTitle() : catrobatProgramName;

		if (job.getDownloadState() == Job.DownloadState.DOWNLOADING) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_currently_downloading_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		if (job.getDownloadState() == Job.DownloadState.NOT_READY || job.getDownloadState() == Job.DownloadState.CANCELED) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_yet_downloaded_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		if (!StorageHandler.getInstance().projectExists(catrobatProgramName)) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.warning)
					.setMessage(R.string.error_cannot_open_not_existing_scratch_program)
					.setNeutralButton(R.string.close, null)
					.create()
					.show();
			return;
		}

		ProjectLoaderTask loadProjectTask = new ProjectLoaderTask(this, getSlidingUpFragment());
		loadProjectTask.execute(catrobatProgramName);
	}

	public void onItemLongClick(Job item, ViewHolder h) {
	}
}
