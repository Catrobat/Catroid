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

package at.tugraz.ist.catroid.test.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import at.tugraz.ist.catroid.utils.ImageEditing;

public class ImageEditingTest extends TestCase {

	public void testScaleImage() {
		// create a 100x100 bitmap
		Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);

		Bitmap scaledBitmap = ImageEditing.scaleBitmap(bitmap, 0.5f, false);

		assertEquals(50, scaledBitmap.getWidth());
		assertEquals(50, scaledBitmap.getHeight());

		scaledBitmap = ImageEditing.scaleBitmap(bitmap, 60, 70);

		assertEquals(60, scaledBitmap.getWidth());
		assertEquals(70, scaledBitmap.getHeight());
	}

	public void testGetImageDimensions() {
		File sdImageMainDirectory = new File("/mnt/sdcard/tmp");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(100, 200, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be createt in /mnt/sdcard/tmp/", true);
			e.printStackTrace();
		}

		int dimensions[] = new int[2];

		dimensions = ImageEditing.getImageDimensions("/mnt/sdcard/tmp/tmp.jpg");

		assertEquals(100, dimensions[0]);
		assertEquals(200, dimensions[1]);

	}

	public void testGetBitmap() {
		int maxBitmapWidth = 500;
		int maxBitmapHeight = 500;

		int bitmapWidth = 100;
		int bitmapHeight = 200;

		File sdImageMainDirectory = new File("/mnt/sdcard/tmp");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be createt in /mnt/sdcard/tmp/", true);
			e.printStackTrace();
		}

		Bitmap loadedBitmap = ImageEditing.getBitmap("/mnt/sdcard/tmp/tmp.jpg", maxBitmapWidth, maxBitmapHeight);

		assertEquals(bitmap.getHeight(),loadedBitmap.getHeight());
		assertEquals(bitmap.getWidth(),loadedBitmap.getWidth());
		

		bitmapWidth = 600;
		bitmapHeight = 800;
		
		double sampleSizeWidth = (bitmapWidth / (double) maxBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) maxBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);

		sdImageMainDirectory = new File("/mnt/sdcard/tmp");
		fileOutputStream = null;

		bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be createt in /mnt/sdcard/tmp/", true);
			e.printStackTrace();
		}

		loadedBitmap = ImageEditing.getBitmap("/mnt/sdcard/tmp/tmp.jpg", maxBitmapWidth, maxBitmapHeight);
		bitmap = ImageEditing.scaleBitmap(bitmap, newWidth, newHeight);
		
		
		assertEquals(bitmap.getHeight(),loadedBitmap.getHeight());
		assertEquals(bitmap.getWidth(),loadedBitmap.getWidth());
	}
	
	public void testGetScaledBitmap(){
		int targetBitmapWidth = 300;
		int targetBitmapHeight = 500;

		int bitmapWidth = 1000;
		int bitmapHeight = 900;

		File sdImageMainDirectory = new File("/mnt/sdcard/tmp");
		FileOutputStream fileOutputStream = null;

		Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.RGB_565);

		try {
			fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + "tmp" + ".jpg");
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			bitmap.compress(CompressFormat.PNG, 0, bos);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			assertFalse("Test file could not be createt in /mnt/sdcard/tmp/", true);
			e.printStackTrace();
		}

		Bitmap loadedBitmap = ImageEditing.getScaledBitmap("/mnt/sdcard/tmp/tmp.jpg", targetBitmapWidth, targetBitmapHeight);
		
		double sampleSizeWidth = (bitmapWidth / (double) targetBitmapWidth);
		double sampleSizeHeight = bitmapHeight / (double) targetBitmapHeight;
		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);

		int newWidth = (int) Math.ceil(bitmapWidth / sampleSize);
		int newHeight = (int) Math.ceil(bitmapHeight / sampleSize);
		bitmap = ImageEditing.scaleBitmap(bitmap, newWidth, newHeight);
		
		
		assertEquals(bitmap.getHeight(),loadedBitmap.getHeight());
		assertEquals(bitmap.getWidth(),loadedBitmap.getWidth());
	}

}
