package org.catrobat.catroid.test;

import android.app.Activity;
import android.test.AndroidTestCase;
import android.widget.Button;
import com.jayway.android.robotium.solo.Solo;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

public class MainMenuSteps extends AndroidTestCase {
    @Given("^I am in the main menu$")
    public void I_am_in_the_main_menu() {
        Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
        assertEquals(MainMenuActivity.class, solo.getCurrentActivity().getClass());
    }

    @When("^I press the (\\w+) button$")
    public void I_press_the_s_Button(String button) {
        // searchButton(String) apparently returns true even for
        // partial matches, but clickOnButton(String) doesn't work
        // that way. Thus we must always use clickOnText(String) because
        // the features may not contain the full text of the button.
        Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
        solo.clickOnText(button);
    }

    @Then("^I should see the following buttons:$")
    public void I_should_see_the_following_buttons(List<String> expectedButtons) {
        Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
        List<String> actualButtons = new ArrayList<String>();
        for (Button button : solo.getCurrentViews(Button.class)) {
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
        Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
        solo.waitForActivity(activityClass.getSimpleName(), 3000);
        assertEquals(activityClass, solo.getCurrentActivity().getClass());
        solo.sleep(2000); // give activity time to completely load
        solo.getCurrentActivity().finish();
    }
}
