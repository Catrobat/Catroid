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
        import static org.catrobat.catroid.Localization.Assertions.RTLTextDirectionAssertions.isRTLTextDirection;
        import static org.catrobat.catroid.Localization.Assertions.VisibilityAssertions.isVisible;
        import static org.hamcrest.core.IsNull.notNullValue;

/**
 * Created by Aiman Awwad on 7/27/2016.
 */

@RunWith(AndroidJUnit4.class)

public class BricksCategoriesTest extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    private MainMenuActivity mActivity;
    private Sprite sprite1;
    ViewInteraction BrickCategoryControl;
    ViewInteraction BrickCategorySound;
    ViewInteraction BrickCategoryMotion;
    ViewInteraction BrickCategoryLooks;
    ViewInteraction BrickCategoryData;
    ViewInteraction BrickCategoryPen;

    public BricksCategoriesTest() {
        super(MainMenuActivity.class);
    }
    @Before
    public void setUp() throws Exception {
        super.setUp();
        createProject();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        mActivity = getActivity();
        navigateProject();
        BrickCategoryControl =Espresso.onView(withText(R.string.category_control));
        BrickCategorySound =Espresso.onView(withText(R.string.category_sound));
        BrickCategoryMotion =Espresso.onView(withText(R.string.category_motion));
        BrickCategoryLooks =Espresso.onView(withText(R.string.category_looks));
        BrickCategoryData =Espresso.onView(withText(R.string.category_data));
        BrickCategoryPen =Espresso.onView(withText(R.string.category_pen));

    }

    @Test
    public void assertNoEllipsizedTextInRTLMode()
    {
        BrickCategoryControl.check(noEllipsizedText());
        BrickCategorySound.check(noEllipsizedText());
        BrickCategoryMotion.check(noEllipsizedText());
        BrickCategoryLooks.check(noEllipsizedText());
        BrickCategoryData.check(noEllipsizedText());
        BrickCategoryPen.check(noEllipsizedText());

    }

    @Test
    public void assertNotNullValueInRTLMode()
    {
        BrickCategoryControl.check(matches(notNullValue()));
        BrickCategorySound.check(matches(notNullValue()));
        BrickCategoryMotion.check(matches(notNullValue()));
        BrickCategoryLooks.check(matches(notNullValue()));
        BrickCategoryData.check(matches(notNullValue()));
        BrickCategoryPen.check(matches(notNullValue()));

    }

    @Test
    public void assertNoOverLappingBricksInRTLMode()
    {
        BrickCategoryControl.check(noOverlaps());
        BrickCategorySound.check(noOverlaps());
        BrickCategoryMotion.check(noOverlaps());
        BrickCategoryLooks.check(noOverlaps());
        BrickCategoryData.check(noOverlaps());
        BrickCategoryPen.check(noOverlaps());

    }

    @Test
    public void assertLayoutDirectionIsRTL()
    {
        BrickCategoryControl.check(isLayoutDirectionRTL());
        BrickCategorySound.check(isLayoutDirectionRTL());
        BrickCategoryMotion.check(isLayoutDirectionRTL());
        BrickCategoryLooks.check(isLayoutDirectionRTL());
        BrickCategoryData.check(isLayoutDirectionRTL());
        BrickCategoryPen.check(isLayoutDirectionRTL());

    }

    @Test
    public void assertTextDirectionIsRTL()
    {
        BrickCategoryControl.check(isRTLTextDirection());
        BrickCategorySound.check(isRTLTextDirection());
        BrickCategoryMotion.check(isRTLTextDirection());
        BrickCategoryLooks.check(isRTLTextDirection());
        BrickCategoryData.check(isRTLTextDirection());
        BrickCategoryPen.check(isRTLTextDirection());

    }

    @Test
    public void assertIsVisibleBrickInRTL()
    {
        BrickCategoryControl.check(isVisible());
        BrickCategorySound.check(isVisible());
        BrickCategoryMotion.check(isVisible());
        BrickCategoryLooks.check(isVisible());
        BrickCategoryData.check(isVisible());
        BrickCategoryPen.check(isVisible());

    }

    private void navigateProject()
    {
        Espresso.onView(withId(R.id.main_menu_button_continue)).perform(ViewActions.click());
        Espresso.onView(withText("كائن1")).perform(ViewActions.click());
        Espresso.onView(withText(R.string.scripts)).perform(ViewActions.click());
        Espresso.onView(withId(R.id.button_add)).perform(ViewActions.click());
    }

    private void createProject() {
        Project project = new Project(null, UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        sprite1 = new Sprite("كائن1");
        Script script = new StartScript();
        sprite1.addScript(script);
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite1);
        ProjectManager.getInstance().setCurrentScript(script);
    }


}

