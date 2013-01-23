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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

public class PointInDirectionBrick implements Brick, OnItemSelectedListener {

	private static final long serialVersionUID = 1L;

	public PointInDirectionBrick() {

	}

	public static enum Direction {
		DIRECTION_RIGHT(90), DIRECTION_LEFT(-90), DIRECTION_UP(0), DIRECTION_DOWN(180);

		private double directionDegrees;

		private Direction(double degrees) {
			directionDegrees = degrees;
		}

		public double getDegrees() {
			return directionDegrees;
		}
	}

	private Sprite sprite;
	private double degrees;

	private transient Direction direction;

	protected Object readResolve() {
		for (Direction direction : Direction.values()) {
			if (Math.abs(direction.getDegrees() - degrees) < 0.1) {
				this.direction = direction;
				break;
			}
		}
		return this;
	}

	public PointInDirectionBrick(Sprite sprite, Direction direction) {
		this.sprite = sprite;
		this.direction = direction;
		this.degrees = direction.getDegrees();
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		double degreeOffset = 90f;
		sprite.costume.rotation = (float) (-degrees + degreeOffset);
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		View view = View.inflate(context, R.layout.brick_point_in_direction, null);
		ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(context,
				R.array.point_in_direction_strings, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = (Spinner) view.findViewById(R.id.point_in_direction_spinner);
		spinner.setAdapter(arrayAdapter);

		spinner.setClickable(true);
		spinner.setFocusable(true);

		spinner.setOnItemSelectedListener(this);

		spinner.setSelection(direction.ordinal());

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_point_in_direction, null);
	}

	@Override
	public Brick clone() {
		return new PointInDirectionBrick(getSprite(), direction);
	}

	@Override
	public void setDefaultValues(Context context) {
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		direction = Direction.values()[position];
		degrees = direction.getDegrees();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}
}
