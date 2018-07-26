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

package org.catrobat.catroid.uiespresso.stage;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenClonedScript;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.CloneBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;

@RunWith(AndroidJUnit4.class)
public class DisabledBrickInClonesRegressionTest {

	private static final String VARIABLE_NAME = "var1";

	private UserVariable userVariable;
	private ScriptEvaluationGateBrick lastBrickInScript;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class);

	@Before
	public void setUp() throws Exception {
		createProject();
	}

	private void createProject() {
		Project project = new Project(InstrumentationRegistry.getTargetContext(),
				"DisabledBrickInClonesRegressionTest");
		Sprite sprite = new Sprite("someSprite");

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());

		DataContainer dataContainer = project.getDefaultScene().getDataContainer();
		userVariable = new UserVariable(VARIABLE_NAME);
		dataContainer.addUserVariable(userVariable);
		userVariable.setValue(1D);

		Script startScript = new StartScript();
		sprite.addScript(startScript);
		startScript.addBrick(new CloneBrick());

		Script whenClonedScript = new WhenClonedScript();
		sprite.addScript(startScript);
		Brick shouldntBeExecuted = new SetVariableBrick(new Formula(9000.1D), userVariable);
		shouldntBeExecuted.setCommentedOut(true);
		whenClonedScript.addBrick(shouldntBeExecuted);

		lastBrickInScript = ScriptEvaluationGateBrick.appendToScript(whenClonedScript);
	}

	@Category({Level.Functional.class, Cat.CatrobatLanguage.class})
	@Test
	public void setVariableInCloneShouldNotBeExecutedTest() {
		baseActivityTestRule.launchActivity();
		lastBrickInScript.waitUntilEvaluated(3000);
		assertUserVariableEqualsWithTimeout(userVariable, 1D, 2000);
	}
}
