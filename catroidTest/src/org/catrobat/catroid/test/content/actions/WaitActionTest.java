/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.test.AndroidTestCase;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.physics.content.ActionFactory;

public class WaitActionTest extends AndroidTestCase {

	public void testWait() throws InterruptedException {
		float waitOneSecond = 1.0f;
		ActionFactory factory = new ActionFactory();
		WaitAction action = factory.createDelayAction(null, new Formula(waitOneSecond));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertTrue("Unexpected waited time!", (action.getTime() - waitOneSecond) > 0.5f);
	}

	public void testPauseResume() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		float waitOneSecond = 1.0f;

		ActionFactory factory = testSprite.getActionFactory();
		WaitAction action = factory.createDelayAction(testSprite, new Formula(waitOneSecond));
		testSprite.look.addAction(action);
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
			if (currentTimeInMilliSeconds > 400) {
				testSprite.pause();
				Thread.sleep(200);
				testSprite.resume();
			}
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertTrue("Unexpected waited time!", (action.getTime() - waitOneSecond) > 0.5f);
	}
}
