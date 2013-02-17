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
package org.catrobat.catroid.utils;

import java.io.File;
import java.io.FileNotFoundException;

import org.catrobat.catroid.io.StorageHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageEditing {

	public ImageEditing() {

	}

	/**
	 * Scales the bitmap to the specified size.
	 * 
	 * @param bitmap
	 *            the bitmap to resize
	 * @param xSize
	 *            desired x size
	 * @param ySize
	 *            desired y size
	 * @return a new, scaled bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int xSize, int ySize) {
		if (bitmap == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		float scaleWidth = (((float) xSize) / bitmap.getWidth());
		float scaleHeight = (((float) ySize) / bitmap.getHeight());
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	public static Bitmap getScaledBitmapFromPath(String imagePath, int outputWidth, int outputHeight, boolean justScaleDown) {
		if (imagePath == null) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = getImageDimensions(imagePath);

		int originalWidth = imageDimensions[0];
		int originalHeight = imageDimensions[1];

		double sampleSizeWidth = (originalWidth / (double) outputWidth);
		double sampleSizeHeight = originalHeight / (double) outputHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
		int sampleSizeRounded = (int) Math.floor(sampleSize);

		if (justScaleDown && sampleSize <= 1) {
			return BitmapFactory.decodeFile(imagePath);
		}

		int newHeight = (int) Math.ceil(originalHeight / sampleSize);
		int newWidth = (int) Math.ceil(originalWidth / sampleSize);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = sampleSizeRounded;

		Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
		return scaleBitmap(tempBitmap, newWidth, newHeight);
	}

	public static int[] getImageDimensions(String imagePath) {
		int[] imageDimensions = new int[2];

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, options);

		imageDimensions[0] = options.outWidth;
		imageDimensions[1] = options.outHeight;

		return imageDimensions;
	}

	public static Bitmap createSingleColorBitmap(int width, int height, int color) {
		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		newBitmap.eraseColor(color);
		return newBitmap;
	}

	public static void overwriteImageFileWithNewBitmap(File imageFile) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		Bitmap immutableBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
		int bitmapWidth = immutableBitmap.getWidth();
		int bitmapHeight = immutableBitmap.getHeight();
		int[] bitmapPixels = new int[bitmapWidth * bitmapHeight];
		immutableBitmap.getPixels(bitmapPixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);

		Bitmap mutableBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
		mutableBitmap.setPixels(bitmapPixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapHeight);
		StorageHandler.saveBitmapToImageFile(imageFile, mutableBitmap);
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegree) {
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.postRotate(rotationDegree);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix,
				true);
		return rotatedBitmap;
	}
}
