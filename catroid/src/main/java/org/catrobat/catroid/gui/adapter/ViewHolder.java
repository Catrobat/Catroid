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

package org.catrobat.catroid.gui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import org.catrobat.catroid.R;

public class ViewHolder extends RecyclerView.ViewHolder {

	private static final int SELECTED_BG_COLOR = 0xFFDDDDDD;
	private static final int UNSELECTED_BG_COLOR = 0xFFFFFFFF;

	public View itemView;
	public TextView nameView;
	public ImageView reorderIcon;
	public ImageSwitcher imageSwitcher;

	public ViewHolder(final View itemView) {
		super(itemView);

		this.itemView = itemView;
		nameView = (TextView) itemView.findViewById(R.id.name_view);
		reorderIcon = (ImageView) itemView.findViewById(R.id.reorder_icon);
		imageSwitcher = (ImageSwitcher) itemView.findViewById(R.id.imageswitcher);

		imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
			@Override
			public View makeView() {
				return new ImageView(itemView.getContext());
			}
		});
	}

	void updateBackground(boolean isSelected) {
		itemView.setBackgroundColor(isSelected ? SELECTED_BG_COLOR : UNSELECTED_BG_COLOR);
	}
}
