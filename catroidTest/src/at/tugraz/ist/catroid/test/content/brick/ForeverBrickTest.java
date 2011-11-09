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
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
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
	private final static int BRICK_SLEEP_TIME = 1000;
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
		WaitBrick firstWaitBrick = new WaitBrick(testSprite, BRICK_SLEEP_TIME);
		SetXBrick firstSetXBrick = new SetXBrick(testSprite, 100);
		WaitBrick secondWaitBrick = new WaitBrick(testSprite, BRICK_SLEEP_TIME);
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

		Thread.sleep(BRICK_SLEEP_TIME / 2);

		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());

		Thread.sleep(BRICK_SLEEP_TIME * 6);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(BRICK_SLEEP_TIME);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());

		int timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);

		assertEquals("Wrong number of times to repeat", LoopEndBrick.FOREVER, timesToRepeat);
	}

	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;
		final int repeatTimes = 15;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testSprite.removeAllScripts();
		testScript = new StartScript("foo", testSprite);

		foreverBrick = new ForeverBrick(testSprite);
		loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		final long startTime = System.currentTimeMillis();
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * repeatTimes);

		assertEquals("Loop delay did not work!", repeatTimes * deltaY, (int) testSprite.costume.getYPosition());

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final long delayByContract = 20;
		final long endTime = System.currentTimeMillis();
		assertEquals("Loop delay did was not 20ms!", delayByContract * repeatTimes, endTime - startTime, 15);
	}
}
