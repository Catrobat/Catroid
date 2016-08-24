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
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.utils.Utils;

import java.util.List;
import java.util.Locale;

public class ScratchJobAdapter extends ArrayAdapter<Job> {
	private static final String TAG = ScratchRemixedProgramAdapter.class.getSimpleName();

	private ScratchJobEditListener scratchJobEditListener;

	private static class ViewHolder {
		private RelativeLayout background;
		private TextView title;
		private ImageView image;
		private TextView status;
		public RelativeLayout progressLayout;
		private ProgressBar progressBar;
		private TextView progress;
	}

	private static LayoutInflater inflater;

	public ScratchJobAdapter(Context context, int resource, int textViewResourceId, List<Job> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d(TAG, "Number of remixes: " + objects.size());
	}

	public void setScratchJobEditListener(ScratchJobEditListener listener) {
		scratchJobEditListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.fragment_scratch_job_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) projectView.findViewById(R.id.scratch_job_list_item_background);
			holder.title = (TextView) projectView.findViewById(R.id.scratch_job_list_item_title);
			holder.image = (ImageView) projectView.findViewById(R.id.scratch_job_list_item_image);
			holder.status = (TextView) projectView.findViewById(R.id.scratch_job_list_item_status);
			holder.progressLayout = (RelativeLayout) projectView.findViewById(R.id.scratch_job_list_item_progress_layout);
			holder.progressBar = (ProgressBar) projectView.findViewById(R.id.scratch_job_list_item_progress_bar);
			holder.progress = (TextView) projectView.findViewById(R.id.scratch_job_list_item_progress_text);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		// ------------------------------------------------------------
		final Job job = getItem(position);

		// set name of project:
		holder.title.setText(job.getTitle());
		holder.title.setSingleLine(true);

		// set status of project:
		holder.status.setTextColor(Color.WHITE);
		switch (job.getState()) {
			case UNSCHEDULED:
				holder.status.setText("-");
				break;
			case SCHEDULED:
				holder.status.setText(getContext().getString(R.string.status_scheduled));
				break;
			case READY:
				holder.status.setText(getContext().getString(R.string.status_waiting_for_worker));
				break;
			case RUNNING:
				holder.status.setText(getContext().getString(R.string.status_started));
				break;
			case FINISHED:
				int messageID;
				if (job.isDownloading()) {
					messageID = R.string.status_downloading;
				} else if (job.isAlreadyDownloaded()) {
					messageID = R.string.status_download_finished;
				} else {
					messageID = R.string.status_conversion_finished;
				}
				holder.status.setText(getContext().getString(messageID));
				break;
			case FAILED:
				holder.status.setText(R.string.status_conversion_failed);
				holder.status.setTextColor(Color.RED);
				break;
		}

		if (job.getState() == Job.State.FINISHED || job.getState() == Job.State.FAILED) {
			holder.progressLayout.setVisibility(View.GONE);
		} else {
			// update progress state of project:
			final Double progress = Double.valueOf(job.getProgress());
			holder.progress.setText(String.format(Locale.getDefault(), "%1$d%%", progress.intValue()));
			holder.progressBar.setProgress(progress.intValue());
			holder.progressLayout.setVisibility(View.VISIBLE);
		}

		// set project image (threaded):
		WebImage httpImageMetadata = job.getImage();
		if (httpImageMetadata != null && httpImageMetadata.getUrl() != null) {
			final int height = getContext().getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			final String originalImageURL = httpImageMetadata.getUrl().toString();

			// load image but only thumnail!
			// in order to download only thumbnail version of the original image
			// we have to reduce the image size in the URL
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(getContext()).load(thumbnailImageURL).into(holder.image);
		} else {
			// clear old image of other project if this is a reused view element
			holder.image.setImageBitmap(null);
		}

		holder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scratchJobEditListener != null) {
					scratchJobEditListener.onProjectEdit(position);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
		return projectView;
	}

	public interface ScratchJobEditListener {
		void onProjectEdit(int position);
	}
}
