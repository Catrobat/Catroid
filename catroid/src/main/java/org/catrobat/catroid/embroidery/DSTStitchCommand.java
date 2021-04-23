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

public class DSTStitchCommand implements StitchCommand {
	private final float xCoord;
	private final float yCoord;
	private int layer;
	private final Sprite sprite;
	Color threadColor;

	public DSTStitchCommand(float x, float y, int layer, Sprite sprite, Color threadColor) {
		this.xCoord = x;
		this.yCoord = y;
		this.layer = layer;
		this.sprite = sprite;
		this.threadColor = threadColor;
	}

	@Override
	public float getX() {
		return xCoord;
	}

	@Override
	public float getY() {
		return yCoord;
	}

	@Override
	public Color getColor() {
		return threadColor;
	}

	@Override
	public int getLayer() {
		return layer;
	}

	@Override
	public Sprite getSprite() {
		return sprite;
	}

	@Override
	public void act(EmbroideryWorkSpace workSpace, EmbroideryStream stream, StitchCommand previousCommandOfSprite) {
		if (xCoord == workSpace.getCurrentX() && yCoord == workSpace.getCurrentY() && sprite.equals(workSpace.getLastSprite())) {
			return;
		}

		if (workSpace.getLastSprite() != null && !sprite.equals(workSpace.getLastSprite())) {
			stream.addColorChange();
			stream.addStitchPoint(workSpace.getCurrentX(), workSpace.getCurrentY(), workSpace.getColor());
			if (DSTFileConstants.getMaxDistanceBetweenPoints(workSpace.getCurrentX(), workSpace.getCurrentY(),
					xCoord, yCoord) > DSTFileConstants.MAX_DISTANCE) {
				stream.addJump();
			}
			stream.addStitchPoint(workSpace.getCurrentX(), workSpace.getCurrentY(), workSpace.getColor());
		} else if (!stream.getPointList().isEmpty() && previousCommandOfSprite != null && previousCommandOfSprite.getLayer() != layer) {
			stream.addColorChange();
			if (DSTFileConstants.getMaxDistanceBetweenPoints(previousCommandOfSprite.getX(),
					previousCommandOfSprite.getY(), xCoord, yCoord) > DSTFileConstants.MAX_DISTANCE) {
				stream.addStitchPoint(xCoord, yCoord, threadColor);
			} else {
				stream.addStitchPoint(previousCommandOfSprite.getX(),
						previousCommandOfSprite.getY(), previousCommandOfSprite.getColor());
				stream.addJump();
				stream.addStitchPoint(previousCommandOfSprite.getX(),
						previousCommandOfSprite.getY(), previousCommandOfSprite.getColor());
			}
		}

		if (previousCommandOfSprite != null && previousCommandOfSprite.getLayer() != layer && DSTFileConstants.getMaxDistanceBetweenPoints(previousCommandOfSprite.getX(), previousCommandOfSprite.getY(), xCoord, yCoord) < DSTFileConstants.MAX_DISTANCE) {
			stream.addStitchPoint(previousCommandOfSprite.getX(), previousCommandOfSprite.getY(),
					previousCommandOfSprite.getColor());
		}
		stream.addStitchPoint(xCoord, yCoord, threadColor);
		workSpace.set(xCoord, yCoord, sprite);
	}

	public boolean equals(Object object) {
		if (!(object instanceof DSTStitchCommand)) {
			return false;
		}
		StitchCommand command = (DSTStitchCommand) object;
		return command.getLayer() == this.layer && command.getSprite().equals(this.sprite) && command.getX() == this.xCoord && command.getY() == this.yCoord;
	}

	public int hashCode() {
		int xPart = (int) this.xCoord & 0x3FFF;
		int yPart = ((int) this.yCoord & 0x3FFF) << 15;
		int layerPart = this.layer << 16;
		int spritePart = this.sprite.hashCode();
		return (xPart | yPart | layerPart) ^ spritePart;
	}
}
