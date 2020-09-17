/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.content.ActionFactory;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.actions.RepeatAction;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;
import org.catrobat.catroid.content.bricks.ChangeYByNBrick;
import org.catrobat.catroid.content.bricks.RepeatBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class RepeatActionTest {

	private static final int REPEAT_TIMES = 4;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";
	private int delta = 5;

	@Test
	public void testLoopDelay() {
		final int deltaY = -10;
		final float delta = 0.005f;
		final float delayByContract = 0.020f;

		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(new Formula(REPEAT_TIMES));
		repeatBrick.addBrick(new ChangeYByNBrick(deltaY));
		script.addBrick(repeatBrick);
		script.addBrick(new ChangeYByNBrick(150));

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		for (int index = 0; index < REPEAT_TIMES; index++) {
			for (double time = 0f; time < delayByContract; time += delta) {
				sprite.look.act(delta);
			}
		}

		assertEquals(deltaY * REPEAT_TIMES, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRepeatBrick() {
		final int deltaY = -10;

		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(new Formula(REPEAT_TIMES));
		repeatBrick.addBrick(new ChangeYByNBrick(deltaY));
		script.addBrick(repeatBrick);

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals(REPEAT_TIMES * deltaY, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testRepeatCount() {
		Sprite sprite = new Sprite("sprite");
		Script script = new StartScript();

		Formula repeatFormula = new Formula(new FormulaElement(ElementType.SENSOR, Sensors.OBJECT_Y.name(), null));

		final int deltaY = -10;

		script.addBrick(new ChangeYByNBrick(10));
		RepeatBrick repeatBrick = new RepeatBrick(repeatFormula);
		repeatBrick.addBrick(new ChangeYByNBrick(deltaY));
		script.addBrick(repeatBrick);

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}

		assertEquals(deltaY * 9, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNestedRepeatBrick() {
		final int deltaY = -10;
		final float delta = 0.005f;

		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(new Formula(REPEAT_TIMES));

		RepeatBrick nestedRepeatBrick = new RepeatBrick(new Formula(REPEAT_TIMES));
		nestedRepeatBrick.addBrick(new ChangeYByNBrick(deltaY));

		repeatBrick.addBrick(nestedRepeatBrick);

		script.addBrick(repeatBrick);

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		float timePerActCycle = 0.5f;

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(timePerActCycle);
		}

		sprite.look.act(delta);
		assertEquals(REPEAT_TIMES * REPEAT_TIMES * deltaY, (int) sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNegativeRepeats() throws Exception {
		Sprite sprite = new Sprite("sprite");

		RepeatBrick repeatBrick = new RepeatBrick(new Formula(-1));

		ScriptSequenceAction sequence = (ScriptSequenceAction) ActionFactory.createScriptSequenceAction(new StartScript());
		repeatBrick.addActionToSequence(sprite, sequence);

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

		Sprite sprite = new Sprite("testSprite");

		Action repeatAction = sprite.getActionFactory()
				.createRepeatAction(sprite, new Formula(0), sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(decoyDeltaY)));

		repeatAction.act(1f);

		sprite.getActionFactory().createChangeYByNAction(sprite, new Formula(expectedDeltaY)).act(1f);

		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals(0, executedCount);
		assertEquals(expectedDeltaY, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testBrickWithValidStringFormula() {
		Formula stringFormula = new Formula(String.valueOf(REPEAT_TIMES));
		Sprite testSprite = new Sprite("testSprite");
		testWithFormula(testSprite,
				stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit() + delta * REPEAT_TIMES);
	}

	@Test
	public void testBrickWithInvalidStringFormula() {
		Formula stringFormula = new Formula(NOT_NUMERICAL_STRING);
		Sprite testSprite = new Sprite("testSprite");
		testWithFormula(testSprite, stringFormula, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	@Test
	public void testNullFormula() throws Exception {
		Sprite testSprite = new Sprite("testSprite");
		Action repeatedAction = testSprite.getActionFactory().createSetXAction(testSprite, new Formula(10));
		Action repeatAction = testSprite.getActionFactory().createRepeatAction(testSprite, null, repeatedAction);
		repeatAction.act(1.0f);
		Object repeatCountValue = Reflection.getPrivateField(repeatAction, "repeatCountValue");
		assertEquals(0, repeatCountValue);
	}

	@Test
	public void testNotANumberFormula() {
		Formula notANumber = new Formula(Double.NaN);
		Sprite testSprite = new Sprite("testSprite");
		testWithFormula(testSprite, notANumber, testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	private void testWithFormula(Sprite sprite, Formula formula, Float expected) {
		Script script = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(formula);
		repeatBrick.addBrick(new ChangeYByNBrick(delta));
		script.addBrick(repeatBrick);

		sprite.addScript(script);
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}
		assertEquals(expected, sprite.look.getYInUserInterfaceDimensionUnit());
	}
}
