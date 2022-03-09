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

package org.catrobat.catroid.uiespresso.intents.looks.paintroid;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.AllOf.allOf;
import static org.koin.java.KoinJavaComponent.inject;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasCategories;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class PocketPaintNewSpriteDiscardIntentTest {

	private Matcher expectedIntent;
	private final String projectName = getClass().getSimpleName();
	private final String newSpriteName = UiTestUtils.getResourcesString(R.string.default_sprite_name);

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() throws Exception {
		createProject(projectName);

		baseActivityTestRule.launchActivity();
		Intents.init();

		expectedIntent = allOf(
				hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
				hasAction("android.intent.action.MAIN"),
				hasCategories(hasItem(equalTo("android.intent.category.LAUNCHER"))));

		Intent resultData = new Intent();
		Instrumentation.ActivityResult result =
				new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, resultData);

		intending(expectedIntent).respondWith(result);
	}

	@After
	public void tearDown() {
		Intents.release();
		baseActivityTestRule.finishActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCancelImport() {
		addEmptySprite();

		onView(withText(R.string.cancel))
				.perform(click());

		onRecyclerView().checkHasNumberOfItems(1);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testAddEmptySprite() {
		addEmptySprite();

		onView(Matchers.allOf(withId(android.R.id.button1), withText(R.string.ok)))
				.perform(click());

		onRecyclerView().atPosition(1).onChildView(R.id.title_view)
				.check(matches(withText(newSpriteName)));

		onRecyclerView().checkHasNumberOfItems(2);
	}

	private void createProject(String projectName) {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		final ProjectManager projectManager = inject(ProjectManager.class).getValue();
		projectManager.setCurrentProject(project);
		projectManager.setCurrentlyEditedScene(project.getDefaultScene());
	}

	private void addEmptySprite() {
		onView(withId(R.id.button_add))
				.perform(click());

		onView(withId(R.id.dialog_new_look_paintroid))
				.perform(click());

		intended(expectedIntent);

		closeSoftKeyboard();

		onView(withText(newSpriteName))
				.check(matches(isDisplayed()));

		onView(withText(R.string.new_empty_sprite_note))
				.check(matches(isDisplayed()));
	}
}
