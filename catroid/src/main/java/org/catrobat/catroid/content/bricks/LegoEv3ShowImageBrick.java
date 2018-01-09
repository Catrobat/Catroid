/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.content.bricks;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LegoImageLookData;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.utils.ImageEditing;

import java.util.Collections;
import java.util.List;

public class LegoEv3ShowImageBrick extends SetLookBrick {
	private static final transient int MAX_LEGO_IMAGE_WIDTH = 178;
	private static final transient int MAX_LEGO_IMAGE_HEIGHT = 128;

	public LegoEv3ShowImageBrick() {
	}

	@Override
	protected Sprite getSprite() {
		return ProjectManager.getInstance().getCurrentScene().getSpriteList().get(0);
	}

	@Override
	public int getRequiredResources() {
		return BLUETOOTH_LEGO_EV3;
	}

	@Override
	protected int getTextForView() {
		return R.string.ev3_show_image;
	}

	@Override
	public Brick clone() {
		LegoEv3ShowImageBrick clonedBrick = new LegoEv3ShowImageBrick();
		clonedBrick.setLook(look);
		return clonedBrick;
	}

	@Override
	public List<SequenceAction> addActionToSequence(Sprite sprite, SequenceAction sequence) {
		Sprite backgroundSprite = ProjectManager.getInstance().getSceneToPlay().getSpriteList().get(0);
		sequence.addAction(sprite.getActionFactory().createLegoEv3ShowImageAction(backgroundSprite, look));
		return Collections.emptyList();
	}

	@Override
	public void onLookDataListChangedAfterNew(LookData lookData) {
		look = lookData;
		oldSelectedLook = lookData;
		((LegoImageLookData) look).setLookRgf(convertLookDataToRgf());
		((LegoImageLookData) oldSelectedLook).setLookRgf(((LegoImageLookData) look).getLookRgf());
	}

	private byte[] convertLookDataToRgf() {
		Bitmap tmpBitmap = ImageEditing.getScaledBitmapFromPath(look.getAbsolutePath(), MAX_LEGO_IMAGE_WIDTH,
				MAX_LEGO_IMAGE_HEIGHT, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
		int imageWidth = tmpBitmap.getWidth();
		int imageHeight = tmpBitmap.getHeight();
		int threshold = calcThreshold(tmpBitmap, imageWidth, imageHeight);

		int centerX = (MAX_LEGO_IMAGE_WIDTH - imageWidth) / 2;
		int centerY = (MAX_LEGO_IMAGE_HEIGHT - imageHeight) / 2;
		Matrix matrix = new Matrix();
		matrix.setTranslate(centerX, centerY);
		imageWidth = (int) Math.ceil((double) MAX_LEGO_IMAGE_WIDTH / 8) * 8;
		imageHeight = (int) Math.ceil((double) MAX_LEGO_IMAGE_HEIGHT / 8) * 8;
		Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(tmpBitmap, matrix, null);
		tmpBitmap.recycle();

		int contentLength = (imageWidth * imageHeight) / 8 + 2;
		byte[] rgf = new byte[contentLength];
		rgf[0] = (byte) MAX_LEGO_IMAGE_WIDTH;
		rgf[1] = (byte) MAX_LEGO_IMAGE_HEIGHT;
		int p;
		int r;
		int g;
		int b;
		int gray;
		int iPixel = 0;
		int iByte = 1;
		for (int height = 0; height < imageHeight; height++) {
			for (int width = 0; width < imageWidth; width++) {
				if (iPixel % 8 == 0) {
					iByte++;
				}
				if (iByte < contentLength) {
					p = bitmap.getPixel(width, height);
					r = (p >> 16) & 0xFF;
					g = (p >> 8) & 0xFF;
					b = p & 0xFF;
					gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
					rgf[iByte] = setBit(rgf[iByte], iPixel % 8, (gray < threshold) ? 1 : 0);
				}
				iPixel++;
			}
		}
		bitmap.recycle();
		return rgf;
	}

	private int calcThreshold(Bitmap bitmap, int width, int height) {
		int p;
		int r = 0;
		int g = 0;
		int b = 0;
		int iPixels = width * height;
		for (int x = 0; x < width; x = x + 1) {
			for (int y = 0; y < height; y = y + 1) {
				p = bitmap.getPixel(x, y);
				r += (p >> 16) & 0xFF;
				g += (p >> 8) & 0xFF;
				b += p & 0xFF;
			}
		}
		return (int) (0.299 * r / iPixels + 0.587 * g / iPixels + 0.114 * b / iPixels);
	}

	private static byte setBit(byte number, int index, int value) {
		if ((index >= 0) && (index < 8)) {
			if (value == 0) {
				return (byte) (number & ~(1 << index));
			} else {
				return (byte) (number | (1 << index));
			}
		}
		return number;
	}
}
