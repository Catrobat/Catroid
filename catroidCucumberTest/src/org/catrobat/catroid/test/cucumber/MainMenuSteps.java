/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.catroid.test.cucumber;

import android.app.Activity;
import android.test.AndroidTestCase;
import android.widget.Button;

import com.robotium.solo.Solo;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.ProjectActivity;

import java.util.ArrayList;
import java.util.List;

// CHECKSTYLE DISABLE MethodNameCheck FOR 1000 LINES
public class MainMenuSteps extends AndroidTestCase {
	@Deprecated
	@Given("^I am in the main menu$")
	public void I_am_in_the_main_menu() {
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		assertEquals("I am not in the main menu.", MainMenuActivity.class, solo.getCurrentActivity().getClass());
	}

	@Deprecated
	@When("^I press the (\\w+) button$")
	public void I_press_the_s_Button(String button) {
		// searchButton(String) apparently returns true even for
		// partial matches, but clickOnButton(String) doesn't work
		// that way. Thus we must always use clickOnText(String) because
		// the features may not contain the full text of the button.
		Solo solo = (Solo) Cucumber.get(Cucumber.KEY_SOLO);
		solo.clickOnText(button);
	}

	@Deprecated
	@Then("^I should see the following buttons$")
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
		assertEquals("I do not see the expected buttons.", expectedButtons, actualButtons);
	}

	@Deprecated
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
		assertEquals("I did not switch to the expected view.", activityClass, solo.getCurrentActivity().getClass());
		solo.sleep(2000); // give activity time to completely load
		solo.getCurrentActivity().finish();
	}
}
