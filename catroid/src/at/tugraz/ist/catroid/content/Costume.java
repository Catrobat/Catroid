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
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.util.Pair;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.ui.CostumeActivity.costumeData;
import at.tugraz.ist.catroid.utils.ImageEditing;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Costume implements Serializable {
	private static final long serialVersionUID = 1L;
	private String imagePath;
	private Sprite sprite;
	private ArrayList<costumeData> costumeList;
	private int drawPositionX;
	private int drawPositionY;
	private int actHeight;
	private int actWidth;
	private int origHeight;
	private int origWidth;
	private String costumeName, costumeImage;

	@XStreamOmitField
	private transient Bitmap costumeBitmap, thumbnail;

	public Costume(Sprite sprite, String imagePath) {
		this.sprite = sprite;
		this.setImagePath(imagePath);
		costumeList = new ArrayList<costumeData>();
	}

	public Costume(String costumeName, String costumeImage) {
		this.costumeName = costumeName;
		this.costumeImage = costumeImage;

		if (costumeImage != null) {
			thumbnail = ImageEditing.getScaledBitmap(getAbsoluteImagePath(), Consts.THUMBNAIL_HEIGHT,
										Consts.THUMBNAIL_WIDTH);
		}
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

	public String getCostumeName() {
		return costumeName;
	}

	public Bitmap getCostumeImage() {
		return thumbnail;
	}

	private String getAbsoluteImagePath() {
		return Consts.DEFAULT_ROOT + "/" + ProjectManager.getInstance().getCurrentProject().getName()
							+ Consts.IMAGE_DIRECTORY + "/" + costumeImage;
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

	public ArrayList<costumeData> getCostumeList() {
		return costumeList;
	}

}
