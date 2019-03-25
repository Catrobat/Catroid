/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.FormulaElement;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.actions.CustomActions;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class StageResourceFailedTest {

	@Rule
	public BaseActivityInstrumentationRule<StageActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(StageActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		createProject("StagePausedTest");
		SensorHandler sensorHandler = SensorHandler.getInstance(InstrumentationRegistry.getTargetContext());
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
		Project project = new Project(InstrumentationRegistry.getTargetContext(), projectName);
		Sprite sprite = new Sprite("testSprite");
		Script script = new StartScript();
		Formula accelerationFormula = new Formula(
				new FormulaElement(FormulaElement.ElementType.SENSOR,
						Sensors.X_ACCELERATION.name(),
						null));
		SetXBrick setXBrick = new SetXBrick(accelerationFormula);
		script.addBrick(setXBrick);
		sprite.addScript(script);
		project.getDefaultScene().addSprite(sprite);
		ProjectManager.getInstance().setCurrentProject(project);
		ProjectManager.getInstance().setCurrentSprite(sprite);
	}
}
