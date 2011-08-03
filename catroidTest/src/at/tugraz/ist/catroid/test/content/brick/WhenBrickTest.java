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

package at.tugraz.ist.catroid.test.content.brick;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;

public class WhenBrickTest extends AndroidTestCase {

	public void testWhenBrick() {
		int testPosition = 100;

		Sprite sprite = new Sprite("new sprite");
		WhenScript whenScript = new WhenScript("script", sprite);
		whenScript.setAction(1);
		Brick placeAtBrick = new PlaceAtBrick(sprite, testPosition, testPosition);
		whenScript.addBrick(placeAtBrick);
		sprite.addScript(whenScript);
		sprite.startWhenScripts(whenScript.getAction());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		assertEquals("Simple broadcast failed", testPosition, sprite.getXPosition());
	}

}