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
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.catrobat.catroid.ui.listitems.CheckBoxListItem;

import java.util.List;

public abstract class CheckBoxListAdapter extends ArrayAdapter<CheckBoxListItem> {

	public static final String TAG = CheckBoxListAdapter.class.getSimpleName();

	private List<CheckBoxListItem> itemList;

	CheckBoxListAdapter(Context context, int resource, List<CheckBoxListItem> listItems) {
		super(context, resource, listItems);
	}

	private void setCheckBoxVisibility(boolean visible) {
		for (CheckBoxListItem listItem : itemList) {
			listItem.getCheckbox().setVisibility(visible ? View.VISIBLE : View.GONE);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

		}
		return convertView;
	}

	abstract void deleteCheckedItems();

}
