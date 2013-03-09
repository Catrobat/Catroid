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
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SetLookBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private Sprite sprite;
	private LookData look;
	private transient View view;
	private transient View prototypeView;

	public SetLookBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public SetLookBrick() {

	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	public void setLook(LookData lookData) {
		this.look = lookData;
	}

	public LookData getLook() {
		return this.look;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	public String getImagePath() {
		return look.getAbsolutePath();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_set_look, null);

		Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.setlook_spinner);
		lookbrickSpinner.setAdapter(createLookAdapter(context));
		lookbrickSpinner.setClickable(true);
		lookbrickSpinner.setFocusable(true);

		lookbrickSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					look = null;
				} else {
					look = (LookData) parent.getItemAtPosition(position);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		if (sprite.getLookDataList().contains(look)) {
			lookbrickSpinner.setSelection(sprite.getLookDataList().indexOf(look) + 1, true);
		} else {
			lookbrickSpinner.setSelection(0);
		}

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_set_look_prototype_text_view);
			textView.setText(R.string.brick_set_background);
		}

		return view;
	}

	private ArrayAdapter<?> createLookAdapter(Context context) {
		ArrayAdapter<LookData> arrayAdapter = new ArrayAdapter<LookData>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		LookData dummyLookData = new LookData();
		dummyLookData.setLookName(context.getString(R.string.broadcast_nothing_selected));
		arrayAdapter.add(dummyLookData);
		for (LookData lookData : sprite.getLookDataList()) {
			arrayAdapter.add(lookData);
		}
		return arrayAdapter;
	}

	@Override
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_set_look, null);
		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) prototypeView.findViewById(R.id.brick_set_look_prototype_text_view);
			textView.setText(R.string.brick_set_background);
		}
		return prototypeView;
	}

	@Override
	public Brick clone() {
		SetLookBrick clonedBrick = new SetLookBrick(getSprite());
		if (sprite.look != null) {
			clonedBrick.setLook(null);
		}

		return clonedBrick;
		//test
	}

	@Override
	public SequenceAction addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setLook(sprite, look));
		return null;
	}
}
