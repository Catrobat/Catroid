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

/**
 * @author Fatin Ghazi
 * 
 */

public class PointToBrick implements Brick {

	private static final long serialVersionUID = 1L;
	private Sprite sprite;

	public PointToBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void execute() {
		ProjectManager.getInstance().getCurrentSprite().getSelectedPointToSprite();
	}

	public View getView(final Context context, int brickId, BaseExpandableListAdapter adapter) {

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View brickView = inflater.inflate(R.layout.construction_brick_point_to, null);

		final Spinner spinner = (Spinner) brickView.findViewById(R.id.point_to_spinner);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);

		final ArrayList<Sprite> spriteList;
		spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject().getSpriteList();
		ArrayAdapter<Sprite> spinnerAdapter = new ArrayAdapter<Sprite>(context, android.R.layout.simple_spinner_item);

		for (Sprite sprite : spriteList) {
			Sprite temp1 = ProjectManager.getInstance().getCurrentSprite();
			String temp2 = sprite.toString();
			if (!sprite.equals(temp1) & !temp2.equals("Background")) {
				spinnerAdapter.add(sprite);
			}
		}

		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			private boolean start = true;

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (start) {
					start = false;
					return;
				}
				ProjectManager.getInstance().getCurrentSprite()
						.setSelectedPointToSprite((Sprite) (parent.getItemAtPosition(position)));
				spinner.setSelection(position);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return brickView;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_point_to, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new PointToBrick(sprite);
	}
}
