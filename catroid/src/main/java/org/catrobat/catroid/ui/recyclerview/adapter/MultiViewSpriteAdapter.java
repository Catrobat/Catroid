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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.ViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

public class MultiViewSpriteAdapter extends SpriteAdapter {

	public static final String TAG = MultiViewSpriteAdapter.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({BACKGROUND, SPRITE_SINGLE, SPRITE_GROUP, SPRITE_GROUP_ITEM})
	@interface ViewType {}
	private static final int BACKGROUND = 0;
	private static final int SPRITE_SINGLE = 1;
	private static final int SPRITE_GROUP = 2;
	private static final int SPRITE_GROUP_ITEM = 3;

	public MultiViewSpriteAdapter(List<Sprite> items) {
		super(items);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, @ViewType int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case BACKGROUND:
				View view = inflater.inflate(R.layout.vh_background_sprite, parent, false);
				return new ExtendedVH(view);
			case SPRITE_SINGLE:
				view = inflater.inflate(R.layout.vh_with_checkbox, parent, false);
				return new ExtendedVH(view);
			case SPRITE_GROUP:
				view = inflater.inflate(R.layout.vh_sprite_group, parent, false);
				return new ExtendedVH(view);
			case SPRITE_GROUP_ITEM:
				view = inflater.inflate(R.layout.vh_sprite_group_item, parent, false);
				return new ExtendedVH(view);
			default:
				throw new IllegalArgumentException(TAG + ": viewType was not defined correctly.");
		}
	}

	@Override
	public void onBindViewHolder(ExtendedVH holder, int position) {
		Context context = holder.itemView.getContext();

		Sprite item = items.get(position);
		holder.title.setText(item.getName());

		if (holder.getItemViewType() == SPRITE_GROUP) {
			Drawable drawable = ((GroupSprite) item).collapsed
					? context.getResources().getDrawable(R.drawable.ic_play)
					: context.getResources().getDrawable(R.drawable.ic_play_down);
			holder.image.setImageDrawable(drawable);
			holder.checkBox.setVisibility(View.GONE);
			return;
		}

		if (holder.getItemViewType() == BACKGROUND) {
			holder.itemView.setOnLongClickListener(null);
			holder.checkBox.setVisibility(View.GONE);
		}

		if (holder.getItemViewType() == SPRITE_GROUP_ITEM) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
					.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			holder.itemView.setLayoutParams(params);
			if (((GroupItemSprite) item).collapsed) {
				params.height = 0;
				holder.itemView.setLayoutParams(params);
			}
		}

		Bitmap lookData = null;
		if (!item.getLookList().isEmpty()) {
			lookData = item.getLookList().get(0).getThumbnailBitmap();
		}
		holder.image.setImageBitmap(lookData);

		if (showDetails) {
			holder.details.setText(String.format(Locale.getDefault(),
					context.getString(R.string.sprite_details),
					item.getNumberOfScripts() + item.getNumberOfBricks(),
					item.getLookList().size(),
					item.getSoundList().size()));
			holder.details.setVisibility(View.VISIBLE);
		} else {
			holder.details.setVisibility(View.GONE);
		}
	}

	@Override
	public @ViewType int getItemViewType(int position) {
		if (position == 0) {
			return BACKGROUND;
		}
		if (items.get(position) instanceof GroupSprite) {
			return SPRITE_GROUP;
		}
		if (items.get(position) instanceof GroupItemSprite) {
			return SPRITE_GROUP_ITEM;
		}
		return SPRITE_SINGLE;
	}

	@Override
	public boolean onItemMove(int fromPosition, int toPosition) {
		if (fromPosition == 0 || toPosition == 0) {
			return true;
		}

		Sprite fromItem = items.get(fromPosition);
		Sprite toItem = items.get(toPosition);

		if (fromItem instanceof GroupSprite) {
			return true;
		}

		if (toItem instanceof GroupSprite) {
			if (fromPosition > toPosition) {
				fromItem.setConvertToSingleSprite(true);
			} else {
				fromItem.setConvertToGroupItemSprite(true);
			}
			return super.onItemMove(fromPosition, toPosition);
		}

		if (!(fromItem instanceof GroupItemSprite) && toItem instanceof GroupItemSprite) {
			fromItem.setConvertToGroupItemSprite(true);
			return super.onItemMove(fromPosition, toPosition);
		}

		if (fromItem instanceof GroupItemSprite && !(toItem instanceof GroupItemSprite)) {
			fromItem.setConvertToSingleSprite(true);
			return super.onItemMove(fromPosition, toPosition);
		}

		fromItem.setConvertToGroupItemSprite(false);
		fromItem.setConvertToSingleSprite(false);
		return super.onItemMove(fromPosition, toPosition);
	}
}
