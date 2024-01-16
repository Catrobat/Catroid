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
import org.catrobat.catroid.io.catlang.serializer.CatrobatLanguageBrick;

import java.util.ArrayList;
import java.util.Collection;

import androidx.annotation.NonNull;
import kotlin.Unit;

@CatrobatLanguageBrick(command = "Start Jumping Sumo")
public class JumpingSumoAnimationsBrick extends BrickBaseType implements UpdateableSpinnerBrick {

	private static final long serialVersionUID = 1L;

	private String animationName;
	private int spinnerSelectionIndex;

	public enum Animation {
		SPIN, TAB, SLOWSHAKE, METRONOME, ONDULATION, SPINJUMP, SPIRAL, SLALOM
	}

	public JumpingSumoAnimationsBrick() {
		animationName = Animation.SPIN.name();
		spinnerSelectionIndex = Animation.SPIN.ordinal();
	}

	public JumpingSumoAnimationsBrick(Animation animation) {
		animationName = animation.name();
		spinnerSelectionIndex = animation.ordinal();
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
			spinnerSelectionIndex = position;
			return Unit.INSTANCE;
		}));
		spinner.setSelection(Animation.valueOf(animationName).ordinal());
		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
	}

	@Override
	public void updateSelectedItem(Context context, int spinnerId, String itemName, int itemIndex) {
		Animation[] animations = Animation.values();
		if (itemIndex >= 0 && itemIndex < animations.length) {
			animationName = animations[itemIndex].name();
			spinnerSelectionIndex = itemIndex;
		}
	}

	@Override
	protected String getCatrobatLanguageSpinnerValue(int spinnerIndex) {
		switch (spinnerIndex) {
			case 0:
				return "spin";
			case 1:
				return "tab";
			case 2:
				return "slowshake";
			case 3:
				return "metronome";
			case 4:
				return "ondulation";
			case 5:
				return "spinjump";
			case 6:
				return "spiral";
			case 7:
				return "slalom";
			default:
				throw new IndexOutOfBoundsException("Invalid spinnerIndex");
		}
	}

	@NonNull
	@Override
	public String serializeToCatrobatLanguage(int indentionLevel) {
		return getCatrobatLanguageSpinnerCall(indentionLevel, "animation", spinnerSelectionIndex);
	}

	@Override
	protected Collection<String> getRequiredArgumentNames() {
		ArrayList<String> requiredArguments = new ArrayList<>(super.getRequiredArgumentNames());
		requiredArguments.add("animation");
		return requiredArguments;
	}
}
