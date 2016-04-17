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

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.utils.FileCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.WebImageLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScratchProjectDetailsActivity extends BaseActivity {

    private static final String TAG = ScratchProjectDetailsActivity.class.getSimpleName();

    private TextView projectTitleTextView;
    private ImageView projectImageView;
    private TextView projectInstructionsTextView;
    private TextView projectNotesAndCreditsTextView;
    private WebImageLoader webImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_project_details);

        ScratchProjectData scratchProjectData = getIntent().getParcelableExtra(Constants.SCRATCH_PROJECT_DATA);

        final int WEBIMAGE_DOWNLOADER_POOL_SIZE = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(WEBIMAGE_DOWNLOADER_POOL_SIZE);
        webImageLoader = new WebImageLoader(this, ExpiringLruMemoryImageCache.getInstance(), new FileCache(this), executorService);

        Log.i(TAG, scratchProjectData.getTitle());
        projectTitleTextView = (TextView) findViewById(R.id.scratch_project_title);
        projectImageView = (ImageView) findViewById(R.id.scratch_project_image_view);
        projectInstructionsTextView = (TextView) findViewById(R.id.scratch_project_instructions_text);
        projectNotesAndCreditsTextView = (TextView) findViewById(R.id.scratch_project_notes_and_credits_text);

        int width = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_width);
        int height = getResources().getDimensionPixelSize(R.dimen.scratch_project_image_height);

        if (scratchProjectData != null) {
            projectTitleTextView.setText(scratchProjectData.getTitle());
            projectInstructionsTextView.setText(scratchProjectData.getContent());

            if (scratchProjectData.getProjectImage() != null) {
                webImageLoader.fetchAndShowImage(
                        scratchProjectData.getProjectImage().getUrl().toString(),
                        projectImageView, width, height
                );
            }
        }
    }

}
