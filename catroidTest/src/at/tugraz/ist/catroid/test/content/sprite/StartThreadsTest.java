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
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class StartThreadsTest extends AndroidTestCase {

	public void testStartThreads() {
		double scale = 300;
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript("testScript", testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		ScaleCostumeBrick scaleCostumeBrick = new ScaleCostumeBrick(testSprite, scale);

		testScript.addBrick(hideBrick);
		testScript.addBrick(scaleCostumeBrick);
		testSprite.getScriptList().add(testScript);

		testSprite.startStartScripts();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertFalse("Sprite is not hidden", testSprite.isVisible());
		assertEquals("the scale is not as expected", scale, testSprite.getScale());
	}

	public void testResumeThreads() {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript("testScript", testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 400);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);
		testSprite.getScriptList().add(testScript);

		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		testSprite.pause();
		assertFalse("Sprite is not hidden", testSprite.isVisible());
		testSprite.resume();

		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue("Sprite is hidden", testSprite.isVisible());

		testScript.getBrickList().clear();
		testScript.addBrick(hideBrick);
		testSprite.startStartScripts();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue("Sprite is hidden - this script shall not be execute", testSprite.isVisible());
	}
}
