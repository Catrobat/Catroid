/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff.Mode;

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
		Matrix matrix = new Matrix();
		float scaleWidth = (((float) xSize) / bitmap.getWidth());
		float scaleHeight = (((float) ySize) / bitmap.getHeight());
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return newBitmap;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, double scalingFactor) {
		return scaleBitmap(bitmap, (int) Math.round(bitmap.getWidth() * scalingFactor),
				(int) Math.round(bitmap.getHeight() * scalingFactor));
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, float rotation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation);

		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		return newBitmap;
	}

	public static Bitmap getScaledBitmap(String imagePath, int outWidth, int outHeight) {
		if (imagePath == null) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = getImageDimensions(imagePath);

		int origWidth = imageDimensions[0];
		int origHeight = imageDimensions[1];

		double sampleSizeWidth = (origWidth / (double) outWidth);
		double sampleSizeHeight = origHeight / (double) outHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
		int sampleSizeRounded = (int) Math.floor(sampleSize);

		int newHeight = (int) Math.ceil(origHeight / sampleSize);
		int newWidth = (int) Math.ceil(origWidth / sampleSize);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = sampleSizeRounded;

		Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
		return scaleBitmap(tempBitmap, newWidth, newHeight);
	}

	public static Bitmap getBitmap(String imagePath, int maxOutWidth, int maxOutHeight) {
		if (imagePath == null) {
			return null;
		}
		int[] imageDimensions = new int[2];

		imageDimensions = getImageDimensions(imagePath);

		double sampleSizeWidth = (imageDimensions[0] / (double) maxOutWidth);
		double sampleSizeHeight = imageDimensions[1] / (double) maxOutHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		if (sampleSize < 1) {
			return BitmapFactory.decodeFile(imagePath);
		}

		int sampleSizeRounded = (int) Math.floor(sampleSize);

		int newHeight = (int) Math.ceil(imageDimensions[1] / sampleSize);
		int newWidth = (int) Math.ceil(imageDimensions[0] / sampleSize);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = sampleSizeRounded;

		Bitmap tmpBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
		return scaleBitmap(tmpBitmap, newWidth, newHeight);
	}

	public static int[] getImageDimensions(String imagePath) {
		int[] imageDimensions = new int[2];

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, o);

		imageDimensions[0] = o.outWidth;
		imageDimensions[1] = o.outHeight;

		return imageDimensions;
	}

	public static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
		Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);
		int colour = (opacity & 0xff) << 24;
		canvas.drawColor(colour, Mode.DST_IN);
		return mutableBitmap;
	}

	public static Bitmap adjustBrightness(Bitmap source, double value) {
		if (value == 0.0) {
			return source;
		}

		// image size
		int width = source.getWidth();
		int height = source.getHeight();

		// create output bitmap if necessary
		Bitmap bitmap = source.isMutable() ? source : source.copy(source.getConfig(), true);

		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = bitmap.getPixel(x, y);
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
				bitmap.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bitmap;
	}
}
