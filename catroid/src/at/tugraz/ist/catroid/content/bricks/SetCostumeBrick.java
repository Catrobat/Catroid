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
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.CostumeData;
import at.tugraz.ist.catroid.content.Sprite;

public class SetCostumeBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private CostumeData costumeData;

	public SetCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setCostume(CostumeData costumeData) {
		this.costumeData = costumeData;
	}

	public void execute() {
		if (costumeData != null && sprite != null) {
			sprite.getCostume().changeImagePath(costumeData.getAbsolutePath());
		}
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getImagePath() {
		return costumeData.getAbsolutePath();
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_set_costume, null);

		Spinner costumebrickSpinner = (Spinner) view.findViewById(R.id.setcostume_spinner);
		costumebrickSpinner.setAdapter(createCostumeAdapter(context));

		costumebrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				costumeData = (CostumeData) parent.getItemAtPosition(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (sprite.getCostumeDataList().contains(costumeData)) {
			costumebrickSpinner.setSelection(sprite.getCostumeDataList().indexOf(costumeData) + 1);
		} else {
			costumebrickSpinner.setSelection(0);
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

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_set_costume, null);
		return view;
	}

	@Override
	public Brick clone() {
		SetCostumeBrick clonedBrick = new SetCostumeBrick(getSprite());
		if (sprite.getCostume() != null) {
			clonedBrick.setCostume(null);
		}

		return clonedBrick;

	}
}
