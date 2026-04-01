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

package org.catrobat.catroid.test.stage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.FinishStageBrick;
import org.catrobat.catroid.content.bricks.GlideToBrick;
import org.catrobat.catroid.content.bricks.LookRequestBrick;
import org.catrobat.catroid.content.bricks.MoveNStepsBrick;
import org.catrobat.catroid.content.bricks.OpenUrlBrick;
import org.catrobat.catroid.content.bricks.PlaySoundBrick;
import org.catrobat.catroid.content.bricks.SetFrictionBrick;
import org.catrobat.catroid.content.bricks.SetMassBrick;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.content.bricks.TapAtBrick;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;

import static org.catrobat.catroid.test.utils.TestUtils.deleteProjects;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SearchParameterTest {
	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);
	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	String projectName = "searchTestProject";
	Script script1;
	Script script2;
	Script script3;

	@Before
	public void setUp() {
		createProject(projectName);

		for (int i = 0; i <= 5; i++) {
			script1.addBrick(new SetXBrick(i));
		}

		script2.addBrick(new GlideToBrick(200, 200, 1000));
		script2.addBrick(new FinishStageBrick());
		script2.addBrick(new OpenUrlBrick());
		script2.addBrick(new TapAtBrick());
		script2.addBrick(new MoveNStepsBrick());
		script2.addBrick(new MoveNStepsBrick());
		script2.addBrick(new SetMassBrick());
		script2.addBrick(new SetFrictionBrick());
		script2.addBrick(new PlaySoundBrick());

		script3.addBrick(new LookRequestBrick("look 2"));

		baseActivityTestRule.launchActivity(new Intent());
	}

	@Test
	public void testSearchBrickParams() {
		String[] arguments = new String[] {"1", "2", "3", "4", "5"};
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.search)).perform(click());
		for (String argument : arguments) {
			onView(withId(R.id.search_bar)).perform(replaceText(argument));
			onView(withId(R.id.find)).perform(click());
			onView(withText(argument)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
		}
	}

	@Test
	public void testSearchBrickText() {
		String searchParam =
				baseActivityTestRule.getActivity().getString(R.string.brick_glide) + baseActivityTestRule.getActivity().getString(R.string.brick_glide_to_x);
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.search)).perform(click());
		onView(withId(R.id.search_bar)).perform(replaceText(searchParam));
		onView(withId(R.id.find)).perform(click());
		onView(withText(searchParam)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	public void testSearchSpinner() {
		final String searchTerm = "look 1";
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.search)).perform(click());
		onView(withId(R.id.search_bar)).perform(replaceText(searchTerm));
		onView(withId(R.id.find)).perform(click());
		onView(withText(searchTerm)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	public void tobBarVisibleAfterSelectingBrickFieldTest() {
		String searchParam = "0";
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.search)).perform(click());
		onView(withId(R.id.search_bar)).perform(replaceText(searchParam));
		onView(withId(R.id.find)).perform(click());
		BrickDataInteractionWrapper.onBrickAtPosition(1).onFormulaTextField(R.id.brick_set_x_edit_text).perform(click());
		pressBack();
		onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
	}

	@Test
	public void testForSearchQueryWithTrailingSpaces() {
		Activity activity = baseActivityTestRule.getActivity();
		String searchParam = activity.getString(R.string.brick_play_sound) + " ";
		openActionBarOverflowOrOptionsMenu(activity);
		onView(withText(R.string.search)).perform(click());
		onView(withId(R.id.search_bar)).perform(replaceText(searchParam));
		onView(withId(R.id.find)).perform(click());
		onView(withText(searchParam)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	public void testForSearchQueryWithLeadingSpaces() {
		Activity activity = baseActivityTestRule.getActivity();
		String searchParam = " " + activity.getString(R.string.brick_play_sound);
		openActionBarOverflowOrOptionsMenu(activity);
		onView(withText(R.string.search)).perform(click());
		onView(withId(R.id.search_bar)).perform(replaceText(searchParam));
		onView(withId(R.id.find)).perform(click());
		onView(withText(searchParam)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
	}

	@Test
	@Flaky
	public void closeKeyboardAfterSearching() {
		openActionBarOverflowOrOptionsMenu(baseActivityTestRule.getActivity());
		onView(withText(R.string.search)).perform(click());
		onView(isRoot()).perform(CustomActions.wait(2000));
		assertTrue(isKeyboardVisible());
		onView(withId(R.id.close)).perform(click());
		onView(isRoot()).perform(CustomActions.wait(2000));
		assertFalse(isKeyboardVisible());
	}

	public boolean isKeyboardVisible() {
		try {
			final InputMethodManager manager = (InputMethodManager) ApplicationProvider.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			final Method windowHeightMethod = InputMethodManager.class.getMethod("getInputMethodWindowVisibleHeight");
			final int height = (int) windowHeightMethod.invoke(manager);
			return height > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		project.getSceneList().clear();

		String sceneName1 = "scene1";
		String sceneName2 = "scene2";
		String sceneName3 = "scene3";

		Scene scene1 = new Scene("scene1", project);
		Sprite sprite1 = new Sprite("testSprite1");
		script1 = new StartScript();
		sprite1.addScript(script1);

		Scene scene2 = new Scene("scene2", project);
		Sprite sprite2 = new Sprite("testSprite2");
		script2 = new StartScript();
		sprite2.addScript(script2);

		Scene scene3 = new Scene("scene3", project);
		Sprite sprite3 = new Sprite("testSprite3");
		script3 = new StartScript();
		sprite3.addScript(script3);

		LookData l1 = new LookData();
		l1.setName("look 1");
		sprite3.getLookList().add(l1);

		LookData l2 = new LookData();
		l2.setName("look 2");
		sprite3.getLookList().add(l2);

		LookData l3 = new LookData();
		l3.setName("look 3");
		sprite3.getLookList().add(l3);

		project.addScene(scene1);
		project.addScene(scene2);
		project.addScene(scene3);

		project.getSceneByName(sceneName1).addSprite(sprite1);
		project.getSceneByName(sceneName2).addSprite(sprite2);
		project.getSceneByName(sceneName3).addSprite(sprite3);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSceneAndSprite(sceneName1, sprite1.getName());
		ProjectManager.getInstance().setCurrentlyEditedScene(scene1);
	}

	@After
	public void tearDown() {
		baseActivityTestRule.finishActivity();
		try {
			deleteProjects(projectName);
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), "Cannot delete test project in tear down.");
		}
	}
}
