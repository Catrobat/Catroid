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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.content.Context;
import android.view.View;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;
import org.catrobat.catroid.utils.FileMetaDataExtractor;

import java.util.List;
import java.util.Locale;

public class LookAdapter extends ExtendedRVAdapter<LookData> {

	public LookAdapter(List<LookData> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(final ExtendedVH holder, int position) {
		LookData item = items.get(position);

		holder.title.setText(item.getName());
		holder.image.setImageBitmap(item.getThumbnailBitmap());

		if (showDetails) {
			int[] measure = item.getMeasure();
			String measureString = measure[0] + " x " + measure[1];

			Context context = holder.itemView.getContext();
			holder.details.setText(String.format(Locale.getDefault(),
					context.getString(R.string.look_details),
					measureString,
					FileMetaDataExtractor.getSizeAsString(item.getFile(), context)));
			holder.details.setVisibility(View.VISIBLE);
		} else {
			holder.details.setVisibility(View.GONE);
		}
	}
}
