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

package org.catrobat.catroid.uiespresso.ui.dialog;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewActions;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class DeleteSpriteDialogTest {

	@Rule
	public BaseActivityInstrumentationRule<ProjectActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION,
			ProjectActivity.FRAGMENT_SPRITES);
	private String toBeDeletedSpriteName = "secondSprite";

	@Before
	public void setUp() throws Exception {
		createProject("DeleteSpriteDialogTest");

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void deleteSpriteDialogTest() {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(2)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_sprites, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.perform(click());

		onView(withText(toBeDeletedSpriteName))
				.check(doesNotExist());
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void cancelDeleteSpriteDialogTest() {
		RecyclerViewActions.openOverflowMenu();
		onView(withText(R.string.delete)).perform(click());

		onRecyclerView().atPosition(2)
				.performCheckItem();

		onView(withId(R.id.confirm)).perform(click());

		onView(withText(UiTestUtils.getResources().getQuantityString(R.plurals.delete_sprites, 1)))
				.inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(withText(R.string.dialog_confirm_delete)).inRoot(isDialog())
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button1), withText(R.string.yes)))
				.check(matches(isDisplayed()));
		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.check(matches(isDisplayed()));

		onView(allOf(withId(android.R.id.button2), withText(R.string.no)))
				.perform(click());

		onView(withText(toBeDeletedSpriteName))
				.check(matches(isDisplayed()));

		onView(withText(InstrumentationRegistry.getTargetContext()
				.getResources().getQuantityString(R.plurals.am_delete_sprites_title, 1, 1)))
				.check(matches(isDisplayed()));
	}

	private void createProject(String projectName) {
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);

		Sprite firstSprite = new SingleSprite("firstSprite");
		Sprite secondSprite = new SingleSprite(toBeDeletedSpriteName);

		project.getDefaultScene().addSprite(firstSprite);
		project.getDefaultScene().addSprite(secondSprite);

		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentScene(project.getDefaultScene());
	}
}
