/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.facedetection;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetSizeToBrick;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FaceDetectionResourceStartedTest {

	private Formula formula;
	private ScriptEvaluationGateBrick lastBrickInScript;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void facedetectionResourceEnabledTest() {
		formula = new Formula(
				new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		createProject();

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertTrue(FaceDetectionHandler.isFaceDetectionRunning());

		pressBack();
		onView(withId(R.id.stage_dialog_button_back)).perform(click());

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void facedetectionResourceNotEnabledTest() {
		formula = new Formula(
				new FormulaElement(FormulaElement.ElementType.NUMBER, "42", null));
		createProject();

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());

		pressBack();
		onView(withId(R.id.stage_dialog_button_back)).perform(click());

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());
	}

	@Category({Cat.AppUi.class, Level.Functional.class})
	@Test
	public void facedetectionResourceChangedTest() {
		formula = new Formula(
				new FormulaElement(FormulaElement.ElementType.SENSOR, Sensors.FACE_SIZE.name(), null));
		createProject();

		baseActivityTestRule.launchActivity(null);
		onView(withId(R.id.button_play)).perform(click());

		lastBrickInScript.waitUntilEvaluated(3000);

		assertTrue(FaceDetectionHandler.isFaceDetectionRunning());

		pressBack();
		onView(withId(R.id.stage_dialog_button_back)).perform(click());

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());

		formula.setRoot(new FormulaElement(FormulaElement.ElementType.NUMBER, "42", null));

		onView(withId(R.id.button_play)).perform(click());

		assertFalse(FaceDetectionHandler.isFaceDetectionRunning());
	}

	private void createProject() {
		Project project = new Project(null, "FaceDetectionResourceStartedTest");
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();
		SetSizeToBrick setSizeToBrick = new SetSizeToBrick(formula);
		startScript.addBrick(setSizeToBrick);
		sprite.addScript(startScript);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(startScript);
	}
}
