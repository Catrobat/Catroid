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
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class WaitBrickTest extends AndroidTestCase {

	public void testWait() {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript("testScript", testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 1000);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);

		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse("Unexpected visibility of testSprite", testSprite.isVisible());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertTrue("Unexpected visibility of testSprite", testSprite.isVisible());
	}

	public void testPauseResume() {
		Sprite testSprite = new Sprite("testSprite");
		final Script testScript = new StartScript("test", testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 3000);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);

		testSprite.addScript(testScript);
		for (int i = 0; i < 3; i++) {
			//Should use: void startScript(Script s)
			Thread t = new Thread(new Runnable() {
				public void run() {
					testScript.run();
				}
			});
			t.start();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.isVisible());

			testSprite.pause();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.isVisible());

			testSprite.resume();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.isVisible());

			try {
				Thread.sleep(1200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			assertTrue("Unexpected visibility of testSprite. Run: " + i, testSprite.isVisible());
		}
	}
}
