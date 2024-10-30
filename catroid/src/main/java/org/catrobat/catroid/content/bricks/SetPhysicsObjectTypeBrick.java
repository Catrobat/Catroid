/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.physics.PhysicsObject;

import androidx.annotation.VisibleForTesting;
import kotlin.Unit;

public class SetPhysicsObjectTypeBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private PhysicsObject.Type type = PhysicsObject.Type.NONE;

	public SetPhysicsObjectTypeBrick() {
	}

	public SetPhysicsObjectTypeBrick(PhysicsObject.Type type) {
		this.type = type;
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(PHYSICS);
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_physics_set_physics_object_type;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		final Spinner spinner = view.findViewById(R.id.brick_set_physics_object_type_spinner);
		spinner.setAdapter(createAdapter(context));
		spinner.setSelection(type.ordinal());
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			if (position < PhysicsObject.Type.values().length) {
				type = PhysicsObject.Type.values()[position];
			}
			return Unit.INSTANCE;
		}));
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
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createSetPhysicsObjectTypeAction(sprite, type));
	}

	@VisibleForTesting
	public PhysicsObject.Type getType() {
		return type;
	}
}
