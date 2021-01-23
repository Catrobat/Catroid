/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment;

import android.widget.EditText;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.actionbar.utils.ActionModeWrapper.onActionMode;
import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.openActionBar;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@Category({Cat.AppUi.class, Level.Smoke.class})
@RunWith(AndroidJUnit4.class)
public class RenameSpriteTest {

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	private String firstSpriteName = "firstSprite";
	private String secondSpriteName = "secondSprite";

	@Before
	public void setUp() throws Exception {
		createProject(RenameSpriteTest.class.getSimpleName());

		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		TestUtils.deleteProjects(RenameSpriteTest.class.getSimpleName());
	}

	@Test
	@Flaky
	public void renameSpriteDialogTest() {
		String newSpriteName = "renamedSprite";
		renameSpriteTo(newSpriteName);
	}

	@Test
	public void cancelRenameSpriteDialogTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0).check(matches(not(isDisplayed())));

		onRecyclerView().atPosition(2)
				.perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
				.perform(click());

		onView(withText(secondSpriteName))
				.check(matches(isDisplayed()));
	}

	@Test
	public void invalidInputRenameSoundTest() {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0).check(matches(not(isDisplayed())));

		onRecyclerView().atPosition(1)
				.perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		String emptyInput = "";
		String spacesOnlyInput = "   ";

		onView(allOf(withText(firstSpriteName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(emptyInput));
		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(emptyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(spacesOnlyInput));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(spacesOnlyInput), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText(secondSpriteName));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), not(isEnabled()))));

		onView(allOf(withText(secondSpriteName), isDisplayed(), instanceOf(EditText.class)))
				.perform(replaceText("newSpriteName"));

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.check(matches(allOf(isDisplayed(), isEnabled())));
	}

	@Test
	@Flaky
	public void renameSpriteSwitchCaseDialogTest() {
		String newSpriteName = "SeConDspRite";
		renameSpriteTo(newSpriteName);
	}

	@Test
	public void renameSingleSpriteTest() {
		openActionBar();
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(2).performCheckItem();

		onActionMode().performConfirm();

		onView(withText(R.string.yes)).perform(click());

		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
	}

	private void renameSpriteTo(String newSpriteName) {
		openActionBar();
		onView(withText(R.string.rename)).perform(click());

		onRecyclerView().atPosition(0).check(matches(not(isDisplayed())));

		onRecyclerView().atPosition(2)
				.perform(click());

		onView(withText(R.string.rename_sprite_dialog)).inRoot(isDialog())
				.check(matches(isDisplayed()));
		onView(allOf(withText(secondSpriteName), isDisplayed()))
				.perform(replaceText(newSpriteName));

		closeSoftKeyboard();

		onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onView(withText(newSpriteName))
				.check(matches(isDisplayed()));
		onView(withText(secondSpriteName))
				.check(doesNotExist());
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);

		Sprite firstSprite = new Sprite(firstSpriteName);
		Sprite secondSprite = new Sprite(secondSpriteName);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentlyEditedScene(project.getDefaultScene());
	}
}
