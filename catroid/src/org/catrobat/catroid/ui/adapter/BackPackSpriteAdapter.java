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
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.fragment.BackPackSpriteFragment;

import java.util.ArrayList;
import java.util.List;

public class BackPackSpriteAdapter extends SpriteBaseAdapter implements ActionModeActivityAdapterInterface {

	private final BackPackSpriteFragment backpackSpriteFragment;
	private boolean disableBackgroundSprites;

	public BackPackSpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects,
			BackPackSpriteFragment backpackSpriteFragment) {
		super(context, resource, textViewResourceId, objects);
		this.backpackSpriteFragment = backpackSpriteFragment;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View spriteView = convertView;
		final ViewHolder holder;
		if (convertView == null) {
			spriteView = inflater.inflate(R.layout.activity_project_spritelist_item, parent, false);
			holder = new ViewHolder();
			holder.background = (RelativeLayout) spriteView.findViewById(R.id.spritelist_item_background);
			holder.checkbox = (CheckBox) spriteView.findViewById(R.id.sprite_checkbox);
			holder.text = (TextView) spriteView.findViewById(R.id.project_activity_sprite_title);
			holder.backgroundHeadline = (LinearLayout) spriteView.findViewById(R.id.spritelist_background_headline);
			holder.objectsHeadline = (LinearLayout) spriteView.findViewById(R.id.spritelist_objects_headline);
			holder.image = (ImageView) spriteView.findViewById(R.id.sprite_img);
			holder.scripts = (TextView) spriteView.findViewById(R.id.textView_number_of_scripts);
			holder.bricks = (TextView) spriteView.findViewById(R.id.textView_number_of_bricks);
			holder.looks = (TextView) spriteView.findViewById(R.id.textView_number_of_looks);
			holder.sounds = (TextView) spriteView.findViewById(R.id.textView_number_of_sounds);
			holder.details = spriteView.findViewById(R.id.project_activity_sprite_details);
			spriteView.setTag(holder);
		} else {
			holder = (ViewHolder) spriteView.getTag();
		}

		holder.background.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP && backpackSpriteFragment != null) {
					backpackSpriteFragment.setSelectedSpritePosition(position);
					backpackSpriteFragment.getListView().showContextMenuForChild(v);
				}
				return false;
			}
		});

		handleHolderViews(position, holder);

		if (selectMode != ListView.CHOICE_MODE_NONE) {
			if (disableBackgroundSprites && getItem(position).isBackgroundSprite) {
				holder.checkbox.setVisibility(View.INVISIBLE);
				enableHolderViews(holder, false);
				spriteView.setAlpha((float) 0.25);
			} else {
				holder.checkbox.setVisibility(View.VISIBLE);
			}
			holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
		} else {
			holder.background.setBackgroundResource(R.drawable.button_background_selector);
			holder.checkbox.setVisibility(View.GONE);
			holder.checkbox.setChecked(false);
			enableHolderViews(holder, true);
			clearCheckedItems();
			spriteView.setAlpha(1);
		}
		holder.backgroundHeadline.setVisibility(View.GONE);
		holder.objectsHeadline.setVisibility(View.GONE);

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (selectMode == ListView.CHOICE_MODE_NONE) {
					if (onSpriteEditListener != null) {
						onSpriteEditListener.onSpriteEdit(position);
					}
				} else {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		});

		return spriteView;
	}

	private void enableHolderViews(ViewHolder holder, boolean enabled) {
		holder.checkbox.setEnabled(enabled);
		holder.background.setEnabled(enabled);
		holder.text.setEnabled(enabled);
		holder.backgroundHeadline.setEnabled(enabled);
		holder.objectsHeadline.setEnabled(enabled);
		holder.image.setEnabled(enabled);
		holder.scripts.setEnabled(enabled);
		holder.bricks.setEnabled(enabled);
		holder.looks.setEnabled(enabled);
		holder.sounds.setEnabled(enabled);
		holder.details.setEnabled(enabled);
	}

	public void onDestroyActionModeUnpacking(boolean delete) {
		List<Sprite> spritesToUnpack = new ArrayList<>();
		for (Integer checkedPosition : checkedSprites) {
			spritesToUnpack.add(getItem(checkedPosition));
		}

		for (Sprite sprite : spritesToUnpack) {
			BackPackSpriteController.getInstance().unpack(sprite, delete, false, false);
		}

		boolean returnToProjectActivity = !checkedSprites.isEmpty();
		clearCheckedItems();
		this.disableBackgroundSprites = false;
		if (returnToProjectActivity) {
			returnToProjectActivity();
		}
	}

	public void returnToProjectActivity() {
		Intent intent = new Intent(getContext(), ProjectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getContext().startActivity(intent);
	}

	public void disableBackgroundSprites() {
		this.disableBackgroundSprites = true;
	}

	public int getCountWithBackgroundSprites() {
		int numberOfBackgroundSprites = 0;
		for (int position = 0; position < getCount(); position++) {
			if (getItem(position).isBackgroundSprite) {
				numberOfBackgroundSprites++;
			}
		}
		return getCount() - numberOfBackgroundSprites;
	}
}
