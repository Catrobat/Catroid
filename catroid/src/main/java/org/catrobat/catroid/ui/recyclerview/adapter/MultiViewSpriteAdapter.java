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

package org.catrobat.catroid.ui.recyclerview.adapter;

import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class MultiViewSpriteAdapter extends SpriteAdapter {

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({BACKGROUND, OTHER})
	@interface ViewType {}
	private static final int BACKGROUND = 0;
	private static final int OTHER = 1;

	public MultiViewSpriteAdapter(List<Sprite> items) {
		super(items);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, @ViewType int viewType) {
		switch (viewType) {
			case BACKGROUND:
				View view = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.list_item_background_sprite, parent, false);
				return new ViewHolder(view);
			case OTHER:
			default:
				return super.onCreateViewHolder(parent, viewType);
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		if (holder.getAdapterPosition() == 0) {
			holder.background.setOnLongClickListener(null);
			holder.checkBox.setVisibility(View.GONE);
		}
	}

	@Override
	public @ViewType int getItemViewType(int position) {
		if (position == 0) {
			return BACKGROUND;
		}
		return OTHER;
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		return fromPosition == 0 || toPosition == 0 || super.onItemMove(fromPosition, toPosition);
	}
}
