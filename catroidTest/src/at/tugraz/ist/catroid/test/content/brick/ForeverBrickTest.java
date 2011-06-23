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

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;

public class ForeverBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private Script testScript;
	private int firstPosition = 100;
	private int secondPosition = 300;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		testScript = new StartScript("testScripte", testSprite);

		ShowBrick showBrick = new ShowBrick(testSprite);
		ForeverBrick foreverBrick = new ForeverBrick(testSprite);
		WaitBrick firstWaitBrick = new WaitBrick(testSprite, 1000);
		SetXBrick firstSetXBrick = new SetXBrick(testSprite, firstPosition);
		WaitBrick secondWaitBrick = new WaitBrick(testSprite, 1000);
		SetXBrick secondSetXBrick = new SetXBrick(testSprite, secondPosition);

		testScript.addBrick(showBrick);
		testScript.addBrick(foreverBrick);
		testScript.addBrick(firstWaitBrick);
		testScript.addBrick(firstSetXBrick);
		testScript.addBrick(secondWaitBrick);
		testScript.addBrick(secondSetXBrick);
	}

	public void testForeverBrick() throws Exception {
		Thread t = new Thread(new Runnable() {
			public void run() {
				testScript.run();
			}
		});
		t.start();

		Thread.sleep(500);
		assertEquals("Wrong brick executing", 2, testScript.getExecutingBrickIndex());
		Thread.sleep(1000);
		assertEquals("Wrong brick executing", 4, testScript.getExecutingBrickIndex());
		Thread.sleep(1000);
		assertEquals("Wrong brick executing", 2, testScript.getExecutingBrickIndex());

		Thread.sleep(10000);
		assertEquals("Wrong brick executing", 2, testScript.getExecutingBrickIndex());
		Thread.sleep(1000);
		assertEquals("Wrong brick executing", 4, testScript.getExecutingBrickIndex());
		Thread.sleep(1000);
		assertEquals("Wrong brick executing", 2, testScript.getExecutingBrickIndex());

	}
}
