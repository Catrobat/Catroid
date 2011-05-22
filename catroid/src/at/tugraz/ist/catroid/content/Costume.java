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
import android.graphics.BitmapFactory;
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

	private int resourceId;

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
		setDimensions();
		setDrawPosition();
	}

	public synchronized void setBitmapFromRes(int resourceId) {
		imagePath = null;
		this.resourceId = resourceId;

		BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
		boundsOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(Values.NATIVE_APP_CONTEXT.getResources(), resourceId, boundsOptions);

		double sampleSizeWidth = boundsOptions.outWidth / (double) Values.SCREEN_WIDTH;
		double sampleSizeHeight = boundsOptions.outHeight / (double) Values.SCREEN_HEIGHT;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		if (sampleSize > 1) {
			int sampleSizeRounded = (int) Math.floor(sampleSize);

			int newHeight = (int) Math.ceil(costumeBitmap.getWidth() / sampleSize);
			int newWidth = (int) Math.ceil(costumeBitmap.getDensity() / sampleSize);

			BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
			scaleOptions.inSampleSize = sampleSizeRounded;

			Bitmap tmpBitmap = BitmapFactory.decodeResource(Values.NATIVE_APP_CONTEXT.getResources(), resourceId,
					scaleOptions);
			costumeBitmap = ImageEditing.scaleBitmap(tmpBitmap, newWidth, newHeight, true);
		} else {
			costumeBitmap = BitmapFactory.decodeResource(Values.NATIVE_APP_CONTEXT.getResources(), resourceId);
		}

		setDimensions();
		setDrawPosition();
	}

	public synchronized void scale(double scaleFactorPercent) {
		if (costumeBitmap == null || (imagePath == null && Values.RUNNING_AS_NATIVE_APP == false)) {
			return;
		}

		double scaleFactor = scaleFactorPercent / 100;
		int newHeight = (int) (origHeight * scaleFactor);
		int newWidth = (int) (origWidth * scaleFactor);

		setPositionToSpriteTopLeft();

		if (newHeight > actHeight || newWidth > actWidth) {
			//costumeBitmap.recycle();
			if (Values.RUNNING_AS_NATIVE_APP == false) {
				costumeBitmap = ImageEditing.getBitmap(imagePath, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
			} else {
				costumeBitmap = BitmapFactory.decodeResource(Values.NATIVE_APP_CONTEXT.getResources(), resourceId);
			}
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

	public synchronized void setDimensions() {
		actHeight = costumeBitmap.getHeight();
		actWidth = costumeBitmap.getWidth();

		origHeight = costumeBitmap.getHeight();
		origWidth = costumeBitmap.getWidth();
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

}
