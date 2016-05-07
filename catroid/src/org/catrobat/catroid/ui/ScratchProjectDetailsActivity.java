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

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.common.ScratchProjectPreviewData;
import org.catrobat.catroid.transfers.FetchScratchProjectDetailsTask;
import org.catrobat.catroid.utils.ExpiringDiskCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.WebImageLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.view.View.*;

public class ScratchProjectDetailsActivity extends BaseActivity
        implements FetchScratchProjectDetailsTask.ScratchProjectListTaskDelegate {

    private static final String TAG = ScratchProjectDetailsActivity.class.getSimpleName();

    private TextView titleTextView;
    private TextView ownerTextView;
    private ImageView imageView;
    private TextView instructionsTextView;
    private TextView notesAndCreditsLabelView;
    private TextView notesAndCreditsTextView;
    private TextView favoritesTextView;
    private TextView lovesTextView;
    private TextView viewsTextView;
    private TextView tagsTextView;
    private TextView sharedTextView;
    private TextView modifiedTextView;
    private Button convertButton;
    private WebImageLoader webImageLoader;
    private ProgressDialog progressDialog;
    private FetchScratchProjectDetailsTask currentTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_project_details);

        ScratchProjectPreviewData scratchProjectData = getIntent().getParcelableExtra(Constants.SCRATCH_PROJECT_DATA);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        webImageLoader = new WebImageLoader(
                ExpiringLruMemoryImageCache.getInstance(),
                ExpiringDiskCache.getInstance(this),
                executorService
        );

        Log.i(TAG, scratchProjectData.getTitle());
        titleTextView = (TextView) findViewById(R.id.scratch_project_title);
        ownerTextView = (TextView) findViewById(R.id.scratch_project_owner);
        imageView = (ImageView) findViewById(R.id.scratch_project_image_view);
        instructionsTextView = (TextView) findViewById(R.id.scratch_project_instructions_text);
        notesAndCreditsLabelView = (TextView) findViewById(R.id.scratch_project_notes_and_credits_label);
        notesAndCreditsTextView = (TextView) findViewById(R.id.scratch_project_notes_and_credits_text);
        favoritesTextView = (TextView) findViewById(R.id.scratch_project_favorites_text);
        lovesTextView = (TextView) findViewById(R.id.scratch_project_loves_text);
        viewsTextView = (TextView) findViewById(R.id.scratch_project_views_text);
        tagsTextView = (TextView) findViewById(R.id.scratch_project_tags_text);
        sharedTextView = (TextView) findViewById(R.id.scratch_project_shared_text);
        modifiedTextView = (TextView) findViewById(R.id.scratch_project_modified_text);
        convertButton = (Button) findViewById(R.id.scratch_project_convert_button);

        instructionsTextView.setText("-");
        notesAndCreditsLabelView.setVisibility(INVISIBLE);
        notesAndCreditsTextView.setVisibility(INVISIBLE);
        tagsTextView.setVisibility(INVISIBLE);

        int width = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_width);
        int height = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);

        // TODO: use LRU cache!
        // TODO: show remixes! button and show in separate activity???

        if (scratchProjectData != null) {
            titleTextView.setText(scratchProjectData.getTitle());

            if (scratchProjectData.getProjectImage() != null) {
                webImageLoader.fetchAndShowImage(
                        scratchProjectData.getProjectImage().getUrl().toString(),
                        imageView, width, height
                );
            }
        }

        if (currentTask != null) {
            currentTask.cancel(true);
        }
        currentTask = new FetchScratchProjectDetailsTask();
        currentTask.setDelegate(this).execute(scratchProjectData.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
        if (currentTask != null) {
            currentTask.cancel(true);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Scratch Project Details Task Delegate Methods
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPreExecute() {
        Log.i(TAG, "onPreExecute for FetchScratchProjectsTask called");
        final ScratchProjectDetailsActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog = new ProgressDialog(activity);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setGravity(Gravity.CENTER);
                progressDialog.setMessage(activity.getResources().getString(R.string.loading));
                progressDialog.show();
            }
        });
    }

    @Override
    public void onPostExecute(final ScratchProjectData projectData) {
        Log.i(TAG, "onPostExecute for FetchScratchProjectsTask called");
        final ScratchProjectDetailsActivity activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (! Looper.getMainLooper().equals(Looper.myLooper())) {
                    throw new AssertionError("You should not change the UI from any thread "
                            + "except UI thread!");
                }

                Preconditions.checkNotNull(progressDialog, "No progress dialog set/initialized!");
                progressDialog.dismiss();
                if (projectData == null) {
                    ToastUtil.showError(activity, activity.getString(R.string.error_scratch_project_data_not_available));
                    return;
                }

                activity.titleTextView.setText(projectData.getTitle());
                activity.ownerTextView.setText(activity.getString(R.string.by) + " " + projectData.getOwner());
                final String instructionsText = projectData.getInstructions().replace("\n\n", "\n");
                final String notesAndCredits = projectData.getNotesAndCredits().replace("\n\n", "\n");
                activity.instructionsTextView.setText((instructionsText.length() > 0) ? instructionsText : "-");
                if (notesAndCredits.length() > 0) {
                    activity.notesAndCreditsTextView.setText(notesAndCredits);
                    activity.notesAndCreditsLabelView.setVisibility(VISIBLE);
                    activity.notesAndCreditsTextView.setVisibility(VISIBLE);
                }
                activity.favoritesTextView.setText(shortNumber(projectData.getFavorites()));
                activity.lovesTextView.setText(shortNumber(projectData.getLoves()));
                activity.viewsTextView.setText(shortNumber(projectData.getViews()));

                StringBuilder tagList = new StringBuilder();
                int index = 0;
                for (String tag : projectData.getTags()) {
                    tagList.append((index++ > 0 ? ", " : "") + tag);
                }
                if (tagList.length() > 0) {
                    activity.tagsTextView.setText(tagList);
                    activity.tagsTextView.setVisibility(VISIBLE);
                }
                Log.d(TAG, projectData.getModifiedDate());
                Log.d(TAG, projectData.getSharedDate());
                activity.sharedTextView.setText(activity.getString(R.string.shared) + ": " + projectData.getSharedDate());
                activity.modifiedTextView.setText(activity.getString(R.string.modified) + ": " + projectData.getModifiedDate());
            }
        });
    }

    // TODO: improve and move this helper method to Util class
    private static String shortNumber(final int number) {
        if (number < 1_000) {
            return Integer.toString(number);
        } else if (number < 10_000) {
            return Integer.toString(number/1_000) +
                    (number%1000 > 100 ? "." + Integer.toString((number%1000)/100) : "") + "k";
        } else if (number < 1_000_000) {
            return Integer.toString(number/1_000) + "k";
        }
        return Integer.toString(number/1_000_000) + "M";
    }

}
