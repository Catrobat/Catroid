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
        import static org.catrobat.catroid.Localization.Assertions.LayoutDirectionAssertions.isLayoutDirectionRTL;
        import static org.catrobat.catroid.Localization.Assertions.TextDirectionAssertions.isTextDirectionRTL;
        import static org.catrobat.catroid.Localization.Assertions.VisibilityAssertions.isVisible;
        import static org.hamcrest.core.IsNull.notNullValue;
@RunWith(AndroidJUnit4.class)
public class ProgramMenuActivityTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    private MainMenuActivity mActivity;
    private Sprite sprite;
    ViewInteraction scriptsButton;
    ViewInteraction looksButton;
    ViewInteraction soundButton;
    public ProgramMenuActivityTest() {
        super(MainMenuActivity.class);
    }
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createProject();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        navigateProject();
        scriptsButton=Espresso.onView(withId(R.id.program_menu_button_scripts));
        looksButton=Espresso.onView(withId(R.id.program_menu_button_looks));
        soundButton=Espresso.onView(withId(R.id.program_menu_button_sounds));
    }

    @Test
    public void assertNoEllipsizedTextInRTLMode()
    {
        scriptsButton.check(noEllipsizedText());
        looksButton.check(noEllipsizedText());
        soundButton.check(noEllipsizedText());
    }

    @Test
    public void assertNotNullValueInRTLMode() {
        scriptsButton.check(matches(notNullValue()));
        looksButton.check(matches(notNullValue()));
        soundButton.check(matches(notNullValue()));
    }

    @Test
    public void assertNoOverLappingBricksInRTLMode()
    {
        scriptsButton.check(noOverlaps());
        looksButton.check(noOverlaps());
        soundButton.check(noOverlaps());
    }

    @Test
    public void assertLayoutDirectionIsRTL()
    {
        scriptsButton.check(isLayoutDirectionRTL());
        looksButton.check(isLayoutDirectionRTL());
        soundButton.check(isLayoutDirectionRTL());
    }

    @Test
    public void assertTextDirectionIsRTL()
    {
        scriptsButton.check(isTextDirectionRTL());
        looksButton.check(isTextDirectionRTL());
        soundButton.check(isTextDirectionRTL());
    }

    @Test
    public void assertIsVisibleBrickInRTLMode()
    {
        scriptsButton.check(isVisible());
        looksButton.check(isVisible());
        soundButton.check(isVisible());
    }

    private void navigateProject()
    {
        Espresso.onView(withId(R.id.main_menu_button_continue)).perform(ViewActions.click());
        Espresso.onView(withText("كائن1")).perform(ViewActions.click());
    }

    private void createProject() {
        Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        sprite = new Sprite("كائن1");
        Script script = new StartScript();
        sprite.addScript(script);
        project.getDefaultScene().addSprite(sprite);
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
    }

}