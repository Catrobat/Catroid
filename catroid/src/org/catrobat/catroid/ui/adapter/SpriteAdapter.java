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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.fragment.SpritesListFragment;

import java.util.HashMap;
import java.util.List;

public class SpriteAdapter extends SpriteBaseAdapter implements ActionModeActivityAdapterInterface {

	private static final int INVALID_ID = -1;

	SpritesListFragment spritesListFragment;

	private HashMap<Sprite, Integer> idMap = new HashMap<Sprite, Integer>();

	public SpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects) {
		super(context, resource, textViewResourceId, objects);
		for (int i = 0; i < objects.size(); ++i) {
			idMap.put(objects.get(i), i);
		}
	}

	@Override
	public long getItemId(int position) {
		if (position < 0 || position >= idMap.size()) {
			return INVALID_ID;
		}
		Sprite item = getItem(position);
		return idMap.get(item);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (getCount() != idMap.size()) {
			idMap.clear();
			for (int i = 0; i < getCount(); i++) {
				idMap.put(getItem(i), i);
			}
		}
	}

	@Override
	public boolean hasStableIds() {
		return true;
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

		handleHolderViews(position, holder);

		holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					if (selectMode == ListView.CHOICE_MODE_SINGLE) {
						clearCheckedItems();
					}
					checkedSprites.add(position);
				} else {
					checkedSprites.remove(position);
				}
				notifyDataSetChanged();

				if (onSpriteEditListener != null) {
					onSpriteEditListener.onSpriteChecked();
				}
			}
		});

		if (checkedSprites.contains(position)) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		//------------------------------------------------------------
		Sprite sprite = getItem(position);
		LookData firstLookData = null;
		if (sprite.getLookDataList().size() > 0) {
			firstLookData = sprite.getLookDataList().get(0);
		}
		//------------------------------------------------------------

		holder.text.setText(sprite.getName());
		if (firstLookData == null) {
			holder.image.setImageBitmap(null);
		} else {
			holder.image.setImageBitmap(firstLookData.getThumbnailBitmap());
		}

		holder.scripts.setText(context.getResources().getString(R.string.number_of_scripts).concat(" ").concat(Integer.toString(sprite.getNumberOfScripts())));

		holder.bricks.setText(context.getResources().getString(R.string.number_of_bricks).concat(" ").concat(Integer.toString(sprite.getNumberOfBricks())));

		holder.looks.setText(context.getResources().getString(R.string.number_of_looks).concat(" ").concat(Integer.toString(sprite.getLookDataList().size())));

		holder.sounds.setText(context.getResources().getString(R.string.number_of_sounds).concat(" ").concat(Integer.toString(sprite.getSoundList().size())));

		if (!showDetails) {
			holder.details.setVisibility(View.GONE);
		} else {
			holder.details.setVisibility(View.VISIBLE);
		}

		if (position == 0 && !spritesListFragment.isBackPackActionMode()) {
			holder.backgroundHeadline.setVisibility(View.VISIBLE);
			holder.objectsHeadline.setVisibility(View.VISIBLE);
			holder.checkbox.setVisibility(View.GONE);
			if (selectMode == ListView.CHOICE_MODE_NONE) {
				holder.background.setBackgroundResource(R.drawable.button_background_selector);
			} else {
				holder.background.setBackgroundResource(R.drawable.button_background);
			}

			holder.background.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					openBackPackMenuFromBackground();
					return selectMode != ListView.CHOICE_MODE_NONE;
				}
			});
		} else {
			if (selectMode != ListView.CHOICE_MODE_NONE) {
				holder.checkbox.setVisibility(View.VISIBLE);
				holder.background.setBackgroundResource(R.drawable.button_background_shadowed);
			} else {
				holder.background.setBackgroundResource(R.drawable.button_background_selector);
				holder.checkbox.setVisibility(View.GONE);
				holder.checkbox.setChecked(false);
				clearCheckedItems();
			}
			holder.backgroundHeadline.setVisibility(View.GONE);
			holder.objectsHeadline.setVisibility(View.GONE);

			holder.background.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View view) {
					return selectMode != ListView.CHOICE_MODE_NONE || position == 0;
				}
			});
		}

		holder.background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (selectMode == ListView.CHOICE_MODE_NONE) {
					if (onSpriteEditListener != null) {
						onSpriteEditListener.onSpriteEdit(position);
					}
				} else if (position != 0) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			}
		});

		holder.background.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Intent intent = new Intent(ScriptActivity.ACTION_SPRITE_TOUCH_ACTION_UP);
					getContext().sendBroadcast(intent);
				}
				return false;
			}
		});

		return spriteView;
	}

	private void openBackPackMenuFromBackground() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		CharSequence[] items = new CharSequence[] { getContext().getString(R.string.backpack_add) };
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					BackPackSpriteController.getInstance().backpackVisibleSprite(getItem(0));
					spritesListFragment.switchToBackPack();
				}
				dialog.dismiss();
			}
		});
		String title = getContext().getString(R.string.background);
		if (ProjectManager.getInstance().getCurrentSprite() != null) {
			title = ProjectManager.getInstance().getCurrentSprite().getName();
		}
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.show();
	}

	public void setSpritesListFragment(SpritesListFragment spritesListFragment) {
		this.spritesListFragment = spritesListFragment;
	}
}
