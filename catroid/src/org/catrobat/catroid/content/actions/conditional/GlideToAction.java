/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
package org.catrobat.catroid.content.actions.conditional;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physic.content.PhysicActionSleepExtension;

public class GlideToAction extends TemporalAction implements PhysicActionSleepExtension {

	private float startX;
	private float startY;
	private float currentX;
	private float currentY;
	private Formula endX;
	private Formula endY;
	protected Sprite sprite;
	private Formula duration;
	private float endXValue;
	private float endYValue;

	private boolean restart = false;

	@Override
	protected void begin() {
		if (!restart) {
			if (duration != null) {
				super.setDuration(duration.interpretFloat(sprite));
			}
			endXValue = endX.interpretFloat(sprite);
			endYValue = endY.interpretFloat(sprite);
		}
		restart = false;

		startX = sprite.look.getXInUserInterfaceDimensionUnit();
		startY = sprite.look.getYInUserInterfaceDimensionUnit();
		currentX = startX;
		currentY = startY;
		if (startX == endX.interpretFloat(sprite) && startY == endY.interpretFloat(sprite)) {
			super.finish();
		}
	}

	@Override
	protected void update(float percent) {
		float deltaX = sprite.look.getXInUserInterfaceDimensionUnit() - currentX;
		float deltaY = sprite.look.getYInUserInterfaceDimensionUnit() - currentY;
		if ((-0.1f > deltaX || deltaX > 0.1f) || (-0.1f > deltaY || deltaY > 0.1f)) {
			restart = true;
			setDuration(getDuration() - getTime());
			restart();
		} else {
			currentX = startX + (endXValue - startX) * percent;
			currentY = startY + (endYValue - startY) * percent;
			sprite.look.setPositionInUserInterfaceDimensionUnit(currentX, currentY);
		}
	}

	@Override
	protected void end() {
	}

	public void setDuration(Formula duration) {
		this.duration = duration;
	}

	public void setPosition(Formula x, Formula y) {
		endX = x;
		endY = y;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public void physicsBeginHook() {
		// TODO[PHYSIC]

	}

	@Override
	public void physicsEndHook() {
		// TODO[PHYSIC]

	}
}
