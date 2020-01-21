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
package org.catrobat.catroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

import ar.com.hjg.pngj.IImageLine;
import ar.com.hjg.pngj.PngReader;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.PngjException;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkTextVar;

public final class ImageEditing {

	private static final String TAG = ImageEditing.class.getSimpleName();

	private static final int JPG_COMPRESSION_SETTING = 95;

	public enum ResizeType {
		STRETCH_TO_RECTANGLE, STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO
	}

	private ImageEditing() {
		throw new AssertionError();
	}

	private static Bitmap scaleBitmap(Bitmap bitmap, int xSize, int ySize) {
		if (bitmap == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		float scaleWidth = (((float) xSize) / bitmap.getWidth());
		float scaleHeight = (((float) ySize) / bitmap.getHeight());
		matrix.postScale(scaleWidth, scaleHeight);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap getScaledBitmapFromPath(String imagePath, int outputRectangleWidth, int outputRectangleHeight,
			ResizeType resizeType, boolean justScaleDown) {
		if (imagePath == null) {
			return null;
		}
		int[] imageDimensions = getImageDimensions(imagePath);
		int originalWidth = imageDimensions[0];
		int originalHeight = imageDimensions[1];

		int[] scaledImageDimensions = getScaledImageDimensions(originalWidth, originalHeight, outputRectangleWidth,
				outputRectangleHeight, resizeType, justScaleDown);
		int newWidth = scaledImageDimensions[0];
		int newHeight = scaledImageDimensions[1];

		int loadingSampleSize = calculateInSampleSize(originalWidth, originalHeight, outputRectangleWidth,
				outputRectangleHeight);

		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = loadingSampleSize;
		bitmapOptions.inJustDecodeBounds = false;
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

	private static int[] getScaledImageDimensions(int originalWidth, int originalHeight, int outputRectangleWidth, int
			outputRectangleHeight,
			ResizeType resizeType, boolean justScaleDown) {
		int newWidth = originalWidth;
		int newHeight = originalHeight;

		double sampleSizeWidth = ((double) originalWidth) / (double) outputRectangleWidth;
		double sampleSizeHeight = ((double) originalHeight) / (double) outputRectangleHeight;
		double sampleSizeMinimum = Math.min(sampleSizeWidth, sampleSizeHeight);
		double sampleSizeMaximum = Math.max(sampleSizeWidth, sampleSizeHeight);

		if (!justScaleDown || originalHeight >= outputRectangleHeight || originalWidth >= outputRectangleWidth) {
			if (resizeType == ResizeType.STRETCH_TO_RECTANGLE) {
				newWidth = outputRectangleWidth;
				newHeight = outputRectangleHeight;
			} else if (resizeType == ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO) {
				newWidth = (int) Math.floor(originalWidth / sampleSizeMaximum);
				newHeight = (int) Math.floor(originalHeight / sampleSizeMaximum);
			} else if (resizeType == ResizeType.FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO) {
				newWidth = (int) Math.floor(originalWidth / sampleSizeMinimum);
				newHeight = (int) Math.floor(originalHeight / sampleSizeMinimum);
			}
		}
		int[] scaledImageDimensions = new int[2];
		scaledImageDimensions[0] = newWidth;
		scaledImageDimensions[1] = newHeight;
		return scaledImageDimensions;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int rotationDegree) {
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.postRotate(rotationDegree);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
	}

	public static void scaleImageFile(File file, double scaleFactor) throws FileNotFoundException {
		String path = file.getAbsolutePath();
		int[] originalBackgroundImageDimensions = getImageDimensions(path);
		scaleImageFile(file,
				(int) (originalBackgroundImageDimensions[0] * scaleFactor),
				(int) (originalBackgroundImageDimensions[1] * scaleFactor));
	}

	public static void scaleImageFile(File file, int width, int height) throws FileNotFoundException {
		String path = file.getAbsolutePath();
		Bitmap scaledBitmap = ImageEditing.getScaledBitmapFromPath(path, width, height,
				ImageEditing.ResizeType.FILL_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);
		saveBitmapToImageFile(file, scaledBitmap);
	}

	public static double calculateScaleFactor(int originalWidth, int originalHeight, int newWidth, int newHeight) {
		if (originalHeight == 0 || originalWidth == 0 || newHeight == 0 || newWidth == 0) {
			throw new IllegalArgumentException("One or more values are 0");
		}
		double widthScaleFactor = ((double) newWidth) / ((double) originalWidth);
		double heightScaleFactor = ((double) newHeight) / ((double) originalHeight);

		return Math.max(widthScaleFactor, heightScaleFactor);
	}

	private static int calculateInSampleSize(int origWidth, int origHeight, int reqWidth, int reqHeight) {
		int inSampleSize = 1;

		if (origHeight > reqHeight || origWidth > reqWidth) {
			final int halfHeight = origHeight / 2;
			final int halfWidth = origWidth / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static void saveBitmapToImageFile(File outputFile, Bitmap bitmap) throws FileNotFoundException {
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		try {
			if (outputFile.getName().toLowerCase(Locale.US).endsWith(".jpg")
					|| outputFile.getName().toLowerCase(Locale.US).endsWith(".jpeg")) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, JPG_COMPRESSION_SETTING, outputStream);
			} else {
				bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
			}
			outputStream.flush();
		} catch (IOException e) {
			Log.e(TAG, Log.getStackTraceString(e));
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				Log.e(TAG, "Could not close OutputStream.", e);
			}
		}
	}

	public static String readMetaDataStringFromPNG(String absolutePath, String key) throws PngjException {
		File image = new File(absolutePath);
		PngReader pngr = new PngReader(image);
		pngr.readSkippingAllRows();
		for (PngChunk c : pngr.getChunksList().getChunks()) {
			if (!ChunkHelper.isText(c)) {
				continue;
			}
			PngChunkTextVar ct = (PngChunkTextVar) c;
			String k = ct.getKey();
			String val = ct.getVal();
			if (key.equals(k)) {
				pngr.close();
				return val;
			}
		}
		pngr.close();
		return "";
	}

	public static synchronized void writeMetaDataStringToPNG(String absolutePath, String key, String value) {
		String tempFilename = absolutePath.substring(0, absolutePath.length() - 4) + "___temp.png";

		File oldFile = new File(absolutePath);
		File newFile = new File(tempFilename);

		PngReader pngr = new PngReader(oldFile);
		PngWriter pngw = new PngWriter(newFile, pngr.imgInfo, true);
		pngw.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL);
		pngw.getMetadata().setText(key, value);
		for (int row = 0; row < pngr.imgInfo.rows; row++) {
			IImageLine l1 = pngr.readRow();
			pngw.writeRow(l1);
		}
		pngr.end();
		pngw.end();

		if (!oldFile.delete()) {
			Log.e(TAG, "writeMetaDataStringToPNG: Failed to delete old file");
		}
		if (!newFile.renameTo(new File(absolutePath))) {
			Log.e(TAG, "writeMetaDataStringToPNG: Failed to rename new file");
		}
	}

	public static boolean isPixelTransparent(int[] pixels, int width, int x, int y) {
		return pixels[x + (y * width)] == Color.TRANSPARENT;
	}
}
