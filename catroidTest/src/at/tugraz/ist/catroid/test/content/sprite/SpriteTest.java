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
import at.tugraz.ist.catroid.content.bricks.ShowBrick;

public class SpriteTest extends AndroidTestCase {

	public void testAddScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript(sprite);
		Script secondScript = new StartScript(sprite);
		sprite.addScript(firstScript);
		assertEquals("Script list does not contain script after adding", 1, sprite.getNumberOfScripts());

		sprite.addScript(0, secondScript);
		assertEquals("Script list does not contain script after adding", 2, sprite.getNumberOfScripts());

		assertEquals("Script list does not contain script after adding", 1, sprite.getScriptIndex(firstScript));
		assertEquals("Script list does not contain script after adding", 0, sprite.getScriptIndex(secondScript));

		sprite.removeAllScripts();
		assertEquals("Script list could not be cleared", 0, sprite.getNumberOfScripts());
	}

	public void testGetScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript(sprite);
		Script secondScript = new StartScript(sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Scripts do not match after retrieving", firstScript, sprite.getScript(0));
		assertEquals("Script doo not match after retrieving", secondScript, sprite.getScript(1));
	}

	public void testRemoveAllScripts() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript(sprite);
		Script secondScript = new StartScript(sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeAllScripts();

		assertEquals("Script list was not cleared", 0, sprite.getNumberOfScripts());
	}

	public void testRemoveScript() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript(sprite);
		Script secondScript = new StartScript(sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeScript(firstScript);

		assertEquals("Wrong script list size", 1, sprite.getNumberOfScripts());
		assertEquals("Wrong script remained", secondScript, sprite.getScript(0));

	}

	public void testGetScriptIndex() {
		Sprite sprite = new Sprite("new sprite");
		Script firstScript = new StartScript(sprite);
		Script secondScript = new StartScript(sprite);
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Indexes do not match", 0, sprite.getScriptIndex(firstScript));
		assertEquals("Indexes do not match", 1, sprite.getScriptIndex(secondScript));
	}

	public void testPauseUnPause() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		Script testScript = new StartScript(testSprite);
		HideBrick hideBrick = new HideBrick(testSprite);
		ShowBrick showBrick = new ShowBrick(testSprite);

		for (int i = 0; i < 10000; i++) {
			testScript.addBrick(hideBrick);
			testScript.addBrick(showBrick);
		}

		testSprite.addScript(testScript);

		testSprite.startStartScripts();

		Thread.sleep(20);

		testSprite.pause();
		assertTrue("Sprite isn't paused", testSprite.isPaused);
		assertTrue("Script isn't paused", testScript.isPaused());

		Thread.sleep(1000);

		testSprite.resume();

		assertFalse("Sprite is paused", testSprite.isPaused);
		assertFalse("Script is paused", testScript.isPaused());

		Thread.sleep(1000);

		assertTrue("Script hasn't finished", testScript.isFinished());

	}

}
