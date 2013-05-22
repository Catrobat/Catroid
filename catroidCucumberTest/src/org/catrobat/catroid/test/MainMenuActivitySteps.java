package org.catrobat.catroid.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivitySteps extends ActivityInstrumentationTestCase2<MainMenuActivity> {
    private Solo solo;
    private Activity mActivity;

    public MainMenuActivitySteps() {
        super(MainMenuActivity.class);
    }

    @Before
    public void before() {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Given("^I am in the main menu$")
    public void I_am_in_the_main_menu() {
        mActivity = solo.getCurrentActivity();
        assertEquals(MainMenuActivity.class, mActivity.getClass());
    }

    @When("^I press the (\\w+) button$")
    public void I_press_the_s_Button(String button) {
        // searchButton(String) apparently returns true even for
        // partial matches, but clickOnButton(String) doesn't work
        // that way. Thus we must always use clickOnText(String) because
        // the features may not contain the full text of the button.
        solo.clickOnText(button);
    }

    @Then("^I should the following buttons:$")
    public void I_see_vertical_layout_menu_elements(List<String> expectedButtons) {
        List<String> actualButtons = new ArrayList<String>();
        for (Button button : solo.getCurrentButtons()) {
            String text = button.getText().toString();
            if (!text.isEmpty()) {
                // Only use the first paragraph of a button text.
                int trimIndex = text.contains("\n") ? text.indexOf("\n") : text.length();
                actualButtons.add(text.substring(0, trimIndex));
            }
        }
        assertEquals(expectedButtons, actualButtons);
    }

    @Then("^I should switch to the (\\w+) view$")
    public void I_should_switch_to_the_s_view(String view) {
        Class<? extends Activity> activityClass = null;
        if ("program".equals(view)) {
            activityClass = ProjectActivity.class;
        } else if ("programs".equals(view)) {
            activityClass = MyProjectsActivity.class;
        } else {
            fail(String.format("View '%s' does not exist.", view));
        }
        solo.waitForActivity(activityClass.getSimpleName(), 2000);
        assertEquals(activityClass, solo.getCurrentActivity().getClass());
    }
}
