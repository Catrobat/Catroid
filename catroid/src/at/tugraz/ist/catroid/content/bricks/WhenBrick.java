/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;

public class WhenBrick implements Brick {
	protected WhenScript whenScript;
	private Sprite sprite;
	private static final long serialVersionUID = 1L;

	private transient View view;

	public WhenBrick(Sprite sprite, WhenScript whenScript) {
		this.whenScript = whenScript;
		this.sprite = sprite;
	}

	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void execute() {
	}

	public Sprite getSprite() {
		return sprite;
	}

	public View getView(final Context context, int brickId, final BaseAdapter adapter) {
		view = View.inflate(context, R.layout.brick_when, null);
		TextView spinnerActionText = (TextView) view.findViewById(R.id.WhenBrickActionTapped);
		spinnerActionText.setText(" " + spinnerActionText.getText());

		// inactive until spinner has more than one element
		//		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_when_spinner);
		//		spinner.setFocusable(false);
		//		spinner.setClickable(true);
		//		ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(context,
		//				android.R.layout.simple_spinner_item);
		//		spinnerAdapter.add(context.getString(R.string.action_tapped));

		//		TODO: not working with OpenGL yet, uncomment this when it does
		//		spinnerAdapter.add(context.getString(R.string.action_doubleTapped));
		//		spinnerAdapter.add(context.getString(R.string.action_longPressed));
		//		spinnerAdapter.add(context.getString(R.string.action_swipeUp));
		//		spinnerAdapter.add(context.getString(R.string.action_swipeDown));
		//		spinnerAdapter.add(context.getString(R.string.action_swipeLeft));
		//		spinnerAdapter.add(context.getString(R.string.action_swipeRight));

		//		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//		spinner.setAdapter(spinnerAdapter);
		//
		//		if (whenScript.getAction() != null) {
		//			spinner.setSelection(whenScript.getPosition(), true);
		//		}
		//
		//		if (spinner.getSelectedItem() == null) {
		//			spinner.setSelection(0);
		//		}
		//
		//		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		//				spinner.setSelected(true);
		//				whenScript.setAction(position);
		//				spinner.setSelection(position);
		//				adapter.notifyDataSetChanged();
		//			}
		//
		//			public void onNothingSelected(AdapterView<?> parent) {
		//			}
		//		});

		return view;
	}

	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}

	@Override
	public Brick clone() {
		return new WhenBrick(getSprite(), whenScript);
	}

}
