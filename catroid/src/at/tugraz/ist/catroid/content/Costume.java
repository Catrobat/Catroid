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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
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
	private int actHeight;
	private int actWidth;
	private int origHeight;
	private int origWidth;

	@XStreamOmitField
	private Bitmap costumeBitmap;

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

		actHeight = costumeBitmap.getHeight();
		actWidth = costumeBitmap.getWidth();

		origHeight = costumeBitmap.getHeight();
		origWidth = costumeBitmap.getWidth();

		setDrawPosition();
	}

	public synchronized void scale(double scaleFactorPercent) {
		if (costumeBitmap == null || imagePath == null) {
			return;
		}

		double scaleFactor = scaleFactorPercent / 100;
		int newHeight = (int) (origHeight * scaleFactor);
		int newWidth = (int) (origWidth * scaleFactor);

		setPositionToSpriteTopLeft();

		if (newHeight > actHeight || newWidth > actWidth) {
			//costumeBitmap.recycle();
			costumeBitmap = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
		}

		costumeBitmap = ImageEditing.scaleBitmap(costumeBitmap, newWidth, newHeight, true);

		actWidth = newWidth;
		actHeight = newHeight;

		setPositionToSpriteCenter();

		return;
	}

	public String getImagePath() {
		return imagePath;
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
		return new Pair<Integer, Integer>(actWidth, actHeight);
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

	public synchronized void setGhostEffect(int effectVal) {
		if (costumeBitmap == null) {
			return;
		}

		costumeBitmap = ImageEditing.getScaledBitmap(imagePath, actWidth, actHeight);
		costumeBitmap = adjustOpacity(costumeBitmap, effectVal);
		return;
	}

	private static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
		Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);
		int colour = (opacity & 0xFF) << 24;
		System.out.println("1st" + colour);
		canvas.drawColor(colour, Mode.DST_IN);
		return mutableBitmap;
	}

	public synchronized void setBrightness(int brightness) {
		if (costumeBitmap == null) {
			return;
		}

		costumeBitmap = ImageEditing.getScaledBitmap(imagePath, actWidth, actHeight);
		costumeBitmap = adjustBrightness(costumeBitmap, brightness);

		return;
	}

	private static Bitmap adjustBrightness(Bitmap src, int value) {
		// image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public synchronized void clearGraphicEffect() {
		if (costumeBitmap == null) {
			return;
		}

		costumeBitmap = ImageEditing.getScaledBitmap(imagePath, actWidth, actHeight);
		return;
	}
}
