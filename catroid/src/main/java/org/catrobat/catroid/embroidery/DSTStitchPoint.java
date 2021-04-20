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

import java.io.FileOutputStream;
import java.io.IOException;

import static org.catrobat.catroid.embroidery.DSTFileConstants.toEmbroideryUnit;

public class DSTStitchPoint implements StitchPoint {

	private float xCoord;
	private float yCoord;
	private int relativeX;
	private int relativeY;
	private boolean jumpPoint = false;
	private boolean colorChangePoint = false;
	private Color color;

	public DSTStitchPoint(float x, float y, Color color) {
		this.xCoord = x;
		this.yCoord = y;
		this.color = color;
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
		return color;
	}

	@Override
	public void appendToStream(FileOutputStream fileStream) throws IOException {
		byte[] dstPointBytes = setDSTPointBytes();
		fileStream.write(dstPointBytes);
	}

	@Override
	public void setJump(boolean jumpPoint) {
		this.jumpPoint = jumpPoint;
	}

	@Override
	public boolean isJumpPoint() {
		return jumpPoint;
	}

	@Override
	public void setColorChange(boolean colorChangePoint) {
		this.colorChangePoint = colorChangePoint;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public boolean isColorChangePoint() {
		return colorChangePoint;
	}

	@Override
	public void setRelativeCoordinatesToPreviousPoint(float previousX, float previousY) {
		relativeX = toEmbroideryUnit(xCoord) - toEmbroideryUnit(previousX);
		relativeY = toEmbroideryUnit(yCoord) - toEmbroideryUnit(previousY);
	}

	private int mapToConversionTable(int value) {
		return (value < 0 ? DSTFileConstants.CONVERSION_TABLE[(value * (-1)) + 121]
				: DSTFileConstants.CONVERSION_TABLE[value]);
	}

	private byte[] setDSTPointBytes() {
		byte[] dstPointBytes = new byte[] {0, 0, (char) 0x03};
		int xValue = mapToConversionTable(relativeX);
		int yValue = mapToConversionTable(relativeY);

		char yPart = (char) ((((yValue & 0x1) << 3) | ((yValue & 0x2) << 1) | ((yValue & 0x10) >>> 3)
				| ((yValue & 0x20) >>> 5)) << 4);
		char xPart = (char) (((xValue >>> 2) & 0xC) | (xValue & 0x3));
		dstPointBytes[0] = (byte) (yPart | xPart);

		yPart = (char) ((((yValue & 0x4) << 1) | ((yValue & 0x8) >>> 1) | ((yValue & 0x40) >>> 5)
				| ((yValue & 0x80) >>> 7)) << 4);
		xPart = (char) (((xValue >>> 4) & 0xC) | ((xValue >>> 2) & 0x3));
		dstPointBytes[1] = (byte) (yPart | xPart);

		yPart = (char) (((yValue >>> 5) & 0x10) | ((yValue >>> 3) & 0x20));
		xPart = (char) ((xValue >>> 6) & 0xC);
		dstPointBytes[2] = (byte) (dstPointBytes[2] | yPart | xPart);
		if (jumpPoint) {
			dstPointBytes[2] = (byte) (dstPointBytes[2] | (0x1 << 7));
		}
		if (colorChangePoint) {
			dstPointBytes[2] = (byte) ((char) dstPointBytes[2] | (0x1 << 6) | (0x1 << 7));
		}
		return dstPointBytes;
	}

	@Override
	public boolean isConnectingPoint() {
		return !jumpPoint && !colorChangePoint;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof DSTStitchPoint)) {
			return false;
		}
		DSTStitchPoint stitchPoint = (DSTStitchPoint) object;
		return stitchPoint.getX() == xCoord && stitchPoint.getY() == yCoord && this.isConnectingPoint() && stitchPoint.isConnectingPoint();
	}

	@Override
	public int hashCode() {
		int xPart = (int) this.xCoord & 0x3FFF;
		int yPart = ((int) this.yCoord & 0x3FFF) << 15;
		int typePart = 0;
		if (jumpPoint) {
			typePart = 0x4000 << 15;
		}
		if (colorChangePoint) {
			typePart = 0xC000 << 15;
		}
		return xPart | yPart | typePart;
	}
}
