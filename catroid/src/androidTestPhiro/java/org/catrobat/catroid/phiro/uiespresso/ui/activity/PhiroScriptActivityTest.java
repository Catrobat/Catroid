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
package org.catrobat.catroid.phiro.uiespresso.ui.activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.DataInteraction;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.bricks.SetXBrick;
import org.catrobat.catroid.ui.ScriptActivity;
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickTestUtils;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityInstrumentationRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_HINTS;
import static org.catrobat.catroid.ui.BaseSettingsActivity.SETTINGS_SHOW_PHIRO_BRICKS;
import static org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers.isBrickCategoryListItem;
import static org.catrobat.catroid.uiespresso.util.matchers.BrickCategoryListMatchers.isBrickCategoryView;
import static org.catrobat.catroid.uiespresso.util.matchers.FormulaEditorCategoryListMatchers.isFormulaEditorCategoryListView;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
public class PhiroScriptActivityTest {

	private Map<String, Boolean> initialPreferences = new HashMap<>();

	@Rule
	public BaseActivityInstrumentationRule<ScriptActivity> baseActivityTestRule = new
			BaseActivityInstrumentationRule<>(ScriptActivity.class, true, false);

	@Before
	public void setUp() throws Exception {
		BrickTestUtils.createProjectAndGetStartScript("phiroTestProject").addBrick(new SetXBrick());
		preparePhiroSettings();

		baseActivityTestRule.launchActivity(null);
	}

	@Test
	public void phiroIsFirstInBrickCategoryTest() {
		onView(withId(R.id.button_add))
				.perform(click());

		String expectedCategoryString = UiTestUtils.getResourcesString(R.string.category_phiro);

		onBrickCategory()
				.atPosition(0)
				.check(matches(withText(expectedCategoryString)));
	}

	@Test
	public void phiroIsFirstInFormulaEditorDeviceCategoryTest() {
		onView(withId(R.id.brick_set_x_edit_text))
				.perform(click());

		onView(withId(R.id.formula_editor_keyboard_sensors))
				.perform(click());

		String firstPhiroSensorString = UiTestUtils.getResourcesString(R.string.formula_editor_phiro_sensor_front_left);

		onFormulaEditorCategory()
				.atPosition(0)
				.check(matches(withText(firstPhiroSensorString)));
	}

	@After
	public void teardown() throws Exception {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());
		SharedPreferences.Editor editor = sharedPreferences.edit();

		editor.putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, initialPreferences.get(SETTINGS_SHOW_PHIRO_BRICKS));
		editor.putBoolean(SETTINGS_SHOW_HINTS, initialPreferences.get(SETTINGS_SHOW_HINTS));
		editor.apply();
	}

	private void preparePhiroSettings() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext());

		initialPreferences.put(SETTINGS_SHOW_PHIRO_BRICKS, sharedPreferences.getBoolean(SETTINGS_SHOW_PHIRO_BRICKS, false));
		initialPreferences.put(SETTINGS_SHOW_HINTS, sharedPreferences.getBoolean(SETTINGS_SHOW_HINTS, false));

		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(SETTINGS_SHOW_PHIRO_BRICKS, true);
		editor.putBoolean(SETTINGS_SHOW_HINTS, false);
		editor.apply();
	}

	private DataInteraction onFormulaEditorCategory() {
		return onData(instanceOf(String.class))
				.inAdapterView(isFormulaEditorCategoryListView())
				.onChildView(withId(R.id.fragment_formula_editor_list_item));
	}

	private DataInteraction onBrickCategory() {
		return onData(instanceOf(String.class))
				.inAdapterView(isBrickCategoryView())
				.onChildView(isBrickCategoryListItem());
	}
}
