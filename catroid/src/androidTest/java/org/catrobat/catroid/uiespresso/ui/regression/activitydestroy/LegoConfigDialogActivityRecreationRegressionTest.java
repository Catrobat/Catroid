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

package org.catrobat.catroid.uiespresso.ui.regression.activitydestroy;

import android.preference.PreferenceManager;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.rules.FlakyTestRule;
import org.catrobat.catroid.runner.Flaky;
import org.catrobat.catroid.test.utils.TestUtils;
import org.catrobat.catroid.testsuites.annotations.Cat;
import org.catrobat.catroid.testsuites.annotations.Level;
import org.catrobat.catroid.ui.SpriteActivity;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper;
import org.catrobat.catroid.uiespresso.util.UiTestUtils;
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition;
import static org.catrobat.catroid.uiespresso.formulaeditor.utils.FormulaEditorWrapper.onFormulaEditor;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@Category({Cat.AppUi.class, Level.Smoke.class})
public class LegoConfigDialogActivityRecreationRegressionTest {

	@Rule
	public FragmentActivityTestRule<SpriteActivity> baseActivityTestRule = new
			FragmentActivityTestRule<>(SpriteActivity.class, SpriteActivity.EXTRA_FRAGMENT_POSITION, SpriteActivity.FRAGMENT_SCRIPTS);

	@Rule
	public FlakyTestRule flakyTestRule = new FlakyTestRule();

	private NXTSensor.Sensor[] sensorMappingBuffer;
	private boolean nxtSettingBuffer;

	@Before
	public void setUp() throws Exception {
		Script script = UiTestUtils.createProjectAndGetStartScript(getClass().getSimpleName());
		script.addBrick(new ChangeSizeByNBrick(0));

		nxtSettingBuffer = PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
				.getBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, false);

		setNXTBrickSetting(true);

		sensorMappingBuffer = SettingsFragment.getLegoNXTSensorMapping(ApplicationProvider.getApplicationContext());

		SettingsFragment.setLegoMindstormsNXTSensorMapping(ApplicationProvider.getApplicationContext(),
				new NXTSensor.Sensor[] {
						NXTSensor.Sensor.NO_SENSOR,
						NXTSensor.Sensor.NO_SENSOR,
						NXTSensor.Sensor.NO_SENSOR,
						NXTSensor.Sensor.NO_SENSOR});

		baseActivityTestRule.launchActivity();
	}

	@Category({Cat.AppUi.class, Level.Smoke.class, Cat.Quarantine.class})
	@Flaky
	@Test
	public void testActivityRecreateLegoConfigDialog() {
		onBrickAtPosition(1).onChildView(withId(R.id.brick_change_size_by_edit_text)).perform(click());

		onFormulaEditor()
				.performOpenCategory(FormulaEditorWrapper.Category.DEVICE)
				.performSelect(R.string.formula_editor_sensor_lego_nxt_touch);

		InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> baseActivityTestRule.getActivity().recreate());

		InstrumentationRegistry.getInstrumentation().waitForIdleSync();
	}

	@After
	public void tearDown() throws IOException {
		setNXTBrickSetting(nxtSettingBuffer);

		SettingsFragment
				.setLegoMindstormsNXTSensorMapping(ApplicationProvider.getApplicationContext(), sensorMappingBuffer);

		TestUtils.deleteProjects(getClass().getSimpleName());
	}

	private void setNXTBrickSetting(boolean bricksEnabled) {
		PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext()).edit()
				.putBoolean(SettingsFragment.SETTINGS_MINDSTORMS_NXT_BRICKS_CHECKBOX_PREFERENCE, bricksEnabled)
				.commit();
	}
}
