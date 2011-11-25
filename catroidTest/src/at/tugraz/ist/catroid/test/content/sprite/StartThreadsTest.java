/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.SetSizeToBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class StartThreadsTest extends AndroidTestCase {

	public void testStartThreads() throws InterruptedException {
		double size = 300;
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript(testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(testSprite, size);

		testScript.addBrick(hideBrick);
		testScript.addBrick(setSizeToBrick);
		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		Thread.sleep(200);

		assertFalse("Sprite is not hidden", testSprite.costume.show);
		assertEquals("the size is not as expected", (float) size / 100, testSprite.costume.scaleX);
		assertEquals("the size is not as expected", (float) size / 100, testSprite.costume.scaleY);
	}

	public void testResumeThreads() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript(testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		WaitBrick waitBrick = new WaitBrick(testSprite, 400);
		ShowBrick showBrick = new ShowBrick(testSprite);

		testScript.addBrick(hideBrick);
		testScript.addBrick(waitBrick);
		testScript.addBrick(showBrick);
		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		Thread.sleep(100);

		testSprite.pause();
		assertFalse("Sprite is not hidden", testSprite.costume.show);
		testSprite.resume();

		Thread.sleep(400);

		assertTrue("Sprite is hidden", testSprite.costume.show);

		testScript.getBrickList().clear();
		testScript.addBrick(hideBrick);
		testSprite.startStartScripts();

		Thread.sleep(100);

		assertTrue("Sprite is hidden - this script shall not be execute", testSprite.costume.show);
	}
}
