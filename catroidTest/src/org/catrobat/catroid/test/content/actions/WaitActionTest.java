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

import android.test.AndroidTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.DelayAction;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.WaitAction;
import org.catrobat.catroid.formulaeditor.Formula;

public class WaitActionTest extends AndroidTestCase {

	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private static final float VALUE = 2f;

	public void testWait() throws InterruptedException {
		float waitOneSecond = 1.0f;
		WaitAction action = ExtendedActions.delay(null, new Formula(waitOneSecond));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));

		assertTrue("Unexpected waited time!", (action.getTime() - waitOneSecond) > 0.5f);
	}

	public void testPauseResume() throws InterruptedException {
		Sprite testSprite = new Sprite("testSprite");
		float waitOneSecond = 1.0f;
		DelayAction action = ExtendedActions.delay(waitOneSecond);
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

	public void testBrickWithStringFormula() {
		WaitAction action = ExtendedActions.delay(null, new Formula(String.valueOf(VALUE)));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertTrue("Unexpected waited time!", (action.getTime() - VALUE) > 0.5f);

		action = ExtendedActions.delay(null, new Formula(NOT_NUMERICAL_STRING));
		currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertTrue("Unexpected waited time!", action.getTime() == 0f);
	}

	public void testNullFormula() {
		WaitAction action = ExtendedActions.delay(null, null);
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertTrue("Unexpected waited time!", action.getTime() == 0f);
	}

	public void testNotANumberFormula() {
		WaitAction action = ExtendedActions.delay(null, new Formula(Double.NaN));
		long currentTimeInMilliSeconds = System.currentTimeMillis();
		do {
			currentTimeInMilliSeconds = System.currentTimeMillis() - currentTimeInMilliSeconds;
		} while (!action.act(currentTimeInMilliSeconds / 1000f));
		assertTrue("Unexpected waited time!", action.getTime() == 0f);
	}
}
