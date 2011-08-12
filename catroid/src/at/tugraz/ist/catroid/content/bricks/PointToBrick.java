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
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;

public class PointToBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Sprite pointedSprite;
	private double rotationDegrees = 0.0;
	private int spinnerPosition = 0;

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
		}

		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		if (!spriteList.contains(pointedSprite)) {
			return;
		}

		int spriteXPosition = 0, spriteYPosition = 0;
		int pointedSpriteXPosition = 0, pointedSpriteYPosition = 0;
		double base = 0.0, height = 0.0, value = 0.0;

		spriteXPosition = sprite.getXPosition();
		spriteYPosition = sprite.getYPosition();
		pointedSpriteXPosition = pointedSprite.getXPosition();
		pointedSpriteYPosition = pointedSprite.getYPosition();

		if (spriteXPosition == pointedSpriteXPosition && spriteYPosition == pointedSpriteYPosition) {
			rotationDegrees = 90;
		}

		else if (spriteXPosition == pointedSpriteXPosition || spriteYPosition == pointedSpriteYPosition) {
			if (spriteXPosition == pointedSpriteXPosition) {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 180;
				} else {
					rotationDegrees = 0;
				}
			} else {
				if (spriteXPosition > pointedSpriteXPosition) {
					rotationDegrees = 270;
				} else {
					rotationDegrees = 90;
				}
			}

		} else {
			base = Math.abs(spriteYPosition - pointedSpriteYPosition);
			height = Math.abs(spriteXPosition - pointedSpriteXPosition);
			value = Math.toDegrees(Math.atan(base / height));

			if (spriteXPosition < pointedSpriteXPosition) {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 90 + value;
				} else {
					rotationDegrees = 90 - value;
				}
			} else {
				if (spriteYPosition > pointedSpriteYPosition) {
					rotationDegrees = 270 - value;
				} else {
					rotationDegrees = 270 + value;
				}
			}
		}
		sprite.setDirection(rotationDegrees);
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_point_to, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		CostumeData dummyCostumeData = new CostumeData();
		dummyCostumeData.setCostumeName(context.getString(R.string.broadcast_nothing_selected));
		spinnerAdapter.add(dummyCostumeData.toString());

		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		for (Sprite sprite : spriteList) {
			String spriteName = sprite.getName();
			String temp = this.sprite.getName();
			if (!spriteName.equals(temp) && !spriteName.equals("Background")) {
				spinnerAdapter.add(sprite.getName());
			}
		}

		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String temp1 = parent.getSelectedItem().toString();
				String temp2 = context.getString(R.string.broadcast_nothing_selected);
				final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
						.getCurrentProject().getSpriteList();
				if (temp1.equals(temp2)) {
					pointedSprite = null;
				}
				for (Sprite sprite : spriteList) {
					if (sprite.getName().equals(temp1)) {
						pointedSprite = sprite;
					}
				}
				spinnerPosition = position;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		spinner.setSelection(spinnerPosition, true);
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
