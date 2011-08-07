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

	@XStreamOmitField
	private transient Bitmap costumeBitmap;

	public Costume(Sprite sprite, String imagePath) {
		this.sprite = sprite;
		this.changeImagePath(imagePath);
	}

	public synchronized void changeImagePath(String imagePath) {
		this.imagePath = imagePath;
		updateImage();
	}

	public synchronized void updateImage() {
		if (imagePath != null) {

			Bitmap buffer = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

			if (buffer != null) {

				double scaleFactor = sprite.getSize() / 100;
				int newHeight = (int) (buffer.getHeight() * scaleFactor);
				int newWidth = (int) (buffer.getWidth() * scaleFactor);

				buffer = ImageEditing.scaleBitmap(buffer, newWidth, newHeight);
				buffer = ImageEditing.rotateBitmap(buffer, (float) -(90 - sprite.getDirection()));
				buffer = ImageEditing.adjustOpacity(buffer, convertOpacity(sprite.getGhostEffectValue()));
				buffer = ImageEditing.adjustBrightness(buffer, convertBrightness(sprite.getBrightnessValue()));

				costumeBitmap = buffer;

				updatePosition();
			}
		}
	}

	public synchronized void updatePosition() {

		if (costumeBitmap != null) {
			float imageCenterX = toDeviceXCoordinate(sprite.getXPosition());
			float imageCenterY = toDeviceYCoordinate(sprite.getYPosition());

			drawPositionX = Math.round(imageCenterX - costumeBitmap.getWidth() / 2f);
			drawPositionY = Math.round(imageCenterY - costumeBitmap.getHeight() / 2f);
		}
	}

	public String getImagePath() {
		return imagePath;
	}

	public Bitmap getBitmap() {
		return costumeBitmap;
	}

	public int getDrawPositionX() {
		return this.drawPositionX;
	}

	public int getDrawPositionY() {
		return this.drawPositionY;
	}

	public int getImageWidth() {
		return costumeBitmap == null ? 0 : costumeBitmap.getWidth();
	}

	public int getImageHeight() {
		return costumeBitmap == null ? 0 : costumeBitmap.getHeight();
	}

	public double getVirtuelWidth() {
		return 2. * Consts.MAX_REL_COORDINATES / Values.SCREEN_WIDTH * costumeBitmap.getWidth();
	}

	public double getVirtuelHeight() {
		return 2. * Consts.MAX_REL_COORDINATES / Values.SCREEN_HEIGHT * costumeBitmap.getHeight();
	}

	private float toDeviceXCoordinate(int virtuelXCoordinate) {
		return (Values.SCREEN_WIDTH / 2f)
				+ ((Values.SCREEN_WIDTH / (2f * Consts.MAX_REL_COORDINATES)) * virtuelXCoordinate);
	}

	private float toDeviceYCoordinate(int virtuelYCoordinate) {
		return (Values.SCREEN_HEIGHT / 2f)
				- ((Values.SCREEN_HEIGHT / (2f * Consts.MAX_REL_COORDINATES)) * virtuelYCoordinate);
	}

	private int convertOpacity(double percent) {
		double calculation = Math.floor(255 - (percent * 2.55));
		int opacityValue = 0;
		//		 calculation: a value between 0 (completely transparent) and 255 (completely opaque).
		if (calculation > 0) {
			if (calculation >= 12) {
				opacityValue = (int) calculation; // Effect value from 0% to 95%
			} else {
				opacityValue = 12; // Effect value from 96% to 99%. Opacity Value more than 12 sprite would be untouchable.
			}
		} else if (calculation <= 0) {
			opacityValue = 0; //  Effect value 100%. Sprite untouchable.
		}

		return opacityValue;
	}

	private double convertBrightness(double percent) {
		double brightness = 0;

		if (percent > 100.0) {
			brightness = 200;
		} else if (percent <= -100.0) {
			brightness = -255;
		} else if (percent < 0.0 && percent > -100.0) {
			brightness = percent * 2.55;
		} else {
			brightness = percent * 2;
		}

		return brightness;
	}
}
