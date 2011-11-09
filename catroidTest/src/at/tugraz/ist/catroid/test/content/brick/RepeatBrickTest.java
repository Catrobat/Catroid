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
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.RepeatBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.test.utils.TestUtils;

public class RepeatBrickTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private StartScript testScript;
	private int brickSleepTime = 1000;
	private int positionOfFirstWaitBrick;
	private int positionOfSecondWaitBrick;
	private int repeatTimes = 3;
	private LoopEndBrick loopEndBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
		testScript = new StartScript("testScript", testSprite);

		ShowBrick showBrick = new ShowBrick(testSprite);
		RepeatBrick foreverBrick = new RepeatBrick(testSprite, repeatTimes);
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

	public void testForeverBrick() throws Exception {
		testSprite.startStartScripts();

		Thread.sleep(brickSleepTime / 2);

		int timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", repeatTimes, timesToRepeat);

		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);

		timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", repeatTimes - 1, timesToRepeat);

		Thread.sleep(brickSleepTime * (repeatTimes - 1) * 2);
		assertEquals("Wrong brick executing", positionOfFirstWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);
		assertEquals("Wrong brick executing", positionOfSecondWaitBrick, testScript.getExecutingBrickIndex());
		Thread.sleep(brickSleepTime);

		timesToRepeat = (Integer) TestUtils.getPrivateField("timesToRepeat", loopEndBrick, false);
		assertEquals("Wrong number of times to repeat", 0, timesToRepeat);
	}
}
