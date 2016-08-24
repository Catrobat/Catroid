/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.test.content.sprite;

import android.test.AndroidTestCase;
import android.util.Log;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick;
import org.catrobat.catroid.content.bricks.ChangeXByNBrick;
import org.catrobat.catroid.content.bricks.HideBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.content.bricks.UserBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrick;
import org.catrobat.catroid.content.bricks.UserScriptDefinitionBrickElement;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.test.utils.TestUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpriteTest extends AndroidTestCase {

	private static final String LOCAL_VARIABLE_NAME = "test_local";
	private static final double LOCAL_VARIABLE_VALUE = 0xDEADBEEF;

	private static final String GLOBAL_VARIABLE_NAME = "test_global";
	private static final double GLOBAL_VARIABLE_VALUE = 0xC0FFEE;

	private static final String TAG = SpriteTest.class.getName();

	private Sprite sprite;
	private Project project;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		sprite = new SingleSprite("testSprite");
		project = new Project(getContext(), TestUtils.DEFAULT_TEST_PROJECT_NAME);
		project.getDefaultScene().addSprite(sprite);
		project.getDefaultScene().getDataContainer().addSpriteUserVariableToSprite(sprite, LOCAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, sprite).setValue(LOCAL_VARIABLE_VALUE);

		project.getDefaultScene().getDataContainer().addProjectUserVariable(GLOBAL_VARIABLE_NAME);
		project.getDefaultScene().getDataContainer().getUserVariable(GLOBAL_VARIABLE_NAME, null).setValue(GLOBAL_VARIABLE_VALUE);

		ProjectManager.getInstance().setProject(project);
	}

	public void testAddScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		assertEquals("Script list does not contain script after adding", 1, sprite.getNumberOfScripts());

		sprite.addScript(0, secondScript);
		assertEquals("Script list does not contain script after adding", 2, sprite.getNumberOfScripts());

		assertEquals("Script list does not contain script after adding", 1, sprite.getScriptIndex(firstScript));
		assertEquals("Script list does not contain script after adding", 0, sprite.getScriptIndex(secondScript));

		sprite.removeAllScripts();
		assertEquals("Script list could not be cleared", 0, sprite.getNumberOfScripts());
	}

	public void testGetScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Scripts do not match after retrieving", firstScript, sprite.getScript(0));
		assertEquals("Script doo not match after retrieving", secondScript, sprite.getScript(1));
	}

	public void testRemoveAllScripts() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeAllScripts();

		assertEquals("Script list was not cleared", 0, sprite.getNumberOfScripts());
	}

	public void testRemoveScript() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);

		sprite.removeScript(firstScript);

		assertEquals("Wrong script list size", 1, sprite.getNumberOfScripts());
		assertEquals("Wrong script remained", secondScript, sprite.getScript(0));
	}

	public void testGetScriptIndex() {
		Sprite sprite = new SingleSprite("new SingleSprite");
		Script firstScript = new StartScript();
		Script secondScript = new StartScript();
		sprite.addScript(firstScript);
		sprite.addScript(secondScript);
		assertEquals("Indexes do not match", 0, sprite.getScriptIndex(firstScript));
		assertEquals("Indexes do not match", 1, sprite.getScriptIndex(secondScript));
	}

	public void testPauseResume() throws InterruptedException {
		Sprite testSprite = new SingleSprite("testSprite");
		Script testScript = new StartScript();
		HideBrick hideBrick = new HideBrick();
		ShowBrick showBrick = new ShowBrick();

		for (int i = 0; i < 10000; i++) {
			testScript.addBrick(hideBrick);
			testScript.addBrick(showBrick);
		}

		testSprite.addScript(testScript);

		testSprite.createStartScriptActionSequenceAndPutToMap(new HashMap<String, List<String>>());

		testSprite.look.act(1.0f);

		testSprite.pause();
		assertTrue("Sprite isn't paused", testSprite.isPaused);
		assertTrue("Script isn't paused", testScript.isPaused());

		testSprite.resume();

		assertFalse("Sprite is paused", testSprite.isPaused);
		assertFalse("Script is paused", testScript.isPaused());

		while (!testSprite.look.getAllActionsAreFinished()) {
			testSprite.look.act(1.0f);
		}
	}

	public void testSpriteCloneWithLocalVariable() {
		Script script = new StartScript();
		Brick brick = new ChangeBrightnessByNBrick(new Formula(new FormulaElement(ElementType.USER_VARIABLE,
				LOCAL_VARIABLE_NAME, null)));
		script.addBrick(brick);
		sprite.addScript(script);
		Sprite clonedSprite = sprite.clone();

		UserVariable clonedVariable = project.getDefaultScene().getDataContainer().getUserVariable(LOCAL_VARIABLE_NAME, clonedSprite);
		assertNotNull("local variable isn't copied properly", clonedVariable);
		assertEquals("variable not cloned properly", LOCAL_VARIABLE_NAME, clonedVariable.getName());
		assertEquals("variable not cloned properly", LOCAL_VARIABLE_VALUE, clonedVariable.getValue());

		List<UserVariable> userVariableList = project.getDefaultScene().getDataContainer().getOrCreateVariableListForSprite(clonedSprite);
		Set<String> hashSet = new HashSet<>();
		for (UserVariable userVariable : userVariableList) {
			assertTrue("Variable already exists", hashSet.add(userVariable.getName()));
		}
	}

	public void testSpriteCloneWithUserBrick() {
		Integer moveValue = 0;
		Integer secondMoveValue = 4;

		UserBrick outerUserBrick = new UserBrick(new UserScriptDefinitionBrick());
		outerUserBrick.getDefinitionBrick().addUIText("outerBrick");
		outerUserBrick.getDefinitionBrick().addUILocalizedVariable("outerBrickVariable");
		sprite.addUserBrick(outerUserBrick);
		outerUserBrick.updateUserBrickParametersAndVariables();

		Formula innerFormula = new Formula(new FormulaElement(ElementType.USER_VARIABLE, "outerBrickVariable", null));
		outerUserBrick.getDefinitionBrick().getUserScript().addBrick(new ChangeXByNBrick(innerFormula));

		StartScript startScript = new StartScript();
		sprite.addScript(startScript);

		UserBrick outerBrickCopy = outerUserBrick.copyBrickForSprite(sprite);
		setOneFormula(outerBrickCopy, ElementType.NUMBER, moveValue.toString(), (float) moveValue, 1);
		startScript.addBrick(outerUserBrick);
		startScript.addBrick(outerBrickCopy);
		sprite.addUserBrick(outerBrickCopy);

		Sprite clonedSprite = sprite.clone();
		checkUserBrickSpriteClones(sprite, clonedSprite, 0);
		checkUserBrickSpriteClones(sprite, clonedSprite, 1);

		UserBrick clonedInnerBrick = (UserBrick) clonedSprite.getScript(0).getBrickList().get(0);
		setOneFormula(clonedInnerBrick, ElementType.NUMBER, secondMoveValue.toString(), (float) secondMoveValue, 1);

		runScriptOnSprite(sprite, 0, moveValue);
		runScriptOnSprite(clonedSprite, 0, secondMoveValue);

		runScriptOnSprite(sprite, moveValue, moveValue);
		runScriptOnSprite(clonedSprite, secondMoveValue, secondMoveValue);

		runScriptOnSprite(sprite, moveValue * 2, moveValue);
		runScriptOnSprite(clonedSprite, secondMoveValue * 2, secondMoveValue);
	}

	private void checkUserBrickSpriteClones(Sprite originalSprite, Sprite clonedSprite, int indexOfUserbrick) {
		UserBrick brick = originalSprite.getUserBrickList().get(indexOfUserbrick);
		UserBrick clonedBrick = clonedSprite.getUserBrickList().get(indexOfUserbrick);

		assertNotSame("Cloned brick == original brick!", brick, clonedBrick);

		UserScriptDefinitionBrick originalDefinitionBrick = brick.getDefinitionBrick();
		UserScriptDefinitionBrick clonedDefinitionBrick = clonedBrick.getDefinitionBrick();

		assertNotSame("Cloned definition brick == original definition brick!", originalDefinitionBrick, clonedDefinitionBrick);

		assertNotSame("Cloned script == original script!", originalDefinitionBrick.getUserScript(), clonedDefinitionBrick.getUserScript());

		assertNotSame("Cloned userBrickElements == original userBrickElements.",
				brick.getUserScriptDefinitionBrickElements(), clonedBrick.getUserScriptDefinitionBrickElements());

		assertEquals("Cloned userBrickElements size != original userBrickElements size.",
				brick.getUserScriptDefinitionBrickElements().size(), clonedBrick.getUserScriptDefinitionBrickElements().size());

		for (int elementPosition = 0; elementPosition < brick.getUserScriptDefinitionBrickElements().size(); elementPosition++) {
			UserScriptDefinitionBrickElement originalElement = brick.getUserScriptDefinitionBrickElements().get(elementPosition);
			UserScriptDefinitionBrickElement clonedElement = clonedBrick.getUserScriptDefinitionBrickElements().get(elementPosition);
			assertNotSame("Cloned userBrickElements element == original userBrickElements element." + ". arrayId: " + elementPosition,
					originalElement, clonedElement);

			boolean equivalent = checkArrayElementEquivalence(originalElement, clonedElement);
			assertTrue("Cloned userBrickElements element not equivalent to original userBrickElements element. "
					+ ". arrayId: " + elementPosition, equivalent);
		}
	}

	private void runScriptOnSprite(Sprite sprite, float expectedOriginalX, float expectedDeltaX) {
		assertEquals("Script has more than one script", 1, sprite.getNumberOfScripts());
		Script startScript = sprite.getScript(0);

		SequenceAction sequence = new SequenceAction();
		startScript.run(sprite, sequence);

		float x = sprite.look.getXInUserInterfaceDimensionUnit();
		float y = sprite.look.getYInUserInterfaceDimensionUnit();

		assertEquals("Unexpected initial sprite x position: ", expectedOriginalX, x);
		assertEquals("Unexpected initial sprite y position: ", 0f, y);

		sequence.act(1f);

		assertEquals("Unexpected final sprite x position: ", expectedOriginalX + expectedDeltaX,
				sprite.look.getXInUserInterfaceDimensionUnit());
		assertEquals("Unexpected final sprite y position: ", 0f, sprite.look.getYInUserInterfaceDimensionUnit());
	}

	private void setOneFormula(UserBrick subject, ElementType elementType, String value, Float expectedValue, int expectedFormulaListSize) {
		List<Formula> formulaList = subject.getFormulas();
		assertEquals("formulaList.size() after innerBrick.updateUserBrickParameters()" + formulaList.size(), expectedFormulaListSize,
				formulaList.size());
		for (Formula formula : formulaList) {
			formula.setRoot(new FormulaElement(elementType, value, null));
			if (expectedValue != null) {
				try {
					assertEquals("Unexpected value from interpretFloat: ", expectedValue, formula.interpretFloat(sprite));
				} catch (InterpretationException interpretationException) {
					Log.e(TAG, "InterpretationException!", interpretationException);
				}
			}
		}
	}

	private boolean checkArrayElementEquivalence(UserScriptDefinitionBrickElement leftHandSide, UserScriptDefinitionBrickElement rightHandSide) {
		boolean foundProblem;
		foundProblem = (leftHandSide.isLineBreak() != rightHandSide.isLineBreak());
		foundProblem = foundProblem || (leftHandSide.isVariable() != rightHandSide.isVariable());
		foundProblem = foundProblem || (leftHandSide.isNewLineHint() != rightHandSide.isNewLineHint());
		foundProblem = foundProblem || (!leftHandSide.getText().equals(rightHandSide.getText()));

		return !foundProblem;
	}
}
