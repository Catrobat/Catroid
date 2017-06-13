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

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
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
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

//TODO incomplete Test!

@RunWith(AndroidJUnit4.class)
public class SceneStartBrickSpinnerChangesTest {
	private IdlingResource idlingResource;
	private int brickPosition;
	private String projectName = "sceneStartBrick";
	private String sceneName;
	private String sceneNameInMenu;
	private String sceneName2 = "testScene2";
	private String spriteName = "testSprite";

	@Rule
	public BaseActivityInstrumentationRule<MainMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(MainMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject();
		baseActivityTestRule.launchActivity(null);
		idlingResource = baseActivityTestRule.getActivity().getIdlingResource();
		Espresso.registerIdlingResources(idlingResource);
	}

	@Test
	public void testSpinnerUpdatesDelete() {
		getIntoScriptActivityFromMainMenu();

		//TODO write spinner check / assertion to check if something is (not) selectable
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(sceneName2)))
				.check(matches(isDisplayed()));

		pressBack();
		pressBack();
		pressBack();
		pressBack();

		deleteScene(sceneName2);
		pressBack();
		getIntoScriptActivityFromProgramsMenuWithOneScene();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onView(withText(sceneName2))
				.check(doesNotExist());
	}

	@Test
	public void testSpinnerUpdatesRename() {
		String newName = "nameRenamed";

		getIntoScriptActivityFromMainMenu();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(sceneName)))
				.check(matches(isDisplayed()));
		onData(allOf(is(instanceOf(String.class)), is(sceneName2)))
				.check(matches(isDisplayed()));

		pressBack();
		pressBack();
		pressBack();
		pressBack();

		renameScene(sceneName2, newName);
		pressBack();
		getIntoScriptActivityFromProgramsMenu();

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onView(withText(sceneName2))
				.check(doesNotExist());
		onView(withText(newName))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testAddNewScene() {
		String newName = "newScene";

		getIntoScriptActivityFromMainMenu();
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onView(withText(R.string.brick_variable_spinner_create_new_variable))
				.perform(click());

		onView(withText(R.string.new_scene_dialog_title)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(withId(R.id.scene_name_edittext))
				.perform(typeText(newName));
		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		pressBack();
		getIntoScriptActivityFromProgramsMenu();
		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.checkShowsText(newName);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_scene_start_spinner)
				.perform(click());
		onData(allOf(is(instanceOf(String.class)), is(newName)))
				.check(matches(isDisplayed()));
	}

	private void deleteScene(String name) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.delete))
				.perform(click());

		onView(allOf(withId(R.id.list_item_checkbox), hasSibling(hasDescendant(withText(name)))))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());
		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());
	}

	private void renameScene(String oldName, String newName) {
		openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
		onView(withText(R.string.rename))
				.perform(click());

		onView(allOf(withId(R.id.list_item_checkbox), hasSibling(hasDescendant(withText(oldName)))))
				.perform(click());
		onView(withContentDescription(R.string.done))
				.perform(click());

		onView(withText(R.string.rename_scene_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withId(R.id.edit_text), withText(oldName), isDisplayed()))
				.perform(replaceText(newName));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());
	}

	private void getIntoScriptActivityFromMainMenu() {
		onView(withId(R.id.main_menu_button_programs))
				.perform(click());
		getIntoScriptActivityFromProgramsMenu();
	}

	private void getIntoScriptActivityFromProgramsMenu() {
		onView(withText(projectName))
				.perform(click());
		onView(withText(sceneNameInMenu))
				.perform(click());
		onView(withText(spriteName))
				.perform(click());
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());
	}

	private void getIntoScriptActivityFromProgramsMenuWithOneScene() {
		onView(withText(projectName))
				.perform(click());
		onView(withText(spriteName))
				.perform(click());
		onView(withId(R.id.program_menu_button_scripts))
				.perform(click());
	}

	private void createProject() {
		Project project = new Project(null, projectName);
		Scene scene2 = new Scene(null, sceneName2, project);
		project.addScene(scene2);
		Sprite sprite = new SingleSprite(spriteName);
		Script script = new StartScript();

		SceneStartBrick sceneStartBrick = new SceneStartBrick(sceneName2);
		script.addBrick(sceneStartBrick);
		brickPosition = 1;

		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentScene(project.getDefaultScene());
		ProjectManager.getInstance().setCurrentSprite(sprite);
		ProjectManager.getInstance().setCurrentScript(script);
		ProjectManager.getInstance().saveProject(UiTestUtils.getCurrentActivity());

		sceneName = project.getDefaultScene().getName();
		sceneNameInMenu = UiTestUtils.getResources().getString(R.string.start_scene_name, sceneName);
	}

	@After
	public void tearDown() throws Exception {
		Espresso.unregisterIdlingResources(idlingResource);
	}
}
