/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.formulaeditor.Formula;

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class GlideToAction extends TemporalAction {

	private float startX, startY;
	private float currentX, currentY;
	private Formula endX, endY;
	private Sprite sprite;
	private Formula duration;
	private float endXValue;
	private float endYValue;

	@Override
	protected void begin() {
		if (duration != null) {
			super.setDuration((float) duration.interpretDouble(sprite));
		}

		startX = actor.getX() + actor.getWidth() / 2f;
		startY = actor.getY() + actor.getHeight() / 2f;
		endXValue = endX.interpretFloat(sprite);
		endYValue = endY.interpretFloat(sprite);
		currentX = startX;
		currentY = startY;
		if (Float.compare(startX, endX.interpretFloat(sprite)) == 0
				&& Float.compare(startY, endY.interpretFloat(sprite)) == 0) {
			super.finish();
		}
	}

	@Override
	protected void update(float percent) {

		float deltaX = actor.getX() + actor.getWidth() / 2f - currentX;
		float deltaY = actor.getY() + actor.getHeight() / 2f - currentY;
		if ((-0.1f > deltaX || deltaX > 0.1f) || (-0.1f > deltaY || deltaY > 0.1f)) {
			float currentDuration = getDuration() - getTime();
			restart();
			setDuration(currentDuration);
		} else {
			currentX = startX + (endXValue - startX) * percent;
			currentY = startY + (endYValue - startY) * percent;
			actor.setPosition(currentX - actor.getWidth() / 2f, currentY - actor.getHeight() / 2f);
		}
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
}
