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

package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScratchProjectData;
import org.catrobat.catroid.utils.ExpiringDiskCache;
import org.catrobat.catroid.utils.ExpiringLruMemoryImageCache;
import org.catrobat.catroid.utils.WebImageLoader;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScratchRemixedProjectAdapter extends ArrayAdapter<ScratchProjectData.ScratchRemixProjectData> {
	private static final String TAG = ScratchRemixedProjectAdapter.class.getSimpleName();
	private static final int WEBIMAGE_DOWNLOADER_POOL_SIZE = 5;

	private WebImageLoader webImageLoader;
	private ScratchRemixedProjectEditListener scratchRemixedProjectEditListener;

	private static class ViewHolder {
		private RelativeLayout background;
		private TextView projectName;
		private ImageView image;
		private TextView detailsText;
		private View projectDetails;
	}

	private static LayoutInflater inflater;

	public ScratchRemixedProjectAdapter(Context context, int resource, int textViewResourceId,
			List<ScratchProjectData.ScratchRemixProjectData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ExecutorService executorService = Executors.newFixedThreadPool(WEBIMAGE_DOWNLOADER_POOL_SIZE);
		Log.d(TAG, "Number of remixes: " + objects.size());
		webImageLoader = new WebImageLoader(
				ExpiringLruMemoryImageCache.getInstance(),
				ExpiringDiskCache.getInstance(context),
				executorService
		);
	}

	public void setScratchRemixedProjectEditListener(ScratchRemixedProjectEditListener listener) {
		scratchRemixedProjectEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.fragment_scratch_project_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.scratch_projects_list_item_background);
			holder.projectName = (TextView) projectView.findViewById(R.id.scratch_projects_list_item_title);
			holder.image = (ImageView) projectView.findViewById(R.id.scratch_projects_list_item_image);
			holder.detailsText = (TextView) projectView.findViewById(R.id.scratch_projects_list_item_details_text);
			holder.projectDetails = projectView.findViewById(R.id.scratch_projects_list_item_details);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		// ------------------------------------------------------------
		ScratchProjectData.ScratchRemixProjectData projectData = getItem(position);

		// set name of project:
		holder.projectName.setText(projectData.getTitle());

		// set details of project:
		holder.detailsText.setText(getContext().getString(R.string.by) + " " + projectData.getOwner());
		holder.detailsText.setSingleLine(false);

		// set project image (threaded):
		WebImage httpImageMetadata = projectData.getProjectImage();
		if (httpImageMetadata != null) {
			int width = getContext().getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_width);
			int height = getContext().getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			webImageLoader.fetchAndShowImage(httpImageMetadata.getUrl().toString(),
					holder.image, width, height);
		} else {
			// clear old image of other project if this is a reused view element
			holder.image.setImageBitmap(null);
		}

		holder.projectDetails.setVisibility(View.VISIBLE);
		holder.projectName.setSingleLine(true);
		holder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scratchRemixedProjectEditListener != null) {
					scratchRemixedProjectEditListener.onProjectEdit(position);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
		return projectView;
	}

	public interface ScratchRemixedProjectEditListener {
		void onProjectEdit(int position);
	}

}
