/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick;
import org.catrobat.catroid.content.eventids.EventId;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.test.StaticSingletonInitializer.initializeStaticSingletonMethods;

@RunWith(JUnit4.class)
public class ForVariableFromToActionTest {
	Sprite sprite;
	Script script;
	UserVariable executedLoops;
	UserVariable controlVariable;
	ChangeVariableBrick changeVariableBrick;

	@Before
	public void setUp() {
		initializeStaticSingletonMethods();
		executedLoops = new UserVariable("executedLoops", 0.0);
		controlVariable = new UserVariable("controlVariable", 0.0);

		sprite = new Sprite("sprite");
		script = new StartScript();

		changeVariableBrick = new ChangeVariableBrick(new Formula(1.0), executedLoops);
	}

	@Test
	public void testExecutedLoopsPositiveCount() {
		int start = -3;
		int end = 3;
		ForVariableFromToBrick forLoopBrick = new ForVariableFromToBrick(start, end);

		forLoopBrick.setUserVariable(controlVariable);
		forLoopBrick.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrick);
		sprite.addScript(script);

		startEventThread();

		assertEquals((double) end, controlVariable.getValue());
		assertEquals(7.0, executedLoops.getValue());
	}

	@Test
	public void testExecutedLoopsNegativeCount() {
		int start = 3;
		int end = -3;
		ForVariableFromToBrick forLoopBrick = new ForVariableFromToBrick(start, end);

		forLoopBrick.setUserVariable(controlVariable);
		forLoopBrick.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrick);
		sprite.addScript(script);
		startEventThread();

		assertEquals((double) end, controlVariable.getValue());
		assertEquals(7.0, executedLoops.getValue());
	}

	@Test
	public void testExecutedLoopsZeroCount() {
		ForVariableFromToBrick forLoopBrick = new ForVariableFromToBrick(123, 123);

		forLoopBrick.setUserVariable(controlVariable);
		forLoopBrick.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrick);
		sprite.addScript(script);
		startEventThread();

		assertEquals(123.0, controlVariable.getValue());
		assertEquals(1.0, executedLoops.getValue());
	}

	@Test
	public void testNestedForLoops() {
		int start = 1;
		int end = 10;
		UserVariable controlVariableInner = new UserVariable("controlVariableInner", 0.0);
		ForVariableFromToBrick forLoopBrickOuter = new ForVariableFromToBrick(start, end);
		ForVariableFromToBrick forLoopBrickInner = new ForVariableFromToBrick(start, end);

		forLoopBrickOuter.setUserVariable(controlVariable);
		forLoopBrickInner.setUserVariable(controlVariableInner);
		forLoopBrickOuter.addBrick(forLoopBrickInner);
		forLoopBrickInner.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrickOuter);
		sprite.addScript(script);
		startEventThread();

		assertEquals((double) end, controlVariable.getValue());
		assertEquals((double) end, controlVariableInner.getValue());
		assertEquals(100.0, executedLoops.getValue());
	}

	@Test
	public void testValidStringFormula() {
		ForVariableFromToBrick forLoopBrick = new ForVariableFromToBrick(new Formula(
				"5"), new Formula(10));

		forLoopBrick.setUserVariable(controlVariable);
		forLoopBrick.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrick);
		sprite.addScript(script);
		startEventThread();

		assertEquals(10.0, controlVariable.getValue());
		assertEquals(6.0, executedLoops.getValue());
	}

	@Test
	public void testInvalidStringFormula() {
		ForVariableFromToBrick forLoopBrick = new ForVariableFromToBrick(new Formula(
				"InvalidString"), new Formula(10));

		forLoopBrick.setUserVariable(controlVariable);
		forLoopBrick.addBrick(changeVariableBrick);
		script.addBrick(forLoopBrick);
		sprite.addScript(script);
		startEventThread();

		assertEquals(0.0, controlVariable.getValue());
		assertEquals(0.0, executedLoops.getValue());
	}

	private void startEventThread() {
		sprite.initializeEventThreads(EventId.START);

		while (!sprite.look.haveAllThreadsFinished()) {
			sprite.look.act(1.0f);
		}
	}
}
