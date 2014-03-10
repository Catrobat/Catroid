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
package org.catrobat.catroid.physic.content.bricks;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.physic.PhysicsObject;

import java.util.List;

public class SetPhysicsObjectTypeBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private PhysicsObject.Type type = PhysicsObject.Type.NONE;
	private transient AdapterView<?> adapterView;

	public SetPhysicsObjectTypeBrick() {
	}

	public SetPhysicsObjectTypeBrick(Sprite sprite, PhysicsObject.Type type) {
		this.sprite = sprite;
		this.type = type;
	}

	@Override
	public int getRequiredResources() {
		return PHYSIC;
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	@Override
	public Brick clone() {
		return new SetPhysicsObjectTypeBrick(sprite, type);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physic_set_physic_object_type, null);
		view = getViewWithAlpha(alphaValue);

		setCheckboxView(R.id.brick_set_physics_object_checkbox);
		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}

		});

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_set_physics_object_type_spinner);
		spinner.setAdapter(createAdapter(context));
		spinner.setSelection(type.ordinal());

		spinner.setClickable(true);
		spinner.setFocusable(true);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position < PhysicsObject.Type.values().length) {
					type = PhysicsObject.Type.values()[position];
					adapterView = parent;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		return view;
	}

	private ArrayAdapter<String> createAdapter(Context context) {
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		for (String type : context.getResources().getStringArray(R.array.physics_object_types)) {
			arrayAdapter.add(type);
		}

		return arrayAdapter;
	}

	@Override
	public View getViewWithAlpha(int alphaValue) {
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.brick_set_physics_object_layout);
		Drawable background = layout.getBackground();
		background.setAlpha(alphaValue);

		Spinner lookbrickSpinner = (Spinner) view.findViewById(R.id.brick_set_physics_object_type_spinner);
		TextView lookbrickTextView = (TextView) view.findViewById(R.id.brick_set_physics_object_text_view);

		ColorStateList color = lookbrickTextView.getTextColors().withAlpha(alphaValue);
		lookbrickTextView.setTextColor(color);
		lookbrickSpinner.getBackground().setAlpha(alphaValue);
		if (adapterView != null) {
			((TextView) adapterView.getChildAt(0)).setTextColor(color);
		}

		this.alphaValue = (alphaValue);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.brick_physic_set_physic_object_type, null);
		Spinner pointToSpinner = (Spinner) view.findViewById(R.id.brick_set_physics_object_type_spinner);
		pointToSpinner.setFocusableInTouchMode(false);
		pointToSpinner.setFocusable(false);
		SpinnerAdapter pointToSpinnerAdapter = createAdapter(context);
		pointToSpinner.setAdapter(pointToSpinnerAdapter);
		pointToSpinner.setSelection(PhysicsObject.Type.DYNAMIC.ordinal());
		return view;
	}

	@Override
	public List<SequenceAction> addActionToSequence(SequenceAction sequence) {
		//		sequence.addAction(ExtendedActions.setPhysicObjectType(sprite, physicsObject, type));
		sequence.addAction(sprite.getActionFactory().createSetPhysicObjectTypeAction(sprite, type));
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite, Script script) {
		SetPhysicsObjectTypeBrick copyBrick = (SetPhysicsObjectTypeBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}
}
