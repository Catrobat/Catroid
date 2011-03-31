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

import junit.framework.TestCase;
import android.graphics.Bitmap;
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
	
}
