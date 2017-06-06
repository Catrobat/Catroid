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
package org.catrobat.catroid.ui.adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.utils.UtilFile;

import java.io.File;
import java.util.List;

public class SoundListAdapter extends CheckBoxListAdapter<SoundInfo> {

	public static final String TAG = SoundListAdapter.class.getSimpleName();

	public SoundListAdapter(Context context, int resource, List<SoundInfo> listItems) {
		super(context, resource, listItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		SoundInfo soundInfo = getItem(position);

		listItemViewHolder.name.setText(soundInfo.getTitle());
		listItemViewHolder.image.setImageResource(R.drawable.ic_media_play);

		listItemViewHolder.details.setVisibility(View.VISIBLE);
		listItemViewHolder.leftTopDetails.setText(getContext().getString(R.string.length));
		listItemViewHolder.rightTopDetails.setText(getSoundDuration(soundInfo));
		if (showDetails) {
			listItemViewHolder.leftBottomDetails.setVisibility(View.VISIBLE);
			listItemViewHolder.rightBottomDetails.setVisibility(View.VISIBLE);
			listItemViewHolder.leftBottomDetails.setText(getContext().getString(R.string.size));
			listItemViewHolder.rightBottomDetails.setText(UtilFile.getSizeAsString(new File(soundInfo.getAbsolutePath
					()),getContext()));
		} else {
			listItemViewHolder.leftBottomDetails.setVisibility(View.GONE);
			listItemViewHolder.rightBottomDetails.setVisibility(View.GONE);
		}

		return listItemView;
	}

	private String getSoundDuration(SoundInfo soundInfo) {
		MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
		metadataRetriever.setDataSource(soundInfo.getAbsolutePath());

		long duration = Integer.parseInt(metadataRetriever.extractMetadata(MediaMetadataRetriever
				.METADATA_KEY_DURATION));

		duration = (duration / 1000) == 0 ? 1 : (duration / 1000);
		return DateUtils.formatElapsedTime(duration);
	}
}
