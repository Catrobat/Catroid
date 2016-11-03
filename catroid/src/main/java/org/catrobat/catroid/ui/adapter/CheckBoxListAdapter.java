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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CheckBoxListAdapter<T> extends ArrayAdapter<T> {

	protected static class ListItemViewHolder {
		protected RelativeLayout background;
		protected CheckBox checkBox;
		protected TextView name;
		protected ImageView image;
		protected LinearLayout details;
		protected TextView leftTopDetails;
		protected TextView leftBottomDetails;
		protected TextView rightTopDetails;
		protected TextView rightBottomDetails;
	}

	public static final String TAG = CheckBoxListAdapter.class.getSimpleName();

	protected int selectMode;
	protected boolean showDetails;
	protected LayoutInflater inflater;

	protected ListItemClickHandler listItemClickHandler;
	protected ListItemLongClickHandler listItemLongClickHandler;
	protected ListItemCheckHandler listItemCheckHandler;

	protected List<T> itemList;
	protected List<T> checkedItems = new ArrayList<>();

	public CheckBoxListAdapter(Context context, int resource, List<T> listItems) {
		super(context, resource, listItems);
		itemList = listItems;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public int getSelectMode() {
		return selectMode;
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
		notifyDataSetChanged();
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	public void setListItemClickHandler(ListItemClickHandler listItemClickHandler) {
		this.listItemClickHandler = listItemClickHandler;
	}

	public void setListItemLongClickHandler(ListItemLongClickHandler listItemLongClickHandler) {
		this.listItemLongClickHandler = listItemLongClickHandler;
	}

	public void setListItemCheckHandler(ListItemCheckHandler listItemCheckHandler) {
		this.listItemCheckHandler = listItemCheckHandler;
	}

	public void setAllItemsCheckedTo(boolean checked) {
		checkedItems.clear();
		if (checked) {
			checkedItems.addAll(itemList);
		}
		notifyDataSetChanged();
	}

	public List<T> getCheckedItems() {
		return checkedItems;
	}

	public int swapItems(int position1, int position2) {
		Collections.swap(itemList, position1, position2);
		notifyDataSetChanged();
		return position2;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final T listItem = getItem(position);
		final ListItemViewHolder viewHolder;

		View listItemView = convertView;

		if (listItemView == null) {
			listItemView = inflater.inflate(R.layout.list_item, parent, false);
			viewHolder = new ListItemViewHolder();
			viewHolder.background = (RelativeLayout) listItemView.findViewById(R.id.list_item_background);
			viewHolder.checkBox = (CheckBox) listItemView.findViewById(R.id.list_item_checkbox);
			viewHolder.name = (TextView) listItemView.findViewById(R.id.list_item_text_view);
			viewHolder.image = (ImageView) listItemView.findViewById(R.id.list_item_image_view);

			viewHolder.details = (LinearLayout) listItemView.findViewById(R.id.list_item_details);
			viewHolder.leftTopDetails = (TextView) listItemView.findViewById(R.id.details_left_top);
			viewHolder.leftBottomDetails = (TextView) listItemView.findViewById(R.id.details_left_bottom);
			viewHolder.rightTopDetails = (TextView) listItemView.findViewById(R.id.details_right_top);
			viewHolder.rightBottomDetails = (TextView) listItemView.findViewById(R.id.details_right_bottom);

			listItemView.setTag(viewHolder);
		} else {
			viewHolder = (ListItemViewHolder) listItemView.getTag();
		}

		viewHolder.details.setVisibility(View.GONE);

		final View itemView = listItemView;
		viewHolder.background.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				listItemClickHandler.handleOnItemClick(position, itemView, listItem);
			}
		});

		viewHolder.background.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				listItemLongClickHandler.handleOnItemLongClick(position, itemView);
				return true;
			}
		});

		viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				if (b) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						setAllItemsCheckedTo(false);
					}
					if (!checkedItems.contains(listItem)) {
						checkedItems.add(listItem);
					}
				} else {
					checkedItems.remove(listItem);
				}
				onCheckBoxChanged();
			}
		});

		viewHolder.checkBox.setVisibility(selectMode == ListView.CHOICE_MODE_NONE ? View.GONE : View.VISIBLE);
		viewHolder.checkBox.setChecked(checkedItems.contains(listItem));

		return listItemView;
	}

	private void onCheckBoxChanged() {
		listItemCheckHandler.onItemChecked();
	}

	public interface ListItemClickHandler<D> {

		void handleOnItemClick(int position, View view, D listItem);
	}

	public interface ListItemLongClickHandler {

		void handleOnItemLongClick(int position, View view);
	}

	public interface ListItemCheckHandler {

		void onItemChecked();
	}
}
