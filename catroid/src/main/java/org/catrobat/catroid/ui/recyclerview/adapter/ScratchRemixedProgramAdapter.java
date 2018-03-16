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

import android.view.View;

import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ScratchRemixedProgramAdapter extends ExtendedRVAdapter<ScratchProgramData> {

	private OnItemClickListener onItemClickListener;

	@Override
	public void onBindViewHolder(final ExtendedVH holder, int position) {

		final ScratchProgramData item = items.get(position);
		holder.name.setText(item.getTitle());
		if (item.getImage().getUrl() != null) {
			final int height = holder.image.getContext().getResources().getDimensionPixelSize(R.dimen
					.scratch_project_thumbnail_height);
			final String originalImageURL = item.getImage().getUrl().toString();
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(holder.image.getContext()).load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}
		holder.details.setVisibility(View.VISIBLE);
		holder.name.setSingleLine(true);
		holder.details.setText(item.getOwner());
		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onItemClickListener.onItemClick(item);
			}
		});
	}

	public ScratchRemixedProgramAdapter(List<ScratchProgramData> objects) {
		super(objects);
	}

	@Override
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.onItemClickListener = listener;
	}
}
