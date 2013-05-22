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
import android.widget.ListView;
import android.widget.RelativeLayout;
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
		this.selectMode = ListView.CHOICE_MODE_NONE;
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
		private TextView lookMeasureTextView;
		private ImageView lookArrowView;
		private RelativeLayout lookElement;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = View.inflate(context, R.layout.fragment_look_looklist_item, null);

			holder = new ViewHolder();

			holder.lookImageView = (ImageView) convertView.findViewById(R.id.fragment_look_item_image_view);
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.fragment_look_item_checkbox);
			holder.lookNameTextView = (TextView) convertView.findViewById(R.id.fragment_look_item_name_text_view);
			holder.lookDetailsLinearLayout = (LinearLayout) convertView
					.findViewById(R.id.fragment_look_item_detail_linear_layout);
			holder.lookFileSizeTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_size_text_view);
			holder.lookMeasureTextView = (TextView) holder.lookDetailsLinearLayout
					.findViewById(R.id.fragment_look_item_measure_text_view);
			holder.lookArrowView = (ImageView) convertView.findViewById(R.id.fragment_look_item_arrow_image_view);
			holder.lookElement = (RelativeLayout) convertView.findViewById(R.id.fragment_look_item_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final LookData lookData = lookDataItems.get(position);

		if (lookData != null) {
			holder.lookNameTextView.setTag(position);
			holder.lookElement.setTag(position);

			holder.lookImageView.setImageBitmap(lookData.getThumbnailBitmap());
			holder.lookNameTextView.setText(lookData.getLookName());

			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						if (selectMode == ListView.CHOICE_MODE_SINGLE) {
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

			if (selectMode != ListView.CHOICE_MODE_NONE) {
				holder.checkbox.setVisibility(View.VISIBLE);
				holder.lookArrowView.setVisibility(View.GONE);
				holder.lookElement.setBackgroundResource(R.drawable.button_background_shadowed);
				holder.lookElement.setClickable(false);
				checkboxIsVisible = true;
			} else {
				holder.checkbox.setVisibility(View.GONE);
				holder.checkbox.setChecked(false);
				holder.lookArrowView.setVisibility(View.VISIBLE);
				holder.lookElement.setBackgroundResource(R.drawable.button_background_selector);
				holder.lookElement.setClickable(true);
				clearCheckedItems();
			}

			if (checkedLooks.contains(position)) {
				holder.checkbox.setChecked(true);
			} else {
				holder.checkbox.setChecked(false);
			}

			if (showDetails) {
				if (lookData.getAbsolutePath() != null) {
					holder.lookFileSizeTextView.setText(UtilFile.getSizeAsString(new File(lookData.getAbsolutePath())));
				}
				int[] measure = lookData.getMeasure();
				String measureString = measure[0] + " x " + measure[1];

				holder.lookMeasureTextView.setText(measureString);
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
			if (holder.lookElement.isClickable()) {
				holder.lookElement.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onLookEditListener != null) {
							onLookEditListener.onLookEdit(v);
						}
					}
				});
			} else {
				holder.lookElement.setOnClickListener(null);
			}
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
