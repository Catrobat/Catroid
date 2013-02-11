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
import org.catrobat.catroid.common.CostumeData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class SetCostumeBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private CostumeData costume;
	private transient View view;

	public SetCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public SetCostumeBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void setCostume(CostumeData costumeData) {
		this.costume = costumeData;
	}

	@Override
	public void execute() {
		if (costume != null && sprite != null && sprite.getCostumeDataList().contains(costume)) {
			sprite.costume.setCostumeData(costume);
		}
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetCostumeBrick copyBrick = (SetCostumeBrick) clone();
		copyBrick.sprite = sprite;

		for (CostumeData data : sprite.getCostumeDataList()) {
			if (data.getAbsolutePath().equals(costume.getAbsolutePath())) {
				copyBrick.costume = data;
			}
		}
		return copyBrick;
	}

	public String getImagePath() {
		return costume.getAbsolutePath();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_costume, null);

		Spinner costumebrickSpinner = (Spinner) view.findViewById(R.id.setcostume_spinner);
		costumebrickSpinner.setAdapter(createCostumeAdapter(context));
		costumebrickSpinner.setClickable(true);
		costumebrickSpinner.setFocusable(true);

		costumebrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					costume = null;
				} else {
					costume = (CostumeData) parent.getItemAtPosition(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (sprite.getCostumeDataList().contains(costume)) {
			costumebrickSpinner.setSelection(sprite.getCostumeDataList().indexOf(costume) + 1, true);
		} else {
			costumebrickSpinner.setSelection(0);
		}

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_set_costume_prototype_text_view);
			textView.setText(R.string.brick_set_background);
		}

		return view;
	}

	private ArrayAdapter<?> createCostumeAdapter(Context context) {
		ArrayAdapter<CostumeData> arrayAdapter = new ArrayAdapter<CostumeData>(context,
				android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		CostumeData dummyCostumeData = new CostumeData();
		dummyCostumeData.setCostumeName(context.getString(R.string.broadcast_nothing_selected));
		arrayAdapter.add(dummyCostumeData);
		for (CostumeData costumeData : sprite.getCostumeDataList()) {
			arrayAdapter.add(costumeData);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = View.inflate(context, R.layout.brick_set_costume, null);
		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) prototypeView.findViewById(R.id.brick_set_costume_prototype_text_view);
			textView.setText(R.string.brick_set_background);
		}
		return prototypeView;
	}

	@Override
	public Brick clone() {
		SetCostumeBrick clonedBrick = new SetCostumeBrick(getSprite());
		if (sprite.costume != null) {
			clonedBrick.setCostume(null);
		}

		return clonedBrick;
	}
}
