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

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.Arrays;
import java.util.List;

import kotlin.Unit;

public class DronePlayLedAnimationBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private String ledAnimationName = "";

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
			return Unit.INSTANCE;
		}));
		if (TextUtils.isEmpty(ledAnimationName)) {
			animationSpinner.setSelection(1);
		} else {
			List<String> spinnerArray = Arrays.asList(context.getResources().getStringArray(
					R.array.brick_drone_play_led_animation_spinner));
			animationSpinner.setSelection(spinnerArray.indexOf(ledAnimationName));
		}

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}
}

