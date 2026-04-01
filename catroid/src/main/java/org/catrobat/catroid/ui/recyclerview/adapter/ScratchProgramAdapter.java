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

import android.view.View;

import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedViewHolder;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ScratchProgramAdapter extends ExtendedRVAdapter<ScratchProgramData> {

	public ScratchProgramAdapter(List<ScratchProgramData> objects) {
		super(objects);
	}

	@Override
	public void onBindViewHolder(ExtendedViewHolder holder, int position) {
		ScratchProgramData item = items.get(position);

		holder.title.setText(item.getTitle());

		if (item.getImage().getUrl() != null) {
			int height = holder.image.getContext().getResources().getDimensionPixelSize(R.dimen
					.scratch_project_thumbnail_height);
			String originalImageURL = item.getImage().getUrl().toString();
			String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.get().load(thumbnailImageURL).into(holder.image);
		} else {
			holder.image.setImageBitmap(null);
		}

		if (showDetails) {
			holder.details.setVisibility(View.VISIBLE);
			holder.details.setText(item.getOwner());
		} else {
			holder.details.setVisibility(View.GONE);
		}
	}
}
