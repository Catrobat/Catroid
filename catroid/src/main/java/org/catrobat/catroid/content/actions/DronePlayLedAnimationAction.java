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
package org.catrobat.catroid.content.actions;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.parrot.freeflight.drone.DroneProxy.ARDRONE_LED_ANIMATION;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;

import java.util.HashMap;

public class DronePlayLedAnimationAction extends TemporalAction {
	private DroneControlService dcs;
	private ARDRONE_LED_ANIMATION ledAnimationType;
	private int ledNumber;
	private float duration = 5.0f;

	private static final HashMap<String, Integer> ANIMATION_TYPE_MAPPING;

	static {
		ANIMATION_TYPE_MAPPING = new HashMap<>();
		for (ARDRONE_LED_ANIMATION ledType : ARDRONE_LED_ANIMATION.values()) {
			ANIMATION_TYPE_MAPPING.put(ledType.name(), ARDRONE_LED_ANIMATION.valueOf(ledType.name()).ordinal());
		}
	}

	public void setAnimationType(ARDRONE_LED_ANIMATION ledAnimationType) {
		this.ledAnimationType = ledAnimationType;
	}

	@Override
	protected void begin() {
		super.begin();
		dcs = DroneServiceWrapper.getDroneService();
		if (dcs != null) {
			ledAnimation();
		}
	}

	protected void sendLedAnimation(int ledType) {
		dcs.playLedAnimation(5.0f, (int) duration, ledType);
		super.setDuration(duration);
	}

	protected void ledAnimation() {
		if (dcs != null) {
			ledNumber = ANIMATION_TYPE_MAPPING.get(ledAnimationType.name());
			sendLedAnimation(ledNumber);
		}
	}

	@Override
	protected void update(float percent) {
		//Nothing to do
	}
}
