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
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.localization.assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.TextDirectionAssertions.isTextDirectionRTL;
import static org.catrobat.catroid.uiespresso.localization.assertions.VisibilityAssertions.isVisible;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)
public class ProgramMenuActivityTest {
	private ViewInteraction programMenuScripts;
	private ViewInteraction programMenuLooks;
	private ViewInteraction programMenuSound;

	@Rule
	public BaseActivityInstrumentationRule<ProgramMenuActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ProgramMenuActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		UiTestUtils.createProject("ProgramMenuActivityTest");
		baseActivityTestRule.launchActivity(null);
		programMenuScripts = Espresso.onView(withId(R.id.program_menu_button_scripts));
		programMenuLooks = Espresso.onView(withId(R.id.program_menu_button_looks));
		programMenuSound = Espresso.onView(withId(R.id.program_menu_button_sounds));
	}

	@Test
	public void assertCompletelyDisplayed() {
		programMenuScripts.check(matches(isCompletelyDisplayed()));
		programMenuLooks.check(matches(isCompletelyDisplayed()));
		programMenuSound.check(matches(isCompletelyDisplayed()));
	}

	@Test
	public void assertNoEllipsizedTextInRTLMode() {
		programMenuScripts.check(noEllipsizedText());
		programMenuLooks.check(noEllipsizedText());
		programMenuSound.check(noEllipsizedText());
	}

	@Test
	public void assertNotNullValueInRTLMode() {
		programMenuScripts.check(matches(notNullValue()));
		programMenuLooks.check(matches(notNullValue()));
		programMenuSound.check(matches(notNullValue()));
	}

	@Test
	public void assertNoOverLappingBricksInRTLMode() {
		programMenuScripts.check(noOverlaps());
		programMenuLooks.check(noOverlaps());
		programMenuSound.check(noOverlaps());
	}

	@Test
	public void assertLayoutDirectionIsRTL() {
		programMenuScripts.check(isLayoutDirectionRTL());
		programMenuLooks.check(isLayoutDirectionRTL());
		programMenuSound.check(isLayoutDirectionRTL());
	}

	@Test
	public void assertTextDirectionIsRTL() {
		programMenuScripts.check(isTextDirectionRTL());
		programMenuLooks.check(isTextDirectionRTL());
		programMenuSound.check(isTextDirectionRTL());
	}

	@Test
	public void assertIsVisibleBrickInRTLMode() {
		programMenuScripts.check(isVisible());
		programMenuLooks.check(isVisible());
		programMenuSound.check(isVisible());
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
