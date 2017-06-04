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
package org.catrobat.catroid.Localization;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static android.support.test.espresso.assertion.LayoutAssertions.noEllipsizedText;
import static android.support.test.espresso.assertion.LayoutAssertions.noOverlaps;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.catrobat.catroid.Localization.Assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
import static org.catrobat.catroid.Localization.Assertions.TextDirectionAssertions.isTextDirectionRTL;
import static org.catrobat.catroid.Localization.Assertions.VisibilityAssertions.isVisible;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(AndroidJUnit4.class)

public class MainMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    ViewInteraction continueButton;
    ViewInteraction newButton;
    ViewInteraction programsButton;
    ViewInteraction helpButton;
    ViewInteraction webButton;
    ViewInteraction uploadButton;

    public MainMenuActivityTest() {
        super(MainMenuActivity.class);
    }

    @UiThreadTest
    public void testShouldNotCrashIfProjectIsNull() {
        ProjectManager.getInstance().setProject(null);
        getInstrumentation().callActivityOnPostCreate(getActivity(), null);
        assertTrue("Test failed!", true);
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        continueButton =Espresso.onView(withId(R.id.main_menu_button_continue));
        newButton =Espresso.onView(withId(R.id.main_menu_button_new));
        programsButton =Espresso.onView(withId(R.id.main_menu_button_programs));
        helpButton =Espresso.onView(withId(R.id.main_menu_button_help));
        webButton =Espresso.onView(withId(R.id.main_menu_button_web));
        uploadButton =Espresso.onView(withId(R.id.main_menu_button_upload));
    }

    @Test
    public void assertNoEllipsizedTextInRTLMode()
    {
        continueButton.check(noEllipsizedText());
        newButton.check(noEllipsizedText());
        programsButton.check(noEllipsizedText());
        helpButton.check(noEllipsizedText());
        webButton.check(noEllipsizedText());
        uploadButton.check(noEllipsizedText());
    }

    @Test
    public void assertNotNullValueInRTLMode()
    {
        continueButton.check(matches(notNullValue()));
        newButton.check(matches(notNullValue()));
        programsButton.check(matches(notNullValue()));
        helpButton.check(matches(notNullValue()));
        webButton.check(matches(notNullValue()));
        uploadButton.check(matches(notNullValue()));
    }
    @Test
    public void assertNoOverLappingBricksInRTLMode()
    {
        continueButton.check(noOverlaps());
        newButton.check(noOverlaps());
        programsButton.check(noOverlaps());
        helpButton.check(noOverlaps());
        webButton.check(noOverlaps());
        uploadButton.check(noOverlaps());
    }
    @Test
    public void assertLayoutDirectionIsRTL()
    {
        continueButton.check(isLayoutDirectionRTL());
        newButton.check(isLayoutDirectionRTL());
        programsButton.check(isLayoutDirectionRTL());
        helpButton.check(isLayoutDirectionRTL());
        webButton.check(isLayoutDirectionRTL());
        uploadButton.check(isLayoutDirectionRTL());
    }
    @Test
    public void assertTextDirectionIsRTL()
    {
        continueButton.check(isTextDirectionRTL());
        newButton.check(isTextDirectionRTL());
        programsButton.check(isTextDirectionRTL());
        helpButton.check(isTextDirectionRTL());
        webButton.check(isTextDirectionRTL());
        uploadButton.check(isTextDirectionRTL());
    }

    @Test
    public void assertIsVisibleBrickInRTL()
    {
        continueButton.check(isVisible());
        newButton.check(isVisible());
        programsButton.check(isVisible());
        helpButton.check(isVisible());
        webButton.check(isVisible());
        uploadButton.check(isVisible());
    }
}
