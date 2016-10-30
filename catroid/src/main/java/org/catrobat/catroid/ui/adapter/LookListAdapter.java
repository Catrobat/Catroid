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
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.util.List;

public class LookListAdapter extends CheckBoxListAdapter<LookData> {

	public static final String TAG = LookListAdapter.class.getSimpleName();

	public LookListAdapter(Context context, int resource, List<LookData> listItems) {
		super(context, resource, listItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		LookData lookData = getItem(position);

		listItemViewHolder.name.setText(lookData.getLookName());
		listItemViewHolder.image.setImageBitmap(lookData.getThumbnailBitmap());

		if (showDetails) {
			listItemViewHolder.details.setVisibility(View.VISIBLE);

			listItemViewHolder.leftBottomDetails.setText(R.string.look_measure);
			int[] measure = lookData.getMeasure();
			String measureString = measure[0] + " x " + measure[1];
			listItemViewHolder.rightBottomDetails.setText(measureString);

			listItemViewHolder.leftTopDetails.setText(R.string.size);
			listItemViewHolder.rightTopDetails.setText(UtilFile.getSizeAsString(new File(lookData.getAbsolutePath())));
		}

		return listItemView;
	}
}
