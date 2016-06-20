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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.base.Preconditions;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.common.ScratchProjectData.ScratchRemixProjectData;
import org.catrobat.catroid.common.ScratchProjectPreviewData;
import org.catrobat.catroid.transfers.FetchScratchProjectDetailsTask;
import org.catrobat.catroid.ui.adapter.ScratchRemixedProjectAdapter;
import org.catrobat.catroid.utils.ExpiringDiskCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.WebImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import uk.co.deanwild.flowtextview.FlowTextView;

import static android.view.View.*;

public class ScratchProjectDetailsActivity extends BaseActivity
        implements FetchScratchProjectDetailsTask.ScratchProjectListTaskDelegate,
        ScratchRemixedProjectAdapter.OnScratchRemixedProjectEditListener {

    private static final String TAG = ScratchProjectDetailsActivity.class.getSimpleName();
    private int imageWidth;
    private int imageHeight;

    private TextView titleTextView;
    private TextView ownerTextView;
    private ImageView imageView;
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
    private WebImageLoader webImageLoader;
    private ListView remixedProjectsListView;
    private ProgressDialog progressDialog;
    private FetchScratchProjectDetailsTask currentTask = null;
    private ScratchRemixedProjectAdapter scratchRemixedProjectAdapter;
    private ScrollView mainScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_project_details);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        webImageLoader = new WebImageLoader(
                ExpiringLruMemoryImageCache.getInstance(),
                ExpiringDiskCache.getInstance(this),
                executorService
        );

        imageWidth = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_width);
        imageHeight = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);
        titleTextView = (TextView) findViewById(R.id.scratch_project_title);
        ownerTextView = (TextView) findViewById(R.id.scratch_project_owner);
        mainScrollView = (ScrollView) findViewById(R.id.scratch_project_scroll_view);
        imageView = (ImageView) findViewById(R.id.scratch_project_image_view);
        imageView.getLayoutParams().width = imageWidth;
        imageView.getLayoutParams().height = imageHeight;
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

        final ScratchProjectPreviewData projectData = getIntent().getParcelableExtra(Constants.SCRATCH_PROJECT_DATA);
        final ScratchProjectDetailsActivity activity = this;

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: check if this is already running on UI-thread?
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: create websocket connection and set up receiver
                        Log.i(TAG, "Converting project:");
                        ScratchConverterActivity.ScratchConverterClient client = ScratchConverterActivity.ScratchConverterClient
                                .getInstance();
                        Log.i(TAG, projectData.getTitle());
                        // TODO: send convert command
                        client.convertProject(projectData.getId(), projectData.getTitle());
                        ToastUtil.showSuccess(activity, activity.getString(R.string.scratch_conversion_started));
                        activity.finish(); // dismiss current activity!
                    }
                });
            }
        });

        loadData(projectData);
    }

    private void loadData(ScratchProjectPreviewData scratchProjectData) {
        Log.i(TAG, scratchProjectData.getTitle());
        instructionsFlowTextView.setText("-");
        notesAndCreditsLabelView.setVisibility(GONE);
        notesAndCreditsTextView.setVisibility(GONE);
        tagsTextView.setVisibility(GONE);

        // TODO: use LRU cache!

        if (scratchRemixedProjectAdapter != null) {
            scratchRemixedProjectAdapter.clear();
        }

        if (scratchProjectData != null) {
            titleTextView.setText(scratchProjectData.getTitle());

            if (scratchProjectData.getProjectImage() != null) {
                webImageLoader.fetchAndShowImage(
                        scratchProjectData.getProjectImage().getUrl().toString(),
                        imageView, imageWidth, imageHeight
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

    private void initRemixAdapter(List<ScratchRemixProjectData> scratchRemixedProjectsData) {
        if (scratchRemixedProjectsData == null) {
            scratchRemixedProjectsData = new ArrayList<>();
        }
        scratchRemixedProjectAdapter = new ScratchRemixedProjectAdapter(this,
                R.layout.fragment_scratch_project_list_item,
                R.id.scratch_projects_list_item_title,
                scratchRemixedProjectsData);
        remixedProjectsListView.setAdapter(scratchRemixedProjectAdapter);
        scratchRemixedProjectAdapter.setOnScratchRemixedProjectEditListener(this);
        setListViewHeightBasedOnItems(remixedProjectsListView);
    }

    public void onProjectEdit(int position) {
        // TODO: improve this
        Log.d(TAG, "Clicked on remix at position: " + position);
        ScratchRemixProjectData scratchRemixProjectData = scratchRemixedProjectAdapter.getItem(position);
        Log.d(TAG, "" + scratchRemixProjectData.getId());
        ScratchProjectPreviewData scratchProjectPreviewData = new ScratchProjectPreviewData(
                scratchRemixProjectData.getId(),
                scratchRemixProjectData.getTitle(), null, null);
        scratchProjectPreviewData.setProjectImage(scratchRemixProjectData.getProjectImage());
        loadData(scratchProjectPreviewData);
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
                String temp = projectData.getInstructions().replace("\n\n", "\n");
                final String instructionsText = (temp.length() > 0) ? temp : "--";
                Log.d(TAG, "Instructions: " + instructionsText);
                final String notesAndCredits = projectData.getNotesAndCredits().replace("\n\n", "\n");

                if (notesAndCredits.length() > 0) {
                    activity.notesAndCreditsTextView.setText(notesAndCredits);
                    activity.notesAndCreditsLabelView.setVisibility(VISIBLE);
                    activity.notesAndCreditsTextView.setVisibility(VISIBLE);
                }
                activity.instructionsFlowTextView.setText(instructionsText);

                float textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, activity.getResources()
                        .getDisplayMetrics());
                activity.instructionsFlowTextView.setTextSize(textSize);
                activity.instructionsFlowTextView.setTextColor(Color.LTGRAY);

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
                activity.initRemixAdapter(projectData.getRemixes());
                activity.mainScrollView.fullScroll(ScrollView.FOCUS_UP); // scroll to top
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

    // TODO: move this helper method to Util class
    public static boolean setListViewHeightBasedOnItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {
            int numberOfItems = listAdapter.getCount();
            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; ++itemPos) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();
            return true;
        } else {
            return false;
        }
    }

}
