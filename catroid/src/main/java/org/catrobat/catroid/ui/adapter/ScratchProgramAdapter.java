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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.images.WebImage;
import com.squareup.picasso.Picasso;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScratchProgramData;
import org.catrobat.catroid.utils.Utils;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ScratchProgramAdapter extends ArrayAdapter<ScratchProgramData> {

	private boolean showDetails;
	private int selectMode;
	private Set<Integer> checkedPrograms = new TreeSet<>();
	private OnScratchProgramEditListener onScratchProgramEditListener;

	private static class ViewHolder {
		private RelativeLayout background;
		private CheckBox checkbox;
		private TextView programName;
		private ImageView image;
		private TextView detailsText;
		private View programDetails;
	}

	private static LayoutInflater inflater;

	public ScratchProgramAdapter(Context context, int resource, int textViewResourceId,
			List<ScratchProgramData> objects) {
		super(context, resource, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		showDetails = true;
		selectMode = ListView.CHOICE_MODE_NONE;
	}

	public void setOnScratchProgramEditListener(OnScratchProgramEditListener listener) {
		onScratchProgramEditListener = listener;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	public Set<Integer> getCheckedPrograms() {
		return checkedPrograms;
	}

	public int getAmountOfCheckedPrograms() {
		return checkedPrograms.size();
	}

	public void clearCheckedPrograms() {
		checkedPrograms.clear();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View programView = convertView;
		final ViewHolder holder;
		if (programView == null) {
			programView = inflater.inflate(R.layout.fragment_scratch_project_list_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) programView.findViewById(R.id.scratch_projects_list_item_background);
			holder.checkbox = (CheckBox) programView.findViewById(R.id.scratch_project_checkbox);
			holder.programName = (TextView) programView.findViewById(R.id.scratch_projects_list_item_title);
			holder.image = (ImageView) programView.findViewById(R.id.scratch_projects_list_item_image);
			holder.detailsText = (TextView) programView.findViewById(R.id.scratch_projects_list_item_details_text);
			holder.programDetails = programView.findViewById(R.id.scratch_projects_list_item_details);
			programView.setTag(holder);
		} else {
			holder = (ViewHolder) programView.getTag();
		}

		// ------------------------------------------------------------
		ScratchProgramData programData = getItem(position);
		holder.programName.setText(programData.getTitle());
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

		if (showDetails) {
			holder.programDetails.setVisibility(View.VISIBLE);
			holder.programName.setSingleLine(true);
		} else {
			holder.programDetails.setVisibility(View.GONE);
			holder.programName.setSingleLine(false);
		}

		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedPrograms();
					}
					checkedPrograms.add(position);
				} else {
					checkedPrograms.remove(position);
				}
				notifyDataSetChanged();

				if (onScratchProgramEditListener != null && !onScratchProgramEditListener.onProgramChecked()) {
					holder.checkbox.setChecked(false);
					checkedPrograms.remove(position);
				}
			}
		});

		holder.background.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				return selectMode != ListView.CHOICE_MODE_NONE;
			}
		});

		holder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (selectMode != ListView.CHOICE_MODE_NONE) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				} else if (onScratchProgramEditListener != null) {
					onScratchProgramEditListener.onProgramEdit(position);
				}
			}
		});

		if (checkedPrograms.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}

		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
			holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
			clearCheckedPrograms();
		}
		return programView;
	}

	public interface OnScratchProgramEditListener {
		boolean onProgramChecked();

		void onProgramEdit(int position);
	}
}
