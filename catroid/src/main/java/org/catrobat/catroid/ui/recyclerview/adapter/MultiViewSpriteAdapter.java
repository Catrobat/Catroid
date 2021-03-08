/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.GroupItemSprite;
import org.catrobat.catroid.content.GroupSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH;
import org.catrobat.catroid.ui.recyclerview.viewholder.ExtendedVH;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Locale;

import androidx.annotation.IntDef;

import static android.view.View.GONE;

public class MultiViewSpriteAdapter extends SpriteAdapter {

	public static final String TAG = MultiViewSpriteAdapter.class.getSimpleName();

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({BACKGROUND, SPRITE_SINGLE, SPRITE_GROUP, SPRITE_GROUP_ITEM})
	@interface ViewType {}
	private static final int BACKGROUND = 0;
	private static final int SPRITE_SINGLE = 1;
	private static final int SPRITE_GROUP = 2;
	private static final int SPRITE_GROUP_ITEM = 3;
	private View backgroundView;

	public MultiViewSpriteAdapter(List<Sprite> items) {
		super(items);
	}

	@Override
	public CheckableVH onCreateViewHolder(ViewGroup parent, @ViewType int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case BACKGROUND:
				backgroundView = inflater.inflate(R.layout.vh_background_sprite, parent, false);
				return new ExtendedVH(backgroundView);
			case SPRITE_SINGLE:
				View view = inflater.inflate(R.layout.vh_with_checkbox, parent, false);
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
			Drawable drawable = ((GroupSprite) item).isCollapsed()
					? context.getResources().getDrawable(R.drawable.ic_play)
					: context.getResources().getDrawable(R.drawable.ic_play_down);
			holder.image.setImageDrawable(drawable);
			holder.checkBox.setVisibility(GONE);
			return;
		}

		if (holder.getItemViewType() == BACKGROUND) {
			holder.itemView.setOnLongClickListener(null);
			holder.checkBox.setVisibility(GONE);
		}

		if (holder.getItemViewType() == SPRITE_GROUP_ITEM) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
					.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			holder.itemView.setLayoutParams(params);
			if (((GroupItemSprite) item).isCollapsed()) {
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
			holder.details.setVisibility(GONE);
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
	public boolean onItemMove(int srcPosition, int targetPosition) {
		if (srcPosition == 0 || targetPosition == 0) {
			return true;
		}

		Sprite fromItem = items.get(srcPosition);
		Sprite toItem = items.get(targetPosition);

		if (fromItem instanceof GroupSprite) {
			return true;
		}

		if (toItem instanceof GroupSprite) {
			GroupSprite groupItem = (GroupSprite) toItem;
			if (targetPosition > srcPosition) {
				targetPosition += groupItem.getNumberOfItems();
				fromItem.setConvertToGroupItemSprite(true);
			} else {
				fromItem.setConvertToSprite(true);
			}
			return super.onItemMove(srcPosition, targetPosition);
		}

		if (!(fromItem instanceof GroupItemSprite) && toItem instanceof GroupItemSprite) {
			fromItem.setConvertToGroupItemSprite(true);
			return super.onItemMove(srcPosition, targetPosition);
		}

		if (fromItem instanceof GroupItemSprite && !(toItem instanceof GroupItemSprite)) {
			fromItem.setConvertToSprite(true);
			return super.onItemMove(srcPosition, targetPosition);
		}

		fromItem.setConvertToGroupItemSprite(false);
		fromItem.setConvertToSprite(false);
		return super.onItemMove(srcPosition, targetPosition);
	}

	@Override
	public boolean setSelection(Sprite item, boolean selection) {
		if (items.indexOf(item) == 0) {
			throw new IllegalArgumentException("You should never select the Background Sprite for any ActionMode "
					+ "operation. Modifying it via ActionMode is not supported.");
		}

		return super.setSelection(item, selection);
	}

	@Override
	public void selectAll() {
		int backgroundIndex = 0;

		for (Sprite item : items) {
			if (items.indexOf(item) == backgroundIndex || item instanceof GroupSprite) {
				continue;
			}
			selectionManager.setSelectionTo(true, items.indexOf(item));
		}
		notifyDataSetChanged();
	}

	@Override
	public int getSelectableItemCount() {
		int backgroundCount = 1;
		int groupSpriteCount = 0;

		for (Sprite item : items) {
			if (item instanceof GroupSprite) {
				groupSpriteCount++;
			}
		}

		return items.size() - backgroundCount - groupSpriteCount;
	}

	public void setBackgroundVisible(int visible) {
		backgroundView.setVisibility(visible);
		if (visible == GONE) {
			backgroundView.getLayoutParams().height = 0;
		} else {
			backgroundView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
		}
	}
}
