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

import org.apache.commons.lang3.NotImplementedException;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.AdapterViewOnItemSelectedListenerImpl;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.io.catlang.CatrobatLanguageBrick;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Play AR.Drone 2.0")
public class DronePlayLedAnimationBrick extends BrickBaseType implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

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
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		String[] animations = context.getResources().getStringArray(
				R.array.brick_drone_play_led_animation_spinner);
		if (itemIndex >= 0 && itemIndex < animations.length) {
			ledAnimationName = animations[itemIndex];
			spinnerSelectionIndex = itemIndex;
		}
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case 0:
				return "blink green red";
			case 1:
				return "blink green";
			case 2:
				return "blink red";
			case 3:
				return "blink orange";
			case 4:
				return "snake green red";
			case 5:
				return "fire";
			case 6:
				return "standard";
			case 7:
				return "red";
			case 8:
				return "green";
			case 9:
				return "red snake";
			case 10:
				return "blank";
			case 11:
				return "right missile";
			case 12:
				return "left missile";
			case 13:
				return "double missle";
			case 14:
				return "front left green others red";
			case 15:
				return "front right green others red";
			case 16:
				return "rear right green others red";
			case 17:
				return "rear left green others red";
			case 18:
				return "left green right red";
			case 19:
				return "left red right green";
			case 20:
				return "blink standard";
			default:
				throw new NotImplementedException("Invalid spinner index");
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageSpinnerCall(indentionLevel, "flash animation", spinnerSelectionIndex);
	}
}

