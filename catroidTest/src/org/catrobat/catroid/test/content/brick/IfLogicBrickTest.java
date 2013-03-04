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
package org.catrobat.catroid.test.content.brick;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick;
import org.catrobat.catroid.content.bricks.IfLogicElseBrick;
import org.catrobat.catroid.content.bricks.IfLogicEndBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.Operators;

import android.test.InstrumentationTestCase;

public class IfLogicBrickTest extends InstrumentationTestCase {

	private static final String TEST_USERVARIABLE = "testUservariable";
	private Sprite testSprite;
	private StartScript testScript;
	private IfLogicBeginBrick ifLogicBeginBrick;
	private IfLogicElseBrick ifLogicElseBrick;
	private IfLogicEndBrick ifLogicEndBrick;

	//	private static final int REPEAT_TIMES = 4;
	//	private LoopEndBrick loopEndBrick;
	//	private LoopBeginBrick repeatBrick;

	@Override
	protected void setUp() throws Exception {
		testSprite = new Sprite("testSprite");
	}

	public void testIfBrick() {
		testSprite.removeAllScripts();

		ProjectManager.getInstance().getCurrentProject().getUserVariables()
				.addProjectUserVariable(TEST_USERVARIABLE, 0d);

		SetVariableBrick setVariableBrick = new SetVariableBrick(testSprite, new Formula(42), null);

		Formula validFormula = new Formula(1);
		validFormula.setRoot(new FormulaElement(ElementType.OPERATOR, Operators.SMALLER_THAN.operatorName, null,
				new FormulaElement(ElementType.NUMBER, "1", null), new FormulaElement(ElementType.NUMBER, "2", null)));

		testScript = new StartScript(testSprite);

		ifLogicBeginBrick = new IfLogicBeginBrick(testSprite, validFormula);
		ifLogicElseBrick = new IfLogicElseBrick(testSprite, ifLogicBeginBrick);
		ifLogicEndBrick = new IfLogicEndBrick(testSprite, ifLogicElseBrick, ifLogicBeginBrick);

		testScript.addBrick(ifLogicBeginBrick);
		testScript.addBrick(ifLogicElseBrick);
		testScript.addBrick(ifLogicEndBrick);

		testSprite.addScript(testScript);

	}
	//	public void testRepeatBrick() throws InterruptedException {
	//		testSprite.removeAllScripts();
	//		testScript = new StartScript(testSprite);
	//
	//		repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
	//		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
	//		repeatBrick.setLoopEndBrick(loopEndBrick);
	//
	//		final int deltaY = -10;
	//		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");
	//
	//		testScript.addBrick(repeatBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
	//		testScript.addBrick(loopEndBrick);
	//
	//		testSprite.addScript(testScript);
	//		testSprite.startStartScripts();
	//
	//		/*
	//		 * Let's wait even longer than necessary, then check if we only executed N times, not N+1 times
	//		 * http://code.google.com/p/catroid/issues/detail?id=24
	//		 */
	//		Thread.sleep(expectedDelay * (REPEAT_TIMES + 1));
	//
	//		assertEquals("Executed the wrong number of times!", REPEAT_TIMES * deltaY, (int) testSprite.look.getYPosition());
	//	}
	//
	//	@FlakyTest(tolerance = 3)
	//	public void testLoopDelay() throws InterruptedException {
	//		testSprite.removeAllScripts();
	//		testScript = new StartScript(testSprite);
	//
	//		repeatBrick = new RepeatBrick(testSprite, REPEAT_TIMES);
	//		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
	//		repeatBrick.setLoopEndBrick(loopEndBrick);
	//
	//		final int deltaY = -10;
	//		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");
	//
	//		testScript.addBrick(repeatBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
	//		testScript.addBrick(loopEndBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, 150));
	//
	//		testSprite.addScript(testScript);
	//		final long startTime = System.currentTimeMillis();
	//		testSprite.startStartScripts();
	//
	//		Thread.sleep(expectedDelay * REPEAT_TIMES);
	//
	//		assertEquals("Loop delay did not work!", REPEAT_TIMES * deltaY, (int) testSprite.look.getYPosition());
	//
	//		/*
	//		 * This is only to document that a delay of 20ms is by contract. See Issue 28 in Google Code
	//		 * http://code.google.com/p/catroid/issues/detail?id=28
	//		 */
	//		final long delayByContract = 20;
	//		final long endTime = System.currentTimeMillis();
	//		assertEquals("Loop delay did was not 20ms!", delayByContract * REPEAT_TIMES, endTime - startTime, 15);
	//	}
	//
	//	public void testNegativeRepeats() throws InterruptedException {
	//		testSprite.removeAllScripts();
	//		testScript = new StartScript(testSprite);
	//
	//		repeatBrick = new RepeatBrick(testSprite, -1);
	//		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
	//		repeatBrick.setLoopEndBrick(loopEndBrick);
	//
	//		final int decoyDeltaY = -150;
	//		final int expectedDeltaY = 150;
	//		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");
	//
	//		testScript.addBrick(repeatBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, decoyDeltaY));
	//		testScript.addBrick(loopEndBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, expectedDeltaY));
	//
	//		testSprite.addScript(testScript);
	//		testSprite.startStartScripts();
	//
	//		/*
	//		 * Waiting less than what a loop delay would be! Loop should not execute and there should be no delay
	//		 * http://code.google.com/p/catroid/issues/detail?id=24#c9
	//		 */
	//		Thread.sleep(expectedDelay / 2);
	//
	//		assertEquals("Loop was executed although repeats were less than zero!", expectedDeltaY,
	//				(int) testSprite.look.getYPosition());
	//	}
	//
	//	public void testZeroRepeats() throws InterruptedException {
	//		testSprite.removeAllScripts();
	//		testScript = new StartScript(testSprite);
	//
	//		repeatBrick = new RepeatBrick(testSprite, 0);
	//		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
	//		repeatBrick.setLoopEndBrick(loopEndBrick);
	//
	//		final int decoyDeltaY = -150;
	//		final int expectedDeltaY = 150;
	//		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");
	//
	//		testScript.addBrick(repeatBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, decoyDeltaY));
	//		testScript.addBrick(loopEndBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, expectedDeltaY));
	//
	//		testSprite.addScript(testScript);
	//		testSprite.startStartScripts();
	//
	//		/*
	//		 * Waiting less than what a loop delay would be! Loop should not execute and there should be no delay
	//		 * http://code.google.com/p/catroid/issues/detail?id=24#c9
	//		 */
	//		Thread.sleep(expectedDelay / 2);
	//
	//		assertEquals("Loop was executed although repeats were set to zero!", expectedDeltaY,
	//				(int) testSprite.look.getYPosition());
	//	}
	//
	//	@FlakyTest(tolerance = 3)
	//	public void testNoDelayAtBeginOfLoop() throws InterruptedException {
	//		testSprite.removeAllScripts();
	//		testScript = new StartScript(testSprite);
	//
	//		repeatBrick = new RepeatBrick(testSprite, 1);
	//		loopEndBrick = new LoopEndBrick(testSprite, repeatBrick);
	//		repeatBrick.setLoopEndBrick(loopEndBrick);
	//
	//		final int deltaY = -10;
	//		final int expectedDelay = (Integer) Reflection.getPrivateField(loopEndBrick, "LOOP_DELAY");
	//
	//		testScript.addBrick(repeatBrick);
	//		testScript.addBrick(new ChangeYByNBrick(testSprite, deltaY));
	//		testScript.addBrick(loopEndBrick);
	//
	//		testSprite.addScript(testScript);
	//		testSprite.startStartScripts();
	//
	//		Thread.sleep(expectedDelay / 5);
	//
	//		assertEquals("There was an unexpected delay at the begin of the loop!", deltaY,
	//				(int) testSprite.look.getYPosition());
	//	}
}
