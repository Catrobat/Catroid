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

package org.catrobat.catroid.uiespresso.content.brick;

import android.os.SystemClock;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.AskBrick;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResourcesString;
import static org.catrobat.catroid.uiespresso.util.matchers.UserVariableTestUtils.userVariableEqualsWithinTimeout;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.StringEndsWith.endsWith;

public class AskBrickTest {

	private UserVariable userVariable;
	private Project project;

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("AskBrickTest");
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testAskBrick() {
		int askBrickPosition = 1;
		String question = "Will it work?";
		onBrickAtPosition(0).checkShowsText(R.string.brick_when_started);

		onBrickAtPosition(askBrickPosition).checkShowsText(R.string.brick_ask_label);
		onBrickAtPosition(askBrickPosition).checkShowsText(R.string.brick_ask_store);

		onBrickAtPosition(askBrickPosition).onFormulaTextField(R.id.brick_ask_question_edit_text)
				.performEnterString(question)
				.checkShowsText(question);
	}

	@Test
	public void testAskBrickEmptyAnswer() {
		String testAnswer = "";
		onView(withId(R.id.button_play))
				.perform(click());
		SystemClock.sleep(1000);
		onView(withText(R.string.brick_ask_dialog_submit))
				.perform(click());

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, testAnswer, 2000));
	}

	@Test
	public void testAskBrickNormalAnswer() {
		String testAnswer = "TestA";
		onView(withId(R.id.button_play))
				.perform(click());

		onView(allOf(withClassName(endsWith("EditText"))))
				.perform(typeText(testAnswer));
		onView(withText(R.string.brick_ask_dialog_submit))
				.perform(click());

		Assert.assertTrue(userVariableEqualsWithinTimeout(userVariable, testAnswer, 2000));
	}

	public void createProject(String projectName) {
		String userVariableName = "TempVariable";
		project = new Project(null, projectName);
		Sprite sprite1 = new Sprite("testSprite");
		Script sprite1StartScript = new StartScript();
		sprite1StartScript.addBrick(new AskBrick(getResourcesString(R.string.brick_ask_default_question)));
		sprite1.addScript(sprite1StartScript);
		project.getDefaultScene().addSprite(sprite1);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = dataContainer.addProjectUserVariable(userVariableName);
	}
}
