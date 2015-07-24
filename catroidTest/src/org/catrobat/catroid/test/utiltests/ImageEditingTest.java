/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.utiltests;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

import junit.framework.TestCase;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.utils.ImageEditing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageEditingTest extends TestCase {
	private static final String TAG = ImageEditingTest.class.getSimpleName();

	public void testScaleImage() {
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		Bitmap scaledBitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(
				bitmap, 60, 70));

		assertEquals("Wrong bitmap width after scaling", 60, scaledBitmap.getWidth());
		assertEquals("Wrong bitmap height after scaling", 70, scaledBitmap.getHeight());
	}

	public void testGetImageDimensions() {
		File testImageFile = new File(Constants.DEFAULT_ROOT, "tmp.jpg");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(testImageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e(TAG, "Test file could not be created", e);
			fail("Test file could not be created");
		}

		int[] dimensions = new int[2];

		dimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());

		assertEquals("Wrong image width", 100, dimensions[0]);
		assertEquals("Wrong image height", 200, dimensions[1]);
	}

	public void testGetBitmap() {
		int maxBitmapWidth = 500;
		int maxBitmapHeight = 500;

		int bitmapWidth = 100;
		int bitmapHeight = 200;

		File testImageFile = new File(Constants.DEFAULT_ROOT, "tmp.jpg");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(testImageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e(TAG, "Test file could not be created", e);
			fail("Test file could not be created");
		}

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), maxBitmapWidth,
				maxBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);

		assertEquals("Loaded bitmap has incorrect height", 500, loadedBitmap.getHeight());
		assertEquals("Loaded bitmap has incorrect width", 250, loadedBitmap.getWidth());

		bitmapWidth = 600;
		bitmapHeight = 800;

		double sampleSizeWidth = (bitmapWidth / (double) maxBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) maxBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);

		fileOutputStream = null;

		bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(testImageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e(TAG, "Test file could not be created", e);
			fail("Test file could not be created");
		}

		loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), maxBitmapWidth,
				maxBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
		bitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(bitmap,
				newWidth, newHeight));

		assertEquals("Loaded bitmap has incorrect height", bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals("Loaded bitmap has incorrect width", bitmap.getWidth(), loadedBitmap.getWidth());
	}

	public void testGetScaledBitmap() {
		int targetBitmapWidth = 300;
		int targetBitmapHeight = 500;

		int bitmapWidth = 1000;
		int bitmapHeight = 900;

		File testImageFile = new File(Constants.DEFAULT_ROOT, "tmp.jpg");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(testImageFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			Log.e(TAG, "Test file could not be created", e);
			fail("Test file could not be created");
		}

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), targetBitmapWidth,
				targetBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);

		double sampleSizeReturnValue = 0d;
		int[] imageFileDimensions;
		sampleSizeReturnValue = scaleImageFileAndReturnSampleSize(testImageFile, targetBitmapWidth, targetBitmapHeight);
		imageFileDimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());
		assertEquals("Width should be the same", targetBitmapWidth, imageFileDimensions[0]);
		assertEquals("Height should be according to aspect ratio", bitmapHeight * targetBitmapWidth / bitmapWidth,
				imageFileDimensions[1]);

		double sampleSizeWidth = (bitmapWidth / (double) targetBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) targetBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
		assertEquals("Scale should be the same", 1d / sampleSize, sampleSizeReturnValue);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);
		bitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(bitmap,
				newWidth, newHeight));

		assertEquals("Loaded and scaled bitmap has incorrect height", bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals("Loaded and scaled bitmap has incorrect width", bitmap.getWidth(), loadedBitmap.getWidth());

		try {
			ImageEditing.scaleImageFile(testImageFile, 1 / sampleSizeReturnValue);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Test not found", e);
			fail("Test not found");
		}
		imageFileDimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());
		assertEquals("Width should be initial value again", bitmapWidth, imageFileDimensions[0]);
		assertEquals("Height should be initial value again", bitmapHeight, imageFileDimensions[1]);
	}

	public void testRotatePicture() {
		Bitmap testBitmap = BitmapFactory.decodeResource(Resources.getSystem(), android.R.drawable.bottom_bar);

		int widthBeforeRotation = testBitmap.getWidth();
		int[] testBitmapPixels = new int[testBitmap.getHeight() * testBitmap.getWidth()];

		testBitmap.getPixels(testBitmapPixels, 0, testBitmap.getWidth(), 0, 0, testBitmap.getWidth(),
				testBitmap.getHeight());

		Bitmap rotatedBitmap = ImageEditing.rotateBitmap(testBitmap, 180);
		int[] roatatedBitmapPixels = new int[rotatedBitmap.getHeight() * rotatedBitmap.getWidth()];
		rotatedBitmap.getPixels(roatatedBitmapPixels, 0, rotatedBitmap.getWidth(), 0, 0, rotatedBitmap.getWidth(),
				rotatedBitmap.getHeight());

		for (int i = 0; i < widthBeforeRotation; i++) {
			assertFalse("Pixelvalues should be different", (testBitmapPixels[i] == roatatedBitmapPixels[i]));
		}
	}

	private static double scaleImageFileAndReturnSampleSize(File file, int newWidth, int newHeight) {
		String path = file.getAbsolutePath();
		int[] originalBackgroundImageDimensions = ImageEditing.getImageDimensions(path);
		Bitmap scaledBitmap = ImageEditing.getScaledBitmapFromPath(path, newWidth, newHeight,
				ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);
		try {
			StorageHandler.saveBitmapToImageFile(file, scaledBitmap);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Error while saving file", e);
			fail("Error while saving file");
		}

		double sampleSizeWidth = ((double) originalBackgroundImageDimensions[0]) / ((double) newWidth);
		double sampleSizeHeight = ((double) originalBackgroundImageDimensions[1]) / ((double) newHeight);
		return (1d / Math.max(sampleSizeWidth, sampleSizeHeight));
	}
}
