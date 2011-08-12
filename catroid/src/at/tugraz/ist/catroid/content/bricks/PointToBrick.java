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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;

public class PointToBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Sprite pointedSprite;
	private double degrees = 0.0;
	private int pos = 0;
	private boolean start = true;

	public PointToBrick(Sprite sprite, Sprite pointedSprite) {
		this.sprite = sprite;
		this.pointedSprite = pointedSprite;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void execute() {

		if (pointedSprite == null) {
			pointedSprite = this.sprite;
			final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
					.getSpriteList();
			if (spriteList.size() > 2) {
				int count = 0;
				for (Sprite sprite : spriteList) {
					String spriteName = sprite.getName();
					String temp = this.sprite.getName();
					if (!spriteName.equals(temp) && !spriteName.equals("Background") && count == 0) {
						pointedSprite = sprite;
						System.out.println(pointedSprite);
						count = 1;
					}
				}
			}
		}

		int x1 = 0, x2 = 0;
		int y1 = 0, y2 = 0;
		double base = 0.0, height = 0.0, value = 0.0;

		x1 = sprite.getXPosition();
		y1 = sprite.getYPosition();
		x2 = pointedSprite.getXPosition();
		y2 = pointedSprite.getYPosition();

		if (x1 == x2 && y1 == y2) {
			degrees = 90;
		}

		else if (x1 == x2 || y1 == y2) {
			if (x1 == x2) {
				if (y1 > y2) {
					degrees = 180;
				} else {
					degrees = 0;
				}
			} else {
				if (x1 > x2) {
					degrees = 270;
				} else {
					degrees = 90;
				}
			}

		} else {
			base = Math.abs(y1 - y2);
			height = Math.abs(x1 - x2);
			value = Math.toDegrees(Math.atan(base / height));

			if (x1 < x2) {
				if (y1 > y2) {
					degrees = 90 + value;
				} else {
					degrees = 90 - value;
				}
			} else {
				if (y1 > y2) {
					degrees = 270 - value;
				} else {
					degrees = 270 + value;
				}
			}
		}
		sprite.setDirection(degrees);
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_point_to, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);

		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = this.sprite.getName();
			if (!spriteName.equals(temp) && !spriteName.equals("Background")) {
				spinnerAdapter.add(sprite.getName());
			}
		}

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (start) {
					start = false;
				} else {
					String temp = parent.getSelectedItem().toString();
					final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
							.getCurrentProject().getSpriteList();
					for (Sprite sprite : spriteList) {
						if (sprite.getName().equals(temp)) {
							pointedSprite = sprite;
						}
					}

					System.out.println(pointedSprite);
					pos = position;
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		spinner.setSelection(pos, true);
		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_point_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite, pointedSprite);
	}
}
