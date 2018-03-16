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
import android.widget.BaseAdapter;
import android.widget.Spinner;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.parrot.freeflight.drone.DroneProxy.ARDRONE_LED_ANIMATION;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;

import java.util.List;

public class DronePlayLedAnimationBrick extends BrickBaseType {
	private static final long serialVersionUID = 1L;

	private transient View prototypeView;
	private String ledAnimationName;
	private transient ARDRONE_LED_ANIMATION ledAnimation;

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
	public View getPrototypeView(Context context) {
		prototypeView = View.inflate(context, R.layout.brick_drone_play_led_animation, null);

		Spinner dronePlayLedAnimationSpinner = (Spinner) prototypeView.findViewById(R.id
				.brick_drone_play_led_animation_spinner);
		dronePlayLedAnimationSpinner.setFocusableInTouchMode(false);
		dronePlayLedAnimationSpinner.setFocusable(false);
		dronePlayLedAnimationSpinner.setEnabled(false);

		ArrayAdapter<CharSequence> animationAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_drone_play_led_animation_spinner, android.R.layout.simple_spinner_item);
		animationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		dronePlayLedAnimationSpinner.setAdapter(animationAdapter);
		dronePlayLedAnimationSpinner.setSelection(ledAnimation.ordinal());

		return prototypeView;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
		if (animationState) {
			return view;
		}

		view = View.inflate(context, R.layout.brick_drone_play_led_animation, null);
		view = BrickViewProvider.setAlphaOnView(view, alphaValue);
		setCheckboxView(R.id.brick_drone_play_led_animation_checkbox);

		ArrayAdapter<CharSequence> animationAdapter = ArrayAdapter.createFromResource(context,
				R.array.brick_drone_play_led_animation_spinner, android.R.layout.simple_spinner_item);
		animationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner animationSpinner = (Spinner) view.findViewById(R.id.brick_drone_play_led_animation_spinner);

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
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createDronePlayLedAnimationAction(ledAnimation));
		return null;
	}

	@Override
	public int getRequiredResources() {
		return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
	}
}

