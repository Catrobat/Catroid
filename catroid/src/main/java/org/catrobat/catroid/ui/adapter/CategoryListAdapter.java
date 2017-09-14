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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.List;
import java.util.Map;

public class CategoryListAdapter extends BaseAdapter {

	private Context context;
	private List<String> list;
	private Map<Integer, String> sectionHeader;
	private OnListItemClickListener onListItemClickListener = null;

	public CategoryListAdapter(Context context, List<String> list, Map<Integer, String> sectionHeader) {
		this.context = context;
		this.list = list;
		this.sectionHeader = sectionHeader;
	}

	public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
		this.onListItemClickListener = onListItemClickListener;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public String getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;

		if (view == null) {
			holder = new ViewHolder();
			view = View.inflate(context, R.layout.fragment_formula_editor_categorylist_item, null);
			holder.textView = (TextView) view.findViewById(R.id.fragment_formula_editor_list_item);
			holder.headerTextView = (TextView) view.findViewById(R.id.categorylist_headline_text);
			holder.header = view.findViewById(R.id.categorylist_headline);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(list.get(position));
		if (sectionHeader.containsKey(position)) {
			holder.header.setVisibility(View.VISIBLE);
			holder.headerTextView.setText(sectionHeader.get(position));
		} else {
			holder.header.setVisibility(View.GONE);
		}

		if (onListItemClickListener != null) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onListItemClickListener.onListItemClick(position);
				}
			});
		}
		view.setClickable(true);

		return view;
	}

	public static class ViewHolder {
		public TextView textView;
		public TextView headerTextView;
		public View header;
	}

	public interface OnListItemClickListener {
		void onListItemClick(int position);
	}
}
