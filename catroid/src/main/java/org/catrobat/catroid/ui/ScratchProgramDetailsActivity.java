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

package org.catrobat.catroid.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import org.catrobat.catroid.ui.recyclerview.adapter.ScratchProgramAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.scratchconverter.JobViewListener;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.web.CatrobatWebClient;
import org.catrobat.catroid.web.ScratchDataFetcher;
import org.catrobat.catroid.web.ServerCalls;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static org.catrobat.catroid.utils.NumberFormats.humanFriendlyFormattedShortNumber;

public class ScratchProgramDetailsActivity extends BaseActivity implements
		FetchScratchProgramDetailsTask.ScratchProgramListTaskDelegate,
		JobViewListener, Client.DownloadCallback,
		RVAdapter.OnItemClickListener<ScratchProgramData> {

	public static final String TAG = ScratchProgramDetailsActivity.class.getSimpleName();

	private static ScratchDataFetcher dataFetcher = new ServerCalls(CatrobatWebClient.INSTANCE.getClient());
	private static ConversionManager conversionManager;

	private ScratchProgramData programData;
	private ScratchProgramAdapter adapter;
	private FetchScratchProgramDetailsTask fetchRemixesTask = new FetchScratchProgramDetailsTask();

	private ProgressDialog progressDialog;
	private Button convertButton;

	public static void setConversionManager(final ConversionManager manager) {
		conversionManager = manager;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scratch_project_details);

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

		programData = getIntent().getParcelableExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA);
		Preconditions.checkState(programData != null);

		convertButton = findViewById(R.id.convert_button);
		((TextView) findViewById(R.id.project_title_view)).setText(programData.getTitle());
		((TextView) findViewById(R.id.instructions_view)).setText("-");

		if (conversionManager.isJobInProgress(programData.getId())) {
			onJobInProgress();
		} else if (conversionManager.isJobDownloading(programData.getId())) {
			onJobDownloading();
		} else {
			onJobNotInProgress();
		}

		conversionManager.addJobViewListener(programData.getId(), this);
		conversionManager.addGlobalDownloadCallback(this);

		convertButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final int numberOfJobsInProgress = conversionManager.getNumberOfJobsInProgress();
				if (numberOfJobsInProgress >= Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
					ToastUtil.showError(getApplicationContext(), getResources().getQuantityString(
							R.plurals.error_cannot_convert_more_than_x_programs,
							Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
							Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT));
				} else {
					convertProgram(programData);
				}
			}
		});

		RecyclerView recyclerView = findViewById(R.id.recycler_view_remixes);
		adapter = new ScratchProgramAdapter(new ArrayList<ScratchProgramData>());
		adapter.setOnItemClickListener(this);
		recyclerView.setAdapter(adapter);

		if (programData.getImage() != null && programData.getImage().getUrl() != null) {
			final int height = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);
			final String originalImageURL = programData.getImage().getUrl().toString();
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			ImageView image = findViewById(R.id.project_image_view);
			Picasso.get().load(thumbnailImageURL).into(image);
		}

		fetchRemixesTask
				.setContext(this)
				.setDelegate(this)
				.setFetcher(dataFetcher);
		fetchRemixesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, programData.getId());
	}

	private void convertProgram(ScratchProgramData item) {
		if (conversionManager.getNumberOfJobsInProgress() > Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT) {
			ToastUtil.showError(this, getResources().getQuantityString(
					R.plurals.error_cannot_convert_more_than_x_programs,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT,
					Constants.SCRATCH_CONVERTER_MAX_NUMBER_OF_JOBS_PER_CLIENT));
		} else if (Utils.isDeprecatedScratchProgram(item)) {
			DateFormat dateFormat = DateFormat.getDateInstance();
			ToastUtil.showError(this, getString(R.string.error_cannot_convert_deprecated_scratch_program_x_x,
					item.getTitle(), dateFormat.format(Utils.getScratchSecondReleasePublishedDate())));
		} else if (conversionManager.isJobInProgress(item.getId())) {
			onJobInProgress();
		} else if (conversionManager.isJobDownloading(item.getId())) {
			onJobDownloading();
		} else {
			conversionManager.convertProgram(item.getId(), item.getTitle(), item.getImage(), false);

			ToastUtil.showSuccess(this, getResources().getQuantityString(
					R.plurals.scratch_conversion_scheduled_x,
					1,
					1));
		}
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

	@Override
	public void onItemClick(ScratchProgramData item) {
		Intent intent = new Intent(this, ScratchProgramDetailsActivity.class);
		intent.putExtra(Constants.INTENT_SCRATCH_PROGRAM_DATA, (Parcelable) item);
		startActivityForResult(intent, Constants.INTENT_REQUEST_CODE_CONVERT);
	}

	@Override
	public void onItemLongClick(ScratchProgramData item, CheckableVH h) {
	}

	private void onJobNotInProgress() {
		convertButton.setEnabled(true);
		convertButton.setText(R.string.convert);
	}

	private void onJobInProgress() {
		convertButton.setEnabled(false);
		convertButton.setText(R.string.converting);
	}

	private void onJobDownloading() {
		convertButton.setEnabled(false);
		convertButton.setText(R.string.status_downloading);
	}

	@Override
	public void onPreExecute() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.setMessage(getString(R.string.loading));
		progressDialog.show();
	}

	@Override
	public void onPostExecute(final ScratchProgramData programData) {
		progressDialog.dismiss();
		if (programData == null) {
			ToastUtil.showError(this, R.string.error_scratch_project_data_not_available);
		} else {
			this.programData = programData;
			onProgramDataUpdated();
		}
	}

	private void onProgramDataUpdated() {
		((TextView) findViewById(R.id.project_title_view)).setText(programData.getTitle());
		((TextView) findViewById(R.id.owner_view)).setText(getString(R.string.by_x, programData.getOwner()));

		TextView creditsView = findViewById(R.id.credits_view);

		if (programData.getNotesAndCredits() != null && programData.getNotesAndCredits().length() > 0) {
			String notesAndCredits = programData.getNotesAndCredits().replace("\n\n", "\n");
			findViewById(R.id.credits_title_view).setVisibility(View.VISIBLE);
			creditsView.setText(notesAndCredits);
			creditsView.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.credits_title_view).setVisibility(View.GONE);
			creditsView.setVisibility(View.GONE);
		}

		TextView instructionsView = findViewById(R.id.instructions_view);

		if (programData.getInstructions() != null) {
			String instructions = programData.getInstructions().replace("\n\n", "\n");
			instructionsView.setText(instructions.length() > 0 ? instructions : "-");
		} else {
			instructionsView.setText("-");
		}

		((TextView) findViewById(R.id.scratch_project_favorites_text))
				.setText(humanFriendlyFormattedShortNumber(programData.getFavorites()));
		((TextView) findViewById(R.id.scratch_project_loves_text))
				.setText(humanFriendlyFormattedShortNumber(programData.getLoves()));
		((TextView) findViewById(R.id.scratch_project_views_text))
				.setText(humanFriendlyFormattedShortNumber(programData.getViews()));

		TextView dateSharedView = findViewById(R.id.date_shared_view);
		TextView dateModifiedView = findViewById(R.id.date_modified_view);

		RelativeLayout dateViews = findViewById(R.id.dates_view);
		dateViews.setVisibility(View.GONE);

		if (programData.getSharedDate() != null) {
			String dateSharedText = DateFormat
					.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(programData.getSharedDate());
			dateSharedView.setText(getString(R.string.shared_at_x, dateSharedText));
			dateSharedView.setVisibility(View.VISIBLE);
			dateViews.setVisibility(View.VISIBLE);
		} else {
			dateSharedView.setVisibility(View.GONE);
		}

		if (programData.getModifiedDate() != null) {
			String dateModifiedText = DateFormat
					.getDateInstance(DateFormat.LONG, Locale.getDefault()).format(programData.getSharedDate());
			dateModifiedView.setText(getString(R.string.modified_at_x, dateModifiedText));
			dateModifiedView.setVisibility(View.VISIBLE);
			dateViews.setVisibility(View.VISIBLE);
		} else {
			dateModifiedView.setVisibility(View.GONE);
		}

		findViewById(R.id.project_details_layout).setVisibility(View.VISIBLE);
		ScratchVisibilityState visibilityState = programData.getVisibilityState();

		if (visibilityState != null && visibilityState != ScratchVisibilityState.PUBLIC) {
			findViewById(R.id.privacy_warning).setVisibility(View.VISIBLE);
			convertButton.setVisibility(View.GONE);
		} else {
			findViewById(R.id.privacy_warning).setVisibility(View.GONE);
			convertButton.setVisibility(View.VISIBLE);
		}

		if (programData.getRemixes().size() > 0) {
			findViewById(R.id.remixes_title_view).setVisibility(View.VISIBLE);
			findViewById(R.id.recycler_view_remixes).setVisibility(View.VISIBLE);
			adapter.setItems(programData.getRemixes());
		} else {
			findViewById(R.id.remixes_title_view).setVisibility(View.GONE);
		}
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
