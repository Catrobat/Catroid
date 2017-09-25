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
package org.catrobat.catroid.localization;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.LayoutAssertions.noEllipsizedText;
import static android.support.test.espresso.assertion.LayoutAssertions.noOverlaps;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.localization.assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
import static org.catrobat.catroid.localization.assertions.TextDirectionAssertions.isTextDirectionRTL;
import static org.catrobat.catroid.localization.assertions.VisibilityAssertions.isVisible;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)

public class BricksCategoriesTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
	private MainMenuActivity mainMenuActivity;
	private Sprite sprite1;
	private ViewInteraction brickCategoryControl;
	private ViewInteraction brickCategorySound;
	private ViewInteraction brickCategoryMotion;
	private ViewInteraction brickCategoryLooks;
	private ViewInteraction brickCategoryData;
	private static final String BUTTON_NAME_PROGRAM = "كائن1";

	public BricksCategoriesTest() {
		super(MainMenuActivity.class);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		createProject();
		injectInstrumentation(InstrumentationRegistry.getInstrumentation());
		mainMenuActivity = getActivity();
		navigateProject();
		brickCategoryControl = Espresso.onView(withText(R.string.category_control));
		brickCategorySound = Espresso.onView(withText(R.string.category_sound));
		brickCategoryMotion = Espresso.onView(withText(R.string.category_motion));
		brickCategoryLooks = Espresso.onView(withText(R.string.category_looks));
		brickCategoryData = Espresso.onView(withText(R.string.category_data));
	}

	@Test
	public void assertNoEllipsizedTextInRTLMode() {
		brickCategoryControl.check(noEllipsizedText());
		brickCategorySound.check(noEllipsizedText());
		brickCategoryMotion.check(noEllipsizedText());
		brickCategoryLooks.check(noEllipsizedText());
		brickCategoryData.check(noEllipsizedText());
	}

	@Test
	public void assertNotNullValueInRTLMode() {
		brickCategoryControl.check(matches(notNullValue()));
		brickCategorySound.check(matches(notNullValue()));
		brickCategoryMotion.check(matches(notNullValue()));
		brickCategoryLooks.check(matches(notNullValue()));
		brickCategoryData.check(matches(notNullValue()));
	}

	@Test
	public void assertNoOverLappingBricksInRTLMode() {
		brickCategoryControl.check(noOverlaps());
		brickCategorySound.check(noOverlaps());
		brickCategoryMotion.check(noOverlaps());
		brickCategoryLooks.check(noOverlaps());
		brickCategoryData.check(noOverlaps());
	}

	@Test
	public void assertLayoutDirectionIsRTL() {
		brickCategoryControl.check(isLayoutDirectionRTL());
		brickCategorySound.check(isLayoutDirectionRTL());
		brickCategoryMotion.check(isLayoutDirectionRTL());
		brickCategoryLooks.check(isLayoutDirectionRTL());
		brickCategoryData.check(isLayoutDirectionRTL());
	}

	@Test
	public void assertTextDirectionIsRTL() {
		brickCategoryControl.check(isTextDirectionRTL());
		brickCategorySound.check(isTextDirectionRTL());
		brickCategoryMotion.check(isTextDirectionRTL());
		brickCategoryLooks.check(isTextDirectionRTL());
		brickCategoryData.check(isTextDirectionRTL());
	}

	@Test
	public void assertIsVisibleBrickInRTL() {
		brickCategoryControl.check(isVisible());
		brickCategorySound.check(isVisible());
		brickCategoryMotion.check(isVisible());
		brickCategoryLooks.check(isVisible());
		brickCategoryData.check(isVisible());
	}

	private void navigateProject() {
		Espresso.onView(withId(R.id.main_menu_button_continue)).perform(ViewActions.click());
		Espresso.onView(withText("BUTTON_NAME_PROGRAM")).perform(ViewActions.click());
		Espresso.onView(withText(R.string.scripts)).perform(ViewActions.click());
		Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click());
	}

	private void createProject() {
		Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
		sprite1 = new Sprite("BUTTON_NAME_PROGRAM");
		Script script = new StartScript();
		sprite1.addScript(script);
		ProjectManager.getInstance().setProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite1);
		ProjectManager.getInstance().setCurrentScript(script);
	}
}

