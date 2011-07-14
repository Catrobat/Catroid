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
	 * @param recycleOldBm
	 *            if true, the assigned bitmap at parameter bm will be recycled
	 *            after scaling
	 * @return a new, scaled bitmap
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap, int xSize, int ySize, boolean recycleOldBm) {
		Matrix matrix = new Matrix();
		float scaleWidth = (((float) xSize) / bitmap.getWidth());
		float scaleHeight = (((float) ySize) / bitmap.getHeight());
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		//		if (recycleOldBm)
		//			bm.recycle();
		return newBitmap;
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int xSize, int ySize) {
		return ImageEditing.scaleBitmap(bitmap, xSize, ySize, false);
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, double scalingFactor, boolean recycleOldBm) {
		return scaleBitmap(bitmap, (int) Math.round(bitmap.getWidth() * scalingFactor),
				(int) Math.round(bitmap.getHeight() * scalingFactor), recycleOldBm);
	}

	public static Bitmap getScaledBitmap(String imagePath, int outWidth, int outHeight) {
		if (imagePath == null) {
			return null;
		}

		int[] imageDim = new int[2];
		imageDim = getImageDimensions(imagePath);

		int origWidth = imageDim[0];
		int origHeight = imageDim[1];

		double sampleSizeWidth = (origWidth / (double) outWidth);
		double sampleSizeHeight = origHeight / (double) outHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
		int sampleSizeRounded = (int) Math.floor(sampleSize);

		int newHeight = (int) Math.ceil(origHeight / sampleSize);
		int newWidth = (int) Math.ceil(origWidth / sampleSize);

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = sampleSizeRounded;

		Bitmap tmpBitmap = BitmapFactory.decodeFile(imagePath, o);
		return scaleBitmap(tmpBitmap, newWidth, newHeight, true);
	}

	public static Bitmap getBitmap(String imagePath, int maxOutWidth, int maxOutHeight) {
		if (imagePath == null) {
			return null;
		}
		int[] imageDim = new int[2];

		imageDim = getImageDimensions(imagePath);

		double sampleSizeWidth = (imageDim[0] / (double) maxOutWidth);
		double sampleSizeHeight = imageDim[1] / (double) maxOutHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		if (sampleSize < 1) {
			return BitmapFactory.decodeFile(imagePath);
		}

		int sampleSizeRounded = (int) Math.floor(sampleSize);

		int newHeight = (int) Math.ceil(imageDim[1] / sampleSize);
		int newWidth = (int) Math.ceil(imageDim[0] / sampleSize);

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = sampleSizeRounded;

		Bitmap tmpBitmap = BitmapFactory.decodeFile(imagePath, o);
		return scaleBitmap(tmpBitmap, newWidth, newHeight, true);
	}

	public static int[] getImageDimensions(String imagePath) {
		int[] imageDim = new int[2];

		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, o);

		imageDim[0] = o.outWidth;
		imageDim[1] = o.outHeight;

		return imageDim;
	}

	public static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {
		Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(mutableBitmap);
		int colour = (opacity & 0xff) << 24;
		canvas.drawColor(colour, Mode.DST_IN);
		return mutableBitmap;
	}

	public static Bitmap adjustBrightness(Bitmap src, int value) {
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

}