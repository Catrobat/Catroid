/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;

public class NextCostumeBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private transient View view;
	private int index;

	//	private SetCostumeBrick setCostumeBrick;
	//	private boolean executeOnce = true;

	public NextCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void execute() {
		final ArrayList<CostumeData> costumeDataList = sprite.getCostumeDataList();
		int costumeDataListSize = costumeDataList.size();

		//		if (executeOnce) {
		//			setCostumeBrick = new SetCostumeBrick(sprite);
		//			index = setCostumeBrick.getPositionOfSelectedCostume();
		//			executeOnce = false;
		//			System.out.println("SetCostumeBrick: " + setCostumeBrick);
		//			System.out.println("Index: " + index);
		//		}

		if (index >= (costumeDataListSize - 1)) {
			index = 0;
		} else if (index < (costumeDataListSize - 1)) {
			index++;
		}

		sprite.getCostume().changeImagePath(costumeDataList.get(index).getAbsolutePath());
		//		System.out.println("Index: " + index);
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_next_costume, null);
		}
		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_next_costume, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new NextCostumeBrick(sprite);
	}

}
