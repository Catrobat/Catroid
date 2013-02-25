/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import java.util.ArrayList;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class NextLookBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient View view;
	private transient CheckBox checkbox;
	private transient boolean checked;

	public NextLookBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NextLookBrick() {

	}

	@Override
	public void execute() {

		final ArrayList<LookData> lookDataList = sprite.getLookDataList();
		int lookDataListSize = lookDataList.size();

		if (lookDataListSize > 0 && sprite.look.getLookData() != null) {
			LookData currentLookData = sprite.look.getLookData();
			LookData finalLookData = lookDataList.get(lookDataListSize - 1);
			boolean executeOnce = true;

			for (LookData lookData : lookDataList) {
				int currentIndex = lookDataList.indexOf(lookData);
				int newIndex = currentIndex + 1;

				if (currentLookData.equals(finalLookData) && executeOnce) {
					executeOnce = false;
					currentLookData = lookDataList.get(0);
				}

				else if (currentLookData.equals(lookData) && executeOnce) {
					executeOnce = false;
					currentLookData = lookDataList.get(newIndex);
				}

				sprite.look.setLookData(currentLookData);
			}
		} else {
			// If there are no looks do nothing
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_next_look, null);

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_next_look_text_view);
			textView.setText(R.string.brick_next_background);
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new NextLookBrick(sprite);
	}

	@Override
	public int getRequiredResources() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_next_look, null);

			checkbox = (CheckBox) view.findViewById(R.id.brick_next_look_checkbox);
			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});
		}

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_next_look_text_view);
			textView.setText(R.string.brick_next_background);
		}

		return view;
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}
}
