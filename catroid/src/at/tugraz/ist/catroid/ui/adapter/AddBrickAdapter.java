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
package at.tugraz.ist.catroid.ui.adapter;

/**
 * @author DENISE, DANIEL
 *
 */

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.content.brick.gui.Brick;


public class AddBrickAdapter extends BaseAdapter {

	private Context context;
	private List<Brick> brickList;

	public AddBrickAdapter(Context context, List<Brick> brickList) {
		this.context = context;
		this.brickList = brickList;
		//mViewContainer = new HashMap<String, View>();
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

	public View getView(int position, View convertView, ViewGroup parent) {
		Brick brick = brickList.get(position);
		
		return brick.getPrototypeView(context);
	}
}