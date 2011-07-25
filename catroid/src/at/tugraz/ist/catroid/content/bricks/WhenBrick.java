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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Spinner;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;

public class WhenBrick implements Brick {
	protected WhenScript whenScript;
	private Sprite sprite;
	private static final long serialVersionUID = 1L;

	public WhenBrick(Sprite sprite, WhenScript whenScript) {
		this.whenScript = whenScript;
		this.sprite = sprite;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(final Context context, int brickId, final BaseExpandableListAdapter adapter) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_when, null);
		final Spinner spinner = (Spinner) view.findViewById(R.id.Spinner03);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(context,
				android.R.layout.simple_spinner_item);
		spinnerAdapter.add(context.getString(R.string.action_tapped));
		spinnerAdapter.add(context.getString(R.string.action_doubleTapped));
		spinnerAdapter.add(context.getString(R.string.action_longPressed));
		spinnerAdapter.add(context.getString(R.string.action_swipeUp));
		spinnerAdapter.add(context.getString(R.string.action_swipeDown));
		spinnerAdapter.add(context.getString(R.string.action_swipeLeft));
		spinnerAdapter.add(context.getString(R.string.action_swipeRight));
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		if (whenScript.getAction() != null) {
			spinner.setSelection(whenScript.getPosition());
		}

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			boolean start = true;

			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (start) {
					start = false;
					return;
				}

				spinner.setSelected(true);
				String choice = parent.getItemAtPosition(position).toString();
				Log.i("choosen", choice);
				whenScript.setAction(position);
				spinner.setSelection(position);
				adapter.notifyDataSetChanged();
			}

			public void onNothingSelected(AdapterView<?> parent) {
				//		 Do nothing.
			}
		});
		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_when, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new WhenBrick(getSprite(), whenScript);
	}

}
