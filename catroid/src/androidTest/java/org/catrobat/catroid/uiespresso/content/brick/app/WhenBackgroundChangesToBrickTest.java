/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import com.google.common.base.Stopwatch;

import org.catrobat.catroid.ProjectManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.WhenBackgroundChangesScript;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

import static org.catrobat.catroid.WaitForConditionAction.waitFor;
import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


@RunWith(AndroidJUnit4.class)
public class WhenBackgroundChangesToBrickTest {
	private static final String PROJECT_NAME = "WhenBackgroundChangesToBrickTest";
	private int brickPosition;

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Before
	public void setUp() throws Exception {
		brickPosition = 0;
		UiTestUtils.createProjectWithCustomScript(PROJECT_NAME,
				new WhenBackgroundChangesScript());
		Intents.init();
		baseActivityTestRule.launchActivity();
	}

	@After
	public void tearDown() throws Exception {
		Intents.release();
		baseActivityTestRule.finishActivity();
		TestUtils.deleteProjects(PROJECT_NAME);
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testCreateNewBackground() {
		onBrickAtPosition(brickPosition).checkShowsText(R.string.brick_when_background);

		onBrickAtPosition(brickPosition).onSpinner(R.id.brick_when_background_spinner)
				.performSelectNameable(R.string.new_option);

		onView(withId(R.id.dialog_new_look_paintroid))
				.perform(click());

		waitOnViewAndClick(R.id.pocketpaint_btn_skip);

		onView(withId(R.id.pocketpaint_drawing_surface_view))
				.perform(waitFor(isDisplayed(), 3000))
				.perform(click());
		pressBack();

		onView(isRoot()).perform(CustomActions.wait(500));

		List<LookData> lookDataList =
				ProjectManager.getInstance().getCurrentProject().getDefaultScene().getBackgroundSprite().getLookList();

		assertEquals(1, lookDataList.size());
	}

	private boolean waitOnViewAndClick(int viewId) {
		return waitOnViewAndClick(viewId, 1000);
	}

	private boolean waitOnViewAndClick(int viewId, int timeout) {
		Stopwatch stopWatch = Stopwatch.createStarted();
		boolean viewFound;
		do {
			viewFound = true;
			try {
				onView(withId(viewId)).perform(click());
			} catch (NoMatchingViewException e) {
				viewFound = false;
				if (stopWatch.elapsed(TimeUnit.MILLISECONDS) >= timeout) {
					break;
				}
			}
		} while (!viewFound);
		return viewFound;
	}
}
