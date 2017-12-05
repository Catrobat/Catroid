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

package org.catrobat.catroid.uiespresso.formulaeditor;

import android.support.annotation.StringRes;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.testsuites.Cat;
import org.catrobat.catroid.uiespresso.testsuites.Level;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorFunctionListMatchers;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.util.UiTestUtils.getResources;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;

@Category({Cat.CatrobatLanguage.class, Level.Smoke.class})
@RunWith(Parameterized.class)
public class FormulaEditorDeviceListTest {
	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Parameterized.Parameters(name = "{2}" + "-Test")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{R.string.formula_editor_sensor_loudness, R.string.formula_editor_function_pi_parameter, "loudness"},
				{R.string.formula_editor_function_touched, R.string.formula_editor_function_pi_parameter,
						"touches_finger"},
				{R.string.formula_editor_sensor_x_acceleration, R.string.formula_editor_function_pi_parameter,
						"acceleration_x"},
				{R.string.formula_editor_sensor_y_acceleration, R.string.formula_editor_function_pi_parameter,
						"acceleration_y"},
				{R.string.formula_editor_sensor_z_acceleration, R.string.formula_editor_function_pi_parameter,
						"acceleration_z"},
				{R.string.formula_editor_sensor_x_inclination, R.string.formula_editor_function_pi_parameter,
						"inclination_x"},
				{R.string.formula_editor_sensor_y_inclination, R.string.formula_editor_function_pi_parameter,
						"inclination_y"},
				{R.string.formula_editor_sensor_compass_direction, R.string.formula_editor_function_pi_parameter,
						"compass_direction"},
				{R.string.formula_editor_sensor_latitude, R.string.formula_editor_function_pi_parameter,
						"latitude"},
				{R.string.formula_editor_sensor_longitude, R.string.formula_editor_function_pi_parameter,
						"longitude"},
				{R.string.formula_editor_sensor_location_accuracy, R.string.formula_editor_function_pi_parameter,
						"location_accuracy"},
				{R.string.formula_editor_sensor_altitude, R.string.formula_editor_function_pi_parameter,
						"altitude"},
				{R.string.formula_editor_function_finger_x, R.string.formula_editor_function_pi_parameter,
						"screen_touch_x"},
				{R.string.formula_editor_function_finger_y, R.string.formula_editor_function_pi_parameter,
						"screen_touch_y"},
				{R.string.formula_editor_function_is_finger_touching, R.string.formula_editor_function_pi_parameter,
						"screen_is_touched"},
				{R.string.formula_editor_function_multi_finger_x, R.string.formula_editor_function_touch_parameter,
						"screen_touch_x(1)"},
				{R.string.formula_editor_function_multi_finger_y, R.string.formula_editor_function_touch_parameter,
						"screen_touch_y(1)"},
				{R.string.formula_editor_function_is_multi_finger_touching, R.string.formula_editor_function_touch_parameter,
						"screen_is_touched(1)"},
				{R.string.formula_editor_function_index_of_last_finger, R.string.formula_editor_function_pi_parameter,
						"last_screen_touch_index"},
				{R.string.formula_editor_sensor_face_detected, R.string.formula_editor_function_pi_parameter,
						"face_is_visible"},
				{R.string.formula_editor_sensor_face_size, R.string.formula_editor_function_pi_parameter,
						"face_size"},
				{R.string.formula_editor_sensor_face_x_position, R.string.formula_editor_function_pi_parameter,
						"face_x_position"},
				{R.string.formula_editor_sensor_face_y_position, R.string.formula_editor_function_pi_parameter,
						"face_y_position"},
				{R.string.formula_editor_sensor_date_year, R.string.formula_editor_function_pi_parameter,
						"year"},
				{R.string.formula_editor_sensor_date_month, R.string.formula_editor_function_pi_parameter,
						"month"},
				{R.string.formula_editor_sensor_date_day, R.string.formula_editor_function_pi_parameter,
						"day"},
				{R.string.formula_editor_sensor_time_hour, R.string.formula_editor_function_pi_parameter,
						"hour"},
				{R.string.formula_editor_sensor_time_minute, R.string.formula_editor_function_pi_parameter,
						"minute"},
				{R.string.formula_editor_sensor_time_second, R.string.formula_editor_function_pi_parameter,
						"second"}
		});
	}

	@Parameterized.Parameter
	public @StringRes
	int formulaEditorDeviceFunction;

	@Parameterized.Parameter(1)
	public @StringRes
	int formulaEditorDeviceFunctionParameter;

	@Parameterized.Parameter(2)
	public String testName;

	@Before
	public void setUp() throws Exception {
		Script script = BrickTestUtils.createProjectAndGetStartScript("FormulaEditorListFragmentTest");
		script.addBrick(new ChangeSizeByNBrick(0));
		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void testDeviceListElements() {
		onBrickAtPosition(1)
				.onChildView(withId(R.id.brick_change_size_by_edit_text))
				.perform(click());

		onView(withText(R.string.formula_editor_device))
				.perform(click());

		String formulaEditorFunctionString = UiTestUtils.getResourcesString(formulaEditorDeviceFunction);
		String formulaEditorFunctionParameterString = UiTestUtils.getResourcesString(formulaEditorDeviceFunctionParameter);

		String editorFunction = formulaEditorFunctionString + formulaEditorFunctionParameterString;
		String selectedFunctionString = getSelectedFunctionString(editorFunction);

		onData(allOf(is(instanceOf(String.class)), is(editorFunction)))
				.inAdapterView(FormulaEditorFunctionListMatchers.isFunctionListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item))
				.perform(click());

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(selectedFunctionString)));

		onView(withId(R.id.formula_editor_keyboard_delete))
				.perform(click());

		if (formulaEditorFunctionParameterString.compareTo(getResources().getString(R.string
				.formula_editor_function_pi_parameter)) != 0) {
			onView(withId(R.id.formula_editor_keyboard_delete))
					.perform(click());
		}

		onView(withId(R.id.formula_editor_edit_field))
				.check(matches(withText(" ")));
	}

	private String getSelectedFunctionString(String functionString) {
		return functionString.replace("(", "( ")
				.replace(")", " )")
				.replace(",", " , ")
				.concat(" ");
	}
}
