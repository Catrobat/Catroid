package org.catrobat.catroid.uiespresso.content.brick;

import android.support.test.espresso.Espresso;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeVariableBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.util.BaseActivityInstrumentationRule;
import org.catrobat.catroid.uitest.util.UiTestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.catroid.R.id.brick_change_variable_label;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.checkIfBrickAtPositionShowsString;
import static org.catrobat.catroid.uiespresso.content.brick.BrickTestUtils.onScriptList;

@RunWith(AndroidJUnit4.class)
public class ChangeVariableBrickTest {


    @Rule
    public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
            BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

    @Before
    public void setUp() throws Exception {
        createProject();
        baseActivityTestRule.launchActivity(null);
    }

    @Test
    public void changeVariableBrickTest() {

        checkIfBrickAtPositionShowsString(0, R.string.brick_when_started);
        checkIfBrickAtPositionShowsString(1, R.string.brick_change_variable);

        String userVariableName = "testVariable1";
        String secondUserVariableName = "testVariable2";

        onView(withId(R.id.change_variable_spinner)).perform(click());
        onView(withText(R.string.formula_editor_variable_dialog_title)).check(matches(isDisplayed()));
        onView(withId(R.id.dialog_formula_editor_data_name_edit_text)).perform(typeText(userVariableName));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(brick_change_variable_label)).check(matches(isDisplayed()));
        onScriptList().atPosition(1).onChildView(withId(R.id.change_variable_spinner)).onChildView(withText(userVariableName))
                .check(matches(isDisplayed()));
        onScriptList().atPosition(1).onChildView(withId(R.id.change_variable_spinner))
                .perform(click());
        onView(withText(R.string.brick_variable_spinner_create_new_variable))
                .perform(click());
        onView(withText(R.string.formula_editor_variable_dialog_title)).check(matches(isDisplayed()));
        onView(withId(R.id.dialog_formula_editor_data_name_edit_text))
                .perform(typeText(secondUserVariableName));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(brick_change_variable_label)).check(matches(isDisplayed()));
//        onScriptList().atPosition(1).onChildView(withId(R.id.change_variable_spinner)).onChildView(withText(secondUserVariableName))
//                .check(matches(isDisplayed()));
        onScriptList().atPosition(1).onChildView(withId(R.id.brick_change_variable_edit_text)).perform(click());
        onView(withId(R.id.formula_editor_keyboardview)).check(matches(isDisplayed()));
        onView(withId(R.id.formula_editor_keyboard_data)).perform(click());
        onView(withText(secondUserVariableName)).perform(longClick());
        onView(withText(R.string.delete)).perform(click());
        //for warning dialog
        onView(withText(R.string.deletion_alert_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.deletion_alert_yes)).perform(click());
        Espresso.pressBack();
        onView(withId(R.id.formula_editor_keyboardview)).check(matches(isDisplayed()));
        Espresso.pressBack();
        onScriptList().atPosition(1).onChildView(withId(R.id.change_variable_spinner)).onChildView(withText(userVariableName))
                .check(matches(isDisplayed()));
    }

    @After
    public void tearDown() throws Exception {
    }

    private void createProject() {
        Script startScript = BrickTestUtils.createProjectAndGetStartScript(UiTestUtils.DEFAULT_TEST_PROJECT_NAME);
        ChangeVariableBrick changeVariableBrick = new ChangeVariableBrick(10);
        startScript.addBrick(changeVariableBrick);
    }
}
