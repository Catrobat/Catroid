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

package org.catrobat.catroid.test.content.bricks;

import android.content.Intent;
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.ui.ProjectActivity;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;

import static org.catrobat.catroid.WaitForConditionAction.waitFor;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RecentBricksTest {

	private final String projectName = "recentBrickTest";
	private final String spriteName = "testSprite";

	@Rule
	public FragmentActivityTestRule<ProjectActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(ProjectActivity.class, ProjectActivity.EXTRA_FRAGMENT_POSITION, ProjectActivity.FRAGMENT_SPRITES);

	@Before
	public void setUp() {
		createProject();
		baseActivityTestRule.launchActivity(new Intent());
	}

	@Test
	public void testCheckRecentBrickVisible() {
		onView(withText(spriteName)).perform(click());
		onView(withId(R.id.button_add)).perform(click());
		onView(withText(R.string.category_control)).perform(scrollTo(), click());
		onView(withText(R.string.brick_forever)).perform(scrollTo(), click());
		onView(withId(android.R.id.list)).perform(click());
		onView(withId(R.id.button_add)).perform(click());
		onView(withText(R.string.category_recently_used)).perform(click());
		onBrickAtPosition(1).checkShowsText(R.string.brick_forever);
	}

	@Test
	public void testCheckNonBackgroundBricksAreHiddenForBackgroundSprites() {
		onView(withText(spriteName)).perform(click());

		onView(withId(R.id.button_add)).perform(click());
		onView(withText(R.string.category_motion)).perform(scrollTo(), click());
		onView(withText(R.string.brick_if_on_edge_bounce)).perform(scrollTo());
		onView(withText(R.string.brick_if_on_edge_bounce)).perform(waitFor(isDisplayed(), 2000), click());
		onView(withId(android.R.id.list)).perform(click());

		onView(withId(R.id.button_add)).perform(click());
		onView(withText(R.string.category_recently_used)).perform(click());
		onBrickAtPosition(1).checkShowsText(R.string.brick_if_on_edge_bounce);

		pressBack();
		pressBack();
		pressBack();

		onView(withText(R.string.background)).perform(click());
		onView(withId(R.id.button_add)).perform(click());
		onView(withText(R.string.category_recently_used)).perform(click());
		onView(withText(R.string.brick_if_on_edge_bounce)).check(doesNotExist());
	}

	@After
	public void tearDown() {
		baseActivityTestRule.finishActivity();
		try {
			StorageOperations.deleteDir(new File(DEFAULT_ROOT_DIRECTORY, projectName));
		} catch (IOException e) {
			Log.d(getClass().getSimpleName(), "Cannot delete test project in tear down.");
		}
	}

	public Project createProject() {
		Project project = new Project(ApplicationProvider.getApplicationContext(), projectName);
		Sprite sprite = new Sprite(spriteName);

		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);

		return project;
	}
}
