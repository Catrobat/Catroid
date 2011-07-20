/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.util.Pair;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.utils.ImageEditing;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Costume implements Serializable {
	private static final long serialVersionUID = 1L;
	private String imagePath;
	private Sprite sprite;
	private int drawPositionX;
	private int drawPositionY;
	private int actualHeight;
	private int actualWidth;
	private int originalHeight;
	private int originalWidth;

	@XStreamOmitField
	private transient Bitmap costumeBitmap;

	public Costume(Sprite sprite, String imagePath) {
		this.sprite = sprite;
		this.setImagePath(imagePath);
	}

	public synchronized void setImagePath(String imagePath) {

		this.imagePath = imagePath;
		costumeBitmap = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		if (costumeBitmap == null) {
			return;
		}

		actualHeight = costumeBitmap.getHeight();
		actualWidth = costumeBitmap.getWidth();

		originalHeight = costumeBitmap.getHeight();
		originalWidth = costumeBitmap.getWidth();
		setSizeTo(sprite.getSize());
		rotateTo(sprite.getDirection());
		setDrawPosition();
	}

	public synchronized void setSizeTo(double size) {
		if (imagePath == null) {
			return;
		}

		double scaleFactor = size / 100;
		int newHeight = (int) (originalHeight * scaleFactor);
		int newWidth = (int) (originalWidth * scaleFactor);

		setPositionToSpriteTopLeft();

		costumeBitmap = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

		if (costumeBitmap == null) {
			return;
		}

		costumeBitmap = ImageEditing.scaleBitmap(costumeBitmap, newWidth, newHeight);
		costumeBitmap = ImageEditing.rotateBitmap(costumeBitmap, (float) -(90 - sprite.getDirection()));

		actualWidth = newWidth;
		actualHeight = newHeight;

		setPositionToSpriteCenter();

		return;
	}

	public synchronized void rotateTo(double degrees) {
		if (imagePath == null) {
			return;
		}

		costumeBitmap = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

		if (costumeBitmap == null) {
			return;
		}

		costumeBitmap = ImageEditing.scaleBitmap(costumeBitmap, actualWidth, actualHeight);
		costumeBitmap = ImageEditing.rotateBitmap(costumeBitmap, (float) -(90 - degrees));

		setDrawPosition();
	}

	public Bitmap getBitmap() {
		return costumeBitmap;
	}

	public synchronized void setDrawPosition() {

		setPositionToSpriteTopLeft();
		drawPositionX = Math.round(((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getXPosition())
				+ Values.SCREEN_WIDTH / 2f);
		drawPositionY = Math.round((Values.SCREEN_HEIGHT / 2f)
				- ((Values.SCREEN_HEIGHT / (2f * Consts.MAX_REL_COORDINATES)) * sprite.getYPosition()));
		setPositionToSpriteCenter();
	}

	public int getDrawPositionX() {
		return this.drawPositionX;
	}

	public int getDrawPositionY() {
		return this.drawPositionY;
	}

	public Pair<Integer, Integer> getImageWidthHeight() {
		return new Pair<Integer, Integer>(actualWidth, actualHeight);
	}

	public double getRelativeBoundingBoxWidth() {
		return 2. * Consts.MAX_REL_COORDINATES / Values.SCREEN_WIDTH * costumeBitmap.getWidth();
	}

	public double getRelativeBoundingBoxHeight() {
		return 2. * Consts.MAX_REL_COORDINATES / Values.SCREEN_HEIGHT * costumeBitmap.getHeight();
	}

	private synchronized void setPositionToSpriteCenter() {
		if (costumeBitmap == null) {
			return;
		}
		drawPositionX = drawPositionX - costumeBitmap.getWidth() / 2;
		drawPositionY = drawPositionY - costumeBitmap.getHeight() / 2;
	}

	private synchronized void setPositionToSpriteTopLeft() {
		if (costumeBitmap == null) {
			return;
		}
		drawPositionX = drawPositionX + costumeBitmap.getWidth() / 2;
		drawPositionY = drawPositionY + costumeBitmap.getHeight() / 2;
	}
}
