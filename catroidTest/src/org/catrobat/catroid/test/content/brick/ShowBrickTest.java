/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.brick;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.ShowBrick;

import android.test.AndroidTestCase;

public class ShowBrickTest extends AndroidTestCase {

	public void testShow() {
		Sprite sprite = new Sprite("new sprite");
		sprite.look.show = false;
		assertFalse("Sprite is still visible after calling hide", sprite.look.show);

		ShowBrick showBrick = new ShowBrick(sprite);
		showBrick.execute();
		assertTrue("Sprite is not visible after ShowBrick executed", sprite.look.show);
	}

	public void testNullSprite() {
		ShowBrick showBrick = new ShowBrick(null);
		try {
			showBrick.execute();
			fail("Execution of ShowBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}
}
