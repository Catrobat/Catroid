/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.parrot.freeflight.drone.DroneProxy.ARDRONE_LED_ANIMATION;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

public class DronePlayLedAnimationBrick extends BrickBaseType {

	private static final long serialVersionUID = 1L;

	private String ledAnimationName;
	private transient ARDRONE_LED_ANIMATION ledAnimation;

	public DronePlayLedAnimationBrick() {
		ledAnimation = ARDRONE_LED_ANIMATION.ARDRONE_LED_ANIMATION_BLINK_GREEN;
		ledAnimationName = ledAnimation.name();
	}

	public DronePlayLedAnimationBrick(ARDRONE_LED_ANIMATION ledAnimation) {
		this.ledAnimation = ledAnimation;
		this.ledAnimationName = ledAnimation.name();
	}

	protected Object readResolve() {
		if (ledAnimationName != null) {
			ledAnimation = ARDRONE_LED_ANIMATION.valueOf(ledAnimationName);
		}
		return this;
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
		animationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				ledAnimation = ARDRONE_LED_ANIMATION.values()[position];
				ledAnimationName = ledAnimation.name();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		if (ledAnimation == null) {
			readResolve();
		}

		animationSpinner.setSelection(ledAnimation.ordinal());

		return view;
	}

	@Override
	public void addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createDronePlayLedAnimationAction(ledAnimation));
	}

	@Override
	public void addRequiredResources(final ResourcesSet requiredResourcesSet) {
		requiredResourcesSet.add(ARDRONE_SUPPORT);
	}
}

