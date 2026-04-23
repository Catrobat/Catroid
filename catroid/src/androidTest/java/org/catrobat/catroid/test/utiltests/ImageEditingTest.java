/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.FlavoredConstants;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.catrobat.catroid.utils.ImageEditing;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class ImageEditingTest {

	@Test
	public void testScaleImage() throws Exception {
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
		Bitmap scaledBitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(
				bitmap, 60, 70));

		assertEquals(60, scaledBitmap.getWidth());
		assertEquals(70, scaledBitmap.getHeight());
	}

	@Test
	public void testGetImageDimensions() throws IOException {
		File testImageFile = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "tmp.jpg");

		Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565);

		FileOutputStream fileOutputStream = new FileOutputStream(testImageFile);
		BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
		bitmap.compress(CompressFormat.PNG, 0, bos);
		bos.flush();
		bos.close();

		int[] dimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());

		assertEquals(100, dimensions[0]);
		assertEquals(200, dimensions[1]);
	}

	@Test
	public void testGetBitmap() throws Exception {
		int maxBitmapWidth = 500;
		int maxBitmapHeight = 500;

		int bitmapWidth = 100;
		int bitmapHeight = 200;

		File testImageFile = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "tmp.jpg");

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		FileOutputStream fileOutputStream = new FileOutputStream(testImageFile);
		BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
		bitmap.compress(CompressFormat.PNG, 0, bos);
		bos.flush();
		bos.close();

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), maxBitmapWidth,
				maxBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);

		assertEquals(200, loadedBitmap.getHeight());
		assertEquals(100, loadedBitmap.getWidth());

		loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), maxBitmapWidth,
				maxBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);

		assertEquals(500, loadedBitmap.getHeight());
		assertEquals(250, loadedBitmap.getWidth());

		bitmapWidth = 600;
		bitmapHeight = 800;

		double sampleSizeWidth = (bitmapWidth / (double) maxBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) maxBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);

		bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		fileOutputStream = new FileOutputStream(testImageFile);
		bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
		bitmap.compress(CompressFormat.PNG, 0, bos);
		bos.flush();
		bos.close();

		loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), maxBitmapWidth,
				maxBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, true);
		bitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(bitmap,
				newWidth, newHeight));

		assertEquals(bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals(bitmap.getWidth(), loadedBitmap.getWidth());
	}

	@Test
	public void testGetScaledBitmap() throws Exception {
		int targetBitmapWidth = 300;
		int targetBitmapHeight = 500;

		int bitmapWidth = 1000;
		int bitmapHeight = 900;

		File testImageFile = new File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "tmp.jpg");

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		FileOutputStream fileOutputStream = new FileOutputStream(testImageFile);
		BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Constants.BUFFER_8K);
		bitmap.compress(CompressFormat.PNG, 0, bos);
		bos.flush();
		bos.close();

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(testImageFile.getAbsolutePath(), targetBitmapWidth,
				targetBitmapHeight, ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);

		double sampleSizeReturnValue = 0d;
		int[] imageFileDimensions;
		sampleSizeReturnValue = scaleImageFileAndReturnSampleSize(testImageFile, targetBitmapWidth, targetBitmapHeight);
		imageFileDimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());
		assertEquals(targetBitmapWidth, imageFileDimensions[0]);
		assertEquals(bitmapHeight * targetBitmapWidth / bitmapWidth, imageFileDimensions[1]);

		double sampleSizeWidth = (bitmapWidth / (double) targetBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) targetBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
		assertEquals(1d / sampleSize, sampleSizeReturnValue);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);
		bitmap = (Bitmap) Reflection.invokeMethod(ImageEditing.class, "scaleBitmap", new ParameterList(bitmap,
				newWidth, newHeight));

		assertEquals(bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals(bitmap.getWidth(), loadedBitmap.getWidth());

		ImageEditing.scaleImageFile(testImageFile, 1 / sampleSizeReturnValue);
		imageFileDimensions = ImageEditing.getImageDimensions(testImageFile.getAbsolutePath());
		assertEquals(bitmapWidth, imageFileDimensions[0]);
		assertEquals(bitmapHeight, imageFileDimensions[1]);
	}

	@Test
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
			assertNotEquals(roatatedBitmapPixels[i], testBitmapPixels[i]);
		}
	}

	private static double scaleImageFileAndReturnSampleSize(File file, int newWidth, int newHeight) throws
			FileNotFoundException {
		String path = file.getAbsolutePath();
		int[] originalBackgroundImageDimensions = ImageEditing.getImageDimensions(path);
		Bitmap scaledBitmap = ImageEditing.getScaledBitmapFromPath(path, newWidth, newHeight,
				ImageEditing.ResizeType.STAY_IN_RECTANGLE_WITH_SAME_ASPECT_RATIO, false);

		ImageEditing.saveBitmapToImageFile(file, scaledBitmap);

		double sampleSizeWidth = ((double) originalBackgroundImageDimensions[0]) / ((double) newWidth);
		double sampleSizeHeight = ((double) originalBackgroundImageDimensions[1]) / ((double) newHeight);
		return (1d / Math.max(sampleSizeWidth, sampleSizeHeight));
	}
}
