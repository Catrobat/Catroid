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
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.controller.BackPackListManager;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

import static org.catrobat.catroid.R.drawable.ic_program_menu_scripts;

public class BackPackScriptListAdapter extends CheckBoxListAdapter<String> {

	public static final String TAG = BackPackScriptListAdapter.class.getSimpleName();

	public BackPackScriptListAdapter(Context context, int resource, List<String> listItems) {
		super(context, resource, listItems);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = super.getView(position, convertView, parent);

		ListItemViewHolder listItemViewHolder = (ListItemViewHolder) listItemView.getTag();
		String scriptName = getItem(position);

		listItemViewHolder.name.setText(scriptName);
		listItemViewHolder.image.setImageResource(ic_program_menu_scripts);

		if (showDetails) {
			listItemViewHolder.details.setVisibility(View.VISIBLE);
			int brickCount = BackPackListManager.getInstance().getBackPackedScripts().get(scriptName).size();
			listItemViewHolder.leftTopDetails.setText(getContext().getResources().getString(R.string.number_of_bricks)
					.concat(" ").concat(Integer.toString(brickCount)));
			listItemViewHolder.leftBottomDetails.setVisibility(View.GONE);
			listItemViewHolder.rightBottomDetails.setVisibility(View.GONE);
		}

		TextSizeUtil.enlargeViewGroup(listItemViewHolder.background);

		return listItemView;
	}
}
