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

import java.util.List;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class SetLookBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;
	private LookData look;
	private transient View prototypeView;

	public SetLookBrick(Sprite sprite) {
		this.sprite = sprite;
	}

	public SetLookBrick() {

	}

	public void setLook(LookData lookData) {
		this.look = lookData;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetLookBrick copyBrick = (SetLookBrick) clone();
		copyBrick.sprite = sprite;

		for (LookData data : sprite.getLookDataList()) {
			if (data.getAbsolutePath().equals(look.getAbsolutePath())) {
				copyBrick.look = data;
				break;
			}
		}
		return copyBrick;
	}

	public String getImagePath() {
		return look.getAbsolutePath();
	}

	@Override
	public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}
		final Brick brickInstance = this;
		view = View.inflate(context, R.layout.brick_set_look, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_look_checkbox);
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.brick_set_look_spinner);
		lookbrickSpinner.setAdapter(createLookAdapter(context));
		if (!(checkbox.getVisibility() == View.VISIBLE)) {
			lookbrickSpinner.setClickable(true);
			lookbrickSpinner.setEnabled(true);
		} else {
			lookbrickSpinner.setClickable(false);
			lookbrickSpinner.setEnabled(false);
		}

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

		setSpinnerSelection(lookbrickSpinner);

		if (sprite.getName().equals(context.getString(R.string.background))) {
			TextView textView = (TextView) view.findViewById(R.id.brick_set_look_prototype_text_view);
			textView.setText(R.string.brick_set_background);
		}

		return view;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_look_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);
		this.alphaValue = (alphaValue);
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
		Spinner setLookSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_look_spinner);
		setLookSpinner.setFocusableInTouchMode(false);
		setLookSpinner.setFocusable(false);
		SpinnerAdapter setLookSpinnerAdapter = createLookAdapter(context);
		setLookSpinner.setAdapter(setLookSpinnerAdapter);
		setSpinnerSelection(setLookSpinner);
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
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		sequence.addAction(ExtendedActions.setLook(sprite, look));
		return null;
	}

	private void setSpinnerSelection(Spinner spinner) {
		if (sprite.getLookDataList().contains(look)) {
			spinner.setSelection(sprite.getLookDataList().indexOf(look) + 1, true);
		} else {
			if (spinner.getAdapter() != null && spinner.getAdapter().getCount() > 1) {
				spinner.setSelection(1, true);
			} else {
				spinner.setSelection(0);
			}
		}
	}
}
