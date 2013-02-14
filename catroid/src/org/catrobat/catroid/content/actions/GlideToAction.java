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

import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class GlideToAction extends TemporalAction {

	private float startX, startY;
	private float endX, endY;

	@Override
	protected void begin() {
		startX = actor.getX() + actor.getWidth() / 2f;
		startY = actor.getY() + actor.getHeight() / 2f;
		if (Float.compare(startX, endX) == 0 && Float.compare(startY, endY) == 0) {
			super.finish();
		}
	}

	@Override
	protected void update(float percent) {
		actor.setPosition(startX + (endX - startX) * percent - actor.getWidth() / 2f, startY + (endY - startY)
				* percent - actor.getHeight() / 2f);
	}

	public void setPosition(float x, float y) {
		endX = x;
		endY = y;
	}

	public float getX() {
		return endX;
	}

	public void setX(float x) {
		endX = x;
	}

	public float getY() {
		return endY;
	}

	public void setY(float y) {
		endY = y;
	}
}
