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

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.util.List;

public class LookAdapter extends RecyclerViewAdapter<LookData> {

	public LookAdapter(List<LookData> items) {
		super(items);
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		LookData item = items.get(position);

		holder.name.setText(item.getName());
		holder.image.setImageBitmap(item.getThumbnailBitmap());

		holder.details.setVisibility(View.GONE);

		if (showDetails) {
			holder.details.setVisibility(View.VISIBLE);

			holder.leftBottomDetails.setText(R.string.look_measure);
			int[] measure = item.getMeasure();
			String measureString = measure[0] + " x " + measure[1];
			holder.rightBottomDetails.setText(measureString);

			holder.leftTopDetails.setText(R.string.size);
			holder.rightTopDetails.setText(UtilFile.getSizeAsString(new File(item.getAbsolutePath()),
					holder.itemView.getContext()));
		}
	}
}
