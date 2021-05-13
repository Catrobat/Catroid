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

package org.catrobat.catroid.embroidery;

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.content.Sprite;

public class DSTWorkSpace implements EmbroideryWorkSpace {

	private float currentX;
	private float currentY;
	private Sprite lastSprite;
	private Color threadColor;

	@Override
	public void set(float currentX, float currentY, Sprite currentSprite) {
		this.currentX = currentX;
		this.currentY = currentY;
		this.lastSprite = currentSprite;
	}

	@Override
	public float getCurrentX() {
		return currentX;
	}

	@Override
	public float getCurrentY() {
		return currentY;
	}

	@Override
	public Color getColor() {
		return threadColor;
	}

	@Override
	public Sprite getLastSprite() {
		return lastSprite;
	}
}
