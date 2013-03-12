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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;import java.util.List;

public class PointToBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private Sprite pointedSprite;

	public PointToBrick(Sprite sprite, Sprite pointedSprite) {
		this.sprite = sprite;
		this.pointedSprite = pointedSprite;
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
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		PointToBrick copyBrick = (PointToBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
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
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_point_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite, pointedSprite);
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.pointTo(sprite, pointedSprite));
		return null;
	}
}
