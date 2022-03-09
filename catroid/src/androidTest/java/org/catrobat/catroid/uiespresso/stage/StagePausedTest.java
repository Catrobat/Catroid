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
package org.catrobat.catroid.uiespresso.stage;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class StagePausedTest {

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	private ScriptEvaluationGateBrick evaluationGateBrick;

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
		evaluationGateBrick.waitUntilEvaluated(1000);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testIgnoreTouchEventsWhenStagePaused() {
		InputListenerMock touchListener = new InputListenerMock();
		StageActivity.stageListener.getStage().addListener(touchListener);

		assertFalse(touchListener.called);

		clickOnStage();

		assertTrue(touchListener.called);
		touchListener.called = false;

		pressBack();

		assertFalse(touchListener.called);

		clickOnStage();

		assertFalse(touchListener.called);

		onView(withId(R.id.stage_dialog_button_continue))
				.perform(click());

		assertFalse(touchListener.called);

		clickOnStage();

		assertTrue(touchListener.called);
	}

	private void clickOnStage() {
		onView(isRoot()).perform(click());
	}

	private static class InputListenerMock extends InputListener {
		boolean called;

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			called = true;
			return super.touchDown(event, x, y, pointer, button);
		}
	}

	private void createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), "StagePausedTest");
		Sprite sprite = new Sprite("testSprite");
		Script startScript = new StartScript();
		sprite.addScript(startScript);

		project.getDefaultScene().addSprite(sprite);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentSprite(sprite);

		evaluationGateBrick = ScriptEvaluationGateBrick.appendToScript(startScript);
	}
}
