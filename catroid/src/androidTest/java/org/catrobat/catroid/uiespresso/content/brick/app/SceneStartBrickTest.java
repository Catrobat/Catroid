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

package org.catrobat.catroid.uiespresso.content.brick.app;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SceneStartBrick;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.matchers.SceneListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class SceneStartBrickTest {

	private static Integer whenBrickPosition = 0;
	private static Integer startSceneBrickPosition = 1;
	private static String testSceneName = "TestScene";
	private static String testSpriteName = "TestSprite";
	private static String defaultSceneName;
	private static String defaultSceneNameVisible;

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> projectActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, true, false);

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> mainMenuActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("SceneStartBrickTest");
		projectActivityTestRule.launchActivity(null);
		mainMenuActivityTestRule.launchActivity(null);
		onView(withId(R.id.main_menu_button_continue)).perform(click());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testDeleteScene() {
		checkIfSceneItemAvailable(0, defaultSceneNameVisible);
		checkIfSceneItemAvailable(1, testSceneName);
		navigateToScriptActivity();

		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(startSceneBrickPosition).checkShowsText(R.string.brick_scene_start);
		onBrickAtPosition(startSceneBrickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(testSceneName)
				.perform(click());

		onView(withText(testSceneName)).check(matches(isDisplayed()));
		onView(withText(defaultSceneName)).check(matches(isDisplayed()));

		pressBack();
		pressBack();
		pressBack();
		pressBack();

		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete)).perform(click());
		onData(allOf(is(instanceOf(Scene.class))))
				.inAdapterView(SceneListMatchers.isSceneListView())
				.atPosition(1)
				.onChildView(withId(R.id.list_item_checkbox))
				.perform(click());

		onView(withContentDescription(R.string.done)).perform(click());
		onView(withText(R.string.yes)).perform(click());

		onView(withText(testSpriteName))
				.check(matches(isDisplayed()))
				.perform(click());

		onView(withId(R.id.program_menu_button_scripts)).perform(click());
		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(startSceneBrickPosition).checkShowsText(R.string.brick_scene_start);
		onBrickAtPosition(startSceneBrickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(R.string.new_broadcast_message)
				.perform(click());
		onView(withText(defaultSceneName)).check(doesNotExist());
		onView(withText(testSceneName)).check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testBrickAddNewScene() {
		checkIfSceneItemAvailable(0, defaultSceneNameVisible);
		checkIfSceneItemAvailable(1, testSceneName);
		navigateToScriptActivity();

		onBrickAtPosition(whenBrickPosition).checkShowsText(R.string.brick_when_started);
		onBrickAtPosition(startSceneBrickPosition).checkShowsText(R.string.brick_scene_start);
		onBrickAtPosition(startSceneBrickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(testSceneName)
				.perform(click());

		onView(withText(R.string.new_broadcast_message)).perform(click());

		String newSceneName = "NewTestScene";
		onView(withId(R.id.scene_name_edittext)).perform(replaceText(newSceneName), closeSoftKeyboard());
		onView(withText(R.string.ok)).perform(click());

		pressBack();
		onView(withId(R.id.main_menu_button_continue)).perform(click());

		checkIfSceneItemAvailable(0, defaultSceneNameVisible);
		checkIfSceneItemAvailable(1, testSceneName);
		checkIfSceneItemAvailable(2, newSceneName);

		navigateToScriptActivity();
		onBrickAtPosition(startSceneBrickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(newSceneName)
				.perform(click());
		onView(withText(defaultSceneName)).check(matches(isDisplayed()));
		onView(withText(testSceneName)).check(matches(isDisplayed()));
		onView(withText(newSceneName)).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testSceneAddNewScene() {
		checkIfSceneItemAvailable(0, defaultSceneNameVisible);
		checkIfSceneItemAvailable(1, testSceneName);

		String newSceneName = "NewTestScene";
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.new_scene_dialog_title)).perform(click());
		onView(withId(R.id.scene_name_edittext)).perform(replaceText(newSceneName), closeSoftKeyboard());
		onView(withText(R.string.ok)).perform(click());

		pressBack();
		checkIfSceneItemAvailable(0, defaultSceneNameVisible);
		checkIfSceneItemAvailable(1, testSceneName);
		checkIfSceneItemAvailable(2, newSceneName);

		navigateToScriptActivity();
		onBrickAtPosition(startSceneBrickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(testSceneName)
				.perform(click());
		onView(withText(defaultSceneName)).check(matches(isDisplayed()));
		onView(withText(testSceneName)).check(matches(isDisplayed()));
		onView(withText(newSceneName)).check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = new Project(null, projectName);

		Scene testScene = new Scene(null, testSceneName, project);
		project.addScene(testScene);

		Sprite sprite = new SingleSprite(testSpriteName);
		Script script = new StartScript();
		script.addBrick(new SceneStartBrick(testSceneName));
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		defaultSceneName = project.getDefaultScene().getName();
		defaultSceneNameVisible = defaultSceneName + " (First Scene)";
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
	}

	private void navigateToScriptActivity() {
		onView(withText(defaultSceneNameVisible)).perform(click());
		onView(withText(testSpriteName)).perform(click());
		onView(withId(R.id.program_menu_button_scripts)).perform(click());
	}

	private void checkIfSceneItemAvailable(int position, String sceneName) {
//		This shall be a hint to implement the test if SceneListView is refactored to a RecyclerView
//		onView(new RecyclerViewMatcher(R.id.recycler_view)
//				.atPositionOnView(position, sceneName)
//				.check(matches(isDisplayed()));

		onData(allOf(is(instanceOf(Scene.class))))
				.inAdapterView(SceneListMatchers.isSceneListView())
				.atPosition(position)
				.onChildView(withText(sceneName))
				.check(matches(isDisplayed()));
	}
}
