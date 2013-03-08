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
package org.catrobat.catroid.test.content.actions;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopBeginBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;

import android.test.InstrumentationTestCase;

public class ForeverActionTest extends InstrumentationTestCase {

	private static final int REPEAT_TIMES = 100;

	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;

		Sprite testSprite = new Sprite("testSprite");

		StartScript testScript = new StartScript(testSprite);

		LoopBeginBrick foreverBrick = new ForeverBrick(testSprite);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, REPEAT_TIMES * -deltaY));

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequence();

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final float expectedDelay = 0.20f;

		for (int index = 0; index < REPEAT_TIMES; index++) {

			/*
			 * Run two times with "expectedDelay * 0.5" because of SequenceAction-Bug in
			 * com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
			 */
			testSprite.look.act(expectedDelay * 0.5f);
			testSprite.look.act(expectedDelay * 0.5f);
		}

		assertEquals("Loop delay did not work!", deltaY * REPEAT_TIMES, (int) testSprite.look.getYPosition());
	}

}
