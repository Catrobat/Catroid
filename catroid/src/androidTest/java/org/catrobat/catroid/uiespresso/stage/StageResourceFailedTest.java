/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
package org.catrobat.catroid.uiespresso.stage;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StageResourceFailedTest {

	@Rule
	public BaseActivityTestRule<StageActivity> baseActivityTestRule = new
			BaseActivityTestRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("StagePausedTest");
		SensorHandler sensorHandler = SensorHandler.getInstance(ApplicationProvider.getApplicationContext());
		sensorHandler.setAccelerationUnavailable();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class})
	@Test
	public void testResourceFailedDialog() {
		baseActivityTestRule.launchActivity(null);

		onView(isRoot()).perform(CustomActions.wait(5000));

		onView(withText(R.string.prestage_resource_not_available_title))
				.check(matches(isDisplayed()));

		String failedResourceMessage =
				UiTestUtils.getResourcesString(R.string.prestage_resource_not_available_text)
						+ UiTestUtils.getResourcesString(R.string.prestage_no_acceleration_sensor_available);

		onView(withText(failedResourceMessage))
				.check(matches(isDisplayed()));
	}

	public void createProject(String projectName) {
		Script script = UiTestUtils.createProjectAndGetStartScript(projectName);
		Formula accelerationFormula = new Formula(
				new FormulaElement(FormulaElement.ElementType.SENSOR,
						Sensors.X_ACCELERATION.name(),
						null));
		SetXBrick setXBrick = new SetXBrick(accelerationFormula);
		script.addBrick(setXBrick);
	}
}
