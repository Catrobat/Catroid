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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

public class PointToBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Sprite pointedSprite;
	private String pointTowards;

	private transient View prototype;

	public PointToBrick(Sprite sprite, Sprite pointedSprite, String pointTowards) {
		this.sprite = sprite;
		this.pointedSprite = pointedSprite;
		this.pointTowards = pointTowards;
	}

	public PointToBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void execute() {
		final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		if (!spriteList.contains(pointedSprite)) {
			pointedSprite = null;
		}

		if (pointedSprite == null) {
			pointedSprite = this.sprite;
		}

		int spriteXPosition = 0, spriteYPosition = 0;
		int pointedSpriteXPosition = 0, pointedSpriteYPosition = 0;
		double base = 0.0, height = 0.0, value = 0.0;

		sprite.costume.aquireXYWidthHeightLock();
		spriteXPosition = (int) sprite.costume.getXPosition();
		spriteYPosition = (int) sprite.costume.getYPosition();
		sprite.costume.releaseXYWidthHeightLock();
		pointedSprite.costume.aquireXYWidthHeightLock();
		pointedSpriteXPosition = (int) pointedSprite.costume.getXPosition();
		pointedSpriteYPosition = (int) pointedSprite.costume.getYPosition();
		pointedSprite.costume.releaseXYWidthHeightLock();

		double rotationDegrees;
		if (spriteXPosition == pointedSpriteXPosition && spriteYPosition == pointedSpriteYPosition) {
			rotationDegrees = 90;
		} else if (spriteXPosition == pointedSpriteXPosition || spriteYPosition == pointedSpriteYPosition) {
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
		sprite.costume.rotation = (-(float) rotationDegrees) + 90f;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.brick_point_to, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.brick_point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		spinner.setClickable(true);
		spinner.setEnabled(true);

		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerAdapter.add(context.getString(R.string.broadcast_nothing_selected));

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

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String itemSelected = parent.getSelectedItem().toString();
				String nothingSelected = context.getString(R.string.broadcast_nothing_selected);
				final ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance()
						.getCurrentProject().getSpriteList();

				if (itemSelected.equals(nothingSelected)) {
					pointedSprite = null;
				}
				for (Sprite sprite : spriteList) {
					String spriteName = sprite.getName();
					if (spriteName.equals(itemSelected)) {
						pointedSprite = sprite;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (spriteList.contains(pointedSprite)) {
			int pointedSpriteIndex = spinnerAdapter.getPosition(pointedSprite.getName());
			spinner.setSelection(pointedSpriteIndex);
		} else {
			spinner.setSelection(0);
		}

		return brickView;
	}

	@Override
	public View getPrototypeView(Context context) {
		return setDefaultValues(context);
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite, pointedSprite, pointTowards);
	}

	@Override
	public View setDefaultValues(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		prototype = inflater.inflate(R.layout.brick_point_to, null);
		return prototype;
		//TODO set the Value in the list 
	}
}
