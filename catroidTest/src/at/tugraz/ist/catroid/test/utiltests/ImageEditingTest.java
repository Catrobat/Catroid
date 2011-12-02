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
package at.tugraz.ist.catroid.test.utiltests;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class ImageEditingTest extends TestCase {

	public void testScaleImage() {
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);

		Bitmap scaledBitmap = ImageEditing.scaleBitmap(bitmap, 60, 70);

		assertEquals("Wrong bitmap width after scaling", 60, scaledBitmap.getWidth());
		assertEquals("Wrong bitmap height after scaling", 70, scaledBitmap.getHeight());
	}

	public void testGetImageDimensions() {
		File sdImageMainDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Consts.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be created", true);
			e.printStackTrace();
		}

		int dimensions[] = new int[2];

		dimensions = ImageEditing.getImageDimensions(sdImageMainDirectory.toString() + "/tmp.jpg");

		assertEquals("Wrong image width", 100, dimensions[0]);
		assertEquals("Wrong image height", 200, dimensions[1]);

	}

	public void testGetBitmap() {
		int maxBitmapWidth = 500;
		int maxBitmapHeight = 500;

		int bitmapWidth = 100;
		int bitmapHeight = 200;

		File sdImageMainDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Consts.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be created", true);
			e.printStackTrace();
		}

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(sdImageMainDirectory.toString() + "/tmp.jpg",
				maxBitmapWidth, maxBitmapHeight, true);

		assertEquals("Loaded bitmap has incorrect height", bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals("Loaded bitmap has incorrect width", bitmap.getWidth(), loadedBitmap.getWidth());

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
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Consts.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be created", true);
			e.printStackTrace();
		}

		loadedBitmap = ImageEditing.getScaledBitmapFromPath(sdImageMainDirectory.toString() + "/tmp.jpg",
				maxBitmapWidth, maxBitmapHeight, true);
		bitmap = ImageEditing.scaleBitmap(bitmap, newWidth, newHeight);

		assertEquals("Loaded bitmap has incorrect height", bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals("Loaded bitmap has incorrect width", bitmap.getWidth(), loadedBitmap.getWidth());
	}

	public void testGetScaledBitmap() {
		int targetBitmapWidth = 300;
		int targetBitmapHeight = 500;

		int bitmapWidth = 1000;
		int bitmapHeight = 900;

		File sdImageMainDirectory = Environment.getExternalStorageDirectory().getAbsoluteFile();
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream, Consts.BUFFER_8K);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be created", true);
			e.printStackTrace();
		}

		Bitmap loadedBitmap = ImageEditing.getScaledBitmapFromPath(sdImageMainDirectory.toString() + "/tmp.jpg",
				targetBitmapWidth, targetBitmapHeight, false);

		double sampleSizeWidth = (bitmapWidth / (double) targetBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) targetBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);
		bitmap = ImageEditing.scaleBitmap(bitmap, newWidth, newHeight);

		assertEquals("Loaded and scaled bitmap has incorrect height", bitmap.getHeight(), loadedBitmap.getHeight());
		assertEquals("Loaded and scaled bitmap has incorrect width", bitmap.getWidth(), loadedBitmap.getWidth());
	}

	public void testCreateSingleColorBitmap() {
		int expectedWidth = 100;
		int expectedHeight = 200;
		int expectedColor = Color.CYAN;

		Bitmap testBitmap = ImageEditing.createSingleColorBitmap(expectedWidth, expectedHeight, expectedColor);

		assertEquals("The Bitmap has the wrong width", expectedWidth, testBitmap.getWidth());
		assertEquals("The Bitmap has the wrong height", expectedHeight, testBitmap.getHeight());

		assertEquals("The color of the Pixel is wrong", expectedColor, testBitmap.getPixel(0, 0));
		assertEquals("The color of the Pixel is wrong", expectedColor, testBitmap.getPixel(expectedWidth - 1,
				expectedHeight - 1));
		assertEquals("The color of the Pixel is wrong", expectedColor, testBitmap.getPixel(expectedWidth / 2,
				expectedHeight / 2));

	}

}
