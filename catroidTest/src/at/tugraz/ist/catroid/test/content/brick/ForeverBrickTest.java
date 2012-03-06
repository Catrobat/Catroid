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
import at.tugraz.ist.catroid.content.bricks.ForeverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class ForeverBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private StartScript testScript;
	private LoopEndBrick loopEndBrick;
	private LoopBeginBrick foreverBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
	}

	public void testForeverBrick() throws InterruptedException {
		final int twentyIsAlmostForever = 20;

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		foreverBrick = new ForeverBrick(testSprite);
		loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;
		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * twentyIsAlmostForever);

		assertEquals("Executed the wrong number of times!", twentyIsAlmostForever * deltaY,
				(int) testSprite.costume.getYPosition());

		final int timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		final int forever = (Integer) TestUtils.getPrivateField("FOREVER", loopEndBrick, false);

		assertEquals("Wrong number of times to repeat", forever, timesToRepeat);
	}

	@FlakyTest(tolerance = 3)
	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;
		final int repeatTimes = 15;

		testSprite.removeAllScripts();
		testScript = new StartScript(testSprite);

		foreverBrick = new ForeverBrick(testSprite);
		loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		final int expectedDelay = (Integer) TestUtils.getPrivateField("LOOP_DELAY", loopEndBrick, false);

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		final long startTime = System.currentTimeMillis();
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * repeatTimes);

		final long endTime = System.currentTimeMillis();

		assertEquals("Loop delay did not work!", repeatTimes * deltaY, (int) testSprite.costume.getYPosition());

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final long delayByContract = 20;
		assertEquals("Loop delay was not 20ms!", delayByContract * repeatTimes, endTime - startTime, 15);
	}
}
