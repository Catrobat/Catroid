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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.ui.controller.BackPackSpriteController;
import org.catrobat.catroid.ui.fragment.BackPackSpriteFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BackPackSpriteAdapter extends ArrayAdapter<Sprite> implements ActionModeActivityAdapterInterface {

	private static LayoutInflater inflater = null;
	private Context context;
	private int selectMode;
	private boolean showDetails;
	private Set<Integer> checkedSprites = new TreeSet<>();
	private OnSpriteEditListener onSpriteEditListener;
	private final BackPackSpriteFragment backpackSpriteFragment;

	public BackPackSpriteAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects,
			BackPackSpriteFragment backpackSpriteFragment) {
		super(context, resource, textViewResourceId, objects);
		this.backpackSpriteFragment = backpackSpriteFragment;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		selectMode = ListView.CHOICE_MODE_NONE;
		showDetails = false;
	}

	public void setOnSpriteEditListener(OnSpriteEditListener listener) {
		onSpriteEditListener = listener;
	}

	public void addCheckedSprite(int position) {
		checkedSprites.add(position);
	}

	public int getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(int selectMode) {
		this.selectMode = selectMode;
	}

	public boolean getShowDetails() {
		return showDetails;
	}

	@Override
	public int getAmountOfCheckedItems() {
		return checkedSprites.size();
	}

	@Override
	public Set<Integer> getCheckedItems() {
		return checkedSprites;
	}

	public ArrayList<Sprite> getCheckedSprites() {
		ArrayList<Sprite> result = new ArrayList<>();
		for (Integer pos : checkedSprites) {
			result.add(ProjectManager.getInstance().getCurrentProject().getSpriteList().get(pos));
		}
		return result;
	}

	@Override
	public void clearCheckedItems() {
		checkedSprites.clear();
	}

	public void setShowDetails(boolean showDetails) {
		this.showDetails = showDetails;
	}

	public void handleHolderViews(final int position, ViewHolder holder) {

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

		Sprite sprite = getItem(position);
		holder.text.setText(sprite.getName());
		setImage(holder, sprite);

		holder.scripts.setText(context.getResources().getString(R.string.number_of_scripts).concat(" ").concat(Integer.toString(sprite.getNumberOfScripts())));

		holder.bricks.setText(context.getResources().getString(R.string.number_of_bricks).concat(" ").concat(Integer
				.toString(sprite.getNumberOfBricks())).concat(Integer.toString(sprite.getNumberOfScripts())));

		holder.looks.setText(context.getResources().getString(R.string.number_of_looks).concat(" ").concat(Integer.toString(sprite.getLookDataList().size())));

		holder.sounds.setText(context.getResources().getString(R.string.number_of_sounds).concat(" ").concat(Integer.toString(sprite.getSoundList().size())));

		if (!showDetails) {
			holder.details.setVisibility(View.GONE);
		} else {
			holder.details.setVisibility(View.VISIBLE);
		}
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
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN && backpackSpriteFragment != null) {
					backpackSpriteFragment.setSelectedSpritePosition(position);
					backpackSpriteFragment.getListView().showContextMenuForChild(view);
				}
				return false;
			}
		});

		handleHolderViews(position, holder);

		if (selectMode != ListView.CHOICE_MODE_NONE) {
			holder.checkbox.setVisibility(View.VISIBLE);
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

		holder.background.setOnClickListener(new View.OnClickListener() {

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
			BackPackSpriteController.getInstance().unpack(sprite, delete, false, false, false);
		}

		boolean returnToProjectActivity = !checkedSprites.isEmpty();
		clearCheckedItems();
		if (returnToProjectActivity) {
			returnToProjectActivity();
		}
	}

	public void returnToProjectActivity() {
		Intent intent = new Intent(context, ProjectActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	protected void setImage(ViewHolder holder, Sprite sprite) {
		LookData firstLookData = null;
		if (sprite.getLookDataList().size() > 0) {
			firstLookData = sprite.getLookDataList().get(0);
		}
		if (firstLookData == null) {
			holder.image.setImageBitmap(null);
		} else {
			holder.image.setImageBitmap(firstLookData.getThumbnailBitmap());
		}
	}

	public interface OnSpriteEditListener {
		void onSpriteChecked();

		void onSpriteEdit(int position);
	}

	protected static class ViewHolder {
		protected RelativeLayout background;
		protected CheckBox checkbox;
		protected TextView text;
		protected LinearLayout backgroundHeadline;
		protected LinearLayout objectsHeadline;
		protected ImageView image;
		protected TextView scripts;
		protected TextView bricks;
		protected TextView looks;
		protected TextView sounds;
		protected View details;
	}
}
