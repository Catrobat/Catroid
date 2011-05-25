/**
  Catroid: An on-device graphical programming language for Android devices
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

package at.tugraz.ist.catroid.nativetest.content.sprite;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.nativetest.R;

public class CostumeTest extends InstrumentationTestCase {
	private final int TEST_IMAGE_ID = R.drawable.icon;
	
	public void testSetBitmapFromRes() throws Exception {
		Sprite sprite = new Sprite("testSprite");
		Costume costume = new Costume(sprite, null);
		
		Values.SCREEN_WIDTH = 200;
		Values.SCREEN_HEIGHT = 200;
		
		assertNull("Bitmap of the costume is not null.", costume.getBitmap());
		
		costume.setBitmapFromRes(getInstrumentation().getContext(), TEST_IMAGE_ID);
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(), TEST_IMAGE_ID, options);
		
		assertEquals("Wrong height.", costume.getBitmap().getHeight(), options.outHeight);
		assertEquals("Wrong width.", costume.getBitmap().getWidth(), options.outWidth);
	}
}