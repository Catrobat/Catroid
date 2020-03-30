/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.content.bricks.SetVariableBrick;
import org.catrobat.catroid.content.bricks.WaitUntilBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.InternFormulaParser;
import org.catrobat.catroid.formulaeditor.InternToken;
import org.catrobat.catroid.formulaeditor.InternTokenType;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.StageTestTouchUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.LinkedList;
import java.util.List;

import androidx.test.core.app.ApplicationProvider;

import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

@Category({Cat.AppUi.class, Level.Smoke.class})
public class StartStageTouchTest {

	private UserVariable screenIsTouchedUserVariable = null;

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void switchStageTouchTest() {
		onView(isRoot()).perform(CustomActions.wait(500));
		onView(isFocusable()).perform(StageTestTouchUtils.touchDown(50, 50));
		assertUserVariableEqualsWithTimeout(screenIsTouchedUserVariable, 1, 500);
		onView(isFocusable()).perform(StageTestTouchUtils.touchUp(50, 50));
		assertUserVariableEqualsWithTimeout(screenIsTouchedUserVariable, 0, 500);
	}

	private void createProject() {
		String projectName = getClass().getSimpleName();
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		String scene2Name = "Scene2";
		Scene scene2 = new Scene(scene2Name, project);

		screenIsTouchedUserVariable = new UserVariable("ScreenTouched");
		project.addUserVariable(screenIsTouchedUserVariable);

		Script background1StartScript = new StartScript();
		background1StartScript.addBrick(new WaitUntilBrick(createFormulaWithSensor(Sensors.FINGER_TOUCHED)));

		background1StartScript.addBrick(new SceneStartBrick("Scene2"));
		Scene scene1 = project.getDefaultScene();
		scene1.getBackgroundSprite().addScript(background1StartScript);

		scene2.addSprite(new Sprite("Background"));

		Script background2StartScript = new StartScript();
		ForeverBrick foreverBrick = new ForeverBrick();
		foreverBrick.addBrick(
				new SetVariableBrick(createFormulaWithSensor(Sensors.FINGER_TOUCHED), screenIsTouchedUserVariable));
		background2StartScript.addBrick(foreverBrick);

		scene2.getBackgroundSprite().addScript(background2StartScript);
		project.addScene(scene2);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setStartScene(scene1);
	}

	private Formula createFormulaWithSensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		return new Formula(root);
	}
}
