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

package at.tugraz.ist.catroid.nativetest.content.brick;

import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class SetCostumeBrickTest extends InstrumentationTestCase {
	final int TEST_IMAGE_ID = R.raw.test_image;
	
	public void testSetCostume() throws Exception {
		Values.SCREEN_HEIGHT = 200;
		Values.SCREEN_WIDTH = 200;
		
		NativeAppActivity.setContext(getInstrumentation().getTargetContext());

		Sprite sprite = new Sprite("new sprite");
		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(getInstrumentation().getTargetContext().getResources().getResourceEntryName(TEST_IMAGE_ID));
		
		assertNull("Bitmap is not null before executing setCostumeBrick.", sprite.getCostume().getBitmap());
		
		setCostumeBrick.execute();
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getInstrumentation().getTargetContext().getResources(), TEST_IMAGE_ID, options);
		
		double scaleFactor = (double) options.inTargetDensity / options.inDensity;
		assertEquals("Wrong height.", options.outHeight, (int) (sprite.getCostume().getBitmap().getHeight() * scaleFactor));
		assertEquals("Wrong width.", options.outWidth, (int) (sprite.getCostume().getBitmap().getWidth() * scaleFactor));
	}
}
