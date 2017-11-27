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

package org.catrobat.catroid.uiespresso.content.brick.stage;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.UserVariableTestUtils.userVariableEqualsWithinTimeout;
import static org.hamcrest.core.StringEndsWith.endsWith;

@RunWith(AndroidJUnit4.class)
public class AskBrickStageTest {

	private UserVariable userVariable;
	private ScriptEvaluationGateBrick firstBrickInScript;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("AskBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class})
	@Test
	public void testAskBrickEmptyAnswer() {
		String testAnswer = "";

		firstBrickInScript.waitUntilEvaluated(1000);

		onView(withText(R.string.brick_ask_dialog_submit))
				.perform(click());

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, testAnswer, 1000));
	}

	@Category({Cat.CatrobatLanguage.class, Level.Functional.class})
	@Test
	public void testAskBrickNormalAnswer() {
		String testAnswer = "TestA";

		firstBrickInScript.waitUntilEvaluated(1000);

		onView(withClassName(endsWith("EditText")))
				.perform(typeText(testAnswer));
		onView(withText(R.string.brick_ask_dialog_submit))
				.perform(click());

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, testAnswer, 1000));
	}

	private void createProject(String projectName) {
		String userVariableName = "TempVariable";
		Project project = new Project(null, projectName);
		Sprite sprite1 = new Sprite("testSprite");
		Script sprite1StartScript = new StartScript();
		sprite1.addScript(sprite1StartScript);

		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = dataContainer.addProjectUserVariable(userVariableName);

		firstBrickInScript = ScriptEvaluationGateBrick.appendToScript(sprite1StartScript);
		Formula questionFormula = new Formula(
				getResourcesString(R.string.brick_ask_default_question));
		sprite1StartScript.addBrick(new AskBrick(questionFormula, userVariable));
	}
}
