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
package at.tugraz.ist.catroid.test.content.brick;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class ForeverBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private StartScript testScript;
	private int brickSleepTime = 1000;
	private int positionOfFirstWaitBrick;
	private int positionOfSecondWaitBrick;
	private LoopEndBrick loopEndBrick;
	private LoopBeginBrick foreverBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		testScript = new StartScript("testScript", testSprite);

		ShowBrick showBrick = new ShowBrick(testSprite);
		foreverBrick = new ForeverBrick(testSprite);
		WaitBrick firstWaitBrick = new WaitBrick(testSprite, brickSleepTime);
		SetXBrick firstSetXBrick = new SetXBrick(testSprite, 100);
		WaitBrick secondWaitBrick = new WaitBrick(testSprite, brickSleepTime);
		SetXBrick secondSetXBrick = new SetXBrick(testSprite, 200);
		loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);

		testScript.addBrick(showBrick);
		testScript.addBrick(foreverBrick);
		testScript.addBrick(firstWaitBrick);
		testScript.addBrick(firstSetXBrick);
		testScript.addBrick(secondWaitBrick);
		testScript.addBrick(secondSetXBrick);
		testScript.addBrick(loopEndBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		testSprite.addScript(testScript);

		positionOfFirstWaitBrick = testScript.getBrickList().indexOf(firstWaitBrick);
		positionOfSecondWaitBrick = testScript.getBrickList().indexOf(secondWaitBrick);
	}

	public void testForeverBrick() throws InterruptedException {
		testSprite.startStartScripts();

		Thread.sleep(brickSleepTime / 2);

		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());

		Thread.sleep(brickSleepTime * 6);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());

		int timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);

		assertEquals("Wrong number of times to repeat", LoopEndBrick.FOREVER, timesToRepeat);
	}

	public void testLoopDelay() throws InterruptedException {

		long expectedDelay = LoopEndBrick.LOOP_DELAY;
		foreverBrick.execute();
		long startTime = foreverBrick.getBeginLoopTime() / 1000000;
		loopEndBrick.execute();
		long endTime = System.nanoTime() / 1000000;
		assertTrue("Loop delay was too short...", endTime - startTime >= expectedDelay);
		assertTrue("Loop delay was very long...", endTime - startTime <= expectedDelay + 1000);

		startTime = foreverBrick.getBeginLoopTime() / 1000000;
		loopEndBrick.execute();
		endTime = System.nanoTime() / 1000000;
		assertTrue("Loop delay was too short...", endTime - startTime >= expectedDelay);
		assertTrue("Loop delay was very long...", endTime - startTime <= expectedDelay + 1000);
	}
}
