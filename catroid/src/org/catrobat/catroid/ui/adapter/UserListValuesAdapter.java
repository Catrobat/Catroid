/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.List;

public class UserListValuesAdapter extends BaseAdapter implements View.OnClickListener {

	private Context context;
	private List<String> userListValuesList;

	private static class ViewHolder {
		private TextView text1;
	}

	public UserListValuesAdapter(Context context, List<String> userListValuesList) {
		this.context = context;
		this.userListValuesList = userListValuesList;
	}

	@Override
	public int getCount() {
		if (userListValuesList.size() == 0) {
			return 1;
		}
		return userListValuesList.size();
	}

	@Override
	public Object getItem(int index) {
		if (index < userListValuesList.size()) {
			return userListValuesList.get(index);
		}
		return null;
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			view = View.inflate(context, android.R.layout.simple_spinner_dropdown_item, null);
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		} else if (view.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		} else {
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		}

		holder.text1.setText(view.getContext().getString(R.string.formula_editor_fragment_data_current_items));

		return view;
	}

	@Override
	public View getDropDownView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		} else if (view.getTag() instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		} else {
			holder = new ViewHolder();
			holder.text1 = (TextView) view.findViewById(android.R.id.text1);
			view.setTag(holder);
		}
		view.setOnClickListener(this);

		String currentItemStringValue = getItem(position) == null ? null : getItem(position).toString();
		if (currentItemStringValue == null) {
			currentItemStringValue = "";
		}

		holder.text1.setText(currentItemStringValue);

		return view;
	}

	@Override
	public void onClick(View view) {
	}
}
