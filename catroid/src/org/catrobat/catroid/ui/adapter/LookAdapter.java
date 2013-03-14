/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.utils.UtilFile;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LookAdapter extends ArrayAdapter<LookData> implements ScriptActivityAdapterInterface {

	protected ArrayList<LookData> lookDataItems;
	protected Context context;

	private OnLookEditListener onLookEditListener;

	private int selectMode;
	private boolean showDetails;
	private SortedSet<Integer> checkedLooks = new TreeSet<Integer>();

	public LookAdapter(final Context context, int textViewResourceId, ArrayList<LookData> items, boolean showDetails) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.showDetails = showDetails;
		this.lookDataItems = items;
		this.selectMode = Constants.SELECT_NONE;
	}

	public void setOnLookEditListener(OnLookEditListener listener) {
		onLookEditListener = listener;
	}

	private static class ViewHolder {
		private ImageView lookImageView;
		private CheckBox checkbox;
		private TextView lookNameTextView;
		private LinearLayout lookDetailsLinearLayout;
		private TextView lookFileSizeTextView;
		private TextView lookResolutionTextView;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.fragment_look_looklist_item, null);

			holder = new ViewHolder();

			holder.lookImageView = (ImageView) convertView.findViewById(R.id.look_image);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.look_checkbox);
			holder.lookNameTextView = (TextView) convertView.findViewById(R.id.look_name);
			holder.lookDetailsLinearLayout = (LinearLayout) convertView.findViewById(R.id.look_details);
			holder.lookFileSizeTextView = (TextView) holder.lookDetailsLinearLayout.findViewById(R.id.look_size);
			holder.lookResolutionTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.look_resolution);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final LookData lookData = lookDataItems.get(position);

		if (lookData != null) {
			holder.lookNameTextView.setTag(position);
			holder.lookImageView.setTag(position);

			holder.lookImageView.setImageBitmap(lookData.getThumbnailBitmap());
			holder.lookNameTextView.setText(lookData.getLookName());

			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (selectMode == Constants.SINGLE_SELECT) {
							clearCheckedItems();
						}
						checkedLooks.add(position);
					} else {
						checkedLooks.remove(position);
					}
					notifyDataSetChanged();

					if (onLookEditListener != null) {
						onLookEditListener.onLookChecked();
					}
				}
			});

			boolean checkboxIsVisible = false;

			if (selectMode != Constants.SELECT_NONE) {
				holder.checkbox.setVisibility(View.VISIBLE);
				checkboxIsVisible = true;
			} else {
				holder.checkbox.setVisibility(View.GONE);
				holder.checkbox.setChecked(false);
				clearCheckedItems();
			}

			if (checkedLooks.contains(position)) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
			}

			if (showDetails) {
				if (lookData.getAbsolutePath() != null) {
					holder.lookFileSizeTextView.setText(getContext().getString(R.string.size) + " "
							+ UtilFile.getSizeAsString(new File(lookData.getAbsolutePath())));
				}
				int[] resolution = lookData.getResolution();
				String resolutionString = resolution[0] + " x " + resolution[1];

				// Shorter string on active ActionMode
				if (!checkboxIsVisible) {
					resolutionString = getContext().getString(R.string.look_resolution) + " " + resolutionString;
				}
				holder.lookResolutionTextView.setText(resolutionString);
				holder.lookDetailsLinearLayout.setVisibility(TextView.VISIBLE);
			} else {
				holder.lookDetailsLinearLayout.setVisibility(TextView.GONE);
			}

			// Disable ImageView on active ActionMode
			if (checkboxIsVisible) {
				holder.lookImageView.setEnabled(false);
			} else {
				holder.lookImageView.setEnabled(true);
			}

			holder.lookImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onLookEditListener != null) {
						onLookEditListener.onLookEdit(v);
					}
				}
			});
		}
		return convertView;
	}

	@Override
	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	@Override
	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public void setSelectMode(int mode) {
		selectMode = mode;
	}

	@Override
	public int getSelectMode() {
		return selectMode;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedLooks.size();
	}

	@Override
	public SortedSet<Integer> getCheckedItems() {
		return checkedLooks;
	}

	@Override
	public void clearCheckedItems() {
		checkedLooks.clear();
	}

	public interface OnLookEditListener {

		public void onLookEdit(View v);

		public void onLookChecked();
	}
}
