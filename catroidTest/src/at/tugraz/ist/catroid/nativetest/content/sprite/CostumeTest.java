/** 
 *    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010-2011 The Catroid Team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Affero General Public License as
 *    published by the Free Software Foundation, either version 3 of the
 *    License, or (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.nativetest.content.sprite;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.stage.NativeAppActivity;
import at.tugraz.ist.catroid.test.R;

public class CostumeTest extends InstrumentationTestCase {
	private final int TEST_IMAGE_ID = R.drawable.icon;
	private final int TEST_BIG_IMAGE_ID = R.raw.big_image;

	@Override
	protected void tearDown() throws Exception {
		NativeAppActivity.setContext(null);
	}

	//	public void testSetBitmapFromResource() throws Exception {
	//		Sprite sprite = new Sprite("testSprite");
	//		Costume costume = new Costume(sprite, null);
	//
	//		Values.SCREEN_WIDTH = 200;
	//		Values.SCREEN_HEIGHT = 200;
	//
	//		assertNull("Bitmap of the costume is not null.", costume.getBitmap());
	//
	//		testImage(costume, TEST_IMAGE_ID);
	//		testImage(costume, TEST_BIG_IMAGE_ID);
	//	}
	//
	//	private void testImage(Costume costume, int resId) {
	//		costume.setBitmapFromResource(getInstrumentation().getContext(), resId);
	//
	//		BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(), resId, options);
	//
	//		int initialWidth = options.outWidth;
	//		int initialHeight = options.outHeight;
	//		int scaledWidth = initialWidth;
	//		int scaledHeight = initialHeight;
	//
	//		double sampleSizeWidth = initialWidth / (double) Values.SCREEN_WIDTH;
	//		double sampleSizeHeight = initialHeight / (double) Values.SCREEN_HEIGHT;
	//		double sampleSize = Math.max(sampleSizeWidth, sampleSizeHeight);
	//
	//		if (sampleSize > 1) {
	//			int sampleSizeRounded = (int) Math.floor(sampleSize);
	//
	//			scaledHeight = (int) Math.ceil(initialWidth / sampleSizeRounded);
	//			scaledWidth = (int) Math.ceil(initialHeight / sampleSizeRounded);
	//		}
	//
	//		assertEquals("Wrong height.", scaledHeight, costume.getBitmap().getHeight());
	//		assertEquals("Wrong width.", scaledWidth, costume.getBitmap().getWidth());
	//	}
}