/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.actions;

import android.test.InstrumentationTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;

import java.util.HashMap;
import java.util.List;

public class ForeverActionTest extends InstrumentationTestCase {

	private static final int REPEAT_TIMES = 4;
	private final float delta = 0.005f;

	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;

		Sprite testSprite = new Sprite("testSprite");

		StartScript testScript = new StartScript();

		ForeverBrick foreverBrick = new ForeverBrick();
		LoopEndBrick loopEndBrick = new LoopEndBrick(foreverBrick);
		foreverBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(foreverBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */
		final float delayByContract = 0.020f;

		for (int index = 0; index < REPEAT_TIMES; index++) {

			for (double time = 0f; time < delayByContract; time += delta) {
				testSprite.look.act(delta);
			}
		}

		assertEquals("Loop delay did was not 20ms!", deltaY * REPEAT_TIMES,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}
}
