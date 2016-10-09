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
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.List;

public class AccessibilityCheckboxAdapter extends ArrayAdapter<AccessibilityCheckboxAdapter.AccessibilityCheckbox> {

	private static LayoutInflater inflater;

	public static class AccessibilityCheckbox {
		public boolean value;
		public String title;
		public String summary;
	}

	private static class ViewHolder {
		private TextView preferenceTitle;
		private TextView preferenceSummary;
		private CheckBox checkBox;
	}

	public AccessibilityCheckboxAdapter(Context context, int resource, List<AccessibilityCheckbox> objects) {
		super(context, resource, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View projectView = convertView;
		final ViewHolder holder;
		if (projectView == null) {
			projectView = inflater.inflate(R.layout.fragment_access_preference_list_item, parent, false);
			holder = new ViewHolder();
			holder.preferenceTitle = (TextView) projectView.findViewById(R.id.access_preference_title);
			holder.preferenceSummary = (TextView) projectView.findViewById(R.id.access_preference_summary);
			holder.checkBox = (CheckBox) projectView.findViewById(R.id.access_preference_checkbox);
			projectView.setTag(holder);
		} else {
			holder = (ViewHolder) projectView.getTag();
		}

		final AccessibilityCheckbox preference = getItem(position);
		holder.preferenceTitle.setText(preference.title);
		holder.preferenceSummary.setText(preference.summary);
		holder.checkBox.setChecked(preference.value);

		return projectView;
	}
}
