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
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;

public class WhenBrick implements Brick {
	protected WhenScript WhenScript;
	private Sprite sprite;
	private transient BaseExpandableListAdapter adapter;
	private transient boolean firstTime = true;
	private static final long serialVersionUID = 1L;

	public WhenBrick(Sprite sprite, Script WhenScript) {
		this.WhenScript = (WhenScript) WhenScript;
		this.sprite = sprite;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(final Context context, int brickId, final BaseExpandableListAdapter adapter) {
		this.adapter = adapter;
		View view = getProtoView(context);
		final Spinner spinner = (Spinner) view.findViewById(R.id.Spinner03);
		spinner.setFocusableInTouchMode(false);
		spinner.setFocusable(false);
		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(context,
				android.R.layout.simple_spinner_item);
		spinnerAdapter.add(context.getString(R.string.action_tapped));
		spinnerAdapter.add(context.getString(R.string.action_doubleTapped));
		spinnerAdapter.add(context.getString(R.string.action_longPressed));
		spinnerAdapter.add(context.getString(R.string.action_touchingStarts));
		spinnerAdapter.add(context.getString(R.string.action_touchingStops));
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);

		if (WhenScript.getAction() != null) {
			for (int count = 0; count < spinnerAdapter.getCount(); count++) {
				if (WhenScript.getAction().equalsIgnoreCase(spinnerAdapter.getItem(count).toString())) {
					spinner.setSelection(count);
				}
			}
		}

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				try {
					if (firstTime) {
						spinner.setSelected(false);
						firstTime = false;
					} else {
						spinner.setSelected(true);
						String choice = parent.getItemAtPosition(pos).toString();
						Log.i("choosen", choice);
						WhenScript.setAction(choice);
						spinner.setSelection(pos);
						adapter.notifyDataSetChanged();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				//			        Toast.makeText(parent.getContext(), "The planet is " +
				//			            parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
			}

			public void onNothingSelected(AdapterView parent) {
				//		 Do nothing.
			}
		});
		return view;
	}

	public View getProtoView(final Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_when, null);
		return view;
	}

	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.toolbox_brick_when, null);
		return view;
	}

	@Override
	public Brick clone() {
		return new WhenBrick(getSprite(), WhenScript);
	}

}
