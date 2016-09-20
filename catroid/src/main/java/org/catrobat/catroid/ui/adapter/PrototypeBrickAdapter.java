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

import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.ui.fragment.AddBrickFragment;
import org.catrobat.catroid.ui.fragment.ScriptFragment;
import org.catrobat.catroid.utils.IconsUtil;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class PrototypeBrickAdapter extends BrickBaseAdapter {

	public PrototypeBrickAdapter(Context context, ScriptFragment scriptFragment, AddBrickFragment addBrickFragment, List<Brick> brickList, String selectedCategory) {
		this.context = context;
		this.scriptFragment = scriptFragment;
		this.addBrickFragment = addBrickFragment;
		this.brickList = brickList;
		this.selectedCategory = selectedCategory;
	}

	@Override
	public int getCount() {
		return brickList.size();
	}

	@Override
	public Brick getItem(int position) {
		return brickList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Brick brick = brickList.get(position);
		View view = brick.getPrototypeView(context);
		BrickViewProvider.setSpinnerClickability(view, false);

		IconsUtil.addIcons((ViewGroup) view, selectedCategory);
		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

		return view;
	}
}
