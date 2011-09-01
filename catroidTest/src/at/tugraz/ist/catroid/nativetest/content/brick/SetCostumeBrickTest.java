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

package at.tugraz.ist.catroid.nativetest.content.brick;

import android.test.InstrumentationTestCase;

public class SetCostumeBrickTest extends InstrumentationTestCase {
	//	final int TEST_IMAGE_ID = R.raw.big_image;
	//
	//	@Override
	//	protected void tearDown() throws Exception {
	//		NativeAppActivity.setContext(null);
	//	}
	//
	//	public void testSetCostume() throws Exception {
	//		Values.SCREEN_HEIGHT = 800;
	//		Values.SCREEN_WIDTH = 480;
	//
	//		NativeAppActivity.setContext(getInstrumentation().getContext());
	//
	//		Sprite sprite = new Sprite("new sprite");
	//		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
	//		CostumeData costumeData = new CostumeData();
	//		costumeData.setCostumeFilename(getInstrumentation().getContext().getResources()
	//				.getResourceEntryName(TEST_IMAGE_ID));
	//		sprite.getCostumeDataList().add(costumeData);
	//		setCostumeBrick.setCostume(costumeData);
	//
	//		assertNull("Bitmap is not null before executing setCostumeBrick.", sprite.getCostume().getBitmap());
	//
	//		setCostumeBrick.execute();
	//
	//		BitmapFactory.Options options = new BitmapFactory.Options();
	//		options.inJustDecodeBounds = true;
	//		BitmapFactory.decodeResource(getInstrumentation().getContext().getResources(), TEST_IMAGE_ID, options);
	//
	//		double scaleFactor = (double) options.inTargetDensity / options.inDensity;
	//		assertEquals("Wrong height.", options.outHeight,
	//				(int) (sprite.getCostume().getBitmap().getHeight() * scaleFactor));
	//		assertEquals("Wrong width.", options.outWidth, (int) (sprite.getCostume().getBitmap().getWidth() * scaleFactor));
	//	}
}
