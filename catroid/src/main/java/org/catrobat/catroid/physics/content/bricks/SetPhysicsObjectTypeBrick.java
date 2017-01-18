/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.physics.content.bricks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.BrickBaseType;
import org.catrobat.catroid.content.bricks.BrickViewProvider;
import org.catrobat.catroid.physics.PhysicsObject;
import org.catrobat.catroid.utils.TextSizeUtil;

import java.util.List;

public class SetPhysicsObjectTypeBrick extends BrickBaseType implements Cloneable {
	private static final long serialVersionUID = 1L;

	private PhysicsObject.Type type = PhysicsObject.Type.NONE;

	private transient View prototypeView;

	public SetPhysicsObjectTypeBrick() {
	}

	public SetPhysicsObjectTypeBrick(PhysicsObject.Type type) {
		this.type = type;
	}

	@Override
	public int getRequiredResources() {
		return PHYSICS;
	}

	@Override
	public Brick clone() {
		return new SetPhysicsObjectTypeBrick(type);
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_physics_set_physics_object_type, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);

		setCheckboxView(R.id.brick_set_physics_object_checkbox);

		final Spinner spinner = (Spinner) view.findViewById(R.id.brick_set_physics_object_type_spinner);
		spinner.setAdapter(createAdapter(context));
		spinner.setSelection(type.ordinal());

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position < PhysicsObject.Type.values().length) {
					type = PhysicsObject.Type.values()[position];
				}
				TextView spinnerText = (TextView) parent.getChildAt(0);
				TextSizeUtil.enlargeTextView(spinnerText);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		TextSizeUtil.enlargeViewGroup((ViewGroup) view);

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
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_physics_set_physics_object_type, null);
		Spinner pointToSpinner = (Spinner) prototypeView.findViewById(R.id.brick_set_physics_object_type_spinner);
		SpinnerAdapter objectTypeSpinnerAdapter = createAdapter(context);
		pointToSpinner.setAdapter(objectTypeSpinnerAdapter);
		pointToSpinner.setSelection(PhysicsObject.Type.DYNAMIC.ordinal());
		return prototypeView;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetPhysicsObjectTypeAction(sprite, type));
		return null;
	}

	@Override
	public Brick copyBrickForSprite(Sprite sprite) {
		SetPhysicsObjectTypeBrick copyBrick = (SetPhysicsObjectTypeBrick) clone();
		return copyBrick;
	}
}
