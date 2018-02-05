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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.recyclerview.viewholder.ScratchJobVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;
import org.catrobat.catroid.utils.Utils;

import java.util.List;
import java.util.Locale;

public class ScratchJobAdapter extends RVAdapter<Job> {
	private OnItemClickListener<Job> scratchProgramOnClickListener;

	public ScratchJobAdapter(List<Job> objects) {
		super(objects);
	}

	public void setScratchProgramOnClickListener(OnItemClickListener<Job> listener) {
		scratchProgramOnClickListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_scratch_job_list_item, parent, false);
		return new ScratchJobVH(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		onBindViewHolder((ScratchJobVH) holder, position);
	}

	public void onBindViewHolder(final ScratchJobVH holder, int position) {
		final Job item = items.get(position);

		holder.title.setText(item.getTitle());
		if (item.getImage().getUrl() != null) {
			final int height = holder.image.getContext().getResources().getDimensionPixelSize(R.dimen
					.scratch_project_thumbnail_height);
			final String originalImageURL = item.getImage().getUrl().toString();
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(holder.image.getContext()).load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}

		holder.title.setSingleLine(true);

		holder.status.setTextColor(Color.WHITE);
		holder.details.setVisibility(View.VISIBLE);

		short progress = 0;
		boolean showProgressBar = false;
		switch (item.getState()) {
			case UNSCHEDULED:
				holder.status.setText("-");
				break;
			case SCHEDULED:
				holder.status.setText(holder.status.getContext().getString(R.string.status_scheduled));
				showProgressBar = true;
				break;
			case READY:
				holder.status.setText(holder.status.getContext().getString(R.string.status_waiting_for_worker));
				showProgressBar = true;
				break;
			case RUNNING:
				holder.status.setText(holder.status.getContext().getString(R.string.status_started));
				showProgressBar = true;
				progress = item.getProgress();
				break;
			case FINISHED:
				int messageID;
				switch (item.getDownloadState()) {
					case DOWNLOADING:
						messageID = R.string.status_downloading;
						progress = item.getDownloadProgress();
						showProgressBar = true;
						break;
					case DOWNLOADED:
						messageID = R.string.status_download_finished;
						break;
					default:
						messageID = R.string.status_conversion_finished;
				}
				holder.status.setText(holder.status.getContext().getString(messageID));
				break;
			case FAILED:
				holder.status.setText(R.string.status_conversion_failed);
				holder.status.setTextColor(Color.RED);
				break;
		}

		if (showProgressBar) {
			holder.progress.setText(String.format(Locale.getDefault(), "%1$d%%", progress));
			holder.progressBar.setProgress(progress);
			holder.progressLayout.setVisibility(View.VISIBLE);
		} else {
			holder.progressLayout.setVisibility(View.GONE);
		}

		WebImage httpImageMetadata = item.getImage();
		if (httpImageMetadata != null && httpImageMetadata.getUrl() != null) {
			final int height = holder.status.getContext().getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			final String originalImageURL = httpImageMetadata.getUrl().toString();

			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(holder.status.getContext()).load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}

		holder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scratchProgramOnClickListener != null) {
					scratchProgramOnClickListener.onItemClick(item);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
	}
}
