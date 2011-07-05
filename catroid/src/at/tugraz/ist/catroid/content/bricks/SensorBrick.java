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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

/**
 * @author manuelzoderer
 * 
 */
public class SensorBrick implements Brick {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.content.brick.Brick#execute()
	 */
	public void execute() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getSprite()
	 */
	public Sprite getSprite() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getView(android.content.Context, int,
	 * android.widget.BaseExpandableListAdapter)
	 */
	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getPrototypeView(android.content.Context)
	 */
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_sensor, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new SensorBrick();
	}

}
