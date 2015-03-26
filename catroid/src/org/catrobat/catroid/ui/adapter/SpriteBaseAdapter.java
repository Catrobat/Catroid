/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SpriteBaseAdapter extends ArrayAdapter<Sprite> implements ActionModeActivityAdapterInterface {

	protected static LayoutInflater inflater = null;
	protected Context context;
	protected int selectMode;
	protected boolean showDetails;
	protected Set<Integer> checkedSprites = new TreeSet<>();
	protected OnSpriteEditListener onSpriteEditListener;

	public SpriteBaseAdapter(Context context, int resource, int textViewResourceId, List<Sprite> objects) {
		super(context, resource, textViewResourceId, objects);
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

		holder.scripts.setText(context.getResources().getString(R.string.number_of_scripts) + " "
				+ sprite.getNumberOfScripts());

		holder.bricks.setText(context.getResources().getString(R.string.number_of_bricks) + " "
				+ (sprite.getNumberOfBricks() + sprite.getNumberOfScripts()));

		holder.looks.setText(context.getResources().getString(R.string.number_of_looks) + " "
				+ sprite.getLookDataList().size());

		holder.sounds.setText(context.getResources().getString(R.string.number_of_sounds) + " "
				+ sprite.getSoundList().size());

		if (!showDetails) {
			holder.details.setVisibility(View.GONE);
		} else {
			holder.details.setVisibility(View.VISIBLE);
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
		protected ImageView arrow;
	}
}
