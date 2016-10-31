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
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.common.ScratchVisibilityState;
import org.catrobat.catroid.scratchconverter.Client;
import org.catrobat.catroid.scratchconverter.ConversionManager;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.transfers.FetchScratchProgramDetailsTask;
import org.catrobat.catroid.ui.adapter.ScratchRemixedProgramAdapter;
import org.catrobat.catroid.ui.adapter.ScratchRemixedProgramAdapter.ScratchRemixedProgramEditListener;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ScratchDataFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.flowtextview.FlowTextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ScratchProgramDetailsActivity extends BaseActivity implements
		FetchScratchProgramDetailsTask.ScratchProgramListTaskDelegate, ScratchRemixedProgramEditListener,
		JobViewListener, Client.DownloadCallback {

	private static final String TAG = ScratchProgramDetailsActivity.class.getSimpleName();

	private static ScratchDataFetcher dataFetcher = null;
	private static ConversionManager conversionManager = null;

	private ScratchProgramData programData;
	private TextView titleTextView;
	private TextView ownerTextView;
	private ImageView imageView;
	private TextView visibilityWarningTextView;
	private FlowTextView instructionsFlowTextView;
	private TextView notesAndCreditsLabelView;
	private TextView notesAndCreditsTextView;
	private TextView favoritesTextView;
	private TextView lovesTextView;
	private TextView viewsTextView;
	private TextView tagsTextView;
	private TextView sharedTextView;
	private TextView modifiedTextView;
	private Button convertButton;
	private ListView remixedProjectsListView;
	private ProgressDialog progressDialog;
	private ScratchRemixedProgramAdapter scratchRemixedProgramAdapter;
	private ScrollView mainScrollView;
	private RelativeLayout detailsLayout;
	private TextView remixesLabelView;
	private FetchScratchProgramDetailsTask fetchRemixesTask = new FetchScratchProgramDetailsTask();
	private View separationLineBottom;

	public static void setDataFetcher(final ScratchDataFetcher fetcher) {
		dataFetcher = fetcher;
	}

	public static void setConversionManager(final ConversionManager manager) {
		conversionManager = manager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scratch_project_details);
		setUpActionBar();
		setReturnByPressingBackButton(true);

		programData = getIntent().getParcelableExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA);
		Preconditions.checkState(programData != null);

		titleTextView = (TextView) findViewById(R.id.scratch_project_title);
		ownerTextView = (TextView) findViewById(R.id.scratch_project_owner);
		mainScrollView = (ScrollView) findViewById(R.id.scratch_project_scroll_view);
		imageView = (ImageView) findViewById(R.id.scratch_project_image_view);
		visibilityWarningTextView = (TextView) findViewById(R.id.scratch_project_visibility_warning);
		instructionsFlowTextView = (FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text);
		notesAndCreditsLabelView = (TextView) findViewById(R.id.scratch_project_notes_and_credits_label);
		notesAndCreditsTextView = (TextView) findViewById(R.id.scratch_project_notes_and_credits_text);
		favoritesTextView = (TextView) findViewById(R.id.scratch_project_favorites_text);
		lovesTextView = (TextView) findViewById(R.id.scratch_project_loves_text);
		viewsTextView = (TextView) findViewById(R.id.scratch_project_views_text);
		tagsTextView = (TextView) findViewById(R.id.scratch_project_tags_text);
		sharedTextView = (TextView) findViewById(R.id.scratch_project_shared_text);
		modifiedTextView = (TextView) findViewById(R.id.scratch_project_modified_text);
		remixedProjectsListView = (ListView) findViewById(R.id.scratch_project_remixes_list_view);
		convertButton = (Button) findViewById(R.id.scratch_project_convert_button);
		detailsLayout = (RelativeLayout) findViewById(R.id.scratch_project_details_layout);
		remixesLabelView = (TextView) findViewById(R.id.scratch_project_remixes_label);
		separationLineBottom = findViewById(R.id.separation_line_bottom);

		if (conversionManager.isJobInProgress(programData.getId())) {
			onJobInProgress();
		} else if (conversionManager.isJobDownloading(programData.getId())) {
			onJobDownloading();
		} else {
			onJobNotInProgress();
		}

		conversionManager.addJobViewListener(programData.getId(), this);
		conversionManager.addGlobalDownloadCallback(this);

		final Activity activity = this;
		convertButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int numberOfJobsInProgress = conversionManager.getNumberOfJobsInProgress();
				if (numberOfJobsInProgress >= Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
					ToastUtil.showError(activity, getResources().getQuantityString(
							R.plurals.error_cannot_convert_more_than_x_programs,
							Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
							Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT));
					return;
				}

				onJobInProgress();

				Intent intent = new Intent();
				intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) programData);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		loadAdditionalData(programData);
	}

	@Override
	protected void onStart() {
		super.onStart();
		conversionManager.setCurrentActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroyed " + TAG);
		conversionManager.removeJobViewListener(programData.getId(), this);
		conversionManager.removeGlobalDownloadCallback(this);
		fetchRemixesTask.cancel(true);
		progressDialog.dismiss();
	}

	private void setUpActionBar() {
		final ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.title_activity_scratch_converter);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
		if (requestCode == Constants.INTENT_REQUEST_CODE_CONVERT && resultCode == RESULT_OK) {
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	private void loadAdditionalData(ScratchProgramData scratchProgramData) {
		Preconditions.checkArgument(scratchProgramData != null);
		Log.i(TAG, scratchProgramData.getTitle());
		instructionsFlowTextView.setText("-");
		notesAndCreditsLabelView.setVisibility(GONE);
		notesAndCreditsTextView.setVisibility(GONE);
		remixesLabelView.setVisibility(GONE);
		remixedProjectsListView.setVisibility(GONE);
		detailsLayout.setVisibility(GONE);
		tagsTextView.setVisibility(GONE);
		visibilityWarningTextView.setVisibility(GONE);
		convertButton.setVisibility(GONE);
		separationLineBottom.setVisibility(GONE);

		if (scratchRemixedProgramAdapter != null) {
			scratchRemixedProgramAdapter.clear();
		}

		titleTextView.setText(scratchProgramData.getTitle());
		if (scratchProgramData.getImage() != null && scratchProgramData.getImage().getUrl() != null) {
			final int height = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);
			final String originalImageURL = scratchProgramData.getImage().getUrl().toString();

			// load image but only thumnail!
			// in order to download only thumbnail version of the original image
			// we have to reduce the image size in the URL
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(this).load(thumbnailImageURL).into(imageView);
		}

		fetchRemixesTask.setContext(this).setDelegate(this).setFetcher(dataFetcher);
		fetchRemixesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, scratchProgramData.getId());
	}

	private void initRemixAdapter(List<ScratchProgramData> scratchRemixedProjectsData) {
		if (scratchRemixedProjectsData == null) {
			scratchRemixedProjectsData = new ArrayList<>();
		}
		scratchRemixedProgramAdapter = new ScratchRemixedProgramAdapter(this,
				R.layout.fragment_scratch_project_list_item,
				R.id.scratch_projects_list_item_title,
				scratchRemixedProjectsData);
		remixedProjectsListView.setAdapter(scratchRemixedProgramAdapter);
		scratchRemixedProgramAdapter.setScratchRemixedProgramEditListener(this);
		Utils.setListViewHeightBasedOnItems(remixedProjectsListView);
	}

	public void onProjectEdit(int position) {
		Log.i(TAG, "Clicked on remix at position: " + position);
		ScratchProgramData remixData = scratchRemixedProgramAdapter.getItem(position);
		Log.i(TAG, "Project ID of clicked item is: " + remixData.getId());

		Intent intent = new Intent(this, ScratchProgramDetailsActivity.class);
		intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) remixData);
		startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_CONVERT);
	}

	private void onJobNotInProgress() {
		convertButton.setText(R.string.convert);
		convertButton.setEnabled(true);
	}

	private void onJobInProgress() {
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);
	}

	private void onJobDownloading() {
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);
	}

	//----------------------------------------------------------------------------------------------
	// Scratch Project Details Task Delegate Methods
	//----------------------------------------------------------------------------------------------
	@Override
	public void onPreExecute() {
		Log.d(TAG, "onPreExecute for FetchScratchProgramRemixesTask called");
		final ScratchProgramDetailsActivity activity = this;
		progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.getWindow().setGravity(Gravity.CENTER);
		progressDialog.setMessage(activity.getResources().getString(R.string.loading));
		progressDialog.show();
	}

	@Override
	public void onPostExecute(final ScratchProgramData programData) {
		Log.d(TAG, "onPostExecute for FetchScratchProgramRemixesTask called");
		Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
		progressDialog.dismiss();
		if (programData == null) {
			ToastUtil.showError(this, R.string.error_scratch_project_data_not_available);
			return;
		}
		this.programData = programData;
		updateViews();

		// workaround to avoid scrolling down to list view after all list items have been initialized
		mainScrollView.postDelayed(new Runnable() {
			public void run() {
				mainScrollView.fullScroll(ScrollView.FOCUS_UP);
			}
		}, 300);
	}

	private void updateViews() {
		titleTextView.setText(programData.getTitle());
		ownerTextView.setText(getString(R.string.by_x, programData.getOwner()));

		if (programData.getNotesAndCredits() != null && programData.getNotesAndCredits().length() > 0) {
			final String notesAndCredits = programData.getNotesAndCredits().replace("\n\n", "\n");
			notesAndCreditsTextView.setText(notesAndCredits);
			notesAndCreditsLabelView.setVisibility(VISIBLE);
			notesAndCreditsTextView.setVisibility(VISIBLE);
		} else {
			notesAndCreditsLabelView.setVisibility(GONE);
			notesAndCreditsTextView.setVisibility(GONE);
		}

		if (programData.getInstructions() != null) {
			String instructionsText = programData.getInstructions().replace("\n\n", "\n");
			instructionsText = (instructionsText.length() > 0) ? instructionsText : "--";
			instructionsFlowTextView.setText(instructionsText);

			float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
			instructionsFlowTextView.setTextSize(textSize);
			instructionsFlowTextView.setColor(Color.LTGRAY);
		} else {
			instructionsFlowTextView.setText("-");
		}

		favoritesTextView.setText(Utils.humanFriendlyFormattedShortNumber(programData.getFavorites()));
		lovesTextView.setText(Utils.humanFriendlyFormattedShortNumber(programData.getLoves()));
		viewsTextView.setText(Utils.humanFriendlyFormattedShortNumber(programData.getViews()));

		if (programData.getTags() != null) {
			StringBuilder tagList = new StringBuilder();
			int index = 0;
			for (String tag : programData.getTags()) {
				tagList.append(index++ > 0 ? ", " : "").append(tag);
			}
			if (tagList.length() > 0) {
				tagsTextView.setText(tagList);
				tagsTextView.setVisibility(VISIBLE);
			}
		}

		if (programData.getSharedDate() != null) {
			final String sharedDateString = Utils.formatDate(programData.getSharedDate(), Locale.getDefault());
			sharedTextView.setText(getString(R.string.shared_at_x, sharedDateString));
		} else {
			sharedTextView.setVisibility(GONE);
		}

		if (programData.getModifiedDate() != null) {
			final String modifiedDateString = Utils.formatDate(programData.getModifiedDate(), Locale.getDefault());
			modifiedTextView.setText(getString(R.string.modified_at_x, modifiedDateString));
		} else {
			modifiedTextView.setVisibility(GONE);
		}

		detailsLayout.setVisibility(VISIBLE);
		ScratchVisibilityState visibilityState = programData.getVisibilityState();

		if (visibilityState != null && visibilityState != ScratchVisibilityState.PUBLIC) {
			visibilityWarningTextView.setVisibility(VISIBLE);
			convertButton.setVisibility(GONE);
		} else {
			visibilityWarningTextView.setVisibility(GONE);
			convertButton.setVisibility(VISIBLE);
		}

		if (programData.getRemixes() != null && programData.getRemixes().size() > 0) {
			remixesLabelView.setVisibility(VISIBLE);
			remixedProjectsListView.setVisibility(VISIBLE);
			initRemixAdapter(programData.getRemixes());
		}
		separationLineBottom.setVisibility(VISIBLE);
	}

	//----------------------------------------------------------------------------------------------
	// JobViewListener Events
	//----------------------------------------------------------------------------------------------
	@Override
	public void onJobScheduled(final Job job) {
		if (job.getJobID() == programData.getId()) {
			onJobInProgress();
		}
	}

	@Override
	public void onJobReady(final Job job) {
		// nothing to do
	}

	@Override
	public void onJobStarted(final Job job) {
		// nothing to do
	}

	@Override
	public void onJobProgress(final Job job, final short progress) {
		// nothing to do
	}

	@Override
	public void onJobOutput(final Job job, @NonNull final String[] lines) {
		// nothing to do
	}

	@Override
	public void onJobFinished(final Job job) {
		// nothing to do
	}

	@Override
	public void onJobFailed(final Job job) {
		if (job.getJobID() == programData.getId()) {
			onJobNotInProgress();
		}
	}

	@Override
	public void onUserCanceledJob(final Job job) {
		if (job.getJobID() == programData.getId()) {
			onJobNotInProgress();
		}
	}

	@Override
	public void onDownloadStarted(final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == programData.getId()) {
			onJobDownloading();
		}
	}

	@Override
	public void onDownloadProgress(short progress, String url) {
		// nothing to do
	}

	@Override
	public void onDownloadFinished(final String catrobatProgramName, final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == programData.getId()) {
			onJobNotInProgress();
		}
	}

	@Override
	public void onUserCanceledDownload(final String url) {
		final long jobID = Utils.extractScratchJobIDFromURL(url);
		if (jobID == programData.getId()) {
			onJobNotInProgress();
		}
	}
}
