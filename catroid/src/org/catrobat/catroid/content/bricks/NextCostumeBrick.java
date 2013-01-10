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

import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.catrobat.catroid.R;

public class NextCostumeBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient View view;

	public NextCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public NextCostumeBrick() {

	}

	@Override
	public void execute() {

		final ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();
		int costumeDataListSize = costumeDataList.size();

		if (costumeDataListSize > 0 && sprite.costume.getCostumeData() != null) {
			CostumeData currentCostumeData = sprite.costume.getCostumeData();
			CostumeData finalCostumeData = costumeDataList.get(costumeDataListSize - 1);
			boolean executeOnce = true;

			for (CostumeData costumeData : costumeDataList) {
				int currentIndex = costumeDataList.indexOf(costumeData);
				int newIndex = currentIndex + 1;

				if (currentCostumeData.equals(finalCostumeData) && executeOnce) {
					executeOnce = false;
					currentCostumeData = costumeDataList.get(0);
				}

				else if (currentCostumeData.equals(costumeData) && executeOnce) {
					executeOnce = false;
					currentCostumeData = costumeDataList.get(newIndex);
				}

				sprite.costume.setCostumeData(currentCostumeData);
			}
		} else {
			// If there are no costumes do nothing
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_next_costume, null);
		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.textview_next_costume);
			textView.setText(R.string.brick_next_background);
		}
		return view;
	}

	@Override
	public Brick clone() {
		return new NextCostumeBrick(sprite);
	}

	@Override
	public int getRequiredResources() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_next_costume, null);
		}

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.textview_next_costume);
			textView.setText(R.string.brick_next_background);
		}

		return view;
	}
}
