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

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter;
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchRemixedProgramAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.ScratchDataFetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.deanwild.flowtextview.FlowTextView;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ScratchProgramDetailsActivity extends BaseActivity implements
		FetchScratchProgramDetailsTask.ScratchProgramListTaskDelegate,
		JobViewListener, Client.DownloadCallback,
		RVAdapter.OnItemClickListener<ScratchProgramData> {

	private static final String TAG = ScratchProgramDetailsActivity.class.getSimpleName();

	private static ScratchDataFetcher dataFetcher = null;
	private static ConversionManager conversionManager = null;

	private ScratchProgramData programData;
	private ProgressDialog progressDialog;
	private ScratchRemixedProgramAdapter scratchRemixedProgramAdapter;
	private FetchScratchProgramDetailsTask fetchRemixesTask = new FetchScratchProgramDetailsTask();

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

		programData = getIntent().getParcelableExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA);
		Preconditions.checkState(programData != null);

		if (conversionManager.isJobInProgress(programData.getId())) {
			onJobInProgress();
		} else if (conversionManager.isJobDownloading(programData.getId())) {
			onJobDownloading();
		} else {
			onJobNotInProgress();
		}

		conversionManager.addJobViewListener(programData.getId(), this);
		conversionManager.addGlobalDownloadCallback(this);

		findViewById(R.id.scratch_project_convert_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int numberOfJobsInProgress = conversionManager.getNumberOfJobsInProgress();
				if (numberOfJobsInProgress >= Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
					ToastUtil.showError(getContext(), getResources().getQuantityString(
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
		conversionManager.removeJobViewListener(programData.getId(), this);
		conversionManager.removeGlobalDownloadCallback(this);
		fetchRemixesTask.cancel(true);
		progressDialog.dismiss();
	}

	private void setUpActionBar() {
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		String title = getResources().getString(R.string.title_activity_scratch_converter) + " " + getResources()
				.getString(R.string.beta).toUpperCase();
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
		if (requestCode == Constants.INTENT_REQUEST_CODE_CONVERT && resultCode == RESULT_OK) {
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	public void onItemClick(ScratchProgramData item) {
		Intent intent = new Intent(this, ScratchProgramDetailsActivity.class);
		intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) item);
		startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_CONVERT);
	}

	public void onItemLongClick(ScratchProgramData item, ViewHolder h) {
	}

	private void loadAdditionalData(ScratchProgramData scratchProgramData) {
		Preconditions.checkArgument(scratchProgramData != null);
		Log.i(TAG, scratchProgramData.getTitle());
		((FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text)).setText("-");
		findViewById(R.id.scratch_project_notes_and_credits_label).setVisibility(GONE);
		findViewById(R.id.scratch_project_notes_and_credits_text).setVisibility(GONE);
		findViewById(R.id.scratch_project_remixes_label).setVisibility(GONE);
		findViewById(R.id.scratch_project_remixes_list_view).setVisibility(GONE);
		findViewById(R.id.scratch_project_details_layout).setVisibility(GONE);
		findViewById(R.id.scratch_project_tags_text).setVisibility(GONE);
		findViewById(R.id.scratch_project_visibility_warning).setVisibility(GONE);
		findViewById(R.id.scratch_project_convert_button).setVisibility(GONE);
		findViewById(R.id.separation_line_bottom).setVisibility(GONE);

		if (scratchRemixedProgramAdapter != null) {
			scratchRemixedProgramAdapter.getItems().clear();
		}
		((TextView) findViewById(R.id.scratch_project_title)).setText(scratchProgramData.getTitle());
		if (scratchProgramData.getImage() != null && scratchProgramData.getImage().getUrl() != null) {
			final int height = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);
			final String originalImageURL = scratchProgramData.getImage().getUrl().toString();
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			ImageView image = findViewById(R.id.scratch_project_image_view);
			Picasso.with(this).load(thumbnailImageURL).into(image);
		}

		fetchRemixesTask.setContext(this).setDelegate(this).setFetcher(dataFetcher);
		fetchRemixesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, scratchProgramData.getId());
	}

	private void initRemixAdapter(List<ScratchProgramData> scratchRemixedProjectsData) {
		if (scratchRemixedProjectsData == null) {
			scratchRemixedProjectsData = new ArrayList<>();
		}
		scratchRemixedProgramAdapter = new ScratchRemixedProgramAdapter(scratchRemixedProjectsData);
		((RecyclerView) findViewById(R.id.scratch_project_remixes_list_view)).setAdapter(scratchRemixedProgramAdapter);
		((RecyclerView) findViewById(R.id.scratch_project_remixes_list_view)).addItemDecoration(new
				DividerItemDecoration(findViewById(R.id.scratch_project_remixes_list_view).getContext(),
				DividerItemDecoration.VERTICAL));

		scratchRemixedProgramAdapter.setOnItemClickListener(this);
	}

	private void onJobNotInProgress() {
		((Button) findViewById(R.id.scratch_project_convert_button)).setText(R.string.convert);
		findViewById(R.id.scratch_project_convert_button).setEnabled(true);
	}

	private void onJobInProgress() {
		findViewById(R.id.scratch_project_convert_button).setEnabled(false);
		((Button) findViewById(R.id.scratch_project_convert_button)).setText(R.string.converting);
	}

	private void onJobDownloading() {
		findViewById(R.id.scratch_project_convert_button).setEnabled(false);
		((Button) findViewById(R.id.scratch_project_convert_button)).setText(R.string.status_downloading);
	}

	@Override
	public void onPreExecute() {
		final ScratchProgramDetailsActivity activity = this;
		progressDialog = new ProgressDialog(activity);
		progressDialog.setCancelable(false);
		progressDialog.getWindow().setGravity(Gravity.CENTER);
		progressDialog.setMessage(activity.getResources().getString(R.string.loading));
		progressDialog.show();
	}

	@Override
	public void onPostExecute(final ScratchProgramData programData) {
		Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
		progressDialog.dismiss();
		if (programData == null) {
			ToastUtil.showError(this, R.string.error_scratch_project_data_not_available);
			return;
		}
		this.programData = programData;
		updateViews();

		// workaround to avoid scrolling down to list view after all list items have been initialized
		findViewById(R.id.scratch_project_scroll_view).postDelayed(new Runnable() {
			public void run() {
				((ScrollView) findViewById(R.id.scratch_project_scroll_view)).fullScroll(ScrollView.FOCUS_UP);
			}
		}, 300);
	}

	private void updateViews() {
		((TextView) findViewById(R.id.scratch_project_title)).setText(programData.getTitle());
		((TextView) findViewById(R.id.scratch_project_owner)).setText(getString(R.string.by_x, programData.getOwner()));

		if (programData.getNotesAndCredits() != null && programData.getNotesAndCredits().length() > 0) {
			final String notesAndCredits = programData.getNotesAndCredits().replace("\n\n", "\n");
			((TextView) findViewById(R.id.scratch_project_notes_and_credits_text)).setText(notesAndCredits);
			findViewById(R.id.scratch_project_notes_and_credits_label).setVisibility(VISIBLE);
			findViewById(R.id.scratch_project_notes_and_credits_text).setVisibility(VISIBLE);
		} else {
			findViewById(R.id.scratch_project_notes_and_credits_label).setVisibility(GONE);
			findViewById(R.id.scratch_project_notes_and_credits_text).setVisibility(GONE);
		}

		if (programData.getInstructions() != null) {
			String instructionsText = programData.getInstructions().replace("\n\n", "\n");
			instructionsText = (instructionsText.length() > 0) ? instructionsText : "--";
			((FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text)).setText(instructionsText);

			float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
			((FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text)).setTextSize(textSize);
			((FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text)).setColor(Color.LTGRAY);
		} else {
			((FlowTextView) findViewById(R.id.scratch_project_instructions_flow_text)).setText("-");
		}

		((TextView) findViewById(R.id.scratch_project_favorites_text)).setText(Utils.humanFriendlyFormattedShortNumber(programData.getFavorites()));
		((TextView) findViewById(R.id.scratch_project_loves_text)).setText(Utils.humanFriendlyFormattedShortNumber(programData.getLoves()));
		((TextView) findViewById(R.id.scratch_project_views_text)).setText(Utils.humanFriendlyFormattedShortNumber(programData.getViews()));

		if (programData.getTags() != null) {
			StringBuilder tagList = new StringBuilder();
			int index = 0;
			for (String tag : programData.getTags()) {
				tagList.append(index++ > 0 ? ", " : "").append(tag);
			}
			if (tagList.length() > 0) {
				((TextView) findViewById(R.id.scratch_project_tags_text)).setText(tagList);
				findViewById(R.id.scratch_project_tags_text).setVisibility(VISIBLE);
			}
		}

		if (programData.getSharedDate() != null) {
			final String sharedDateString = Utils.formatDate(programData.getSharedDate(), Locale.getDefault());
			((TextView) findViewById(R.id.scratch_project_shared_text)).setText(getString(R.string.shared_at_x, sharedDateString));
		} else {
			findViewById(R.id.scratch_project_shared_text).setVisibility(GONE);
		}

		if (programData.getModifiedDate() != null) {
			final String modifiedDateString = Utils.formatDate(programData.getModifiedDate(), Locale.getDefault());
			((TextView) findViewById(R.id.scratch_project_modified_text)).setText(getString(R.string.modified_at_x, modifiedDateString));
		} else {
			findViewById(R.id.scratch_project_modified_text).setVisibility(GONE);
		}

		findViewById(R.id.scratch_project_details_layout).setVisibility(VISIBLE);
		ScratchVisibilityState visibilityState = programData.getVisibilityState();

		if (visibilityState != null && visibilityState != ScratchVisibilityState.PUBLIC) {
			findViewById(R.id.scratch_project_visibility_warning).setVisibility(VISIBLE);
			findViewById(R.id.scratch_project_convert_button).setVisibility(GONE);
		} else {
			findViewById(R.id.scratch_project_visibility_warning).setVisibility(GONE);
			findViewById(R.id.scratch_project_convert_button).setVisibility(VISIBLE);
		}

		if (programData.getRemixes() != null && programData.getRemixes().size() > 0) {
			findViewById(R.id.scratch_project_remixes_label).setVisibility(VISIBLE);
			findViewById(R.id.scratch_project_remixes_list_view).setVisibility(VISIBLE);
			initRemixAdapter(programData.getRemixes());
		}
		findViewById(R.id.separation_line_bottom).setVisibility(VISIBLE);
	}

	@Override
	public void onJobScheduled(final Job job) {
		if (job.getJobID() == programData.getId()) {
			onJobInProgress();
		}
	}

	@Override
	public void onJobReady(final Job job) {
	}

	@Override
	public void onJobStarted(final Job job) {
	}

	@Override
	public void onJobProgress(final Job job, final short progress) {
	}

	@Override
	public void onJobOutput(final Job job, @NonNull final String[] lines) {
	}

	@Override
	public void onJobFinished(final Job job) {
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
