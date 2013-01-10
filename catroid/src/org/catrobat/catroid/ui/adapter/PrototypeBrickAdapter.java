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
package org.catrobat.catroid.ui.adapter;

/**
 * @author DENISE, DANIEL
 *
 */

import java.util.List;

import org.catrobat.catroid.content.bricks.Brick;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PrototypeBrickAdapter extends BaseAdapter {

	private Context context;
	private List<Brick> brickList;

	public PrototypeBrickAdapter(Context context, List<Brick> brickList) {
		this.context = context;
		this.brickList = brickList;
	}

	public int getCount() {
		return brickList.size();
	}

	public Brick getItem(int position) {
		return brickList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return brickList.size();
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			Brick brick = brickList.get(position);
			return brick.getPrototypeView(context);
		}

		return convertView;
	}
}
