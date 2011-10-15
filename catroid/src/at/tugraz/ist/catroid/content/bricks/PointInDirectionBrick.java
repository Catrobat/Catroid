/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class PointInDirectionBrick implements Brick, OnItemSelectedListener {

	public static final int DIRECTION_RIGHT = 0;
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_UP = 2;
	public static final int DIRECTION_DOWN = 3;

	private static final long serialVersionUID = 1L;
	private static final int[] directions = { 90, -90, 0, 180 };
	private Sprite sprite;
	private int selectedIndex;

	@XStreamOmitField
	private transient View view;

	public PointInDirectionBrick(Sprite sprite, int selectedIndex) {
		this.sprite = sprite;
		this.selectedIndex = selectedIndex;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
		sprite.costume.rotation = -directions[selectedIndex] + 90f;
	}

	public Sprite getSprite() {
		return this.sprite;
	}

	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.toolbox_brick_point_in_direction, null);
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,
				R.array.point_in_direction_strings, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) view.findViewById(R.id.point_in_direction_spinner);
		spinner.setAdapter(arrayAdapter);

		spinner.setClickable(true);
		spinner.setFocusable(true);

		spinner.setOnItemSelectedListener(this);

		spinner.setSelection(selectedIndex);

		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_point_in_direction, null);
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), selectedIndex);
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		selectedIndex = position;
	}

	public void onNothingSelected(AdapterView<?> arg0) {

	}

}
