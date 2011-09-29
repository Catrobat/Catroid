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
package at.tugraz.ist.catroid.uitest.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.utils.Utils;

public class StageTestUtils {

	public static void savePictureFromResourceInProject(String project, String name, int fileId, Context context)
			throws IOException {

		final String imagePath = Utils.buildPath(Consts.DEFAULT_ROOT, project, Consts.IMAGE_DIRECTORY, name);
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileId);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Consts.BUFFER_8K);
		byte[] buffer = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();
	}

	public static void compareResWithArray(int fileId, byte[] screenArray, Context context) {
		Bitmap file = BitmapFactory.decodeResource(context.getResources(), fileId);
		byte[] fileByteArray = new byte[file.getWidth() * file.getHeight() * 4];
		int counter = 0;
		for (int y = 0; y < file.getHeight(); y++) {
			for (int x = 0; x < file.getWidth(); x++) {
				int pixel = file.getPixel(x, y);
				fileByteArray[counter++] = (byte) ((pixel >> 24) & 0xff);
				fileByteArray[counter++] = (byte) ((pixel >> 16) & 0xff);
				fileByteArray[counter++] = (byte) ((pixel >> 8) & 0xff);
				fileByteArray[counter++] = (byte) (pixel & 0xff);
			}
		}
		compareByteArrays(fileByteArray, screenArray);
	}

	public static void compareByteArrays(byte[] firstArray, byte[] secondArray) {
		assertEquals("Length of byte arrays not equal", firstArray.length, secondArray.length);
		assertTrue("Arrays don't have same content", firstArray.equals(secondArray));
	}

	public static void comparePixelArrayWithPixelScreenArray(byte[] pixelArray, byte[] screenArray, int x, int y,
			int screenWidth, int screenHeight) {
		assertEquals("Length of pixel array not 4", 4, pixelArray.length);
		int convertedX = x + (screenWidth / 2);
		int convertedY = y + (screenHeight / 2);
		byte[] screenPixel = new byte[4];
		for (int i = 0; i < 4; i++) {
			screenPixel[i] = screenArray[(convertedX * 3 + convertedX + convertedY * screenWidth * 4) + i];
		}
		assertEquals("Pixels don't have same content.", pixelArray[0], screenPixel[0], 10);
		assertEquals("Pixels don't have same content.", pixelArray[1], screenPixel[1], 10);
		assertEquals("Pixels don't have same content.", pixelArray[2], screenPixel[2], 10);
		assertEquals("Pixels don't have same content.", pixelArray[3], screenPixel[3], 10);
	}
}
