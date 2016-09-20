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
package org.catrobat.catroid.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.ScratchConversionManager;
import org.catrobat.catroid.scratchconverter.WebSocketClient;
import org.catrobat.catroid.scratchconverter.protocol.WebSocketMessageListener;
import org.catrobat.catroid.ui.fragment.ScratchConverterSlidingUpPanelFragment;
import org.catrobat.catroid.ui.fragment.SearchScratchSearchProjectsListFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.ServerCalls;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScratchConverterActivity extends BaseActivity implements SlidingUpPanelLayout.PanelSlideListener {

	private static final String TAG = ScratchConverterActivity.class.getSimpleName();

	// to avoid using singleton in fragment
	private static Client client = null;
	private static ScratchDataFetcher dataFetcher = ServerCalls.getInstance();

	private SearchScratchSearchProjectsListFragment searchProjectsListFragment;
	private ScratchConverterSlidingUpPanelFragment converterSlidingUpPanelFragment;
	private SlidingUpPanelLayout slidingLayout;
	private ConversionManager conversionManager;

	// dependency-injection for testing with mock object
	public static void setDataFetcher(final ScratchDataFetcher fetcher) {
		dataFetcher = fetcher;
	}

	public static void setClient(final Client converterClient) {
		client = converterClient;
	}

	public ScratchConverterSlidingUpPanelFragment getConverterSlidingUpPanelFragment() {
		return converterSlidingUpPanelFragment;
	}

	public SearchScratchSearchProjectsListFragment getSearchProjectsListFragment() {
		return searchProjectsListFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scratch_converter);
		setUpActionBar();
		setReturnByPressingBackButton(true);

		searchProjectsListFragment = (SearchScratchSearchProjectsListFragment) getFragmentManager().findFragmentById(
				R.id.fragment_scratch_search_projects_list);
		searchProjectsListFragment.setDataFetcher(dataFetcher);
		converterSlidingUpPanelFragment = (ScratchConverterSlidingUpPanelFragment) getFragmentManager().findFragmentById(
				R.id.fragment_scratch_converter_sliding_up_panel);

		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		final long clientID = settings.getLong(Constants.SCRATCH_CONVERTER_CLIENT_ID_SHARED_PREFERENCE_NAME,
				Client.INVALID_CLIENT_ID);

		if (client == null) {
			client = new WebSocketClient(clientID, new WebSocketMessageListener());
		}

		conversionManager = new ScratchConversionManager(this, client, false);
		conversionManager.setCurrentActivity(this);
		conversionManager.addGlobalDownloadCallback(converterSlidingUpPanelFragment);
		conversionManager.addBaseInfoViewListener(converterSlidingUpPanelFragment);
		conversionManager.addGlobalJobViewListener(converterSlidingUpPanelFragment);
		searchProjectsListFragment.setConversionManager(conversionManager);

		slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
		slidingLayout.addPanelSlideListener(this);

		final int betaLabelColor = ContextCompat.getColor(this, R.color.beta_label_color);
		appendColoredBetaLabelToTitle(betaLabelColor);
		hideSlideUpPanelBar();
		Log.i(TAG, "Scratch Converter Activity created");
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
		Log.d(TAG, "Destroyed: " + TAG);
		conversionManager.shutdown();
		conversionManager.removeGlobalDownloadCallback(converterSlidingUpPanelFragment);
		conversionManager.removeBaseInfoViewListener(converterSlidingUpPanelFragment);
		conversionManager.removeGlobalJobViewListener(converterSlidingUpPanelFragment);
		client = null;
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.title_activity_scratch_converter);
		actionBar.setHomeButtonEnabled(true);
	}

	private void appendColoredBetaLabelToTitle(final int color) {
		final String title = getString(R.string.title_activity_scratch_converter);
		final String beta = getString(R.string.beta).toUpperCase(Locale.getDefault());
		final SpannableString spanTitle = new SpannableString(title + " " + beta);
		final int begin = title.length() + 1;
		final int end = begin + beta.length();
		spanTitle.setSpan(new ForegroundColorSpan(color), begin, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		getActionBar().setTitle(spanTitle);
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

			Log.i(TAG, "Converting program: " + programData.getTitle());
			conversionManager.convertProgram(programData.getId(), programData.getTitle(), programData.getImage(), false);
			counter++;
		}

		if (counter > 0) {
			ToastUtil.showSuccess(this, getResources().getQuantityString(R.plurals.scratch_conversion_scheduled_x,
					counter, counter));
		}
	}

	public boolean isSlideUpPanelEmpty() {
		return !converterSlidingUpPanelFragment.hasVisibleJobs();
	}

	public void showSlideUpPanelBar(final long delayMillis) {
		final int marginTop = getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_top);
		final int marginBottom = getResources().getDimensionPixelSize(
				R.dimen.scratch_project_search_list_view_margin_bottom);

		if (delayMillis > 0) {
			slidingLayout.postDelayed(new Runnable() {
				public void run() {
					slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
					searchProjectsListFragment.setSearchResultsListViewMargin(0, marginTop, 0, marginBottom);
				}
			}, delayMillis);
		} else {
			slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
			searchProjectsListFragment.setSearchResultsListViewMargin(0, marginTop, 0, marginBottom);
		}
	}

	public void hideSlideUpPanelBar() {
		int marginTop = getResources().getDimensionPixelSize(R.dimen.scratch_project_search_list_view_margin_top);
		searchProjectsListFragment.setSearchResultsListViewMargin(0, marginTop, 0, 0);
		slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_scratch_projects, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		handleShowDetails(searchProjectsListFragment.getShowDetails(),
				menu.findItem(R.id.menu_scratch_projects_show_details));
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_scratch_projects_convert:
				Log.d(TAG, "Selected menu item 'convert'");
				searchProjectsListFragment.startConvertActionMode();
				break;
			case R.id.menu_scratch_projects_show_details:
				Log.d(TAG, "Selected menu item 'Show/Hide details'");
				handleShowDetails(!searchProjectsListFragment.getShowDetails(), item);
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void handleShowDetails(boolean showDetails, MenuItem item) {
		searchProjectsListFragment.setShowDetails(showDetails);
		item.setTitle(showDetails ? R.string.hide_details : R.string.show_details);
	}

	public void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_SPEECH);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.INTENT_REQUEST_CODE_SPEECH && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			searchProjectsListFragment.searchAndUpdateText(spokenText);
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
		converterSlidingUpPanelFragment.rotateImageButton(slideOffset * 180.0f);
	}

	@Override
	public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
			SlidingUpPanelLayout.PanelState newState) {
		Log.d(TAG, "SlidingUpPanel state changed: " + newState.toString());
		switch (newState) {
			case EXPANDED:
				converterSlidingUpPanelFragment.rotateImageButton(180);
				converterSlidingUpPanelFragment.scrollUpPanelScrollView();
				break;
			case COLLAPSED:
				converterSlidingUpPanelFragment.rotateImageButton(0);
				converterSlidingUpPanelFragment.scrollUpPanelScrollView();
				break;
		}
	}
}
