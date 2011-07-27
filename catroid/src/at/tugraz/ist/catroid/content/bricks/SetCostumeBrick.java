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
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class SetCostumeBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private String imageName;
	@XStreamOmitField
	private transient Bitmap thumbnail;
	private Costume CostumeData;
	private int position;

	public SetCostumeBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public void setCostume(String imageName) {
		this.imageName = imageName;
		if (imageName != null) {
			thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
					Consts.THUMBNAIL_WIDTH);
		}
	}

	private Object readResolve() {
		if (imageName != null && ProjectManager.getInstance().getCurrentProject() != null) {
			String[] checksum = imageName.split("_");
			ProjectManager.getInstance().fileChecksumContainer.addChecksum(checksum[0], getAbsoluteImagePath());
		}
		return this;
	}

	public void execute() {
		this.sprite.getCostume().setImagePath(getAbsoluteImagePath());
	}

	public Sprite getSprite() {
		return sprite;
	}

	public String getImagePath() {
		return getAbsoluteImagePath();
	}

	private String getAbsoluteImagePath() {
		if (imageName == null) {
			return null;
		}
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
				+ Consts.IMAGE_DIRECTORY + "/" + imageName;
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_set_costume, null);

		Spinner costumebrickSpinner = (Spinner) view.findViewById(R.id.setcostume_spinner);
		costumebrickSpinner.setAdapter(createCostumeAdapter(context));

		costumebrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			//private boolean start = true;
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				CostumeData = (Costume) parent.getItemAtPosition(position);
				SetCostumeBrick.this.position = position;
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		costumebrickSpinner.setSelection(position);

		return view;

	}

	private ArrayAdapter<?> createCostumeAdapter(Context context) {
		ArrayAdapter<Costume> arrayAdapter = new ArrayAdapter<Costume>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Costume dummyCostume = new Costume(sprite, null);
		dummyCostume.setCostumeDisplayName(context.getString(R.string.broadcast_nothing_selected));
		arrayAdapter.add(dummyCostume);
		for (Costume CostumeData : sprite.getCostumeList()) {
			arrayAdapter.add(CostumeData);
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
		return new SetCostumeBrick(getSprite());

	}
}
