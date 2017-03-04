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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.utils.Utils;

import java.util.List;

public class ScratchRemixedProgramAdapter extends ArrayAdapter<ScratchProgramData> {
	private static final String TAG = ScratchRemixedProgramAdapter.class.getSimpleName();

	private ScratchRemixedProgramEditListener scratchRemixedProgramEditListener;

	private static class ViewHolder {
		private RelativeLayout background;
		private TextView projectName;
		private ImageView image;
		private TextView detailsText;
		private View projectDetails;
	}

	private static LayoutInflater inflater;

	public ScratchRemixedProgramAdapter(Context context, int resource, int textViewResourceId,
			List<ScratchProgramData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Log.d(TAG, "Number of remixes: " + objects.size());
	}

	public void setScratchRemixedProgramEditListener(ScratchRemixedProgramEditListener listener) {
		scratchRemixedProgramEditListener = listener;
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

		final ScratchProgramData programData = getItem(position);
		holder.projectName.setText(programData.getTitle());
		holder.detailsText.setText(getContext().getString(R.string.by_x, programData.getOwner()));
		holder.detailsText.setSingleLine(false);

		WebImage httpImageMetadata = programData.getImage();
		if (httpImageMetadata != null && httpImageMetadata.getUrl() != null) {
			final int height = getContext().getResources().getDimensionPixelSize(R.dimen.scratch_project_thumbnail_height);
			final String originalImageURL = httpImageMetadata.getUrl().toString();

			// load image but only thumnail!
			// in order to download only thumbnail version of the original image
			// we have to reduce the image size in the URL
			final String thumbnailImageURL = Utils.changeSizeOfScratchImageURL(originalImageURL, height);
			Picasso.with(getContext()).load(thumbnailImageURL).into(holder.image);
		} else {
			// clear old image of other program if this is a reused view element
			holder.image.setImageBitmap(null);
		}

		holder.projectDetails.setVisibility(View.VISIBLE);
		holder.projectName.setSingleLine(true);
		holder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scratchRemixedProgramEditListener != null) {
					scratchRemixedProgramEditListener.onProjectEdit(position);
				}
			}
		});

		holder.background.setBackgroundResource(R.drawable.button_background_selector);
		return projectView;
	}

	public interface ScratchRemixedProgramEditListener {
		void onProjectEdit(int position);
	}
}
