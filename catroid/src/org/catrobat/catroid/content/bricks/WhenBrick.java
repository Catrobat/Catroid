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
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.WhenScript;
import org.catrobat.catroid.ui.adapter.BrickAdapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WhenBrick extends ScriptBrick {
	protected WhenScript whenScript;
	private Sprite sprite;
	private static final long serialVersionUID = 1L;

	private transient View view;
	private transient CheckBox checkbox;
	private transient boolean checked;

	public WhenBrick(Sprite sprite, WhenScript whenScript) {
		this.whenScript = whenScript;
		this.sprite = sprite;
	}

	public WhenBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public View getView(final Context context, int brickId, final BaseAdapter baseAdapter) {
		if (view == null) {
			view = View.inflate(context, R.layout.brick_when, null);
			checkbox = (CheckBox) view.findViewById(R.id.brick_when_checkbox);

			final Brick brickInstance = this;

			checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					checked = !checked;
					adapter.handleCheck(brickInstance, checked);
				}
			});

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
		}
		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_when_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return getView(context, 0, null);
	}

	@Override
	public Brick clone() {
		return new WhenBrick(getSprite(), null);
	}

	@Override
	public Script initScript(Sprite sprite) {
		if (whenScript == null) {
			whenScript = new WhenScript(sprite);
		}

		return whenScript;
	}

	@Override
	public void setCheckboxVisibility(int visibility) {
		if (checkbox != null) {
			checkbox.setVisibility(visibility);
		}
	}

	private transient BrickAdapter adapter;

	@Override
	public void setBrickAdapter(BrickAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public CheckBox getCheckBox() {
		return checkbox;
	}

	@Override
	public void setCheckedBoolean(boolean newValue) {
		checked = newValue;
	}
}
