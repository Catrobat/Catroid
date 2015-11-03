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
package org.catrobat.catroid.uitest.devices.mindstorms.nxt;

import android.content.Context;
import android.widget.ListView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXT;
import org.catrobat.catroid.devices.mindstorms.nxt.LegoNXTImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

public class LegoNXTPreferencesTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Context applicationContext;

	public LegoNXTPreferencesTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();

		applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
		SettingsActivity.disableLegoMindstormsSensorInfoDialog(applicationContext);
	}

	public void testNXTAllBricksAvailable() throws InterruptedException {
		boolean nxtBricksEnabledStart = SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(applicationContext);

		if (!nxtBricksEnabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
			solo.clickOnText(preferenceTitle);

			solo.goBack();
			solo.goBack();
		}

		solo.waitForText(solo.getString(R.string.main_menu_new));
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "testNXTAllBricksAvailable");

		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.sleep(200);
		solo.scrollListToBottom(fragmentListView);
		solo.waitForText(solo.getString(R.string.category_lego_nxt));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));

		solo.sleep(300);

		assertTrue("NXT turn motor brick not available!", solo.searchText(solo.getString(R.string.nxt_brick_motor_turn_angle)));
		assertTrue("NXT stop motor brick not available!", solo.searchText(solo.getString(R.string.nxt_motor_stop)));
		assertTrue("NXT move motor brick not available!", solo.searchText(solo.getString(R.string.nxt_brick_motor_move)));
		assertTrue("NXT play tone brick not available!", solo.searchText(solo.getString(R.string.nxt_play_tone)));
	}

	public void testNXTSensorsSetCorrectly() throws InterruptedException {
		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(applicationContext, true);
		LegoNXT nxt = new LegoNXTImpl(applicationContext);
		ConnectionDataLogger logger = ConnectionDataLogger.createLocalConnectionLogger();
		nxt.setConnection(logger.getConnectionProxy());

		boolean nxtBricksEnabledStart = SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(applicationContext);

		solo.clickOnActionBarItem(R.id.settings);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));

		if (!nxtBricksEnabledStart) {
			solo.clickOnText(preferenceTitle);
		}

		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(applicationContext, true);

		NXTSensor.Sensor sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_1);
		assertEquals("NXT sensor 1 not set correctly!", NXTSensor.Sensor.LIGHT_INACTIVE, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_2);
		assertEquals("NXT sensor 2 not set correctly!", NXTSensor.Sensor.TOUCH, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_3);
		assertEquals("NXT sensor 3 not set correctly!", NXTSensor.Sensor.TOUCH, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_4);
		assertEquals("NXT sensor 4 not set correctly!", NXTSensor.Sensor.ULTRASONIC, sensor);

		nxt.initialise();

		assertNotNull("Sensor 1 not initialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTLightSensor);

		assertNotNull("Sensor 2 not initialized correctly", nxt.getSensor2());
		assertTrue("Sensor 2 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor2() instanceof NXTTouchSensor);

		assertNotNull("Sensor 3 not initialized correctly", nxt.getSensor3());
		assertTrue("Sensor 3 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor3() instanceof NXTTouchSensor);

		assertNotNull("Sensor 4 not initialized correctly", nxt.getSensor4());
		assertTrue("Sensor 4 is of wrong instance, SensorFactory may has an error",
				nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);

		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_sound));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		solo.sleep(500);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_1);
		assertEquals("NXT sensor 1 not set correctly!", NXTSensor.Sensor.TOUCH, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_2);
		assertEquals("NXT sensor 2 not set correctly!", NXTSensor.Sensor.SOUND, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_3);
		assertEquals("NXT sensor 3 not set correctly!", NXTSensor.Sensor.LIGHT_INACTIVE, sensor);

		sensor = SettingsActivity.getLegoMindstormsNXTSensorMapping(applicationContext, SettingsActivity.NXT_SENSOR_4);
		assertEquals("NXT sensor 4 not set correctly!", NXTSensor.Sensor.ULTRASONIC, sensor);

		assertNotNull("Sensor 1 not reinitialized correctly", nxt.getSensor1());
		assertTrue("Sensor 1 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor1() instanceof NXTTouchSensor);

		assertNotNull("Sensor 2 not reinitialized correctly", nxt.getSensor2());
		assertTrue("Sensor 2 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor2() instanceof NXTSoundSensor);

		assertNotNull("Sensor 3 not reinitialized correctly", nxt.getSensor3());
		assertTrue("Sensor 3 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor3() instanceof NXTLightSensor);

		assertNotNull("Sensor 4 not reinitialized correctly", nxt.getSensor4());
		assertTrue("Sensor 4 is of wrong instance now, SensorFactory may has an error",
				nxt.getSensor4() instanceof NXTI2CUltraSonicSensor);

		nxt.disconnect();
		logger.disconnectAndDestroy();
	}

	public void testNXTSensorsAvailable() throws InterruptedException {
		boolean nxtBricksEnabledStart = SettingsActivity.isMindstormsNXTSharedPreferenceEnabled(applicationContext);
		SettingsActivity.setLegoMindstormsNXTSensorChooserEnabled(applicationContext, true);

		solo.clickOnActionBarItem(R.id.settings);

		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_nxt_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_nxt_sensors));

		if (!nxtBricksEnabledStart) {
			solo.clickOnText(preferenceTitle);
		}

		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_1));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_touch));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_2));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_sound));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_3));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_light));
		solo.clickOnText(solo.getString(R.string.nxt_choose_sensor_4));
		solo.waitForText(solo.getString(R.string.nxt_no_sensor));
		solo.clickOnText(solo.getString(R.string.nxt_sensor_ultrasonic));

		solo.goBack();
		solo.goBack();

		solo.waitForText(solo.getString(R.string.main_menu_new));
		solo.clickOnText(solo.getString(R.string.main_menu_new));
		solo.enterText(0, "testNXTSensorsAvailable");

		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForText(solo.getString(R.string.ok));
		solo.clickOnText(solo.getString(R.string.ok));

		solo.waitForText(solo.getString(R.string.background));
		solo.clickOnText(solo.getString(R.string.background));
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));
		UiTestUtils.clickOnBottomBar(solo, R.id.button_add);
		solo.sleep(200);
		ListView fragmentListView = solo.getCurrentViews(ListView.class).get(
				solo.getCurrentViews(ListView.class).size() - 1);
		solo.sleep(200);
		solo.scrollListToBottom(fragmentListView);
		solo.waitForText(solo.getString(R.string.category_lego_nxt));
		solo.clickOnText(solo.getString(R.string.category_lego_nxt));
		solo.waitForText(solo.getString(R.string.nxt_play_tone));
		solo.clickOnText(solo.getString(R.string.nxt_play_tone));
		solo.sleep(300);
		solo.clickOnView(solo.getViews().get(0));
		solo.sleep(300);
		solo.clickOnText("1");
		solo.waitForText(solo.getString(R.string.formula_editor_sensors));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensors));

		solo.sleep(300);

		assertTrue("NXT sensor 1 not available!", solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_1)));
		assertTrue("NXT sensor 2 not available!", solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_2)));
		assertTrue("NXT sensor 3 not available!", solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_3)));
		assertTrue("NXT sensor 4 not available!", solo.searchText(solo.getString(R.string.formula_editor_sensor_lego_nxt_4)));
	}
}
