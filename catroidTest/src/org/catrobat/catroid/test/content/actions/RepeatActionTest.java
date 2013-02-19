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
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.test.utils.Reflection;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class RepeatActionTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private static final int REPEAT_TIMES = 4;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
	}

	public void testRepeatBrick() throws InterruptedException {
		final int deltaY = -10;

		RepeatAction action = ExtendedActions.repeat(REPEAT_TIMES,
				ExtendedActions.sequence(ExtendedActions.changeYByN(testSprite, deltaY)));
		while (!action.act(1.0f)) {
		}
		int executedCount = (Integer) Reflection.getPrivateField(action, "executedCount");

		assertEquals("Executed the wrong number of times!", REPEAT_TIMES, executedCount);
		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * deltaY, (int) testSprite.look.getYPosition());
	}

	public void testNegativeRepeats() throws InterruptedException {
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, -1);
		SequenceAction sequence = ExtendedActions.sequence();
		repeatBrick.addActionToSequence(sequence);
		RepeatAction repeatAction = (RepeatAction) sequence.getActions().get(0);

		while (!sequence.act(1.0f)) {
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals("Executed the wrong number of times!", 0, executedCount);
	}

	public void testZeroRepeats() throws InterruptedException {
		final int decoyDeltaY = -150;
		final int expectedDeltaY = 150;

		RepeatAction repeatAction = ExtendedActions.repeat(0,
				ExtendedActions.sequence(ExtendedActions.changeYByN(testSprite, decoyDeltaY)));
		SequenceAction action = ExtendedActions.sequence(repeatAction,
				ExtendedActions.changeYByN(testSprite, expectedDeltaY));
		while (!action.act(1.0f)) {
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals("Executed the wrong number of times!", 0, executedCount);
		assertEquals("Loop was executed although repeats were set to zero!", expectedDeltaY,
				(int) testSprite.look.getYPosition());
	}
}
