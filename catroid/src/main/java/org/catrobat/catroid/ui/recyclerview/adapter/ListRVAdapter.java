/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.ListVH;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.utils.NumberFormats.trimTrailingCharacters;

public class ListRVAdapter extends RVAdapter<UserList> {

	ListRVAdapter(List<UserList> items) {
		super(items);
	}

	@Override
	public CheckableVH onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new ListVH(view);
	}

	@Override
	public void onBindViewHolder(CheckableVH holder, int position) {
		super.onBindViewHolder(holder, position);

		UserList item = items.get(position);
		ListVH listVH = (ListVH) holder;
		listVH.title.setText(item.getName());

		List<String> userList = new ArrayList<>();
		for (Object userListItem : item.getValue()) {
			userList.add(trimTrailingCharacters(userListItem.toString()));
		}

		listVH.spinner.setAdapter(new UserListValuesAdapter(holder.itemView.getContext(), userList));
	}
}
