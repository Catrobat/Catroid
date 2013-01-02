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

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;

import android.test.AndroidTestCase;

public class WaitBrickTest extends AndroidTestCase {

	public void testWait() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript(testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 1000);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);

		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		Thread.sleep(200);

		assertFalse("Unexpected visibility of testSprite", testSprite.costume.show);

		Thread.sleep(1000);

		assertTrue("Unexpected visibility of testSprite", testSprite.costume.show);
	}

	public void testPauseResume() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		final Script testScript = new StartScript(testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 3000);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);

		testSprite.addScript(testScript);
		for (int i = 0; i < 3; i++) {
			//Should use: void startScript(Script s)
			Thread thread = new Thread(new Runnable() {
				public void run() {
					testScript.run();
				}
			});
			thread.start();

			Thread.sleep(1000);

			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.costume.show);

			testSprite.pause();

			Thread.sleep(200);

			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.costume.show);

			testSprite.resume();

			Thread.sleep(1000);

			assertFalse("Unexpected visibility of testSprite. Run: " + i, testSprite.costume.show);

			Thread.sleep(1200);

			assertTrue("Unexpected visibility of testSprite. Run: " + i, testSprite.costume.show);
		}
	}
}
