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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Start Jumping Sumo")
public class JumpingSumoAnimationsBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private static final String ANIMATION_CATLANG_PARAMETER_NAME = "animation";
	private static final BiMap<Animation, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Animation, String>()
	{{
		put(Animation.SPIN, "spin");
		put(Animation.TAB, "tab");
		put(Animation.SLOWSHAKE, "slowshake");
		put(Animation.METRONOME, "metronome");
		put(Animation.ONDULATION, "ondulation");
		put(Animation.SPINJUMP, "spinjump");
		put(Animation.SPIRAL, "spiral");
		put(Animation.SLALOM, "slalom");
	}});

	private String animationName;

	public enum Animation {
		SPIN, TAB, SLOWSHAKE, METRONOME, ONDULATION, SPINJUMP, SPIRAL, SLALOM
	}

	public JumpingSumoAnimationsBrick() {
		animationName = Animation.SPIN.name();
	}

	public JumpingSumoAnimationsBrick(Animation animation) {
		animationName = animation.name();
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_jumping_sumo_animations;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_jumping_sumo_select_animation_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinner = view.findViewById(R.id.brick_jumping_sumo_animation_spinner);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			animationName = Animation.values()[position].name();
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Animation.valueOf(animationName).ordinal());
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(ANIMATION_CATLANG_PARAMETER_NAME))
			return new HashMap.SimpleEntry<>(ANIMATION_CATLANG_PARAMETER_NAME, CATLANG_SPINNER_VALUES.get(Animation.valueOf(animationName)));
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(ANIMATION_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);

		String animation = arguments.get(ANIMATION_CATLANG_PARAMETER_NAME);
		if (animation != null) {
			Animation selectedAnimation = CATLANG_SPINNER_VALUES.inverse().get(animation);
			if (selectedAnimation != null) {
				animationName = selectedAnimation.name();
			} else {
				throw new CatrobatLanguageParsingException("Invalid animation argument: " + animation);
			}
		}
	}
}
