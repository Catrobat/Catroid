/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.scratchconverter.protocol.Job;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedViewHolder;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ScratchJobAdapter extends ExtendedRVAdapter<Job> {

	public ScratchJobAdapter(List<Job> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(ExtendedViewHolder holder, int position) {
		Job item = items.get(position);

		Context context = holder.itemView.getContext();
		holder.title.setText(item.getTitle());

		if (item.getImage() != null && item.getImage().getUrl() != null) {
			int height = context.getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			String originalImageURL = item.getImage().getUrl().toString();
			String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.get().load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}

		switch (item.getState()) {
			case UNSCHEDULED:
				holder.details.setText("-");
				break;
			case SCHEDULED:
				holder.details.setText(context.getString(R.string.status_scheduled));
				break;
			case READY:
				holder.details.setText(context.getString(R.string.status_waiting_for_worker));
				break;
			case RUNNING:
				holder.details.setText(context.getString(R.string.status_started));
				break;
			case FINISHED:
				int messageID;
				switch (item.getDownloadState()) {
					case DOWNLOADING:
						messageID = R.string.status_downloading;
						break;
					case DOWNLOADED:
						messageID = R.string.status_download_finished;
						break;
					case CANCELED:
						messageID = R.string.status_download_canceled;
						break;
					default:
						messageID = R.string.status_conversion_finished;
				}
				holder.details.setText(context.getString(messageID));
				break;
			case FAILED:
				holder.details.setText(R.string.status_conversion_failed);
				holder.details.setTextColor(Color.RED);
				break;
		}

		WebImage httpImageMetadata = item.getImage();
		if (httpImageMetadata != null && httpImageMetadata.getUrl() != null) {
			int height = context.getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			String originalImageURL = httpImageMetadata.getUrl().toString();

			String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.get().load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}
	}
}
