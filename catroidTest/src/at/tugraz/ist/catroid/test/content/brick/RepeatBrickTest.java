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
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class RepeatBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private StartScript testScript;
	private static final int BRICK_SLEEP_TIME = 1000;
	private static final int REPEAT_TIMES = 3;
	private static final int MILLION = 1000 * 1000;
	private int positionOfFirstWaitBrick;
	private int positionOfSecondWaitBrick;
	private LoopEndBrick loopEndBrick;
	private LoopBeginBrick repeatBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		testScript = new StartScript("testScript", testSprite);

		ShowBrick showBrick = new ShowBrick(testSprite);
		repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		WaitBrick firstWaitBrick = new WaitBrick(testSprite, BRICK_SLEEP_TIME);
		SetXBrick firstSetXBrick = new SetXBrick(testSprite, 100);
		WaitBrick secondWaitBrick = new WaitBrick(testSprite, BRICK_SLEEP_TIME);
		SetXBrick secondSetXBrick = new SetXBrick(testSprite, 200);
		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);

		testScript.addBrick(showBrick);
		testScript.addBrick(repeatBrick);
		testScript.addBrick(firstWaitBrick);
		testScript.addBrick(firstSetXBrick);
		testScript.addBrick(secondWaitBrick);
		testScript.addBrick(secondSetXBrick);
		testScript.addBrick(loopEndBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testSprite.addScript(testScript);

		positionOfFirstWaitBrick = testScript.getBrickList().indexOf(firstWaitBrick);
		positionOfSecondWaitBrick = testScript.getBrickList().indexOf(secondWaitBrick);
	}

	public void testRepeatBrick() throws InterruptedException {
		testSprite.startStartScripts();

		Thread.sleep(BRICK_SLEEP_TIME / 2);

		int timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", REPEAT_TIMES, timesToRepeat);

		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);

		timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", REPEAT_TIMES - 1, timesToRepeat);

		Thread.sleep(BRICK_SLEEP_TIME * (REPEAT_TIMES - 1) * 2);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);

		timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", 0, timesToRepeat);
	}

	public void testLoopDelay() throws InterruptedException {

		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);
		repeatBrick.execute();
		long startTime = repeatBrick.getBeginLoopTime() / MILLION;
		loopEndBrick.execute();
		long endTime = System.nanoTime() / MILLION;
		assertTrue("Loop delay was too short...", endTime - startTime >= expectedDelay);
		assertTrue("Loop delay was very long...", endTime - startTime <= expectedDelay + 1000);

		startTime = repeatBrick.getBeginLoopTime() / MILLION;
		loopEndBrick.execute();
		endTime = System.nanoTime() / MILLION;
		assertTrue("Loop delay was too short...", endTime - startTime >= expectedDelay);
		assertTrue("Loop delay was very long...", endTime - startTime <= expectedDelay + 1000);
	}
}
