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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.ForeverBrick;
import org.catrobat.catroid.content.bricks.LoopEndlessBrick;
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
import org.catrobat.catroid.uiespresso.stage.utils.StageTestTouchUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.LinkedList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;

@Category({Cat.AppUi.class, Level.Smoke.class})
public class StartStageTouchTest {
	private String scene2Name = "Scene2";
	private UserVariable screenIsTouchedUserVariable = null;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("StartStageTouchTest");
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
	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		screenIsTouchedUserVariable = new UserVariable("ScreenTouched");
		project.getProjectVariables().add(screenIsTouchedUserVariable);

		Script background1StartScript = new StartScript();
		background1StartScript.addBrick(new WaitUntilBrick(createFormulaWithSensor(Sensors.FINGER_TOUCHED)));
		background1StartScript.addBrick(new SceneStartBrick(scene2Name));
		Scene scene1 = project.getDefaultScene();
		scene1.getBackgroundSprite().addScript(background1StartScript);

		Scene scene2 = new Scene(scene2Name, project);
		scene2.addSprite(new Sprite("Background"));
		Script background2StartScript = new StartScript();
		ForeverBrick foreverBrick = new ForeverBrick();
		background2StartScript.addBrick(foreverBrick);
		background2StartScript.addBrick(new SetVariableBrick(createFormulaWithSensor(Sensors.FINGER_TOUCHED),
				screenIsTouchedUserVariable));
		background2StartScript.addBrick(new LoopEndlessBrick(foreverBrick));
		scene2.getBackgroundSprite().addScript(background2StartScript);
		project.addScene(scene2);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setStartScene(scene1);
	}

	private Formula createFormulaWithSensor(Sensors sensor) {
		List<InternToken> internTokenList = new LinkedList<InternToken>();
		internTokenList.add(new InternToken(InternTokenType.SENSOR, sensor.name()));
		InternFormulaParser internFormulaParser = new InternFormulaParser(internTokenList);
		FormulaElement root = internFormulaParser.parseFormula();
		return new Formula(root);
	}
}
