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
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.GoNStepsBackAction;

import android.test.AndroidTestCase;

public class GoNStepsBackActionTest extends AndroidTestCase {

	private final int steps = 17;

	public void testSteps() {
		Sprite sprite = new Sprite("testSprite");
		assertEquals("Unexpected initial sprite Z position", 0, sprite.look.zPosition);

		int oldPosition = sprite.look.zPosition;

		GoNStepsBackAction action = ExtendedActions.goNStepsBack(sprite, steps);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed", (oldPosition - steps),
				sprite.look.zPosition);

		oldPosition = sprite.look.zPosition;

		action = ExtendedActions.goNStepsBack(sprite, -steps);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("Incorrect sprite Z position after GoNStepsBackBrick executed", (oldPosition + steps),
				sprite.look.zPosition);
	}

	public void testNullSprite() {
		GoNStepsBackAction action = ExtendedActions.goNStepsBack(null, steps);
		try {
			action.act(1.0f);
			fail("Execution of GoNStepsBackBrick with null Sprite did not cause a NullPointerException to be thrown");
		} catch (NullPointerException expected) {
			// expected behavior
		}
	}

	public void testBoundarySteps() {
		Sprite sprite = new Sprite("testSprite");

		int oldPosition = sprite.look.zPosition;

		GoNStepsBackAction action = ExtendedActions.goNStepsBack(sprite, Integer.MAX_VALUE);
		sprite.look.addAction(action);
		action.act(1.0f);
		assertEquals("GoNStepsBackBrick execution failed. Wrong Z position.", (oldPosition - Integer.MAX_VALUE),
				sprite.look.zPosition);

		action = ExtendedActions.goNStepsBack(sprite, Integer.MAX_VALUE);
		sprite.look.addAction(action);
		action.act(1.0f);
		action.restart();
		action.act(1.0f);
		assertEquals("An unwanted Integer underflow occured during GoNStepsBackBrick execution.", Integer.MIN_VALUE,
				sprite.look.zPosition);

		action = ExtendedActions.goNStepsBack(sprite, Integer.MIN_VALUE);
		sprite.look.addAction(action);
		action.act(1.0f);
		action.restart();
		action.act(1.0f);
		assertEquals("An unwanted Integer overflow occured during GoNStepsBackBrick execution.", Integer.MAX_VALUE,
				sprite.look.zPosition);
	}

}
