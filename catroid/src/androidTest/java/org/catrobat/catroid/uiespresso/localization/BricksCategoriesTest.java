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
package org.catrobat.catroid.uiespresso.localization;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.ProgramMenuActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.LayoutAssertions.noEllipsizedText;
import static android.support.test.espresso.assertion.LayoutAssertions.noOverlaps;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.localization.assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.TextDirectionAssertions.isTextDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.VisibilityAssertions.isVisible;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)

public class BricksCategoriesTest {
	private ViewInteraction brickCategoryEvent;
	private ViewInteraction brickCategoryControl;
	private ViewInteraction brickCategorySound;
	private ViewInteraction brickCategoryMotion;
	private ViewInteraction brickCategoryLooks;
	private ViewInteraction brickCategoryData;
	private ViewInteraction brickCategoryPen;

	@Rule
	public BaseActivityInstrumentationRule<ProgramMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProgramMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createProject("BricksCategoriesTest");
		baseActivityTestRule.launchActivity(null);
		Espresso.onView(withId(R.id.program_menu_button_scripts)).perform(ViewActions.click());
		Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click());
		brickCategoryEvent = Espresso.onView(withText(R.string.category_event));
		brickCategoryControl = Espresso.onView(withText(R.string.category_control));
		brickCategorySound = Espresso.onView(withText(R.string.category_sound));
		brickCategoryMotion = Espresso.onView(withText(R.string.category_motion));
		brickCategoryLooks = Espresso.onView(withText(R.string.category_looks));
		brickCategoryData = Espresso.onView(withText(R.string.category_data));
		brickCategoryPen = Espresso.onView(withText(R.string.category_pen));
	}

	@Test
	public void assertCompletelyDisplayed() {
		brickCategoryEvent.check(matches(isCompletelyDisplayed()));
		brickCategoryControl.check(matches(isCompletelyDisplayed()));
		brickCategorySound.check(matches(isCompletelyDisplayed()));
		brickCategoryMotion.check(matches(isCompletelyDisplayed()));
		brickCategoryLooks.check(matches(isCompletelyDisplayed()));
		Espresso.onView(isRoot()).perform(ViewActions.swipeUp());
		brickCategoryData.check(matches(isCompletelyDisplayed()));
		brickCategoryPen.check(matches(isCompletelyDisplayed()));
	}

	@Test
	public void assertNoEllipsizedTextInRTLMode() {
		brickCategoryEvent.check(noEllipsizedText());
		brickCategoryControl.check(noEllipsizedText());
		brickCategorySound.check(noEllipsizedText());
		brickCategoryMotion.check(noEllipsizedText());
		brickCategoryLooks.check(noEllipsizedText());
		brickCategoryData.check(noEllipsizedText());
		brickCategoryPen.check(noEllipsizedText());
	}

	@Test
	public void assertNotNullValueInRTLMode() {
		brickCategoryEvent.check(matches(notNullValue()));
		brickCategoryControl.check(matches(notNullValue()));
		brickCategorySound.check(matches(notNullValue()));
		brickCategoryMotion.check(matches(notNullValue()));
		brickCategoryLooks.check(matches(notNullValue()));
		brickCategoryData.check(matches(notNullValue()));
		brickCategoryPen.check(matches(notNullValue()));
	}

	@Test
	public void assertNoOverLappingBricksInRTLMode() {
		brickCategoryEvent.check(noOverlaps());
		brickCategoryControl.check(noOverlaps());
		brickCategorySound.check(noOverlaps());
		brickCategoryMotion.check(noOverlaps());
		brickCategoryLooks.check(noOverlaps());
		brickCategoryData.check(noOverlaps());
		brickCategoryPen.check(noOverlaps());
	}

	@Test
	public void assertLayoutDirectionIsRTL() {
		brickCategoryEvent.check(isLayoutDirectionRTL());
		brickCategoryControl.check(isLayoutDirectionRTL());
		brickCategorySound.check(isLayoutDirectionRTL());
		brickCategoryMotion.check(isLayoutDirectionRTL());
		brickCategoryLooks.check(isLayoutDirectionRTL());
		brickCategoryData.check(isLayoutDirectionRTL());
		brickCategoryPen.check(isLayoutDirectionRTL());
	}

	@Test
	public void assertTextDirectionIsRTL() {
		brickCategoryEvent.check(isTextDirectionRTL());
		brickCategoryControl.check(isTextDirectionRTL());
		brickCategorySound.check(isTextDirectionRTL());
		brickCategoryMotion.check(isTextDirectionRTL());
		brickCategoryLooks.check(isTextDirectionRTL());
		brickCategoryData.check(isTextDirectionRTL());
		brickCategoryPen.check(isTextDirectionRTL());
	}

	@Test
	public void assertIsVisibleBrickInRTL() {
		brickCategoryEvent.check(isVisible());
		brickCategoryControl.check(isVisible());
		brickCategorySound.check(isVisible());
		brickCategoryMotion.check(isVisible());
		brickCategoryLooks.check(isVisible());
		brickCategoryData.check(isVisible());
		brickCategoryPen.check(isVisible());
	}

	@After
	public void tearDown() throws Exception {
	}

	public Project createProject(String projectName) {
		Project project = new Project(null, projectName);
		Sprite sprite = new Sprite("testSprite");
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
		return project;
	}
}
