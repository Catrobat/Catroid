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

package org.catrobat.catroid.uiespresso.content.brick.app;

import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.DeleteLookBrick;
import org.catrobat.catroid.content.bricks.ShowBrick;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.ProjectListActivity;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertFalse;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class EmptyEventBrickTest {
	private static final String PROJECT_NAME = "testEmptyEventBrick";
	private static final String SPRITE_NAME = "testSprite";
	private Project project;

	@Rule
	public BaseActivityTestRule<ProjectListActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(ProjectListActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		project = UiTestUtils.createDefaultTestProject(PROJECT_NAME);
		Script script = UiTestUtils.getDefaultTestScript(project);
		script.addBrick(new ShowBrick());
		script.addBrick(new DeleteLookBrick());
		XstreamSerializer.getInstance().saveProject(project);

		baseActivityTestRule.launchActivity(null);
	}

	@Category({Cat.AppUi.class, Level.Detailed.class})
	@Test
	public void testIsEmptyBrickShownInScripts() {
		onView(withText(PROJECT_NAME)).perform(click());
		onView(withText(SPRITE_NAME)).perform(click());
		onBrickAtPosition(0).check(matches(isDisplayed()));
	}

	@Category({Cat.AppUi.class, Level.Detailed.class})
	@Test
	public void testBelowBricksAreNotDisabled() {
		onView(withText(PROJECT_NAME)).perform(click());
		onView(withText(SPRITE_NAME)).perform(click());
		for (Brick brick : project.getDefaultScene().getSprite(SPRITE_NAME).getAllBricks()) {
			assertFalse(brick.isCommentedOut());
		}
	}
}
