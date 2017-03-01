/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.uitest.devices.mindstorms.ev3;

import android.content.Context;

import org.catrobat.catroid.R;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.SingleSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.StartScript;
import org.catrobat.catroid.content.bricks.LegoEv3MotorMoveBrick;
import org.catrobat.catroid.content.bricks.LegoEv3PlayToneBrick;
import org.catrobat.catroid.content.bricks.SetLookBrick;
import org.catrobat.catroid.content.bricks.WaitBrick;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.ui.MainMenuActivity;
import org.catrobat.catroid.ui.MyProjectsActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;
import org.catrobat.catroid.uitest.util.UiTestUtils;

import java.io.File;
import java.util.ArrayList;

public class LegoEV3SensorInfoTests extends BaseActivityInstrumentationTestCase<MainMenuActivity> {

	private Context applicationContext;

	private final String projectNameEv3 = UiTestUtils.PROJECTNAME1;
	private final String projectNameNoEv3 = UiTestUtils.PROJECTNAME2;
	private final String spriteName = "testSprite";

	private static final int IMAGE_FILE_ID = org.catrobat.catroid.test.R.raw.icon;

	public LegoEV3SensorInfoTests() {
		super(MainMenuActivity.class);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		UiTestUtils.prepareStageForTest();
		setSensors(EV3Sensor.Sensor.TOUCH);

		applicationContext = getInstrumentation().getTargetContext().getApplicationContext();
	}

	private void setSensors(EV3Sensor.Sensor sensor) {
		SettingsActivity.setLegoMindstormsEV3SensorMapping(this.getInstrumentation().getTargetContext(),
				new EV3Sensor.Sensor[] { sensor, sensor, sensor, sensor });
	}

	public void testEV3SensorInfoDialog() throws InterruptedException {
		createBrickTestproject(projectNameEv3);
		createNoEV3UseTestproject(projectNameNoEv3);

		boolean ev3BricksEnabledStart = SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(applicationContext);

		boolean ev3DialogDisabledStart = SettingsActivity.getShowLegoEV3MindstormsSensorInfoDialog(applicationContext);

		if (!ev3BricksEnabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_ev3_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
			solo.clickOnText(preferenceTitle);

			solo.goBack();
			solo.goBack();
		}

		if (ev3DialogDisabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_ev3_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
			solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

			solo.goBack();
			solo.goBack();
		}

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));

		solo.clickOnText(projectNameEv3);

		assertTrue("EV3 Sensor Dialog not shown for Project with EV3 Brick",
				solo.waitForText(solo.getString(R.string.lego_ev3_sensor_config_info_title)));

		assertTrue("Wrong Sensors listed in EV3 Sensor Dialog",
				solo.searchText(solo.getString(R.string.ev3_sensor_touch), 4));

		solo.goBack();
		solo.goBack();

		setSensors(EV3Sensor.Sensor.COLOR);

		solo.clickOnText(projectNameEv3);
		solo.waitForText(solo.getString(R.string.lego_ev3_sensor_config_info_title));

		assertTrue("Wrong Sensors listed in EV3 Sensor Dialog",
				solo.searchText(solo.getString(R.string.ev3_sensor_color), 4));

		solo.goBack();
		solo.goBack();

		solo.clickOnText(projectNameNoEv3);
		solo.waitForText(solo.getString(R.string.background));
		solo.sleep(200);

		assertFalse("EV3 Sensor Dialog was shown for Project without EV3 elements",
				solo.searchText(solo.getString(R.string.lego_ev3_sensor_config_info_title)));

		solo.goBack();
		solo.goBack();

		solo.clickOnActionBarItem(R.id.settings);
		String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_ev3_bricks);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
		solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

		solo.goBack();
		solo.goBack();

		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));

		solo.clickOnText(projectNameEv3);
		solo.waitForText(solo.getString(R.string.spritelist_background_headline));

		assertFalse("EV3 Sensor Dialog was shown while disabled in settings",
				solo.searchText(solo.getString(R.string.lego_ev3_sensor_config_info_title)));

		solo.goBack();
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));
		solo.clickOnText(projectNameNoEv3);

		solo.clickOnText(spriteName);
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));

		solo.waitForText(solo.getString(R.string.brick_wait));
		solo.clickOnText(solo.getString(R.string.brick_wait));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));

		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_device));
		solo.sleep(200);
		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_ev3_sensor_touch), 1, true);
		solo.clickOnText(solo.getString(R.string.ev3_sensor_color));
		solo.clickOnText(solo.getString(R.string.yes));
		solo.clickOnText(solo.getString(R.string.ok));

		solo.goBack();
		solo.goBack();
		solo.goBack();

		solo.clickOnActionBarItem(R.id.settings);
		solo.waitForText(preferenceTitle);
		solo.clickOnText(preferenceTitle);
		solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
		solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

		solo.goBack();
		solo.goBack();
		solo.goBack();

		assertTrue("not in main menu2", solo.waitForText(solo.getString(R.string.main_menu_continue)));

		solo.waitForText(solo.getString(R.string.main_menu_continue));
		solo.waitForActivity(MainMenuActivity.class.getSimpleName());

		solo.clickOnText(solo.getString(R.string.main_menu_continue));

		assertTrue("EV3 Sensor Dialog not shown for Project with EV3 Sensor but no EV3 Brick",
				solo.waitForText(solo.getString(R.string.lego_ev3_sensor_config_info_title)));
	}

	public void testEV3SensorConfigurationDialog() throws InterruptedException {
		createBrickTestproject(projectNameEv3);
		boolean ev3BricksEnabledStart = SettingsActivity.isMindstormsEV3SharedPreferenceEnabled(applicationContext);
		boolean ev3DialogDisabledStart = SettingsActivity.getShowLegoEV3MindstormsSensorInfoDialog(applicationContext);

		if (!ev3BricksEnabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_ev3_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
			solo.clickOnText(preferenceTitle);

			solo.goBack();
			solo.goBack();
		}

		if (!ev3DialogDisabledStart) {
			solo.clickOnActionBarItem(R.id.settings);

			String preferenceTitle = solo.getString(R.string.preference_title_enable_mindstorms_ev3_bricks);
			solo.waitForText(preferenceTitle);
			solo.clickOnText(preferenceTitle);
			solo.waitForText(solo.getString(R.string.preference_title_mindstorms_ev3_sensors));
			solo.clickOnText(solo.getString(R.string.preference_disable_nxt_info_dialog));

			solo.goBack();
			solo.goBack();
		}
		setSensors(EV3Sensor.Sensor.COLOR);
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.main_menu_programs));
		solo.clickOnText(solo.getString(R.string.main_menu_programs));
		solo.waitForActivity(MyProjectsActivity.class.getSimpleName());
		solo.waitForText(solo.getString(R.string.programs));
		solo.clickOnText(projectNameEv3);

		solo.clickOnText(spriteName);
		solo.waitForText(solo.getString(R.string.scripts));
		solo.clickOnText(solo.getString(R.string.scripts));

		solo.waitForText(solo.getString(R.string.brick_wait));
		solo.clickOnText(solo.getString(R.string.brick_wait));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));

		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_device));
		solo.sleep(200);
		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_ev3_sensor_touch), 1, true);
		solo.clickOnText(solo.getString(R.string.nxt_port_1));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.yes));
		// Next sensor no replace because set the same sensor
		solo.clickOnText(solo.getString(R.string.formula_editor_device));
		solo.sleep(200);
		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_ev3_sensor_touch), 1, true);
		solo.waitForText(solo.getString(R.string.ev3_sensor_touch));
		assertTrue("EV3 Sensor was not replaced.",
				solo.searchText(solo.getString(R.string.ev3_sensor_touch)));
		solo.clickOnText(solo.getString(R.string.ev3_sensor_touch), 2);
		assertFalse("Replace dialog shown spuriously.",
				solo.searchText(solo.getString(R.string.yes)));
		// Next sensor not replaced
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.formula_editor_device));
		solo.sleep(200);
		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_ev3_sensor_color), 1, true);
		solo.clickOnText(solo.getString(R.string.nxt_port_1));
		solo.clickOnText(solo.getString(R.string.no));
		solo.goBack();
		solo.clickOnText(solo.getString(R.string.no));
		// Look if ultrasonic sensor ist still mapped
		solo.waitForText(solo.getString(R.string.brick_wait));
		solo.clickOnText(solo.getString(R.string.brick_wait));
		solo.waitForText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));
		solo.clickOnText(solo.getString(R.string.brick_context_dialog_formula_edit_brick));

		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_device));
		solo.sleep(200);
		solo.waitForText(solo.getString(R.string.formula_editor_device));
		solo.clickOnText(solo.getString(R.string.formula_editor_sensor_lego_ev3_sensor_color), 1, true);
		assertTrue("EV3 Sensor was not replaced.",
				solo.searchText(solo.getString(R.string.ev3_sensor_touch))); // could depend on screen-resolution!

		solo.clickOnText(solo.getString(R.string.nxt_port_1));
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.yes));
		solo.goBack();
		solo.sleep(200);
		solo.clickOnText(solo.getString(R.string.yes));

		solo.goBack();
		solo.goBack();
		solo.goBack();
		solo.goBack();

		assertTrue("not in main menu2", solo.waitForText(solo.getString(R.string.main_menu_continue)));
	}

	private void createBrickTestproject(String projectName) {

		Sprite firstSprite = new SingleSprite(spriteName);
		Script startScript = new StartScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		LegoEv3MotorMoveBrick legoMotorActionBrick = new LegoEv3MotorMoveBrick(
				LegoEv3MotorMoveBrick.Motor.MOTOR_B_C, 100);
		WaitBrick firstWaitBrick = new WaitBrick(500);

		LegoEv3PlayToneBrick legoPlayToneBrick = new LegoEv3PlayToneBrick(50, 1.5f, 50);

		startScript.addBrick(legoMotorActionBrick);
		startScript.addBrick(firstWaitBrick);
		startScript.addBrick(legoPlayToneBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		String imageName = "image";
		File image = UiTestUtils.saveFileToProject(projectName, project.getDefaultScene().getName(), imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);

		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName(imageName);
		setLookBrick.setLook(lookData);
		firstSprite.getLookDataList().add(lookData);

		StorageHandler.getInstance().saveProject(project);
	}

	private void createNoEV3UseTestproject(String projectName) {

		Sprite firstSprite = new SingleSprite(spriteName);
		Script startScript = new StartScript();
		SetLookBrick setLookBrick = new SetLookBrick();

		WaitBrick firstWaitBrick = new WaitBrick(123);

		startScript.addBrick(firstWaitBrick);

		firstSprite.addScript(startScript);

		ArrayList<Sprite> spriteList = new ArrayList<Sprite>();
		spriteList.add(firstSprite);
		Project project = UiTestUtils.createProject(projectName, spriteList, getActivity());

		String imageName = "image";
		File image = UiTestUtils.saveFileToProject(projectName, project.getDefaultScene().getName(), imageName, IMAGE_FILE_ID, getInstrumentation()
				.getContext(), UiTestUtils.FileTypes.IMAGE);

		LookData lookData = new LookData();
		lookData.setLookFilename(image.getName());
		lookData.setLookName(imageName);
		setLookBrick.setLook(lookData);
		firstSprite.getLookDataList().add(lookData);

		StorageHandler.getInstance().saveProject(project);
	}
}
