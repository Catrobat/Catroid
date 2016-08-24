/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Sprite;

public class GoToRandomPositionAction extends TemporalAction {

	private Sprite sprite;
	private float randomXPosition;
	private float randomYPosition;

	@Override
	protected void update(float percent) {
		randomXPosition = (float) Math.random()
				* (ScreenValues.SCREEN_WIDTH + 1) - (ScreenValues.SCREEN_WIDTH / 2);
		randomYPosition = (float) Math.random()
				* (ScreenValues.SCREEN_HEIGHT + 1) - (ScreenValues.SCREEN_HEIGHT / 2);

		sprite.look.setXInUserInterfaceDimensionUnit(randomXPosition);
		sprite.look.setYInUserInterfaceDimensionUnit(randomYPosition);
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public float getRandomXPosition() {
		return this.randomXPosition;
	}

	public float getRandomYPosition() {
		return this.randomYPosition;
	}
}
