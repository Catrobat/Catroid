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

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.test.utils.Reflection;

import android.test.FlakyTest;
import android.test.InstrumentationTestCase;

public class ForeverBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;

	@Override
	protected void tearDown() throws Exception {

		if (testSprite != null) {
			testSprite.pause();
			testSprite.finish();
		}
		super.tearDown();
	}

	@FlakyTest(tolerance = 3)
	public void testForeverBrick() throws InterruptedException {
		final int fiveIsAlmostForever = 5;

		testSprite = new Sprite("testSprite");

		StartScript testScript = new StartScript(testSprite);

		LoopBeginBrick foreverBrick = new ForeverBrick(testSprite);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;
		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * fiveIsAlmostForever);

		assertEquals("Executed the wrong number of times!", fiveIsAlmostForever * deltaY,
				(int) testSprite.look.getYPosition());

		final int timesToRepeat = (Integer) Reflection.getPrivateField(loopEndBrick, "timesToRepeat");
		final int forever = (Integer) Reflection.getPrivateField(loopEndBrick, "FOREVER");

		assertEquals("Wrong number of times to repeat", forever, timesToRepeat);

	}

	@FlakyTest(tolerance = 3)
	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;
		final int repeatTimes = 5;

		testSprite = new Sprite("testSprite");

		StartScript testScript = new StartScript(testSprite);

		LoopBeginBrick foreverBrick = new ForeverBrick(testSprite);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		final long startTime = System.currentTimeMillis();
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay * repeatTimes);

		final long endTime = System.currentTimeMillis();

		assertEquals("Loop delay did not work!", repeatTimes * deltaY, (int) testSprite.look.getYPosition());

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final long delayByContract = 20;
		assertEquals("Loop delay was not 20ms!", delayByContract * repeatTimes, endTime - startTime, 15);

	}

	@FlakyTest(tolerance = 3)
	public void testNoDelayAtBeginOfLoop() throws InterruptedException {
		testSprite = new Sprite("testSprite");

		StartScript testScript = new StartScript(testSprite);

		LoopBeginBrick foreverBrick = new ForeverBrick(testSprite);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;
		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.startStartScripts();

		Thread.sleep(expectedDelay / 5);

		assertEquals("There was an unexpected delay at the begin of the loop!", deltaY,
				(int) testSprite.look.getYPosition());

	}
}
