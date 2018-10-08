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

import android.support.test.espresso.Espresso;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.WhenTouchDownScript;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.content.bricks.SceneTransitionBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.formulaeditor.datacontainer.DataContainer;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.stage.utils.StageTestTouchUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;

import static org.catrobat.catroid.uiespresso.util.UiTestUtils.assertEqualsWithTimeout;
import static org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Category({Cat.AppUi.class})
public class TransitionSceneTest {
	private String scene2Name = "Scene 2";
	private String scene1Name;

	private Project project;
	private UserVariable variable;
	private DataContainer dataContainer;

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createEmptyProject("test");
		dataContainer = project.getDefaultScene().getDataContainer();
		variable = new UserVariable("var");
		dataContainer.addUserVariable(variable);
		scene1Name = project.getDefaultScene().getName();
		setupSpriteActions(project.getDefaultScene().getBackgroundSprite(), scene2Name);

		Scene scene2 = new Scene(scene2Name, project);
		project.addScene(scene2);

		Sprite sprite2 = new Sprite();
		scene2.addSprite(sprite2);
		setupSpriteActions(sprite2, scene1Name);

		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void allLooksOfCurrentSceneAreActorsOnStageAfterSceneTransition() {
		waitUntilScene2TransitionsToScene1();

		List<Sprite> spritesOfCurrentScene = ProjectManager.getInstance().getCurrentlyPlayingScene().getSpriteList();
		Array<Actor> actorsOnStage = StageActivity.stageListener.getStage().getActors();
		for (Sprite sprite : spritesOfCurrentScene) {
			assertTrue(actorsOnStage.contains(sprite.look, true));
			assertEquals(0, sprite.look.getActions().size);
		}
	}

	private void waitUntilScene2TransitionsToScene1() {
		assertUserVariableEqualsWithTimeout(variable, 2, 1000);
		assertEqualsWithTimeout(scene1Name, ProjectManager.getInstance().getCurrentlyPlayingScene().getName(), 200);
	}

	@Test
	public void continueSceneWorksMultipleTimes() {
		waitUntilScene2TransitionsToScene1();

		Espresso.onView(isFocusable()).perform(StageTestTouchUtils.touchDown(50, 50));
		Espresso.onView(isFocusable()).perform(StageTestTouchUtils.touchUp(50, 50));
		assertUserVariableEqualsWithTimeout(variable, 3, 1000);
	}

	private void setupSpriteActions(Sprite sprite, String transitionToSceneName) {
		StartScript startScript = new StartScript();
		startScript.addBrick(new ChangeVariableBrick(new Formula(1), variable));
		startScript.addBrick(new SceneTransitionBrick(transitionToSceneName));

		WhenTouchDownScript whenScreenTouched = new WhenTouchDownScript();
		whenScreenTouched.addBrick(new ChangeVariableBrick(new Formula(1), variable));
		whenScreenTouched.addBrick(new SceneTransitionBrick(transitionToSceneName));
		sprite.addScript(startScript);
		sprite.addScript(whenScreenTouched);
	}
}
