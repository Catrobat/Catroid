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

import android.test.FlakyTest;
import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class RepeatBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private StartScript testScript;
	private static final int REPEAT_TIMES = 10;
	private LoopEndBrick loopEndBrick;
	private LoopBeginBrick repeatBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
	}

	public void testRepeatBrick() throws InterruptedException {
		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		/*
		 * Let's wait even longer than necessary, then check if we only executed N times, not N+1 times
		 * http://code.google.com/p/catroid/issues/detail?id=24
		 */
		Thread.sleep(expectedDelay * REPEAT_TIMES * 2);

		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * deltaY,
				(int) testSprite.costume.getYPosition());
	}

	@FlakyTest(tolerance = 3)
	public void testLoopDelay() throws InterruptedException {
		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, 150));

		testSprite.addScript(testScript);
		final long startTime = System.currentTimeMillis();
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * REPEAT_TIMES);

		assertEquals("Loop delay did not work!", REPEAT_TIMES * deltaY, (int) testSprite.costume.getYPosition());

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final long delayByContract = 20;
		final long endTime = System.currentTimeMillis();
		assertEquals("Loop delay did was not 20ms!", delayByContract * REPEAT_TIMES, endTime - startTime, 15);
	}

	public void testNegativeRepeats() throws InterruptedException {
		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		repeatBrick = new RepeatBrick(testSprite, -1);
		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int decoyDeltaY = -150;
		final int expectedDeltaY = 150;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, decoyDeltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, expectedDeltaY));

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		/*
		 * Waiting less than what a loop delay would be! Loop should not execute and there should be no delay
		 * http://code.google.com/p/catroid/issues/detail?id=24#c9
		 */
		Thread.sleep(expectedDelay / 2);

		assertEquals("Loop was executed although repeats were less than zero!", expectedDeltaY,
				(int) testSprite.costume.getYPosition());
	}

	public void testZeroRepeats() throws InterruptedException {
		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		repeatBrick = new RepeatBrick(testSprite, 0);
		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int decoyDeltaY = -150;
		final int expectedDeltaY = 150;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, decoyDeltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, expectedDeltaY));

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		/*
		 * Waiting less than what a loop delay would be! Loop should not execute and there should be no delay
		 * http://code.google.com/p/catroid/issues/detail?id=24#c9
		 */
		Thread.sleep(expectedDelay / 2);

		assertEquals("Loop was executed although repeats were set to zero!", expectedDeltaY,
				(int) testSprite.costume.getYPosition());
	}
}
