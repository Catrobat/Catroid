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
import android.text.TextUtils;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Play AR.Drone 2.0")
public class DronePlayLedAnimationBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;
	private static final String FLASH_ANIMATION_CATLANG_PARAMETER_NAME = "flash animation";
	private static final BiMap<Integer, String> CATLANG_SPINNER_VALUES = HashBiMap.create(new HashMap<Integer, String>() {
		{
			put(0, "blink green red");
			put(1, "blink green");
			put(2, "blink red");
			put(3, "blink orange");
			put(4, "snake green red");
			put(5, "fire");
			put(6, "standard");
			put(7, "red");
			put(8, "green");
			put(9, "red snake");
			put(10, "blank");
			put(11, "right missile");
			put(12, "left missile");
			put(13, "double missle");
			put(14, "front left green others red");
			put(15, "front right green others red");
			put(16, "rear right green others red");
			put(17, "rear left green others red");
			put(18, "left green right red");
			put(19, "left red right green");
			put(20, "blink standard");
		}
	});

	private String ledAnimationName = "";
	private int spinnerSelectionIndex;

	public DronePlayLedAnimationBrick() {
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_drone_play_led_animation;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);
		ArrayAdapter<CharSequence> animationAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_drone_play_led_animation_spinner, android.R.layout.simple_spinner_item);
		animationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner animationSpinner = view.findViewById(R.id.brick_drone_play_led_animation_spinner);
		animationSpinner.setAdapter(animationAdapter);
		animationSpinner.setOnItemSelectedListener(new AdapterViewOnItemSelectedListenerImpl(position -> {
			ledAnimationName = context.getResources().getStringArray(
					R.array.brick_drone_play_led_animation_spinner)[position];
			spinnerSelectionIndex = position;
			return Unit.INSTANCE;
		}));
		if (TextUtils.isEmpty(ledAnimationName)) {
			spinnerSelectionIndex = 1;
		} else {
			List<String> spinnerArray = Arrays.asList(context.getResources().getStringArray(
					R.array.brick_drone_play_led_animation_spinner));
			spinnerSelectionIndex = spinnerArray.indexOf(ledAnimationName);
		}
		animationSpinner.setSelection(spinnerSelectionIndex);

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	protected Map.Entry<String, String> getArgumentByCatlangName(String name) {
		if (name.equals(FLASH_ANIMATION_CATLANG_PARAMETER_NAME)) {
			return new HashMap.SimpleEntry<>(name, CATLANG_SPINNER_VALUES.get(spinnerSelectionIndex));
		}
		return super.getArgumentByCatlangName(name);
	}

	@Override
	protected Collection<String> getRequiredCatlangArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredCatlangArgumentNames());
		requiredArguments.add(FLASH_ANIMATION_CATLANG_PARAMETER_NAME);
		return requiredArguments;
	}

	@Override
	public void setParameters(@NonNull Context context, @NonNull Project project, @NonNull Scene scene, @NonNull Sprite sprite, @NonNull Map<String, String> arguments) throws CatrobatLanguageParsingException {
		super.setParameters(context, project, scene, sprite, arguments);
		String animation = arguments.get(FLASH_ANIMATION_CATLANG_PARAMETER_NAME);
		if (animation != null) {
			Integer selectedAnimation = CATLANG_SPINNER_VALUES.inverse().get(animation);
			if (selectedAnimation == null) {
				throw new CatrobatLanguageParsingException("Invalid animation: " + animation);
			}
			spinnerSelectionIndex = selectedAnimation;
			ledAnimationName = context.getResources().getStringArray(
					R.array.brick_drone_play_led_animation_spinner)[spinnerSelectionIndex];
		}
	}
}

