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

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.content.actions.RepeatAction;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

public class RepeatActionTest extends InstrumentationTestCase {

	private Sprite testSprite;
	private static final int REPEAT_TIMES = 4;
	final float delta = 0.005f;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");

	}

	public void testLoopDelay() throws InterruptedException {
		testSprite.removeAllScripts();
		Script testScript = new StartScript(testSprite);

		RepeatBrick repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, 150));

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequence();

		/*
		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
		 * http://code.google.com/p/catroid/issues/detail?id=28
		 */

		final float delayByContract = 0.020f;

		for (int index = 0; index < REPEAT_TIMES; index++) {

			for (float time = 0f; time < delayByContract; time += delta) {
				testSprite.look.act(delta);
			}
		}

		assertEquals("Loop delay did was not 20ms!", deltaY * REPEAT_TIMES, (int) testSprite.look.getYInUserInterfaceDimensionUnit());

	}

	public void testRepeatBrick() throws InterruptedException {
		testSprite.removeAllScripts();
		Script testScript = new StartScript(testSprite);

		RepeatBrick repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequence();

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * deltaY, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testRepeatCount() {
		testSprite.removeAllScripts();
		Script testScript = new StartScript(testSprite);

		Formula repeatFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.OBJECT_Y.name(), null));
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, repeatFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(new ChangeYByNBrick(testSprite, 10));
		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequence();

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals("Executed the wrong number of times!", deltaY * 9, (int) testSprite.look.getYInUserInterfaceDimensionUnit());

	}

	public void testNestedRepeatBrick() throws InterruptedException {
		final int deltaY = -10;

		testSprite.removeAllScripts();
		Script testScript = new StartScript(testSprite);

		RepeatBrick repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		RepeatBrick nestedRepeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
		LoopEndBrick nestedLoopEndBrick = new LoopEndBrick(testSprite, nestedRepeatBrick);
		nestedRepeatBrick.setLoopEndBrick(nestedLoopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(nestedRepeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(nestedLoopEndBrick);
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequence();

		float timePerActCycle = 0.5f;

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(timePerActCycle);
		}

		testSprite.look.act(delta);
		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * REPEAT_TIMES * deltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
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

		RepeatAction repeatAction = ExtendedActions.repeat(testSprite, new Formula(0),
				ExtendedActions.sequence(ExtendedActions.changeYByN(testSprite, new Formula(decoyDeltaY))));
		SequenceAction action = ExtendedActions.sequence(repeatAction,
				ExtendedActions.changeYByN(testSprite, new Formula(expectedDeltaY)));
		while (!action.act(1.0f)) {
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals("Executed the wrong number of times!", 0, executedCount);
		assertEquals("Loop was executed although repeats were set to zero!", expectedDeltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

}
