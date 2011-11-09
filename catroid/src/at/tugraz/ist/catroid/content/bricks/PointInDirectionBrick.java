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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class PointInDirectionBrick implements Brick, OnItemSelectedListener {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private double direction;

	public PointInDirectionBrick(Sprite sprite, double direction) {
		this.sprite = sprite;
		this.direction = direction;
	}

	public void execute() {
		sprite.setDirection(direction);
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_point_in_direction, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.point_in_direction_spinner);

		spinner.setOnItemSelectedListener(this);

		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.toolbox_brick_point_in_direction, null);

		return brickView;
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), direction);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		String[] values = parent.getContext().getResources().getStringArray(R.array.point_in_direction_values);
		direction = Double.parseDouble(values[position]);
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
