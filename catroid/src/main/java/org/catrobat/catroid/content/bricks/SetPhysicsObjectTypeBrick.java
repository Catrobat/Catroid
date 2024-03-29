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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageUtils;
import org.catrobat.catroid.physics.PhysicsObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Set")
public class SetPhysicsObjectTypeBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private static final String MOTION_TYPE_CATLANG_PARAMETER_NAME = "motion type";
	private static final BiMap<PhysicsObject.Type, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<PhysicsObject.Type, String>() {
		{
			put(PhysicsObject.Type.DYNAMIC, "moving and bouncing under gravity");
			put(PhysicsObject.Type.FIXED, "not moving under gravity, but others bounce off you under gravity");
			put(PhysicsObject.Type.NONE, "not moving or bouncing under gravity (default)");
		}
	});

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

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(MOTION_TYPE_CATLANG_PARAMETER_NAME)) {
			return CatrobatLanguageUtils.getCatlangArgumentTuple(name, CATLANG_SPINNER_VALUES.get(type));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(MOTION_TYPE_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String motionType = arguments.get(MOTION_TYPE_CATLANG_PARAMETER_NAME);
		if (motionType != null) {
			type = CATLANG_SPINNER_VALUES.inverse().get(motionType);
			if (type == null) {
				throw new CatrobatLanguageParsingException("Invalid motion type: " + motionType);
			}
		}
	}
}
