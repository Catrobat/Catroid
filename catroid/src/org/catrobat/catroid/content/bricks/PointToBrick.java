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
import java.util.List;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class PointToBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private Sprite pointedObject;

	public PointToBrick(Sprite sprite, Sprite pointedSprite) {
		this.sprite = sprite;
		this.pointedObject = pointedSprite;
	}

	public PointToBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.brick_point_to, null);

		setCheckboxView(R.id.brick_point_to_checkbox);

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			spinner.setClickable(true);
			spinner.setEnabled(true);
		} else {
			spinner.setClickable(false);
			spinner.setEnabled(false);
		}

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
					pointedObject = null;
				}
				for (Sprite sprite : spriteList) {
					String spriteName = sprite.getName();
					if (spriteName.equals(itemSelected)) {
						pointedObject = sprite;
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (spriteList.contains(pointedObject)) {
			int pointedSpriteIndex = spinnerAdapter.getPosition(pointedObject.getName());
			spinner.setSelection(pointedSpriteIndex);
		} else {
			spinner.setSelection(0);
		}
		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_point_to_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_point_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite, pointedObject);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointTo(sprite, pointedObject));
		return null;
	}
}
