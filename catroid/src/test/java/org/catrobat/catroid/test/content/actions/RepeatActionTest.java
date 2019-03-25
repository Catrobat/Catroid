/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import com.badlogic.gdx.scenes.scene2d.Action;

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.EventThread;
import org.catrobat.catroid.content.actions.RepeatAction;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.LoopEndBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RepeatActionTest {

	private static final int REPEAT_TIMES = 4;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private Sprite testSprite;
	private Script testScript;
	private int delta = 5;

	@Before
	public void setUp() throws Exception {
		testSprite = new SingleSprite("sprite");
		testScript = new StartScript();
	}

	@Test
	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;
		final float delta = 0.005f;
		final float delayByContract = 0.020f;

		RepeatBrick repeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByNBrick(150));

		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		// http://code.google.com/p/catroid/issues/detail?id=28
		for (int index = 0; index < REPEAT_TIMES; index++) {
			for (double time = 0f; time < delayByContract; time += delta) {
				testSprite.look.act(delta);
			}
		}

		assertEquals(deltaY * REPEAT_TIMES, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRepeatBrick() {

		RepeatBrick repeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);

		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals(REPEAT_TIMES * deltaY, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRepeatCount() {
		Sprite testSprite = new SingleSprite("sprite");
		Script testScript = new StartScript();

		Formula repeatFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.OBJECT_Y.name(), null));
		RepeatBrick repeatBrick = new RepeatBrick(repeatFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(new ChangeYByNBrick(10));
		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals(deltaY * 9, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNestedRepeatBrick() throws InterruptedException {
		final int deltaY = -10;
		final float delta = 0.005f;

		RepeatBrick repeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);

		repeatBrick.setLoopEndBrick(loopEndBrick);

		RepeatBrick nestedRepeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick nestedLoopEndBrick = new LoopEndBrick(nestedRepeatBrick);
		nestedRepeatBrick.setLoopEndBrick(nestedLoopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(nestedRepeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(nestedLoopEndBrick);
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		float timePerActCycle = 0.5f;

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(timePerActCycle);
		}

		testSprite.look.act(delta);
		assertEquals(REPEAT_TIMES * REPEAT_TIMES * deltaY, (int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNegativeRepeats() throws Exception {
		Sprite testSprite = new SingleSprite("sprite");
		RepeatBrick repeatBrick = new RepeatBrick(-1);

		EventThread sequence = (EventThread) testSprite.getActionFactory().createEventThread(new StartScript());
		repeatBrick.addActionToSequence(testSprite, sequence);

		RepeatAction repeatAction = (RepeatAction) sequence.getActions().get(0);
		boolean wait = false;
		while (!wait) {
			wait = sequence.act(1.0f);
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals(0, executedCount);
	}

	@Test
	public void testZeroRepeats() throws Exception {
		final float decoyDeltaY = -150f;
		final float expectedDeltaY = 150f;

		final RepeatAction repeatAction = (RepeatAction) testSprite.getActionFactory().createRepeatAction(testSprite,
				new Formula(0), testSprite.getActionFactory().createChangeYByNAction(testSprite, new Formula(decoyDeltaY)));
		repeatAction.act(1f);

		testSprite.getActionFactory().createChangeYByNAction(testSprite, new Formula(expectedDeltaY)).act(1f);

		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals(0, executedCount);
		assertEquals(expectedDeltaY, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBrickWithValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(REPEAT_TIMES));
		testWithFormula(stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit() + delta * REPEAT_TIMES);
	}

	@Test
	public void testBrickWithInValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));
		testWithFormula(stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() throws Exception {
		Action repeatedAction = testSprite.getActionFactory().createSetXAction(testSprite, new Formula(10));
		Action repeatAction = testSprite.getActionFactory().createRepeatAction(testSprite, null, repeatedAction);
		repeatAction.act(1.0f);
		Object repeatCountValue = Reflection.getPrivateField(repeatAction, "repeatCountValue");
		assertEquals(0, repeatCountValue);
	}

	@Test
	public void testNotANumberFormula() {
		Formula notANumber = new Formula(Double.NaN);
		testWithFormula(notANumber, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	private void testWithFormula(Formula formula, Float expected) {
		RepeatBrick repeatBrick = new RepeatBrick(formula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(delta));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.initializeEventThreads(EventId.START);

		while (!testSprite.look.haveAllThreadsFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals(expected, testSprite.look.getYInUserInterfaceDimensionUnit());
	}
}
