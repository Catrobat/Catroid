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

import android.test.InstrumentationTestCase;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
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
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.HashMap;
import java.util.List;

public class RepeatActionTest extends InstrumentationTestCase {

	private static final int REPEAT_TIMES = 4;
	private static final String NOT_NUMERICAL_STRING = "NOT_NUMERICAL_STRING";

	@Override
	protected void setUp() throws Exception {
	}

	public void testLoopDelay() throws InterruptedException {
		final int deltaY = -10;
		final float delta = 0.005f;
		final float delayByContract = 0.020f;
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);
		testScript.addBrick(new ChangeYByNBrick(150));

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		// http://code.google.com/p/catroid/issues/detail?id=28
		for (int index = 0; index < REPEAT_TIMES; index++) {
			for (double time = 0f; time < delayByContract; time += delta) {
				testSprite.look.act(delta);
			}
		}

		assertEquals("Loop delay did was not 20ms!", deltaY * REPEAT_TIMES,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testRepeatBrick() throws InterruptedException {
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript();

		RepeatBrick repeatBrick = new RepeatBrick(REPEAT_TIMES);
		LoopEndBrick loopEndBrick = new LoopEndBrick(repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final int deltaY = -10;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(deltaY));
		testScript.addBrick(loopEndBrick);

		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * deltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testRepeatCount() {
		Project project = new Project(null, TestUtils.DEFAULT_TEST_PROJECT_NAME);
		ProjectManager.getInstance().setProject(project);
		Sprite testSprite = new Sprite("sprite");
		ProjectManager.getInstance().setCurrentSprite(testSprite);
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
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}

		assertEquals("Executed the wrong number of times!", deltaY * 9,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNestedRepeatBrick() throws InterruptedException {
		final int deltaY = -10;
		final float delta = 0.005f;
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript();

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
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		float timePerActCycle = 0.5f;

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(timePerActCycle);
		}

		testSprite.look.act(delta);
		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * REPEAT_TIMES * deltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNegativeRepeats() throws InterruptedException {
		Sprite testSprite = new Sprite("sprite");
		RepeatBrick repeatBrick = new RepeatBrick(-1);
		SequenceAction sequence = ExtendedActions.sequence();
		repeatBrick.addActionToSequence(testSprite, sequence);
		RepeatAction repeatAction = (RepeatAction) sequence.getActions().get(0);
		boolean wait = false;
		while (!wait) {
			wait = sequence.act(1.0f);
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals("Executed the wrong number of times!", 0, executedCount);
	}

	public void testZeroRepeats() throws InterruptedException {
		Sprite testSprite = new Sprite("sprite");
		final int decoyDeltaY = -150;
		final int expectedDeltaY = 150;

		RepeatAction repeatAction = ExtendedActions.repeat(testSprite, new Formula(0),
				ExtendedActions.sequence(ExtendedActions.changeYByN(testSprite, new Formula(decoyDeltaY))));
		SequenceAction action = ExtendedActions.sequence(repeatAction,
				ExtendedActions.changeYByN(testSprite, new Formula(expectedDeltaY)));
		boolean wait = false;
		while (!wait) {
			wait = action.act(1.0f);
		}
		int executedCount = (Integer) Reflection.getPrivateField(repeatAction, "executedCount");

		assertEquals("Executed the wrong number of times!", 0, executedCount);
		assertEquals("Loop was executed although repeats were set to zero!", expectedDeltaY,
				(int) testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithValidStringFormula() {
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript(testSprite);
		Formula stringFormula = new Formula(String.valueOf(REPEAT_TIMES));
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, stringFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final float startValue = testSprite.look.getYInUserInterfaceDimensionUnit();
		final int deltaY = 5;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals("Executed the wrong number of times!", startValue + deltaY * REPEAT_TIMES,
				testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testBrickWithInValidStringFormula() {
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript(testSprite);
		Formula stringFormula = new Formula(String.valueOf(NOT_NUMERICAL_STRING));
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, stringFormula);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final float startValue = testSprite.look.getYInUserInterfaceDimensionUnit();
		final int deltaY = 5;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals("Executed the wrong number of times!", startValue,
				testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNullFormula() {
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript(testSprite);
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, null);
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final float startValue = testSprite.look.getYInUserInterfaceDimensionUnit();
		final int deltaY = 5;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals("Executed the wrong number of times!", startValue,
				testSprite.look.getYInUserInterfaceDimensionUnit());
	}

	public void testNotANumberFormula() {
		Sprite testSprite = new Sprite("sprite");
		Script testScript = new StartScript(new Sprite("sprite"));
		RepeatBrick repeatBrick = new RepeatBrick(testSprite, new Formula(Double.NaN));
		LoopEndBrick loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
		repeatBrick.setLoopEndBrick(loopEndBrick);

		final float startValue = testSprite.look.getYInUserInterfaceDimensionUnit();
		final int deltaY = 5;

		testScript.addBrick(repeatBrick);
		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
		testScript.addBrick(loopEndBrick);
		testSprite.addScript(testScript);
		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
		assertEquals("Executed the wrong number of times!", startValue,
				testSprite.look.getYInUserInterfaceDimensionUnit());
	}
}
