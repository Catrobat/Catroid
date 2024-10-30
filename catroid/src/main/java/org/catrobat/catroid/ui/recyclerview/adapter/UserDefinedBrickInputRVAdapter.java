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
import android.widget.TextView;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder;
import org.catrobat.catroid.ui.recyclerview.viewholder.VariableViewHolder;
import org.catrobat.catroid.userbrick.UserDefinedBrickInput;
import org.catrobat.catroid.utils.ShowTextUtils;
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider;

import java.util.List;

public class UserDefinedBrickInputRVAdapter extends RVAdapter<UserDefinedBrickInput> {

	UserDefinedBrickInputRVAdapter(List<UserDefinedBrickInput> items) {
		super(items);
	}

	@Override
	public CheckableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
		return new VariableViewHolder(view);
	}

	@Override
	public void onBindViewHolder(CheckableViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);
		if (position == 0) {
			((TextView) holder.itemView.findViewById(R.id.headline)).setText(holder.itemView.getResources().getQuantityText(R.plurals.user_defined_brick_input_headline, getItemCount()));
		}

		UserDefinedBrickInput item = items.get(position);
		VariableViewHolder variableViewHolder = (VariableViewHolder) holder;
		variableViewHolder.title.setText(item.getName());

		AndroidStringProvider stringProvider = new AndroidStringProvider(
				CatroidApplication.getAppContext()
		);
		String result = item.getValue().getUserFriendlyString(stringProvider, null);
		result = ShowTextUtils.convertStringToMetricRepresentation(result);
		variableViewHolder.value.setText(result);
	}
}
