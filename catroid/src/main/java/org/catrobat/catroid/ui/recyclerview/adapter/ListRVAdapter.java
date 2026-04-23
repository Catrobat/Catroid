/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.ui.adapter.UserListValuesAdapter;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;
import org.catrobat.catroid.ui.recyclerview.viewholder.ListViewHolder;
import org.catrobat.catroid.utils.ShowTextUtils;

import java.util.ArrayList;
import java.util.List;

public class ListRVAdapter extends RVAdapter<UserList> {

	ListRVAdapter(List<UserList> items) {
		super(items);
	}

	@Override
	public CheckableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new ListViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CheckableViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		UserList item = items.get(position);
		ListViewHolder listViewHolder = (ListViewHolder) holder;
		listViewHolder.title.setText(item.getName());

		List<String> userList = new ArrayList<>();
		for (Object userListItem : item.getValue()) {
			userList.add(ShowTextUtils.convertObjectToString(userListItem));
		}
		listViewHolder.spinner.setAdapter(new UserListValuesAdapter(holder.itemView.getContext(), userList));
	}
}
